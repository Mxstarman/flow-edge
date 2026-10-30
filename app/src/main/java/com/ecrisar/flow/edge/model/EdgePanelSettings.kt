package com.ecrisar.flow.edge.model

/**
 * Customization settings for the Edge Panel and its Edge Handle.
 * Allows users to adjust handle position, look, and enabled panel features.
 */
data class EdgePanelSettings(
    // Handle placement & dimensions
    val handlePositionPercent: Float = 0.42f, // 0.0f (top) to 1.0f (bottom)
    val isRightSide: Boolean = true,
    val handleHeightDp: Int = 85,
    val handleWidthDp: Int = 16,
    val handleColorHex: Long = 0xFF0381FE, // Samsung One UI signature electric blue
    val handleAlpha: Float = 0.85f,
    val isVibrationEnabled: Boolean = true,

    // Behavior & Panels
    val enabledPanels: List<PanelType> = listOf(
        PanelType.APPS,
        PanelType.NOTES,
        PanelType.CALENDAR,
        PanelType.CALCULATOR,
        PanelType.TOOLS
    ),
    val showAllAppsToggle: Boolean = true,
    val dragToPopupEnabled: Boolean = true,
    val isOverlayServiceEnabled: Boolean = false,
    val autoCollapseOnLaunch: Boolean = true
) {
    companion object {
        val PRESET_COLORS = listOf(
            0xFF0381FE, // One UI Blue
            0xFF7C4DFF, // Deep Violet
            0xFFFF4081, // Rose Pink
            0xFF00E676, // Neon Emerald
            0xFFFFAB00, // Amber Sunset
            0xFF00B0FF, // Light Sky
            0xFFFFFFFF  // Minimal Frost White
        )
    }
}
