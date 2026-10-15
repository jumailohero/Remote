package com.example.ir

/**
 * Philips RC-6 Infrared Protocol Encoder (Mode 0).
 *
 * Carrier Frequency: 36,000 Hz.
 * Unit time t = 444 µs.
 * Leader: 6t mark (2666 µs) + 2t space (888 µs).
 * Start bit: 1t mark + 1t space.
 * Mode: 3 bits (000 for Mode 0).
 * Trailer/Toggle bit: double length (2t mark + 2t space or 2t space + 2t mark).
 * Payload: 8-bit Address + 8-bit Command in biphase/Manchester encoding.
 */
object Rc6Encoder {

    const val CARRIER_FREQ = 36000
    private const val T = 444
    private const val LEADER_MARK = 6 * T // 2664 µs
    private const val LEADER_SPACE = 2 * T // 888 µs
    private const val TRAILER_T = 2 * T // 888 µs

    fun buildPattern(address: Int, command: Int, toggle: Boolean = false, repeats: Int = 2): IntArray {
        // Collect half-bit transitions (MARK / SPACE intervals)
        // Bit 1: SPACE then MARK
        // Bit 0: MARK then SPACE
        val halfBits = mutableListOf<Boolean>() // true = MARK, false = SPACE

        // 1. Start bit: '1' -> false (space), true (mark)
        halfBits.add(false)
        halfBits.add(true)

        // 2. Mode 000 (3 bits of 0: true, false)
        for (i in 0 until 3) {
            halfBits.add(true)
            halfBits.add(false)
        }

        // 3. Trailer bit (double length)
        if (toggle) {
            halfBits.add(false)
            halfBits.add(false)
            halfBits.add(true)
            halfBits.add(true)
        } else {
            halfBits.add(true)
            halfBits.add(true)
            halfBits.add(false)
            halfBits.add(false)
        }

        // 4. Address 8-bit (MSB first)
        for (i in 7 downTo 0) {
            val bit = ((address shr i) and 1) == 1
            if (bit) {
                halfBits.add(false)
                halfBits.add(true)
            } else {
                halfBits.add(true)
                halfBits.add(false)
            }
        }

        // 5. Command 8-bit (MSB first)
        for (i in 7 downTo 0) {
            val bit = ((command shr i) and 1) == 1
            if (bit) {
                halfBits.add(false)
                halfBits.add(true)
            } else {
                halfBits.add(true)
                halfBits.add(false)
            }
        }

        // Convert halfBits to alternating Mark / Space pulse array
        val singleList = mutableListOf<Int>()
        singleList.add(LEADER_MARK)
        singleList.add(LEADER_SPACE)

        var currentIsMark = true
        var currentDuration = 0

        for (b in halfBits) {
            if (b == currentIsMark) {
                currentDuration += T
            } else {
                if (currentDuration > 0) {
                    singleList.add(currentDuration)
                }
                currentIsMark = b
                currentDuration = T
            }
        }
        if (currentDuration > 0) {
            singleList.add(currentDuration)
        }

        // Trailing gap (40 ms)
        if (singleList.size % 2 != 0) {
            singleList.add(40000)
        } else {
            val last = singleList.removeAt(singleList.size - 1)
            singleList.add(last + 40000)
        }

        val full = mutableListOf<Int>()
        for (r in 0 until repeats.coerceIn(1, 4)) {
            full.addAll(singleList)
        }

        return full.toIntArray()
    }
}
