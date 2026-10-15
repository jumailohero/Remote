package com.example.ir

/**
 * High-performance NEC Infrared Protocol Encoder for Philips MMS2625B/94 Multimedia Speaker System.
 *
 * Implements 38 kHz carrier frequency pulse-distance modulation with proper lead-out gap and frame repetition:
 * - Preamble / Leader: 9000 µs Mark + 4500 µs Space
 * - Bit 0: 560 µs Mark + 560 µs Space
 * - Bit 1: 560 µs Mark + 1690 µs Space
 * - Stop bit: 560 µs Mark
 * - Trailing Gap / Lead-out: 40000 µs Space
 * - Frame Repeat: Transmits 2 full frames back-to-back so AGC bandpass filters lock on reliably.
 */
object NecEncoder {

    const val CARRIER_FREQ = 38000 // 38 kHz carrier frequency

    private const val LEADER_MARK = 9000
    private const val LEADER_SPACE = 4500
    private const val BIT_MARK = 560
    private const val ZERO_SPACE = 560
    private const val ONE_SPACE = 1690
    private const val STOP_MARK = 560
    private const val LEAD_OUT_GAP = 40000 // 40 ms lead-out gap

    /**
     * Builds microsecond timing pattern suitable for [android.hardware.ConsumerIrManager.transmit].
     * Includes trailing space and repeats frames so hardware receivers register the signal.
     *
     * @param address 8-bit device address (Default: 0x00)
     * @param command 8-bit opcode command
     * @param repeats Number of full frames to transmit (default 2 for high reliability)
     * @return int[] array of alternating mark and space microsecond intervals (even length)
     */
    fun buildPattern(address: Int, command: Int, repeats: Int = 2): IntArray {
        // Single frame consists of: Leader (2) + 32 bits (64) + Stop (1) + Gap (1) = 68 intervals
        val singleFrame = IntArray(68)
        singleFrame[0] = LEADER_MARK
        singleFrame[1] = LEADER_SPACE

        val addr = address and 0xFF
        val invAddr = (addr.inv()) and 0xFF
        val cmd = command and 0xFF
        val invCmd = (cmd.inv()) and 0xFF

        val frame = (addr.toLong()) or
                (invAddr.toLong() shl 8) or
                (cmd.toLong() shl 16) or
                (invCmd.toLong() shl 24)

        var index = 2
        for (i in 0 until 32) {
            singleFrame[index++] = BIT_MARK
            val isBitOne = ((frame shr i) and 1L) == 1L
            singleFrame[index++] = if (isBitOne) ONE_SPACE else ZERO_SPACE
        }

        // Stop bit mark and trailing lead-out gap
        singleFrame[66] = STOP_MARK
        singleFrame[67] = LEAD_OUT_GAP

        val safeRepeats = repeats.coerceIn(1, 4)
        if (safeRepeats == 1) {
            return singleFrame
        }

        // Concatenate multiple frames back-to-back
        val fullPattern = IntArray(68 * safeRepeats)
        for (r in 0 until safeRepeats) {
            System.arraycopy(singleFrame, 0, fullPattern, r * 68, 68)
        }

        return fullPattern
    }

    /**
     * Generates 32-bit NEC Hex representation string (e.g. "0x00FF12ED").
     */
    fun to32BitHexWord(address: Int, command: Int): String {
        val addr = address and 0xFF
        val invAddr = (addr.inv()) and 0xFF
        val cmd = command and 0xFF
        val invCmd = (cmd.inv()) and 0xFF

        val word = (addr shl 24) or (invAddr shl 16) or (cmd shl 8) or invCmd
        return String.format("0x%08X", word.toLong() and 0xFFFFFFFFL)
    }

    /**
     * Converts the NEC pulse pattern to standard Pronto Hex string (38 kHz).
     */
    fun toProntoHex(address: Int, command: Int): String {
        val pattern = buildPattern(address, command, repeats = 1)
        val timeBase = 1000000.0 / CARRIER_FREQ

        val sb = StringBuilder()
        sb.append("0000 006d 0022 0000 ")

        for (i in 0 until pattern.size step 2) {
            val markCycles = Math.round(pattern[i] / timeBase).toInt()
            val spaceCycles = if (i + 1 < pattern.size) {
                Math.round(pattern[i + 1] / timeBase).toInt()
            } else {
                0x0689
            }
            sb.append(String.format("%04x %04x ", markCycles, spaceCycles))
        }

        return sb.toString().trim()
    }
}
