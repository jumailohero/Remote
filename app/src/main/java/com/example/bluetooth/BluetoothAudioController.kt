package com.example.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.view.KeyEvent

/**
 * Controller for smartphone device controls & Bluetooth audio integration with Philips MMS2625B.
 *
 * Allows seamless volume sync and media controls via AudioManager and Bluetooth AVRCP.
 */
class BluetoothAudioController(private val context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    /**
     * Checks if current audio is being routed to a connected Bluetooth A2DP audio device (e.g. Philips speaker).
     */
    fun isConnectedToBluetoothAudio(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && audioManager != null) {
            val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            return devices.any { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP || it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
        }
        @Suppress("DEPRECATION")
        return audioManager?.isBluetoothA2dpOn == true
    }

    /**
     * Get current phone media volume percentage (0 - 100).
     */
    fun getCurrentMediaVolume(): Int {
        val am = audioManager ?: return 50
        val current = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1)
        return (current * 100) / max
    }

    /**
     * Set phone media volume from percentage (0 - 100).
     */
    fun setMediaVolume(percent: Int) {
        val am = audioManager ?: return
        val max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val target = (percent.coerceIn(0, 100) * max) / 100
        am.setStreamVolume(AudioManager.STREAM_MUSIC, target, AudioManager.FLAG_SHOW_UI)
    }

    /**
     * Adjust phone media volume step up.
     */
    fun stepVolumeUp() {
        audioManager?.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    /**
     * Adjust phone media volume step down.
     */
    fun stepVolumeDown() {
        audioManager?.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )
    }

    /**
     * Dispatch media key event (Play/Pause, Next, Prev) over Bluetooth AVRCP to current active player.
     */
    fun sendMediaKeyEvent(keyCode: Int) {
        audioManager?.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        audioManager?.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }
}
