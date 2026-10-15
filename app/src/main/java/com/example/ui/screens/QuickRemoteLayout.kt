package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.RemoteCommand
import com.example.model.SpeakerSource
import com.example.ui.RemoteUiState
import com.example.ui.components.RemotePhysicalButton
import com.example.ui.components.SourceSelectorRow
import com.example.ui.components.SpeakerDisplay
import com.example.ui.theme.AmberLed
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed

@Composable
fun QuickRemoteLayout(
    state: RemoteUiState,
    onSendCommand: (RemoteCommand) -> Unit,
    onSelectSource: (SpeakerSource) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Display
        SpeakerDisplay(state = state)

        Spacer(modifier = Modifier.height(14.dp))

        // Direct Source row
        SourceSelectorRow(
            activeSource = state.currentSource,
            onSelectSource = onSelectSource
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Large Big-Button Grid for fast, glance-free control
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF131824))
                .border(2.dp, Color(0xFF28344A), RoundedCornerShape(22.dp))
                .padding(14.dp)
                .testTag("quick_remote_grid")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Row 1: Huge Power & Mute
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.POWER) },
                        icon = Icons.Default.PowerSettingsNew,
                        label = "POWER ON/OFF",
                        buttonColor = if (state.isPoweredOn) PowerRed else Color(0xFF4A101D),
                        contentColor = Color.White,
                        minHeight = 64.dp,
                        modifier = Modifier.weight(1.4f),
                        testTag = "quick_btn_power"
                    )

                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.MUTE) },
                        icon = Icons.Default.VolumeOff,
                        label = "MUTE",
                        buttonColor = if (state.isMuted) Color(0xFF5A1A24) else Color(0xFF232A39),
                        contentColor = if (state.isMuted) PowerRed else Color.White,
                        minHeight = 64.dp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_btn_mute"
                    )
                }

                // Row 2: Massive Volume Up & Down Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.VOL_DOWN) },
                        icon = Icons.Default.Remove,
                        label = "VOLUME -",
                        subLabel = "CURRENT: ${state.volumeLevel}",
                        buttonColor = Color(0xFF1E283A),
                        contentColor = PhilipsCyan,
                        minHeight = 76.dp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_btn_vol_down"
                    )

                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.VOL_UP) },
                        icon = Icons.Default.Add,
                        label = "VOLUME +",
                        subLabel = "CURRENT: ${state.volumeLevel}",
                        buttonColor = Color(0xFF1E283A),
                        contentColor = PhilipsCyan,
                        minHeight = 76.dp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_btn_vol_up"
                    )
                }

                // Row 3: Huge Playback Transport (Prev, Play/Scan, Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.PREVIOUS) },
                        icon = Icons.Default.ArrowBackIosNew,
                        label = "PREV",
                        minHeight = 68.dp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_btn_prev"
                    )

                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.PLAY_SCAN) },
                        icon = Icons.Default.PlayArrow,
                        label = "PLAY / SCAN",
                        buttonColor = Color(0xFF007AD9),
                        contentColor = Color.White,
                        minHeight = 68.dp,
                        modifier = Modifier.weight(1.4f),
                        testTag = "quick_btn_play_scan"
                    )

                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.NEXT) },
                        icon = Icons.Default.ArrowForwardIos,
                        label = "NEXT",
                        minHeight = 68.dp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_btn_next"
                    )
                }

                // Row 4: Bass - / Bass + / EQ Preset Cycle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.BASS_DOWN) },
                        label = "BASS -",
                        subLabel = "${state.bassLevel}",
                        contentColor = AmberLed,
                        minHeight = 60.dp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_btn_bass_down"
                    )

                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.BASS_UP) },
                        label = "BASS +",
                        subLabel = "${state.bassLevel}",
                        contentColor = AmberLed,
                        minHeight = 60.dp,
                        modifier = Modifier.weight(1f),
                        testTag = "quick_btn_bass_up"
                    )

                    RemotePhysicalButton(
                        onClick = { onSendCommand(RemoteCommand.EQ_CYCLE) },
                        icon = Icons.Default.GraphicEq,
                        label = "EQ PRESET",
                        subLabel = state.currentEq.badge,
                        buttonColor = Color(0xFF1E3A5F),
                        contentColor = PhilipsCyan,
                        borderColor = PhilipsCyan,
                        minHeight = 60.dp,
                        modifier = Modifier.weight(1.3f),
                        testTag = "quick_btn_eq"
                    )
                }
            }
        }
    }
}
