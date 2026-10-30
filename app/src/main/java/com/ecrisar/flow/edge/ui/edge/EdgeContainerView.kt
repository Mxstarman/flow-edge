package com.ecrisar.flow.edge.ui.edge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ecrisar.flow.edge.data.LauncherHelper
import com.ecrisar.flow.edge.model.AppItem
import com.ecrisar.flow.edge.model.CalendarEvent
import com.ecrisar.flow.edge.model.EdgePanelSettings
import com.ecrisar.flow.edge.model.NoteItem
import com.ecrisar.flow.edge.model.PopupWindowState
import com.ecrisar.flow.edge.ui.components.DragDropContainer
import com.ecrisar.flow.edge.ui.components.DragDropState
import com.ecrisar.flow.edge.ui.components.DropZoneIndicator
import com.ecrisar.flow.edge.ui.components.EdgeHandleView
import com.ecrisar.flow.edge.ui.components.FloatingPopupView
import java.time.LocalDate
import java.util.UUID

/**
 * Root Edge Container:
 * Manages handle position, slide drawer, drag-drop gestures, and floating pop-up windows.
 */
@Composable
fun EdgeContainerView(
    settings: EdgePanelSettings,
    favoriteApps: List<AppItem>,
    allApps: List<AppItem>,
    notes: List<NoteItem>,
    calendarEvents: List<CalendarEvent>,
    onUpdateHandlePosition: (Float) -> Unit,
    onOpenSettings: () -> Unit,
    onToggleFavoriteApp: (AppItem) -> Unit,
    onAddNote: (String, String, Long) -> Unit,
    onDeleteNote: (String) -> Unit,
    onAddCalendarEvent: (String, String, LocalDate) -> Unit,
    onDeleteCalendarEvent: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var isPanelOpen by remember { mutableStateOf(false) }
    val dragDropState = remember { DragDropState() }
    val popupWindows = remember { mutableStateListOf<PopupWindowState>() }
    var highestZIndex by remember { mutableFloatStateOf(10f) }

    fun addPopupWindow(app: AppItem, dropOffset: Offset) {
        val dropXDp = with(density) { (dropOffset.x - 140).coerceAtLeast(20f).toDp() }
        val dropYDp = with(density) { (dropOffset.y - 100).coerceAtLeast(60f).toDp() }

        highestZIndex += 1f
        popupWindows.add(
            PopupWindowState(
                id = UUID.randomUUID().toString(),
                app = app,
                xOffsetDp = dropXDp,
                yOffsetDp = dropYDp,
                zIndex = highestZIndex
            )
        )

        // Also trigger native Android OS Freeform / Multi-Window pop-up launch
        LauncherHelper.launchAppInPopupView(context, app)

        if (settings.autoCollapseOnLaunch) {
            isPanelOpen = false
        }
    }

    DragDropContainer(
        state = dragDropState,
        modifier = modifier.fillMaxSize()
    ) {
        // Main App Background Content
        Box(modifier = Modifier.fillMaxSize()) {
            content()

            // Active Floating Pop-up Windows
            popupWindows.forEach { popupState ->
                FloatingPopupView(
                    state = popupState,
                    onUpdate = { updatedState ->
                        val index = popupWindows.indexOfFirst { it.id == updatedState.id }
                        if (index != -1) {
                            popupWindows[index] = updatedState
                        }
                    },
                    onClose = {
                        popupWindows.removeAll { it.id == popupState.id }
                    },
                    onBringToFront = {
                        highestZIndex += 1f
                        val index = popupWindows.indexOfFirst { it.id == popupState.id }
                        if (index != -1) {
                            popupWindows[index] = popupState.copy(zIndex = highestZIndex)
                        }
                    }
                )
            }

            // Drop Target Guidance Indicator when dragging an app
            DropZoneIndicator(
                isDragging = dragDropState.isDragging,
                modifier = Modifier.fillMaxSize()
            )

            // Dim backdrop when Edge Panel is expanded
            AnimatedVisibility(
                visible = isPanelOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable { isPanelOpen = false }
                )
            }
        }

        // The Edge Handle (sits along the right edge)
        if (!isPanelOpen) {
            EdgeHandleView(
                settings = settings,
                isPanelOpen = isPanelOpen,
                onOpenPanel = { isPanelOpen = true },
                onPositionChanged = onUpdateHandlePosition,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        // The Slide-Out Drawer
        EdgePanelDrawer(
            isOpen = isPanelOpen,
            settings = settings,
            favoriteApps = favoriteApps,
            allApps = allApps,
            notes = notes,
            calendarEvents = calendarEvents,
            onClose = { isPanelOpen = false },
            onOpenSettings = {
                isPanelOpen = false
                onOpenSettings()
            },
            onToggleFavoriteApp = onToggleFavoriteApp,
            onAppClicked = { app ->
                LauncherHelper.launchApp(context, app)
                if (settings.autoCollapseOnLaunch) {
                    isPanelOpen = false
                }
            },
            onAppDropped = { app, offset ->
                addPopupWindow(app, offset)
            },
            onAddNote = onAddNote,
            onDeleteNote = onDeleteNote,
            onAddCalendarEvent = onAddCalendarEvent,
            onDeleteCalendarEvent = onDeleteCalendarEvent,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}
