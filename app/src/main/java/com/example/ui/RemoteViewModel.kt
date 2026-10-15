package com.example.ui

import android.app.Application
import android.view.KeyEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetooth.BluetoothAudioController
import com.example.ir.IrTransmitter
import com.example.ir.TransmissionResult
import com.example.model.EqPreset
import com.example.model.RemoteCommand
import com.example.model.RemoteProfile
import com.example.model.SpeakerSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RemoteLayoutType(val label: String, val iconName: String) {
    CLASSIC("Classic Remote", "Remote"),
    CONSOLE("Sound Console", "Dial"),
    QUICK("Big Buttons", "Grid")
}

enum class ControlMode(val label: String, val description: String) {
    HYBRID("Hybrid IR + Bluetooth", "Blasts IR pulses & syncs smartphone volume"),
    IR_ONLY("Infrared Only", "Pure IR blaster pulses"),
    BLUETOOTH_ONLY("Bluetooth Only", "Controls phone audio & connected BT speaker")
}

data class RemoteUiState(
    val isPoweredOn: Boolean = true,
    val currentSource: SpeakerSource = SpeakerSource.BLUETOOTH,
    val volumeLevel: Int = 18,
    val maxVolume: Int = 30,
    val isMuted: Boolean = false,
    val bassLevel: Int = 2,
    val currentEq: EqPreset = EqPreset.ROCK,
    val fmFrequency: Float = 98.3f,
    val activeProfile: RemoteProfile = RemoteProfile.ALL_PROFILES[0],
    val repeatCount: Int = 2, // 2 frames sent back-to-back for high reliability
    val hasHardwareIr: Boolean = false,
    val isBluetoothConnected: Boolean = false,
    val controlMode: ControlMode = ControlMode.HYBRID,
    val activeLayout: RemoteLayoutType = RemoteLayoutType.CLASSIC,
    val hapticEnabled: Boolean = true,
    val lastTransmission: TransmissionResult? = null,
    val isTransmittingAnim: Boolean = false,
    val recentHistory: List<TransmissionResult> = emptyList(),
    val isScanningFm: Boolean = false,
    val isAutoSyncRunning: Boolean = false,
    val autoSyncProfileIndex: Int = 0
)

class RemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val irTransmitter = IrTransmitter(application)
    private val btController = BluetoothAudioController(application)

    private val _uiState = MutableStateFlow(
        RemoteUiState(
            hasHardwareIr = irTransmitter.hasHardwareIr,
            isBluetoothConnected = btController.isConnectedToBluetoothAudio()
        )
    )
    val uiState: StateFlow<RemoteUiState> = _uiState.asStateFlow()

    private var transmitAnimJob: Job? = null
    private var fmScanJob: Job? = null
    private var autoSyncJob: Job? = null

    init {
        refreshBluetoothStatus()
    }

    fun refreshBluetoothStatus() {
        _uiState.update { it.copy(isBluetoothConnected = btController.isConnectedToBluetoothAudio()) }
    }

    fun setLayout(layout: RemoteLayoutType) {
        _uiState.update { it.copy(activeLayout = layout) }
    }

    fun setControlMode(mode: ControlMode) {
        _uiState.update { it.copy(controlMode = mode) }
    }

    fun setProfile(profile: RemoteProfile) {
        _uiState.update { it.copy(activeProfile = profile) }
        // Send a quick test burst on profile switch
        sendCommand(RemoteCommand.SOURCE_CYCLE)
    }

    fun setRepeatCount(repeats: Int) {
        _uiState.update { it.copy(repeatCount = repeats.coerceIn(1, 4)) }
    }

    fun toggleHaptic() {
        _uiState.update { it.copy(hapticEnabled = !it.hapticEnabled) }
    }

    /**
     * Central dispatch for remote commands with IR blasting and/or Bluetooth sync.
     */
    fun sendCommand(command: RemoteCommand) {
        val currentState = _uiState.value

        var transResult: TransmissionResult? = null
        if (currentState.controlMode != ControlMode.BLUETOOTH_ONLY) {
            transResult = irTransmitter.transmit(
                command = command,
                profile = currentState.activeProfile,
                customRepeats = currentState.repeatCount,
                haptic = currentState.hapticEnabled
            )
        } else if (currentState.hapticEnabled) {
            irTransmitter.transmit(command, currentState.activeProfile, customRepeats = 1, haptic = true)
        }

        if (currentState.controlMode != ControlMode.IR_ONLY) {
            when (command) {
                RemoteCommand.VOL_UP -> btController.stepVolumeUp()
                RemoteCommand.VOL_DOWN -> btController.stepVolumeDown()
                RemoteCommand.PLAY_SCAN -> btController.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                RemoteCommand.NEXT -> btController.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
                RemoteCommand.PREVIOUS -> btController.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                RemoteCommand.REWIND_TUNE_DOWN -> btController.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_REWIND)
                RemoteCommand.FORWARD_TUNE_UP -> btController.sendMediaKeyEvent(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
                else -> {}
            }
        }

        _uiState.update { state ->
            val updated = when (command) {
                RemoteCommand.POWER -> state.copy(isPoweredOn = !state.isPoweredOn)
                RemoteCommand.SOURCE_CYCLE -> {
                    val nextSource = when (state.currentSource) {
                        SpeakerSource.AUX -> SpeakerSource.USB
                        SpeakerSource.USB -> SpeakerSource.FM
                        SpeakerSource.FM -> SpeakerSource.BLUETOOTH
                        SpeakerSource.BLUETOOTH -> SpeakerSource.AUX
                    }
                    state.copy(currentSource = nextSource)
                }
                RemoteCommand.BT_DIRECT -> state.copy(currentSource = SpeakerSource.BLUETOOTH)
                RemoteCommand.AUX_DIRECT -> state.copy(currentSource = SpeakerSource.AUX)
                RemoteCommand.FM_DIRECT -> state.copy(currentSource = SpeakerSource.FM)
                RemoteCommand.USB_DIRECT -> state.copy(currentSource = SpeakerSource.USB)

                RemoteCommand.VOL_UP -> state.copy(
                    volumeLevel = (state.volumeLevel + 1).coerceAtMost(state.maxVolume),
                    isMuted = false
                )
                RemoteCommand.VOL_DOWN -> state.copy(
                    volumeLevel = (state.volumeLevel - 1).coerceAtLeast(0),
                    isMuted = false
                )
                RemoteCommand.MUTE -> state.copy(isMuted = !state.isMuted)

                RemoteCommand.BASS_UP -> state.copy(bassLevel = (state.bassLevel + 1).coerceAtMost(6))
                RemoteCommand.BASS_DOWN -> state.copy(bassLevel = (state.bassLevel - 1).coerceAtLeast(-6))

                RemoteCommand.EQ_CYCLE -> {
                    val presets = EqPreset.entries
                    val nextIndex = (state.currentEq.ordinal + 1) % presets.size
                    state.copy(currentEq = presets[nextIndex])
                }

                RemoteCommand.REWIND_TUNE_DOWN -> {
                    if (state.currentSource == SpeakerSource.FM) {
                        val newFreq = ((state.fmFrequency - 0.1f) * 10).toInt() / 10f
                        state.copy(fmFrequency = if (newFreq < 87.5f) 108.0f else newFreq)
                    } else state
                }
                RemoteCommand.FORWARD_TUNE_UP -> {
                    if (state.currentSource == SpeakerSource.FM) {
                        val newFreq = ((state.fmFrequency + 0.1f) * 10).toInt() / 10f
                        state.copy(fmFrequency = if (newFreq > 108.0f) 87.5f else newFreq)
                    } else state
                }

                RemoteCommand.PLAY_SCAN -> {
                    if (state.currentSource == SpeakerSource.FM) {
                        triggerFmAutoScan()
                        state.copy(isScanningFm = true)
                    } else state
                }

                else -> state
            }

            val history = if (transResult != null) {
                (listOf(transResult) + state.recentHistory).take(15)
            } else state.recentHistory

            updated.copy(
                lastTransmission = transResult ?: state.lastTransmission,
                recentHistory = history
            )
        }

        flashTransmitIndicator()
    }

    fun setDirectSource(source: SpeakerSource) {
        when (source) {
            SpeakerSource.BLUETOOTH -> sendCommand(RemoteCommand.BT_DIRECT)
            SpeakerSource.AUX -> sendCommand(RemoteCommand.AUX_DIRECT)
            SpeakerSource.FM -> sendCommand(RemoteCommand.FM_DIRECT)
            SpeakerSource.USB -> sendCommand(RemoteCommand.USB_DIRECT)
        }
    }

    fun setDirectEq(preset: EqPreset) {
        _uiState.update { it.copy(currentEq = preset) }
        sendCommand(RemoteCommand.EQ_CYCLE)
    }

    fun setVolumeLevel(level: Int) {
        val target = level.coerceIn(0, 30)
        _uiState.update { it.copy(volumeLevel = target, isMuted = false) }
        sendCommand(RemoteCommand.VOL_UP)
    }

    private fun flashTransmitIndicator() {
        transmitAnimJob?.cancel()
        transmitAnimJob = viewModelScope.launch {
            _uiState.update { it.copy(isTransmittingAnim = true) }
            delay(180)
            _uiState.update { it.copy(isTransmittingAnim = false) }
        }
    }

    private fun triggerFmAutoScan() {
        fmScanJob?.cancel()
        fmScanJob = viewModelScope.launch {
            val frequencies = listOf(91.1f, 92.7f, 93.5f, 94.3f, 98.3f, 101.0f, 104.0f, 106.4f)
            for (freq in frequencies) {
                delay(350)
                _uiState.update { it.copy(fmFrequency = freq) }
            }
            _uiState.update { it.copy(isScanningFm = false) }
        }
    }

    /**
     * Auto Code Search / Quick Scanner:
     * Cycles through each profile and blasts Power or Source every 1.5 seconds so user can see which one responds.
     */
    fun startAutoSync(testCommand: RemoteCommand = RemoteCommand.SOURCE_CYCLE) {
        autoSyncJob?.cancel()
        _uiState.update { it.copy(isAutoSyncRunning = true) }

        autoSyncJob = viewModelScope.launch {
            val profiles = RemoteProfile.ALL_PROFILES
            for (i in profiles.indices) {
                _uiState.update { it.copy(activeProfile = profiles[i], autoSyncProfileIndex = i) }
                sendCommand(testCommand)
                delay(1600)
            }
            _uiState.update { it.copy(isAutoSyncRunning = false) }
        }
    }

    fun stopAutoSync() {
        autoSyncJob?.cancel()
        _uiState.update { it.copy(isAutoSyncRunning = false) }
    }

    fun applyPartyBassPreset() {
        viewModelScope.launch {
            sendCommand(RemoteCommand.BT_DIRECT)
            delay(120)
            _uiState.update { it.copy(volumeLevel = 24, bassLevel = 5, currentEq = EqPreset.BASS_BOOST) }
            sendCommand(RemoteCommand.BASS_UP)
        }
    }

    fun applyVocalCinemaPreset() {
        viewModelScope.launch {
            sendCommand(RemoteCommand.AUX_DIRECT)
            delay(120)
            _uiState.update { it.copy(volumeLevel = 18, bassLevel = 1, currentEq = EqPreset.POP) }
            sendCommand(RemoteCommand.EQ_CYCLE)
        }
    }
}
