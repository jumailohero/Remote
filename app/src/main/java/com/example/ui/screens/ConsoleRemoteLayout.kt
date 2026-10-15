package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EqPreset
import com.example.model.RemoteCommand
import com.example.model.SpeakerSource
import com.example.ui.RemoteUiState
import com.example.ui.components.RemotePhysicalButton
import com.example.ui.components.SourceSelectorRow
import com.example.ui.components.SpeakerDisplay
import com.example.ui.theme.AmberLed
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.PhilipsBlue
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated

@Composable
fun ConsoleRemoteLayout(
    state: RemoteUiState,
    onSendCommand: (RemoteCommand) -> Unit,
    onSelectSource: (SpeakerSource) -> Unit,
    onSetVolume: (Int) -> Unit,
    onSelectEq: (EqPreset) -> Unit,
    onPartyBass: () -> Unit,
    onVocalCinema: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Speaker Status Display
        SpeakerDisplay(state = state)

        Spacer(modifier = Modifier.height(14.dp))

        // Direct Sources
        SourceSelectorRow(
            activeSource = state.currentSource,
            onSelectSource = onSelectSource
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Master Volume Fader & Touch Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E2536), Color(0xFF131824))
                    )
                )
                .border(1.5.dp, Color(0xFF2C374D), RoundedCornerShape(20.dp))
                .padding(16.dp)
                .testTag("console_volume_section")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Volume",
                            tint = PhilipsCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MASTER VOLUME",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = "${state.volumeLevel} / 30",
                        color = PhilipsCyan,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Interactive Continuous Volume Slider
                Slider(
                    value = state.volumeLevel.toFloat(),
                    onValueChange = { onSetVolume(it.toInt()) },
                    valueRange = 0f..30f,
                    steps = 29,
                    colors = SliderDefaults.colors(
                        thumbColor = PhilipsCyan,
                        activeTrackColor = PhilipsBlue,
                        inactiveTrackColor = Color(0xFF252D3E)
                    ),
                    modifier = Modifier.testTag("volume_slider")
                )

                // Quick Volume Steps (0%, 25%, 50%, 75%, 100%)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val presets = listOf(
                        "MUTE" to 0,
                        "25%" to 8,
                        "50%" to 15,
                        "75%" to 22,
                        "MAX" to 30
                    )
                    for ((label, vol) in presets) {
                        val isCurrent = state.volumeLevel == vol
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) PhilipsCyan else Color(0xFF1B2332))
                                .border(1.dp, if (isCurrent) PhilipsCyan else Color(0xFF28354D), RoundedCornerShape(8.dp))
                                .clickable { onSetVolume(vol) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("quick_vol_$vol")
                        ) {
                            Text(
                                text = label,
                                color = if (isCurrent) Color.Black else Color(0xFF94A3B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Transport Media Deck (Prev, Rew, Play/Pause/Scan, Fwd, Next)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF131824))
                .border(1.5.dp, Color(0xFF263248), RoundedCornerShape(20.dp))
                .padding(14.dp)
                .testTag("console_transport_deck")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev
                RemotePhysicalButton(
                    onClick = { onSendCommand(RemoteCommand.PREVIOUS) },
                    icon = Icons.Default.SkipPrevious,
                    label = "PREV",
                    minHeight = 48.dp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Rewind
                RemotePhysicalButton(
                    onClick = { onSendCommand(RemoteCommand.REWIND_TUNE_DOWN) },
                    icon = Icons.Default.FastRewind,
                    label = "REW",
                    minHeight = 48.dp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Play / Pause / Scan (Primary central button)
                RemotePhysicalButton(
                    onClick = { onSendCommand(RemoteCommand.PLAY_SCAN) },
                    icon = Icons.Default.PlayArrow,
                    label = "PLAY/SCAN",
                    buttonColor = PhilipsBlue,
                    contentColor = Color.White,
                    borderColor = PhilipsCyan,
                    minHeight = 54.dp,
                    modifier = Modifier.weight(1.4f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Fast Forward
                RemotePhysicalButton(
                    onClick = { onSendCommand(RemoteCommand.FORWARD_TUNE_UP) },
                    icon = Icons.Default.FastForward,
                    label = "FWD",
                    minHeight = 48.dp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Next
                RemotePhysicalButton(
                    onClick = { onSendCommand(RemoteCommand.NEXT) },
                    icon = Icons.Default.SkipNext,
                    label = "NEXT",
                    minHeight = 48.dp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sound Optimization: Equalizer Presets Grid
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF161B28), Color(0xFF0F141F))
                    )
                )
                .border(1.5.dp, Color(0xFF2B364D), RoundedCornerShape(20.dp))
                .padding(14.dp)
                .testTag("console_eq_presets_section")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "EQ",
                            tint = AmberLed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DSP EQUALIZER PRESETS",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Text(
                        text = state.currentEq.displayName,
                        color = AmberLed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2 Rows of 3 EQ Presets each
                val eqList = EqPreset.entries
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (preset in eqList.take(3)) {
                            EqPresetBadge(
                                preset = preset,
                                isSelected = state.currentEq == preset,
                                onClick = { onSelectEq(preset) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (preset in eqList.drop(3)) {
                            EqPresetBadge(
                                preset = preset,
                                isSelected = state.currentEq == preset,
                                onClick = { onSelectEq(preset) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Sound Macros (Party Bass Boost & Cinema Dialog)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RemotePhysicalButton(
                onClick = onPartyBass,
                icon = Icons.Default.SurroundSound,
                label = "PARTY BASS BOOST",
                subLabel = "VOL 24 + MAX BASS",
                buttonColor = Color(0xFF381A22),
                contentColor = PowerRed,
                borderColor = PowerRed,
                modifier = Modifier.weight(1f),
                testTag = "macro_party_bass"
            )

            RemotePhysicalButton(
                onClick = onVocalCinema,
                icon = Icons.Default.Nightlight,
                label = "VOCAL CINEMA",
                subLabel = "BALANCED DIALOG",
                buttonColor = Color(0xFF132B3D),
                contentColor = PhilipsCyan,
                borderColor = PhilipsCyan,
                modifier = Modifier.weight(1f),
                testTag = "macro_vocal_cinema"
            )
        }
    }
}

@Composable
private fun EqPresetBadge(
    preset: EqPreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) AmberLed.copy(alpha = 0.2f) else Color(0xFF1F2636))
            .border(1.dp, if (isSelected) AmberLed else Color(0xFF2C374D), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = preset.badge,
                color = if (isSelected) AmberLed else Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = preset.name.lowercase().replaceFirstChar { it.uppercase() },
                color = if (isSelected) Color(0xFFFEF3C7) else Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
