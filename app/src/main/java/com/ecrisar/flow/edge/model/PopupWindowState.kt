package com.ecrisar.flow.edge.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * State representing an active floating pop-up window opened via drag-and-drop.
 * Matches Samsung One UI pop-up view capabilities: movable, resizable, minimizable, closeable.
 */
data class PopupWindowState(
    val id: String,
    val app: AppItem,
    val xOffsetDp: Dp = 40.dp,
    val yOffsetDp: Dp = 100.dp,
    val widthDp: Dp = 280.dp,
    val heightDp: Dp = 380.dp,
    val isMinimized: Boolean = false,
    val isMaximized: Boolean = false,
    val zIndex: Float = 1.0f
)
