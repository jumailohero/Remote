package com.example.model

enum class ProtocolType {
    NEC_38KHZ,
    RC6_36KHZ,
    RC5_36KHZ
}

data class RemoteProfile(
    val id: String,
    val name: String,
    val description: String,
    val protocol: ProtocolType,
    val address: Int,
    val carrierFreq: Int = 38000,
    val defaultRepeats: Int = 2
) {
    companion object {
        val ALL_PROFILES = listOf(
            RemoteProfile(
                id = "mms_default",
                name = "Profile 1 (NEC Addr 0x00)",
                description = "Standard Philips MMS2625B (38kHz, Addr 0x00, 2x frame repeat)",
                protocol = ProtocolType.NEC_38KHZ,
                address = 0x00,
                carrierFreq = 38000,
                defaultRepeats = 2
            ),
            RemoteProfile(
                id = "mms_abs8776",
                name = "Profile 2 (ABS-8776 / Addr 0x08)",
                description = "NEC 38kHz Addr 0x08 (Matched ABS-8776 profile that triggered Source mode)",
                protocol = ProtocolType.NEC_38KHZ,
                address = 0x08,
                carrierFreq = 38000,
                defaultRepeats = 2
            ),
            RemoteProfile(
                id = "mms_alt_7f",
                name = "Profile 3 (NEC Addr 0x7F)",
                description = "NEC 38kHz Addr 0x7F (ODM Motherboard revision for Indian 2.1 systems)",
                protocol = ProtocolType.NEC_38KHZ,
                address = 0x7F,
                carrierFreq = 38000,
                defaultRepeats = 2
            ),
            RemoteProfile(
                id = "mms_alt_ff",
                name = "Profile 4 (NEC Addr 0xFF)",
                description = "NEC 38kHz Addr 0xFF (Inverted address variant)",
                protocol = ProtocolType.NEC_38KHZ,
                address = 0xFF,
                carrierFreq = 38000,
                defaultRepeats = 2
            ),
            RemoteProfile(
                id = "mms_alt_01",
                name = "Profile 5 (NEC Addr 0x01)",
                description = "NEC 38kHz Addr 0x01 (Revision B controller)",
                protocol = ProtocolType.NEC_38KHZ,
                address = 0x01,
                carrierFreq = 38000,
                defaultRepeats = 2
            ),
            RemoteProfile(
                id = "philips_rc6",
                name = "Profile 6 (Philips RC-6 36kHz)",
                description = "Philips Audio Standard RC-6 Mode 0 (Addr 0x10, 36kHz)",
                protocol = ProtocolType.RC6_36KHZ,
                address = 0x10,
                carrierFreq = 36000,
                defaultRepeats = 2
            ),
            RemoteProfile(
                id = "philips_rc5",
                name = "Profile 7 (Philips RC-5 36kHz)",
                description = "Classic Philips Audio RC-5 (Addr 0x10/0x14, 36kHz)",
                protocol = ProtocolType.RC5_36KHZ,
                address = 0x10,
                carrierFreq = 36000,
                defaultRepeats = 2
            )
        )
    }
}
