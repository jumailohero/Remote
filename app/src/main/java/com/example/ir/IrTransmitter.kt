package com.example.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.model.ProtocolType
import com.example.model.RemoteCommand
import com.example.model.RemoteProfile

data class TransmissionResult(
    val command: RemoteCommand,
    val profileName: String,
    val protocol: ProtocolType,
    val address: Int,
    val opcode: Int,
    val carrierFreq: Int,
    val pulseCount: Int,
    val hexWord: String,
    val prontoHex: String,
    val isHardwareTransmitted: Boolean,
    val timestampMs: Long = System.currentTimeMillis()
)

class IrTransmitter(private val context: Context) {

    private val TAG = "PhilipsIrTransmitter"
    private val irManager: ConsumerIrManager? =
        context.getSystemService(Context.CONSUMER_IR_SERVICE) as? ConsumerIrManager

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var rcToggleBit = false

    val hasHardwareIr: Boolean
        get() = irManager?.hasIrEmitter() == true

    /**
     * Transmits the IR packet according to the active RemoteProfile.
     */
    fun transmit(
        command: RemoteCommand,
        profile: RemoteProfile,
        customRepeats: Int? = null,
        haptic: Boolean = true
    ): TransmissionResult {
        if (haptic) {
            triggerHapticClick()
        }

        val repeats = customRepeats ?: profile.defaultRepeats
        val frequency = profile.carrierFreq
        rcToggleBit = !rcToggleBit

        val pattern: IntArray
        val hexWord: String
        val prontoHex: String

        when (profile.protocol) {
            ProtocolType.NEC_38KHZ -> {
                pattern = NecEncoder.buildPattern(profile.address, command.opcode, repeats = repeats)
                hexWord = NecEncoder.to32BitHexWord(profile.address, command.opcode)
                prontoHex = NecEncoder.toProntoHex(profile.address, command.opcode)
            }
            ProtocolType.RC6_36KHZ -> {
                val rc6Cmd = mapToRc6(command)
                pattern = Rc6Encoder.buildPattern(profile.address, rc6Cmd, toggle = rcToggleBit, repeats = repeats)
                hexWord = "RC6: Addr 0x%02X Cmd 0x%02X".format(profile.address, rc6Cmd)
                prontoHex = "RC-6 36kHz (Pulse count: ${pattern.size})"
            }
            ProtocolType.RC5_36KHZ -> {
                val rc5Cmd = mapToRc5(command)
                pattern = Rc5Encoder.buildPattern(profile.address, rc5Cmd, toggle = rcToggleBit, repeats = repeats)
                hexWord = "RC5: Addr 0x%02X Cmd 0x%02X".format(profile.address, rc5Cmd)
                prontoHex = "RC-5 36kHz (Pulse count: ${pattern.size})"
            }
        }

        var hardwareSent = false
        if (hasHardwareIr) {
            try {
                irManager?.transmit(frequency, pattern)
                hardwareSent = true
                Log.d(TAG, "IR Blasted: ${command.name} via ${profile.name} (Freq: $frequency Hz, Pulses: ${pattern.size})")
            } catch (e: Exception) {
                Log.e(TAG, "Failed transmitting IR pulse", e)
            }
        } else {
            Log.d(TAG, "Simulated IR Blast: ${command.name} - $hexWord")
        }

        return TransmissionResult(
            command = command,
            profileName = profile.name,
            protocol = profile.protocol,
            address = profile.address,
            opcode = command.opcode,
            carrierFreq = frequency,
            pulseCount = pattern.size,
            hexWord = hexWord,
            prontoHex = prontoHex,
            isHardwareTransmitted = hardwareSent
        )
    }

    private fun mapToRc6(command: RemoteCommand): Int {
        return when (command) {
            RemoteCommand.POWER -> 0x0C
            RemoteCommand.VOL_UP -> 0x10
            RemoteCommand.VOL_DOWN -> 0x11
            RemoteCommand.MUTE -> 0x0D
            RemoteCommand.PLAY_SCAN -> 0x2C
            RemoteCommand.PREVIOUS -> 0x21
            RemoteCommand.NEXT -> 0x20
            RemoteCommand.SOURCE_CYCLE -> 0x38
            RemoteCommand.BT_DIRECT -> 0x82
            RemoteCommand.AUX_DIRECT -> 0x39
            RemoteCommand.FM_DIRECT -> 0x2F
            RemoteCommand.USB_DIRECT -> 0x84
            RemoteCommand.BASS_UP -> 0x1E
            RemoteCommand.BASS_DOWN -> 0x1F
            RemoteCommand.EQ_CYCLE -> 0x51
            RemoteCommand.FORWARD_TUNE_UP -> 0x2B
            RemoteCommand.REWIND_TUNE_DOWN -> 0x2A
            else -> command.opcode
        }
    }

    private fun mapToRc5(command: RemoteCommand): Int {
        return when (command) {
            RemoteCommand.POWER -> 0x0C
            RemoteCommand.VOL_UP -> 0x10
            RemoteCommand.VOL_DOWN -> 0x11
            RemoteCommand.MUTE -> 0x0D
            RemoteCommand.PLAY_SCAN -> 0x35
            RemoteCommand.PREVIOUS -> 0x21
            RemoteCommand.NEXT -> 0x20
            RemoteCommand.SOURCE_CYCLE -> 0x3F
            RemoteCommand.BT_DIRECT -> 0x23
            RemoteCommand.AUX_DIRECT -> 0x38
            RemoteCommand.FM_DIRECT -> 0x3F
            RemoteCommand.USB_DIRECT -> 0x24
            RemoteCommand.BASS_UP -> 0x14
            RemoteCommand.BASS_DOWN -> 0x15
            RemoteCommand.EQ_CYCLE -> 0x1A
            RemoteCommand.FORWARD_TUNE_UP -> 0x34
            RemoteCommand.REWIND_TUNE_DOWN -> 0x32
            else -> command.opcode
        }
    }

    private fun triggerHapticClick() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20L)
            }
        } catch (_: Exception) {
        }
    }
}
