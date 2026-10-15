package com.example.ir

/**
 * High-performance NEC Infrared Protocol Encoder for Philips MMS2625B/94 Multimedia Speaker System.
 *
 * Implements 38 kHz carrier frequency pulse-distance modulation:
 * - Preamble / Leader: 9000 µs Mark + 4500 µs Space
 * - Bit 0: 560 µs Mark + 560 µs Space (Total 1120 µs)
 * - Bit 1: 560 µs Mark + 1690 µs Space (Total 2250 µs)
 * - Stop bit: 560 µs Mark
 * - 32-bit Frame: [Addr 8-bit] + [~Addr 8-bit] + [Cmd 8-bit] + [~Cmd 8-bit] (LSB first)
 */
object NecEncoder {

    const val CARRIER_FREQ = 38000 // 38 kHz carrier frequency

    private const val LEADER_MARK = 9000
    private const val LEADER_SPACE = 4500
    private const val BIT_MARK = 560
    private const val ZERO_SPACE = 560
    private const val ONE_SPACE = 1690
    private const val STOP_MARK = 560

    /**
     * Builds a 67-element microsecond timing pattern suitable for [android.hardware.ConsumerIrManager.transmit].
     *
     * @param address 8-bit device address (Default Philips MMS2625B: 0x00)
     * @param command 8-bit opcode command (e.g. 0x12 for Power)
     * @return int[] array of alternating mark and space microsecond intervals
     */
    fun buildPattern(address: Int, command: Int): IntArray {
        val pattern = IntArray(67)

        // 1. Leader code
        pattern[0] = LEADER_MARK
        pattern[1] = LEADER_SPACE

        // 2. 32-bit Frame payload: Addr + ~Addr + Cmd + ~Cmd
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
            pattern[index++] = BIT_MARK
            val isBitOne = ((frame shr i) and 1L) == 1L
            pattern[index++] = if (isBitOne) ONE_SPACE else ZERO_SPACE
        }

        // 3. Stop bit
        pattern[66] = STOP_MARK

        return pattern
    }

    /**
     * Generates the standard 32-bit NEC Hex representation string (e.g. "0x00FF12ED").
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
        val pattern = buildPattern(address, command)
        // 38 kHz pronto frequency multiplier: 1000000 / (38000 * 0.241246) approx 0x006D
        val timeBase = 1000000.0 / CARRIER_FREQ

        val sb = StringBuilder()
        sb.append("0000 006d 0022 0000 ") // Pronto preamble: raw, 38kHz, burst pair count 34

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
