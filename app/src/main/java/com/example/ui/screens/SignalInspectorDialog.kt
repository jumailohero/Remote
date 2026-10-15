package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.ControlMode
import com.example.ui.RemoteUiState
import com.example.ui.components.RemotePhysicalButton
import com.example.ui.theme.AmberLed
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SuccessGreen

@Composable
fun SignalInspectorDialog(
    state: RemoteUiState,
    onDismiss: () -> Unit,
    onSetDeviceAddress: (Int) -> Unit,
    onSetControlMode: (ControlMode) -> Unit,
    onToggleHaptic: () -> Unit,
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
                    text = "IR Protocol & Settings",
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
                                text = if (state.hasHardwareIr) "Hardware IR Blaster Active" else "IR Simulation Mode Active",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (state.hasHardwareIr)
                                    "Ready to transmit 38 kHz NEC pulses to Philips MMS2625B"
                                else
                                    "This phone has no built-in IR blaster. Simulated IR visualizer & Bluetooth speaker volume are active.",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Control Mode Switcher
                Text(
                    text = "Control Integration Mode",
                    color = PhilipsCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                for (mode in ControlMode.entries) {
                    val isSelected = state.controlMode == mode
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF1E3A5F) else Color(0xFF181E2B))
                            .border(1.dp, if (isSelected) PhilipsCyan else Color(0xFF28344A), RoundedCornerShape(10.dp))
                            .clickable { onSetControlMode(mode) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("mode_${mode.name}")
                    ) {
                        Column {
                            Text(
                                text = mode.label,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = mode.description,
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Device Address Tuning (for motherboard hardware revisions)
                Text(
                    text = "NEC Device Address (Philips MMS2625B)",
                    color = PhilipsCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Default is 0x00. If your unit's ODM motherboard varies, switch to alternate address:",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val addresses = listOf(
                        0x00 to "0x00 (Default)",
                        0x01 to "0x01 (Alt 1)",
                        0x7F to "0x7F (Alt 2)"
                    )
                    for ((addr, label) in addresses) {
                        val isCurrent = state.deviceAddress == addr
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) PhilipsCyan else Color(0xFF181E2B))
                                .border(1.dp, if (isCurrent) PhilipsCyan else Color(0xFF28344A), RoundedCornerShape(8.dp))
                                .clickable { onSetDeviceAddress(addr) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isCurrent) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

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
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = null,
                            tint = ElectricAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Tactile Haptic Click",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Vibration feel on remote buttons",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = state.hapticEnabled,
                        onCheckedChange = { onToggleHaptic() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PhilipsCyan,
                            checkedTrackColor = Color(0xFF1E3A5F)
                        ),
                        modifier = Modifier.testTag("switch_haptic")
                    )
                }

                // Last Transmission Telemetry
                if (state.lastTransmission != null) {
                    val last = state.lastTransmission
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Last Transmitted Signal Telemetry",
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Command: ${last.command.title}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "38,000 Hz",
                                    color = ElectricAccent,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = "Opcode: 0x%02X | 32-bit Word: ${last.hexWord}".format(last.opcode),
                                color = AmberLed,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pronto Hex: ${last.prontoHex.take(28)}...",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(last.prontoHex))
                                        Toast.makeText(context, "Pronto Hex copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Pronto Hex",
                                        tint = PhilipsCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("dialog_close_btn")) {
                Text(text = "Close", color = PhilipsCyan, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF131824),
        shape = RoundedCornerShape(20.dp)
    )
}
