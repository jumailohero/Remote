package com.example.ir

/**
 * Philips RC-5 Infrared Protocol Encoder.
 *
 * Carrier Frequency: 36,000 Hz.
 * Unit time: 889 µs.
 * 14-bit frame: 2 Start bits ('1','1') + 1 Toggle bit + 5 Address bits + 6 Command bits.
 * Manchester:
 * '1' is Space (889 µs) then Mark (889 µs).
 * '0' is Mark (889 µs) then Space (889 µs).
 */
object Rc5Encoder {

    const val CARRIER_FREQ = 36000
    private const val T = 889

    fun buildPattern(address: Int, command: Int, toggle: Boolean = false, repeats: Int = 2): IntArray {
        val halfBits = mutableListOf<Boolean>() // true = MARK, false = SPACE

        // 1. Two Start bits '1', '1'
        halfBits.add(false); halfBits.add(true)
        halfBits.add(false); halfBits.add(true)

        // 2. Toggle bit
        if (toggle) {
            halfBits.add(false); halfBits.add(true)
        } else {
            halfBits.add(true); halfBits.add(false)
        }

        // 3. 5-bit Address (MSB first)
        for (i in 4 downTo 0) {
            val bit = ((address shr i) and 1) == 1
            if (bit) {
                halfBits.add(false); halfBits.add(true)
            } else {
                halfBits.add(true); halfBits.add(false)
            }
        }

        // 4. 6-bit Command (MSB first)
        for (i in 5 downTo 0) {
            val bit = ((command shr i) and 1) == 1
            if (bit) {
                halfBits.add(false); halfBits.add(true)
            } else {
                halfBits.add(true); halfBits.add(false)
            }
        }

        val singleList = mutableListOf<Int>()
        var currentIsMark = halfBits[0]
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

        // Trailing gap (45 ms)
        if (singleList.size % 2 != 0) {
            singleList.add(45000)
        } else {
            val last = singleList.removeAt(singleList.size - 1)
            singleList.add(last + 45000)
        }

        val full = mutableListOf<Int>()
        for (r in 0 until repeats.coerceIn(1, 4)) {
            full.addAll(singleList)
        }

        return full.toIntArray()
    }
}
