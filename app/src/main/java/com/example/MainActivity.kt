package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.RemoteCommand
import com.example.ui.ControlMode
import com.example.ui.RemoteLayoutType
import com.example.ui.RemoteViewModel
import com.example.ui.screens.ClassicRemoteLayout
import com.example.ui.screens.ConsoleRemoteLayout
import com.example.ui.screens.QuickRemoteLayout
import com.example.ui.screens.SignalInspectorDialog
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElectricAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PhilipsBlue
import com.example.ui.theme.PhilipsCyan
import com.example.ui.theme.PowerRed
import com.example.ui.theme.PowerRedGlow
import com.example.ui.theme.SuccessGreen

class MainActivity : ComponentActivity() {

    private val viewModel: RemoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshBluetoothStatus()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: RemoteViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showInspectorDialog by remember { mutableStateOf(false) }

    val irLedColor by animateColorAsState(
        targetValue = if (uiState.isTransmittingAnim) PowerRedGlow else PhilipsCyan,
        animationSpec = tween(120),
        label = "irLed"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Glowing Transmitter LED
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(irLedColor)
                                .shadow(if (uiState.isTransmittingAnim) 8.dp else 2.dp, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "PHILIPS",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(PhilipsBlue.copy(alpha = 0.3f))
                                        .border(0.8.dp, PhilipsCyan, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "MMS2625B",
                                        color = PhilipsCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "NEC 38kHz • ${uiState.controlMode.label}",
                                color = Color(0xFF94A3B8),
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    // Protocol inspector & Settings button
                    IconButton(
                        onClick = { showInspectorDialog = true },
                        modifier = Modifier.testTag("btn_open_inspector")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "IR Protocol Settings",
                            tint = PhilipsCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF11141C)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Layout Selector Segment (Classic Remote / Sound Console / Big Buttons)
            LayoutSelectorTabs(
                selectedLayout = uiState.activeLayout,
                onSelectLayout = { viewModel.setLayout(it) },
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            )

            // Content container (centered for tablets / foldables)
            Box(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Crossfade(targetState = uiState.activeLayout, label = "layoutTransition") { layout ->
                    when (layout) {
                        RemoteLayoutType.CLASSIC -> {
                            ClassicRemoteLayout(
                                state = uiState,
                                onSendCommand = { viewModel.sendCommand(it) },
                                onSelectSource = { viewModel.setDirectSource(it) }
                            )
                        }
                        RemoteLayoutType.CONSOLE -> {
                            ConsoleRemoteLayout(
                                state = uiState,
                                onSendCommand = { viewModel.sendCommand(it) },
                                onSelectSource = { viewModel.setDirectSource(it) },
                                onSetVolume = { viewModel.setVolumeLevel(it) },
                                onSelectEq = { viewModel.setDirectEq(it) },
                                onPartyBass = { viewModel.applyPartyBassPreset() },
                                onVocalCinema = { viewModel.applyVocalCinemaPreset() }
                            )
                        }
                        RemoteLayoutType.QUICK -> {
                            QuickRemoteLayout(
                                state = uiState,
                                onSendCommand = { viewModel.sendCommand(it) },
                                onSelectSource = { viewModel.setDirectSource(it) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showInspectorDialog) {
            SignalInspectorDialog(
                state = uiState,
                onDismiss = { showInspectorDialog = false },
                onSetDeviceAddress = { viewModel.setDeviceAddress(it) },
                onSetControlMode = { viewModel.setControlMode(it) },
                onToggleHaptic = { viewModel.toggleHaptic() },
                onTestCommand = { viewModel.sendCommand(it) }
            )
        }
    }
}

@Composable
fun LayoutSelectorTabs(
    selectedLayout: RemoteLayoutType,
    onSelectLayout: (RemoteLayoutType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141923))
            .border(1.dp, Color(0xFF283347), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (layout in RemoteLayoutType.entries) {
            val isSelected = selectedLayout == layout
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(9.dp))
                    .background(if (isSelected) PhilipsBlue else Color.Transparent)
                    .clickable { onSelectLayout(layout) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = layout.label,
                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}
