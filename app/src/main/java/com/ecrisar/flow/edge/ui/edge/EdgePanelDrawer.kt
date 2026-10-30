package com.ecrisar.flow.edge.ui.edge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ecrisar.flow.edge.model.AppItem
import com.ecrisar.flow.edge.model.CalendarEvent
import com.ecrisar.flow.edge.model.EdgePanelSettings
import com.ecrisar.flow.edge.model.NoteItem
import com.ecrisar.flow.edge.model.PanelType
import com.ecrisar.flow.edge.ui.components.GlassmorphicCard
import com.ecrisar.flow.edge.ui.panels.AppsPanel
import com.ecrisar.flow.edge.ui.panels.CalculatorPanel
import com.ecrisar.flow.edge.ui.panels.CalendarPanel
import com.ecrisar.flow.edge.ui.panels.NotesPanel
import com.ecrisar.flow.edge.ui.panels.ToolsPanel
import com.ecrisar.flow.edge.ui.theme.OneUIBlue
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Slide-out Edge Panel Drawer:
 * - Slides in from the right edge with spring physics.
 * - Horizontal swipe pager to switch between Apps, Notes, Calendar, Calculator, and Tools.
 * - Indicator dots for currently active panel.
 * - Bottom quick settings action.
 */
@Composable
fun EdgePanelDrawer(
    isOpen: Boolean,
    settings: EdgePanelSettings,
    favoriteApps: List<AppItem>,
    allApps: List<AppItem>,
    notes: List<NoteItem>,
    calendarEvents: List<CalendarEvent>,
    onClose: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleFavoriteApp: (AppItem) -> Unit,
    onAppClicked: (AppItem) -> Unit,
    onAppDropped: (AppItem, androidx.compose.ui.geometry.Offset) -> Unit = { _, _ -> },
    onAddNote: (String, String, Long) -> Unit,
    onDeleteNote: (String) -> Unit,
    onAddCalendarEvent: (String, String, LocalDate) -> Unit,
    onDeleteCalendarEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val enabledPanels = settings.enabledPanels.ifEmpty { listOf(PanelType.APPS) }
    val pagerState = rememberPagerState(pageCount = { enabledPanels.size })
    val coroutineScope = rememberCoroutineScope()

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dismiss handle / touch zone on the left edge of drawer
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(64.dp)
                    .shadow(4.dp, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                    .background(Color(0xDD22242A))
                    .clickable { onClose() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Close Edge Panel",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Main Acrylic Edge Drawer Body
            GlassmorphicCard(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                shape = RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Bar: Panel Tabs & Swiper Dots
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current panel title
                        val currentPanel = enabledPanels.getOrNull(pagerState.currentPage) ?: PanelType.APPS
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = currentPanel.icon,
                                contentDescription = null,
                                tint = OneUIBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = currentPanel.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Indicator dots for all enabled panels
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            enabledPanels.forEachIndexed { index, _ ->
                                val isSelected = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 8.dp else 5.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) OneUIBlue else Color(0xFF6B7280))
                                        .clickable {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                )
                            }
                        }
                    }

                    // Horizontal Pager for Panels (Swipe left/right)
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { pageIndex ->
                        val panel = enabledPanels[pageIndex]
                        when (panel) {
                            PanelType.APPS -> {
                                AppsPanel(
                                    favoriteApps = favoriteApps,
                                    allApps = allApps,
                                    onToggleFavorite = onToggleFavoriteApp,
                                    onAppClicked = onAppClicked,
                                    onAppDropped = onAppDropped
                                )
                            }
                            PanelType.NOTES -> {
                                NotesPanel(
                                    notes = notes,
                                    onAddNote = onAddNote,
                                    onDeleteNote = onDeleteNote
                                )
                            }
                            PanelType.CALENDAR -> {
                                CalendarPanel(
                                    events = calendarEvents,
                                    onAddEvent = onAddCalendarEvent,
                                    onDeleteEvent = onDeleteCalendarEvent
                                )
                            }
                            PanelType.CALCULATOR -> {
                                CalculatorPanel()
                            }
                            PanelType.TOOLS -> {
                                ToolsPanel()
                            }
                        }
                    }

                    // Bottom Toolbar with Settings Gear
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        color = Color(0x6618191E)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Swipe sideways to switch panels",
                                color = Color(0xFF757A88),
                                fontSize = 10.sp
                            )

                            IconButton(
                                onClick = onOpenSettings,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Customize Edge Panel",
                                    tint = Color(0xFFA0A5B2),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
