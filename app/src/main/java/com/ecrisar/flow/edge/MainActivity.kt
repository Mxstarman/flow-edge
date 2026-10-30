package com.ecrisar.flow.edge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.data.AppRepository
import com.ecrisar.flow.edge.data.CalendarRepository
import com.ecrisar.flow.edge.data.LauncherHelper
import com.ecrisar.flow.edge.data.NotesRepository
import com.ecrisar.flow.edge.data.SettingsRepository
import com.ecrisar.flow.edge.service.EdgeOverlayService
import com.ecrisar.flow.edge.ui.edge.EdgeContainerView
import com.ecrisar.flow.edge.ui.settings.EdgeSettingsScreen
import com.ecrisar.flow.edge.ui.theme.EdgePanelTheme
import com.ecrisar.flow.edge.ui.theme.OneUIBlue
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var settingsRepo: SettingsRepository
    private lateinit var appRepo: AppRepository
    private lateinit var notesRepo: NotesRepository
    private lateinit var calendarRepo: CalendarRepository

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (LauncherHelper.canDrawOverlays(this)) {
            settingsRepo.updateSettings(settingsRepo.settings.value.copy(isOverlayServiceEnabled = true))
            EdgeOverlayService.start(this)
            Toast.makeText(this, "Overlay service enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Overlay permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settingsRepo = SettingsRepository(this)
        appRepo = AppRepository(this)
        notesRepo = NotesRepository(this)
        calendarRepo = CalendarRepository()

        setContent {
            EdgePanelTheme {
                val settings by settingsRepo.settings.collectAsState()
                val favoriteApps by appRepo.favoriteApps.collectAsState()
                val allApps by appRepo.installedApps.collectAsState()
                val notes by notesRepo.notes.collectAsState()
                val calendarEvents by calendarRepo.events.collectAsState()

                var showSettingsScreen by remember {
                    mutableStateOf(intent?.getBooleanExtra("open_settings", false) ?: false)
                }

                LaunchedEffect(Unit) {
                    appRepo.loadApps()
                }

                if (showSettingsScreen) {
                    EdgeSettingsScreen(
                        settings = settings,
                        onUpdateSettings = { updated ->
                            settingsRepo.updateSettings(updated)
                            if (updated.isOverlayServiceEnabled) {
                                if (LauncherHelper.canDrawOverlays(this@MainActivity)) {
                                    EdgeOverlayService.start(this@MainActivity)
                                } else {
                                    overlayPermissionLauncher.launch(LauncherHelper.getOverlayPermissionIntent(this@MainActivity))
                                }
                            } else {
                                EdgeOverlayService.stop(this@MainActivity)
                            }
                        },
                        onTogglePanel = { panel, enabled ->
                            settingsRepo.togglePanel(panel, enabled)
                        },
                        onBack = { showSettingsScreen = false }
                    )
                } else {
                    // Full interactive Edge Container (in-app simulator + live edge handle)
                    EdgeContainerView(
                        settings = settings,
                        favoriteApps = favoriteApps,
                        allApps = allApps,
                        notes = notes,
                        calendarEvents = calendarEvents,
                        onUpdateHandlePosition = { percent ->
                            settingsRepo.updateHandlePosition(percent)
                        },
                        onOpenSettings = { showSettingsScreen = true },
                        onToggleFavoriteApp = { appRepo.toggleFavorite(it) },
                        onAddNote = { t, c, col -> notesRepo.addNote(t, c, col) },
                        onDeleteNote = { notesRepo.deleteNote(it) },
                        onAddCalendarEvent = { t, tm, d -> calendarRepo.addEvent(t, tm, d) },
                        onDeleteCalendarEvent = { calendarRepo.deleteEvent(it) }
                    ) {
                        HomeScreenSimulator(
                            isOverlayActive = settings.isOverlayServiceEnabled && LauncherHelper.canDrawOverlays(this@MainActivity),
                            onToggleOverlay = {
                                if (LauncherHelper.canDrawOverlays(this@MainActivity)) {
                                    val newState = !settings.isOverlayServiceEnabled
                                    settingsRepo.updateSettings(settings.copy(isOverlayServiceEnabled = newState))
                                    if (newState) {
                                        EdgeOverlayService.start(this@MainActivity)
                                    } else {
                                        EdgeOverlayService.stop(this@MainActivity)
                                    }
                                } else {
                                    overlayPermissionLauncher.launch(LauncherHelper.getOverlayPermissionIntent(this@MainActivity))
                                }
                            },
                            onOpenSettings = { showSettingsScreen = true }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modern One UI inspired Home Simulator:
 * Provides guidance, overlay service activation, and interactive workspace.
 */
@Composable
fun HomeScreenSimulator(
    isOverlayActive: Boolean,
    onToggleOverlay: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1015),
                        Color(0xFF141720),
                        Color(0xFF1B1F2C)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // One UI Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Flow Edge",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "One UI Multitasking & Quick Tools",
                        color = OneUIBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22242E))
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Quick Tutorial Banner
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xCC1A1C24)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "How to use your Edge Panel:",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    InstructionRow(
                        step = "1",
                        text = "Swipe the blue handle on the right edge to slide out the Edge Panel."
                    )
                    InstructionRow(
                        step = "2",
                        text = "Drag the handle vertically up or down to adjust its position."
                    )
                    InstructionRow(
                        step = "3",
                        text = "Swipe horizontally in the panel to switch to Notes, Calendar, Calculator, and Tools."
                    )
                    InstructionRow(
                        step = "4",
                        text = "Long-press any app icon and drag it onto the screen to open Pop-up view!"
                    )
                }
            }

            // System Overlay Service Toggle Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E212B)),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isOverlayActive) OneUIBlue.copy(alpha = 0.5f) else Color(0x22FFFFFF)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isOverlayActive) OneUIBlue else Color(0xFF2A2E3D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "System-Wide Overlay",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isOverlayActive) "Active across all Android apps" else "Run outside the app",
                                color = if (isOverlayActive) OneUIBlue else Color(0xFF888B94),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = onToggleOverlay,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOverlayActive) Color(0xFFE53935) else OneUIBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isOverlayActive) "Stop" else "Enable",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Quick Access to Settings Button
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF191B22),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenSettings() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = OneUIBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Customize Panels, Colors & Handle",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF888B94),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InstructionRow(step: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(OneUIBlue.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = step,
                color = OneUIBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = text,
            color = Color(0xFFD1D5DB),
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
