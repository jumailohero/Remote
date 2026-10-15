package com.example.ir

import android.content.Context
import android.hardware.ConsumerIrManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.example.model.RemoteCommand

data class TransmissionResult(
    val command: RemoteCommand,
    val address: Int,
    val opcode: Int,
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

    /**
     * Checks whether device has an actual physical infrared blaster.
     */
    val hasHardwareIr: Boolean
        get() = irManager?.hasIrEmitter() == true

    /**
     * Transmits the 38 kHz NEC packet for the requested command using the specified device address.
     */
    fun transmit(command: RemoteCommand, address: Int = 0x00, haptic: Boolean = true): TransmissionResult {
        // Trigger subtle haptic tactile feedback for physical remote feel
        if (haptic) {
            triggerHapticClick()
        }

        val pattern = NecEncoder.buildPattern(address, command.opcode)
        val hexWord = NecEncoder.to32BitHexWord(address, command.opcode)
        val prontoHex = NecEncoder.toProntoHex(address, command.opcode)

        var hardwareSent = false
        if (hasHardwareIr) {
            try {
                irManager?.transmit(NecEncoder.CARRIER_FREQ, pattern)
                hardwareSent = true
                Log.d(TAG, "IR Blasted: ${command.name} (Addr: 0x%02X, Cmd: 0x%02X, Hex: $hexWord)".format(address, command.opcode))
            } catch (e: Exception) {
                Log.e(TAG, "Failed transmitting IR pulse", e)
            }
        } else {
            Log.d(TAG, "Simulated IR Blast (No hardware blaster): ${command.name} - $hexWord")
        }

        return TransmissionResult(
            command = command,
            address = address,
            opcode = command.opcode,
            hexWord = hexWord,
            prontoHex = prontoHex,
            isHardwareTransmitted = hardwareSent
        )
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
            // Ignore if vibration not permitted or disabled
        }
    }
}
