package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RemoteCommand
import com.example.model.SpeakerSource
import com.example.ui.RemoteUiState
import com.example.ui.components.RemoteDPadJogWheel
import com.example.ui.components.RemotePhysicalButton
import com.example.ui.components.SourceSelectorRow
import com.example.ui.components.SpeakerDisplay
import com.example.ui.theme.AmberLed
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated

@Composable
fun ClassicRemoteLayout(
    state: RemoteUiState,
    onSendCommand: (RemoteCommand) -> Unit,
    onSelectSource: (SpeakerSource) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNumpad by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Acrylic Speaker Display
        SpeakerDisplay(state = state)

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Physical Remote Chassis Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(26.dp))
                .clip(RoundedCornerShape(26.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B202C),
                            Color(0xFF121620),
                            Color(0xFF0C0F16)
                        )
                    )
                )
                .border(2.dp, Color(0xFF2B3447), RoundedCornerShape(26.dp))
                .padding(16.dp)
                .testTag("classic_remote_chassis")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Top Action Bar: Power (Red Halo), Source Cycle, Mute
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Power Button
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.POWER) },
                        icon = Icons.Default.PowerSettingsNew,
                        label = "POWER",
                        buttonColor = if (state.isPoweredOn) PowerRed else Color(0xFF4A101D),
                        contentColor = Color.White,
                        borderColor = Color(0xFFF43F5E),
                        modifier = Modifier.weight(1.2f),
                        testTag = "btn_power"
                    )

                    // Source Cycle Button
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.SOURCE_CYCLE) },
                        icon = Icons.Default.Refresh,
                        label = "SOURCE",
                        subLabel = "CYCLE",
                        buttonColor = Color(0xFF263043),
                        contentColor = PhilipsCyan,
                        modifier = Modifier.weight(1.2f),
                        testTag = "btn_source_cycle"
                    )

                    // Mute Button
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.MUTE) },
                        icon = Icons.Default.VolumeOff,
                        label = "MUTE",
                        buttonColor = if (state.isMuted) Color(0xFF5A1A24) else Color(0xFF232A39),
                        contentColor = if (state.isMuted) PowerRed else Color(0xFFCBD5E1),
                        modifier = Modifier.weight(1f),
                        testTag = "btn_mute"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct Input Mode Badges (Bluetooth, FM, AUX, USB)
                SourceSelectorRow(
                    activeSource = state.currentSource,
                    onSelectSource = onSelectSource
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Centerpiece: Circular D-Pad Jog Wheel (Vol+, Vol-, Prev, Next, Play/Scan)
                RemoteDPadJogWheel(
                    onPlayPause = { onSendCommand(RemoteCommand.PLAY_SCAN) },
                    onVolUp = { onSendCommand(RemoteCommand.VOL_UP) },
                    onVolDown = { onSendCommand(RemoteCommand.VOL_DOWN) },
                    onPrev = { onSendCommand(RemoteCommand.PREVIOUS) },
                    onNext = { onSendCommand(RemoteCommand.NEXT) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Rewind / Forward and Bass Control Deck
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Rewind / Tune -
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.REWIND_TUNE_DOWN) },
                        icon = Icons.Default.FastRewind,
                        label = "REW / TUNE-",
                        subLabel = "-0.1 MHz",
                        modifier = Modifier.weight(1f),
                        testTag = "btn_rewind_tune_down"
                    )

                    // Fast Forward / Tune +
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.FORWARD_TUNE_UP) },
                        icon = Icons.Default.FastForward,
                        label = "FWD / TUNE+",
                        subLabel = "+0.1 MHz",
                        modifier = Modifier.weight(1f),
                        testTag = "btn_forward_tune_up"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bass Up / Bass Down & EQ Preset Cycle Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bass -
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.BASS_DOWN) },
                        label = "BASS -",
                        subLabel = "SUB DOWN",
                        contentColor = AmberLed,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_bass_down"
                    )

                    // Bass +
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.BASS_UP) },
                        label = "BASS +",
                        subLabel = "SUB UP",
                        contentColor = AmberLed,
                        modifier = Modifier.weight(1f),
                        testTag = "btn_bass_up"
                    )

                    // EQ Preset Cycle Button
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.EQ_CYCLE) },
                        icon = Icons.Default.GraphicEq,
                        label = "EQ MODE",
                        subLabel = state.currentEq.badge,
                        buttonColor = Color(0xFF1E3A5F),
                        contentColor = PhilipsCyan,
                        borderColor = PhilipsCyan,
                        modifier = Modifier.weight(1.2f),
                        testTag = "btn_eq_cycle"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Collapsible Direct Numeric Pad for Track/Station Direct Selection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF181E2B))
                        .border(1.dp, Color(0xFF28344A), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dialpad,
                            contentDescription = "Keypad",
                            tint = ElectricAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Numeric Keypad (Track / FM Preset)",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    RemotePhysicalButton(
                        onClick = { showNumpad = !showNumpad },
                        label = if (showNumpad) "HIDE" else "SHOW",
                        minHeight = 32.dp,
                        buttonColor = Color(0xFF232D3F),
                        contentColor = PhilipsCyan,
                        testTag = "toggle_numpad"
                    )
                }

                AnimatedVisibility(visible = showNumpad) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_1) }, label = "1", modifier = Modifier.weight(1f), minHeight = 44.dp)
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_2) }, label = "2", modifier = Modifier.weight(1f), minHeight = 44.dp)
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_3) }, label = "3", modifier = Modifier.weight(1f), minHeight = 44.dp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_4) }, label = "4", modifier = Modifier.weight(1f), minHeight = 44.dp)
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_5) }, label = "5", modifier = Modifier.weight(1f), minHeight = 44.dp)
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_6) }, label = "6", modifier = Modifier.weight(1f), minHeight = 44.dp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_7) }, label = "7", modifier = Modifier.weight(1f), minHeight = 44.dp)
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_8) }, label = "8", modifier = Modifier.weight(1f), minHeight = 44.dp)
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_9) }, label = "9", modifier = Modifier.weight(1f), minHeight = 44.dp)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.PLAY_SCAN) }, label = "AUTO SCAN", subLabel = "FM SEARCH", modifier = Modifier.weight(2f), minHeight = 44.dp)
                            RemotePhysicalButton(onClick = { onSendCommand(RemoteCommand.NUM_0) }, label = "0", modifier = Modifier.weight(1f), minHeight = 44.dp)
                        }
                    }
                }
            }
        }
    }
}
