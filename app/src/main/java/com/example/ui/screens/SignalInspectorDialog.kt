package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RemoteCommand
import com.example.model.RemoteProfile
import com.example.ui.ControlMode
import com.example.ui.RemoteUiState
import com.example.ui.theme.AmberLed
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SuccessGreen

@Composable
fun SignalInspectorDialog(
    state: RemoteUiState,
    onDismiss: () -> Unit,
    onSelectProfile: (RemoteProfile) -> Unit,
    onSetRepeatCount: (Int) -> Unit,
    onSetControlMode: (ControlMode) -> Unit,
    onToggleHaptic: () -> Unit,
    onStartAutoSync: () -> Unit,
    onStopAutoSync: () -> Unit,
    onTestCommand: (RemoteCommand) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = PhilipsCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Speaker IR Profiles & Sync",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hardware Status Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (state.hasHardwareIr) Color(0xFF0F2E22) else Color(0xFF2E2214))
                        .border(1.dp, if (state.hasHardwareIr) SuccessGreen else AmberLed, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.hasHardwareIr) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (state.hasHardwareIr) SuccessGreen else AmberLed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (state.hasHardwareIr) "Redmi IR Blaster Hardware Active" else "IR Simulation Mode",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (state.hasHardwareIr)
                                    "Physical 38 kHz / 36 kHz optical emitter active. Aim top of phone at speaker."
                                else
                                    "No IR hardware detected. Virtual signals enabled.",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Auto Code Search / Scanner Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B2433))
                        .border(1.dp, PhilipsCyan, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoMode,
                                    contentDescription = null,
                                    tint = PhilipsCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Auto Code Search (Quick Fix)",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cycles through all Philips MMS profiles every 1.5s. Point your phone at the speaker until it responds!",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (state.isAutoSyncRunning) {
                            LinearProgressIndicator(
                                progress = { (state.autoSyncProfileIndex + 1f) / RemoteProfile.ALL_PROFILES.size },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PhilipsCyan,
                                trackColor = Color(0xFF28344A)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Testing: ${state.activeProfile.name}...",
                                color = AmberLed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FilledTonalButton(
                                onClick = onStopAutoSync,
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = PowerRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "STOP SCANNER", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            FilledTonalButton(
                                onClick = onStartAutoSync,
                                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF007AD9)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "START AUTO CODE SEARCH", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Select Device Profile (7 options)
                Text(
                    text = "Select Remote Profile",
                    color = PhilipsCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                for (profile in RemoteProfile.ALL_PROFILES) {
                    val isSelected = state.activeProfile.id == profile.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF1E3A5F) else Color(0xFF181E2B))
                            .border(1.dp, if (isSelected) PhilipsCyan else Color(0xFF28344A), RoundedCornerShape(10.dp))
                            .clickable { onSelectProfile(profile) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("profile_${profile.id}")
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = profile.name,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isSelected) {
                                    Text(text = "ACTIVE", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = profile.description,
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Signal Burst Repetition (Crucial for physical amplifiers)
                Text(
                    text = "Signal Repeat Count: ${state.repeatCount}x",
                    color = PhilipsCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Repeats the IR burst so the receiver's AGC filter doesn't ignore it (default 2x).",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Slider(
                    value = state.repeatCount.toFloat(),
                    onValueChange = { onSetRepeatCount(it.toInt()) },
                    valueRange = 1f..4f,
                    steps = 2,
                    colors = SliderDefaults.colors(
                        thumbColor = PhilipsCyan,
                        activeTrackColor = PhilipsCyan,
                        inactiveTrackColor = Color(0xFF28344A)
                    ),
                    modifier = Modifier.testTag("slider_repeats")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Haptic Feedback Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF181E2B))
                        .border(1.dp, Color(0xFF28344A), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Vibration, contentDescription = null, tint = ElectricAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Tactile Vibration", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Switch(
                        checked = state.hapticEnabled,
                        onCheckedChange = { onToggleHaptic() },
                        colors = SwitchDefaults.colors(checkedThumbColor = PhilipsCyan, checkedTrackColor = Color(0xFF1E3A5F)),
                        modifier = Modifier.testTag("switch_haptic")
                    )
                }

                // Last Transmission Telemetry
                if (state.lastTransmission != null) {
                    val last = state.lastTransmission
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Last Signal Dispatched",
                        color = PhilipsCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF090D14))
                            .border(1.dp, Color(0xFF243046), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "${last.command.title} (${last.profileName})", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${last.carrierFreq} Hz (${last.pulseCount} pulses)", color = ElectricAccent, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            Text(text = "Code: ${last.hexWord}", color = AmberLed, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = last.prontoHex.take(28) + "...", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(last.prontoHex))
                                        Toast.makeText(context, "Hex copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = PhilipsCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_close_btn")) {
                Text(text = "Done", color = PhilipsCyan, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF131824),
        shape = RoundedCornerShape(20.dp)
    )
}
