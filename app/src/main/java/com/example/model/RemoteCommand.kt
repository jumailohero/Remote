package com.example.model

/**
 * Commands for the Philips MMS2625B/94 2.1 Speaker Amplifier System.
 * Protocol: NEC 38kHz. Default Device Address: 0x00.
 */
enum class RemoteCommand(
    val opcode: Int,
    val title: String,
    val subtitle: String,
    val category: CommandCategory,
    val isPrimary: Boolean = false
) {
    POWER(0x12, "Power", "Power On / Standby", CommandCategory.POWER, isPrimary = true),
    SOURCE_CYCLE(0x05, "Source", "Cycle AUX / USB / FM / BT", CommandCategory.SOURCE, isPrimary = true),
    BT_DIRECT(0x06, "Bluetooth", "Direct BT Selection", CommandCategory.SOURCE),
    AUX_DIRECT(0x04, "AUX In", "3.5mm / Line In", CommandCategory.SOURCE),
    FM_DIRECT(0x07, "FM Radio", "87.5 - 108.0 MHz", CommandCategory.SOURCE),
    USB_DIRECT(0x02, "USB Audio", "Flash drive playback", CommandCategory.SOURCE),

    VOL_UP(0x09, "VOL +", "Master Volume Up", CommandCategory.VOLUME, isPrimary = true),
    VOL_DOWN(0x0D, "VOL -", "Master Volume Down", CommandCategory.VOLUME, isPrimary = true),
    MUTE(0x03, "Mute", "Audio Silence Toggle", CommandCategory.VOLUME),

    PLAY_SCAN(0x0A, "Play / Scan", "Play/Pause & FM Auto Search", CommandCategory.PLAYBACK, isPrimary = true),
    PREVIOUS(0x08, "Previous", "Prev Track / Saved Channel -", CommandCategory.PLAYBACK),
    NEXT(0x0C, "Next", "Next Track / Saved Channel +", CommandCategory.PLAYBACK),
    REWIND_TUNE_DOWN(0x0E, "Tune - / Rewind", "FM -0.1 MHz / Fast Rewind", CommandCategory.PLAYBACK),
    FORWARD_TUNE_UP(0x0F, "Tune + / Fast Fwd", "FM +0.1 MHz / Fast Forward", CommandCategory.PLAYBACK),

    BASS_UP(0x16, "BASS +", "Subwoofer Level Up", CommandCategory.TONE),
    BASS_DOWN(0x17, "BASS -", "Subwoofer Level Down", CommandCategory.TONE),
    EQ_CYCLE(0x11, "EQ Mode", "Cycle Equalizer Presets", CommandCategory.TONE, isPrimary = true),

    // Numeric keypad for direct track / frequency entry
    NUM_1(0x21, "1", "Key 1", CommandCategory.NUMERIC),
    NUM_2(0x22, "2", "Key 2", CommandCategory.NUMERIC),
    NUM_3(0x23, "3", "Key 3", CommandCategory.NUMERIC),
    NUM_4(0x24, "4", "Key 4", CommandCategory.NUMERIC),
    NUM_5(0x25, "5", "Key 5", CommandCategory.NUMERIC),
    NUM_6(0x26, "6", "Key 6", CommandCategory.NUMERIC),
    NUM_7(0x27, "7", "Key 7", CommandCategory.NUMERIC),
    NUM_8(0x28, "8", "Key 8", CommandCategory.NUMERIC),
    NUM_9(0x29, "9", "Key 9", CommandCategory.NUMERIC),
    NUM_0(0x20, "0", "Key 0", CommandCategory.NUMERIC);
}

enum class CommandCategory {
    POWER,
    SOURCE,
    VOLUME,
    PLAYBACK,
    TONE,
    NUMERIC
}

enum class SpeakerSource(val displayName: String, val ledText: String) {
    BLUETOOTH("Bluetooth", "bLUE"),
    FM("FM Radio", "FM 98.3"),
    AUX("AUX In", "AUH"),
    USB("USB Drive", "USb")
}

enum class EqPreset(val displayName: String, val badge: String, val bassOffset: Int, val trebleOffset: Int) {
    FLAT("Flat / Standard", "STD", 0, 0),
    ROCK("Rock & Punch", "ROK", +3, +2),
    POP("Pop Vocal", "POP", +1, +2),
    JAZZ("Smooth Jazz", "JAZ", +2, +1),
    CLASSIC("Classic Acoustic", "CLS", -1, +1),
    BASS_BOOST("Mega Bass Max", "BASS+", +5, 0)
}
