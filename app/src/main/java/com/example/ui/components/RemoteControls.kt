package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RemoteCommand
import com.example.model.SpeakerSource
import com.example.ui.theme.ButtonDark
import com.example.ui.theme.ButtonPressed
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.PhilipsBlue
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceElevated

@Composable
fun RemotePhysicalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    label: String? = null,
    subLabel: String? = null,
    buttonColor: Color = ButtonDark,
    contentColor: Color = Color(0xFFF1F5F9),
    borderColor: Color = SurfaceBorder,
    minHeight: Dp = 54.dp,
    shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    testTag: String = "remote_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1.0f, label = "buttonPress")

    Box(
        modifier = modifier
            .scale(scale)
            .shadow(if (isPressed) 2.dp else 6.dp, shape)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isPressed) {
                        listOf(buttonColor.copy(alpha = 0.7f), ButtonPressed)
                    } else {
                        listOf(buttonColor.copy(alpha = 0.95f), buttonColor.copy(alpha = 0.75f))
                    }
                )
            )
            .border(1.2.dp, if (isPressed) PhilipsCyan.copy(alpha = 0.5f) else borderColor, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .defaultMinSize(minHeight = minHeight)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label ?: "button",
                    tint = contentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            if (label != null) {
                if (icon != null) Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = if (icon == null) 14.sp else 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = contentColor.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Modern circular D-Pad / Jog Wheel for navigation, Volume, Track, and Play/Pause.
 */
@Composable
fun RemoteDPadJogWheel(
    onPlayPause: () -> Unit,
    onVolUp: () -> Unit,
    onVolDown: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(220.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF232A3B),
                        Color(0xFF161B26),
                        Color(0xFF0F131C)
                    )
                )
            )
            .border(2.dp, Color(0xFF2C3549), CircleShape)
            .testTag("remote_dpad_jog_wheel"),
        contentAlignment = Alignment.Center
    ) {
        // Outer directional buttons
        // Top: VOL +
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .size(64.dp, 52.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onVolUp() }
                .testTag("dpad_vol_up"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Volume Up",
                    tint = PhilipsCyan,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "VOL +",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bottom: VOL -
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp)
                .size(64.dp, 52.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onVolDown() }
                .testTag("dpad_vol_down"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "VOL -",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Volume Down",
                    tint = PhilipsCyan,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Left: PREV
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 10.dp)
                .size(52.dp, 64.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onPrev() }
                .testTag("dpad_prev"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Previous Track",
                    tint = ElectricAccent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "PREV",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Right: NEXT
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp)
                .size(52.dp, 64.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onNext() }
                .testTag("dpad_next"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Next Track",
                    tint = ElectricAccent,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "NEXT",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Center Button: Play/Pause/Scan
        val centerInteraction = remember { MutableInteractionSource() }
        val centerPressed by centerInteraction.collectIsPressedAsState()
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (centerPressed) {
                            listOf(PhilipsCyan, PhilipsBlue)
                        } else {
                            listOf(Color(0xFF007AD9), Color(0xFF004D8C))
                        }
                    )
                )
                .border(2.dp, PhilipsCyan, CircleShape)
                .clickable(
                    interactionSource = centerInteraction,
                    indication = null,
                    onClick = onPlayPause
                )
                .testTag("dpad_play_pause"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "SCAN",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * Direct Source Selector Buttons Row (Bluetooth, FM, AUX, USB).
 */
@Composable
fun SourceSelectorRow(
    activeSource: SpeakerSource,
    onSelectSource: (SpeakerSource) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SourceCardButton(
            title = "BT",
            subtitle = "Bluetooth",
            icon = Icons.Default.Bluetooth,
            isSelected = activeSource == SpeakerSource.BLUETOOTH,
            onClick = { onSelectSource(SpeakerSource.BLUETOOTH) },
            modifier = Modifier.weight(1f),
            testTag = "source_btn_bluetooth"
        )

        SourceCardButton(
            title = "FM",
            subtitle = "Radio",
            icon = Icons.Default.Radio,
            isSelected = activeSource == SpeakerSource.FM,
            onClick = { onSelectSource(SpeakerSource.FM) },
            modifier = Modifier.weight(1f),
            testTag = "source_btn_fm"
        )

        SourceCardButton(
            title = "AUX",
            subtitle = "Line In",
            icon = Icons.Default.Cable,
            isSelected = activeSource == SpeakerSource.AUX,
            onClick = { onSelectSource(SpeakerSource.AUX) },
            modifier = Modifier.weight(1f),
            testTag = "source_btn_aux"
        )

        SourceCardButton(
            title = "USB",
            subtitle = "Media",
            icon = Icons.Default.Usb,
            isSelected = activeSource == SpeakerSource.USB,
            onClick = { onSelectSource(SpeakerSource.USB) },
            modifier = Modifier.weight(1f),
            testTag = "source_btn_usb"
        )
    }
}

@Composable
fun SourceCardButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Box(
        modifier = modifier
            .shadow(if (isSelected) 6.dp else 2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.verticalGradient(listOf(PhilipsBlue, Color(0xFF024479)))
                } else {
                    Brush.verticalGradient(listOf(Color(0xFF1E2534), Color(0xFF141924)))
                }
            )
            .border(
                1.5.dp,
                if (isSelected) PhilipsCyan else Color(0xFF2D374A),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) PhilipsCyan else Color(0xFF94A3B8),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = if (isSelected) Color(0xFFE2E8F0) else Color(0xFF64748B),
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
