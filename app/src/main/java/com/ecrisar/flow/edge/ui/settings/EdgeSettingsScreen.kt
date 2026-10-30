package com.ecrisar.flow.edge.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.data.LauncherHelper
import com.ecrisar.flow.edge.model.EdgePanelSettings
import com.ecrisar.flow.edge.model.PanelType
import com.ecrisar.flow.edge.ui.theme.OneUIBlue

/**
 * Highly customizable settings screen for Edge Panel.
 * Configures handle position, colors, sizes, active panels, and system overlay.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdgeSettingsScreen(
    settings: EdgePanelSettings,
    onUpdateSettings: (EdgePanelSettings) -> Unit,
    onTogglePanel: (PanelType, Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hasOverlayPermission = remember(settings) { LauncherHelper.canDrawOverlays(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edge Panel Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF16171B),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F1012),
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Handle Customization
            item {
                SettingsSectionCard(
                    title = "Handle Position & Look",
                    icon = Icons.Default.PanTool
                ) {
                    // Position Slider
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Vertical Position", color = Color.White, fontSize = 13.sp)
                            Text(
                                "${(settings.handlePositionPercent * 100).toInt()}%",
                                color = OneUIBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = settings.handlePositionPercent,
                            onValueChange = { onUpdateSettings(settings.copy(handlePositionPercent = it)) },
                            valueRange = 0.08f..0.92f,
                            colors = SliderDefaults.colors(thumbColor = OneUIBlue, activeTrackColor = OneUIBlue)
                        )
                    }

                    // Height Slider
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Handle Length", color = Color.White, fontSize = 13.sp)
                            Text("${settings.handleHeightDp} dp", color = OneUIBlue, fontSize = 13.sp)
                        }
                        Slider(
                            value = settings.handleHeightDp.toFloat(),
                            onValueChange = { onUpdateSettings(settings.copy(handleHeightDp = it.toInt())) },
                            valueRange = 50f..160f,
                            colors = SliderDefaults.colors(thumbColor = OneUIBlue, activeTrackColor = OneUIBlue)
                        )
                    }

                    // Width / Thickness Slider
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Handle Thickness", color = Color.White, fontSize = 13.sp)
                            Text("${settings.handleWidthDp} dp", color = OneUIBlue, fontSize = 13.sp)
                        }
                        Slider(
                            value = settings.handleWidthDp.toFloat(),
                            onValueChange = { onUpdateSettings(settings.copy(handleWidthDp = it.toInt())) },
                            valueRange = 8f..32f,
                            colors = SliderDefaults.colors(thumbColor = OneUIBlue, activeTrackColor = OneUIBlue)
                        )
                    }

                    // Transparency Slider
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Transparency", color = Color.White, fontSize = 13.sp)
                            Text("${((1f - settings.handleAlpha) * 100).toInt()}%", color = OneUIBlue, fontSize = 13.sp)
                        }
                        Slider(
                            value = settings.handleAlpha,
                            onValueChange = { onUpdateSettings(settings.copy(handleAlpha = it)) },
                            valueRange = 0.2f..1.0f,
                            colors = SliderDefaults.colors(thumbColor = OneUIBlue, activeTrackColor = OneUIBlue)
                        )
                    }

                    // Color Picker
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text("Handle Color", color = Color.White, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EdgePanelSettings.PRESET_COLORS.forEach { colorHex ->
                                val isSelected = settings.handleColorHex == colorHex
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(colorHex))
                                        .border(
                                            width = if (isSelected) 2.5.dp else 0.dp,
                                            color = if (isSelected) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            onUpdateSettings(settings.copy(handleColorHex = colorHex))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = if (colorHex == 0xFFFFFFFFL) Color.Black else Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Vibration Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = Color(0xFFA0A5B2), modifier = Modifier.size(20.dp))
                            Text("Haptic Feedback on Touch", color = Color.White, fontSize = 13.sp)
                        }
                        Switch(
                            checked = settings.isVibrationEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(isVibrationEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = OneUIBlue)
                        )
                    }
                }
            }

            // Section 2: Enabled Panels
            item {
                SettingsSectionCard(
                    title = "Enabled Edge Panels",
                    icon = Icons.Default.Layers
                ) {
                    PanelType.values().forEach { panelType ->
                        val isEnabled = settings.enabledPanels.contains(panelType)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = panelType.icon,
                                    contentDescription = null,
                                    tint = if (isEnabled) OneUIBlue else Color(0xFF6B7280),
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = panelType.title,
                                        color = if (isEnabled) Color.White else Color(0xFF888B94),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = panelType.description,
                                        color = Color(0xFF6B7280),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Checkbox(
                                checked = isEnabled,
                                onCheckedChange = { checked ->
                                    onTogglePanel(panelType, checked)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = OneUIBlue,
                                    uncheckedColor = Color(0xFF6B7280)
                                )
                            )
                        }
                    }
                }
            }

            // Section 3: System Overlay Service
            item {
                SettingsSectionCard(
                    title = "System Floating Overlay",
                    icon = Icons.Default.Security
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Display the Edge Panel handle over all apps across the Android system.",
                            color = Color(0xFF9E9E9E),
                            fontSize = 12.sp
                        )

                        if (!hasOverlayPermission) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF332A15),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "Permission Required: Display over other apps",
                                        color = Color(0xFFFFB300),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "To show the floating edge handle globally, grant overlay permission.",
                                        color = Color.White,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            context.startActivity(LauncherHelper.getOverlayPermissionIntent(context))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = OneUIBlue),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Grant Permission", fontSize = 11.sp)
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Enable Overlay Service",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Switch(
                                    checked = settings.isOverlayServiceEnabled,
                                    onCheckedChange = { enabled ->
                                        onUpdateSettings(settings.copy(isOverlayServiceEnabled = enabled))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = OneUIBlue
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF1A1B20),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OneUIBlue,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}
