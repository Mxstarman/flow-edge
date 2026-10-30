package com.ecrisar.flow.edge.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enumeration of available Samsung Edge panels.
 * Users can swipe horizontally to switch between panels.
 */
enum class PanelType(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    APPS(
        title = "Apps Edge",
        description = "Quick access to favorite apps, all apps, and pop-up view",
        icon = Icons.Default.Apps
    ),
    NOTES(
        title = "Quick Notes",
        description = "Instantly jot down thoughts, memos, and checklists",
        icon = Icons.Default.EditNote
    ),
    CALENDAR(
        title = "Calendar",
        description = "Interactive monthly calendar and daily event agenda",
        icon = Icons.Default.CalendarMonth
    ),
    CALCULATOR(
        title = "Calculator",
        description = "Full featured floating One UI calculator",
        icon = Icons.Default.Calculate
    ),
    TOOLS(
        title = "Quick Tools",
        description = "Flashlight, ruler, volume slider and utilities",
        icon = Icons.Default.Build
    )
}
