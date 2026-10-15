package com.example

import com.example.ir.NecEncoder
import com.example.model.RemoteCommand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testNecCarrierFrequency() {
        assertEquals(38000, NecEncoder.CARRIER_FREQ)
    }

    @Test
    fun testNecPatternLength() {
        val pattern = NecEncoder.buildPattern(address = 0x00, command = RemoteCommand.POWER.opcode)
        // 1 leader mark + 1 leader space + 32 bits * 2 (mark & space) + 1 stop mark = 67 elements
        assertEquals(67, pattern.size)
        assertEquals(9000, pattern[0]) // Leader mark 9000us
        assertEquals(4500, pattern[1]) // Leader space 4500us
        assertEquals(560, pattern[66]) // Stop bit mark 560us
    }

    @Test
    fun testMasterOpcodes32BitHexWords() {
        // Power 0x12 -> 0x00FF12ED
        assertEquals("0x00FF12ED", NecEncoder.to32BitHexWord(0x00, RemoteCommand.POWER.opcode))

        // Source 0x05 -> 0x00FF05FA
        assertEquals("0x00FF05FA", NecEncoder.to32BitHexWord(0x00, RemoteCommand.SOURCE_CYCLE.opcode))

        // BT Direct 0x06 -> 0x00FF06F9
        assertEquals("0x00FF06F9", NecEncoder.to32BitHexWord(0x00, RemoteCommand.BT_DIRECT.opcode))

        // Vol + 0x09 -> 0x00FF09F6
        assertEquals("0x00FF09F6", NecEncoder.to32BitHexWord(0x00, RemoteCommand.VOL_UP.opcode))

        // Vol - 0x0D -> 0x00FF0DF2
        assertEquals("0x00FF0DF2", NecEncoder.to32BitHexWord(0x00, RemoteCommand.VOL_DOWN.opcode))

        // Play/Scan 0x0A -> 0x00FF0AF5
        assertEquals("0x00FF0AF5", NecEncoder.to32BitHexWord(0x00, RemoteCommand.PLAY_SCAN.opcode))

        // Mute 0x03 -> 0x00FF03FC
        assertEquals("0x00FF03FC", NecEncoder.to32BitHexWord(0x00, RemoteCommand.MUTE.opcode))

        // Bass + 0x16 -> 0x00FF16E9
        assertEquals("0x00FF16E9", NecEncoder.to32BitHexWord(0x00, RemoteCommand.BASS_UP.opcode))

        // Bass - 0x17 -> 0x00FF17E8
        assertEquals("0x00FF17E8", NecEncoder.to32BitHexWord(0x00, RemoteCommand.BASS_DOWN.opcode))
    }

    @Test
    fun testProntoHexFormat() {
        val pronto = NecEncoder.toProntoHex(0x00, RemoteCommand.POWER.opcode)
        assertTrue(pronto.startsWith("0000 006d 0022 0000"))
    }
}
