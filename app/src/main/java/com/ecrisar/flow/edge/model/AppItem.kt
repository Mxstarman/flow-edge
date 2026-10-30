package com.ecrisar.flow.edge.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color

/**
 * Model representing an application accessible in the Apps Edge Panel.
 */
data class AppItem(
    val packageName: String,
    val activityName: String = "",
    val label: String,
    val iconDrawable: Drawable? = null,
    val accentColor: Color = Color(0xFF0381FE),
    val isFavorite: Boolean = false,
    val category: String = "App"
)
