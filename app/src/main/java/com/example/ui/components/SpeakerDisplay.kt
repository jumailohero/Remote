package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.model.SpeakerSource
import com.example.ui.RemoteUiState
import com.example.ui.theme.AmberLed
import com.example.ui.theme.DisplayBg
import com.example.ui.theme.DisplayLedCyan
import com.example.ui.theme.DisplayLedDim
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.PowerRedGlow
import com.example.ui.theme.SuccessGreen

@Composable
fun SpeakerDisplay(
    state: RemoteUiState,
    modifier: Modifier = Modifier
) {
    val irAnimColor by animateColorAsState(
        targetValue = if (state.isTransmittingAnim) PowerRedGlow else Color.Transparent,
        animationSpec = tween(durationMillis = 150),
        label = "irFlash"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scanAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141923),
                        DisplayBg,
                        Color(0xFF090D14)
                    )
                )
            )
            .border(1.5.dp, Color(0xFF283347), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
            .testTag("speaker_display_card")
    ) {
        Column {
            // Header Row: Philips MMS2625B badge and Transmitter Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Power status LED
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (state.isPoweredOn) SuccessGreen else PowerRed)
                            .shadow(if (state.isPoweredOn) 6.dp else 2.dp, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PHILIPS",
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MMS2625B 2.1",
                        color = TextDimmed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // IR & BT hardware telemetry badges
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // IR Blaster active indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (state.isTransmittingAnim) Color(0x33EF4444) else Color(0x1A38BDF8))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (state.isTransmittingAnim) PowerRed else if (state.hasHardwareIr) PhilipsCyan else AmberLed)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.Sensors,
                            contentDescription = "IR status",
                            tint = if (state.isTransmittingAnim) PowerRed else ElectricAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (state.isTransmittingAnim) "BLASTING" else if (state.hasHardwareIr) "IR 38kHz" else "IR SIM",
                            color = if (state.isTransmittingAnim) PowerRed else ElectricAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (state.isBluetoothConnected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Bluetooth,
                            contentDescription = "Bluetooth Connected",
                            tint = PhilipsCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Display Screen Segment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF03070E))
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                if (!state.isPoweredOn) {
                    // Standby Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STbY",
                            color = Color(0xFFEF4444).copy(alpha = 0.8f),
                            fontSize = 28.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Source / Mode Segment
                        Column {
                            val sourceDisplay = when (state.currentSource) {
                                SpeakerSource.BLUETOOTH -> "bLUE"
                                SpeakerSource.FM -> if (state.isScanningFm) "SCAn" else "FM ${String.format("%.1f", state.fmFrequency)}"
                                SpeakerSource.AUX -> "AUH"
                                SpeakerSource.USB -> "USb"
                            }

                            Text(
                                text = sourceDisplay,
                                color = if (state.isScanningFm) DisplayLedCyan.copy(alpha = scanAlpha) else DisplayLedCyan,
                                fontSize = 26.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.shadow(8.dp)
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = state.currentSource.displayName.uppercase(),
                                    color = TextDimmed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (state.currentSource == SpeakerSource.FM) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Radio,
                                        contentDescription = "Radio",
                                        tint = ElectricAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        // Right: Volume / Bass / EQ Badge
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (state.isMuted) {
                                    Text(
                                        text = "MUTE",
                                        color = PowerRed,
                                        fontSize = 22.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Volume",
                                        tint = ElectricAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "VOL ${state.volumeLevel.toString().padStart(2, '0')}",
                                        color = DisplayLedCyan,
                                        fontSize = 22.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Secondary line: Bass level & EQ badge
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "BASS: ${if (state.bassLevel >= 0) "+${state.bassLevel}" else state.bassLevel}",
                                    color = AmberLed,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ElectricAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = state.currentEq.badge,
                                        color = ElectricAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Visual Graphic Equalizer bars simulation
            if (state.isPoweredOn && !state.isMuted) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val volFrac = state.volumeLevel / 30f
                    val barCount = 14
                    for (i in 0 until barCount) {
                        val active = (i.toFloat() / barCount) <= volFrac
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .padding(horizontal = 1.5.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    when {
                                        !active -> DisplayLedDim
                                        i >= 11 -> PowerRed
                                        i >= 8 -> AmberLed
                                        else -> PhilipsCyan
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}

private val TextDimmed = Color(0xFF64748B)
