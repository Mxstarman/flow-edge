package com.ecrisar.flow.edge.data

import android.content.Context
import android.content.SharedPreferences
import com.ecrisar.flow.edge.model.EdgePanelSettings
import com.ecrisar.flow.edge.model.PanelType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("edge_panel_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<EdgePanelSettings> = _settings.asStateFlow()

    private fun loadSettings(): EdgePanelSettings {
        val handlePos = prefs.getFloat(KEY_HANDLE_POS, 0.42f)
        val isRight = prefs.getBoolean(KEY_IS_RIGHT, true)
        val height = prefs.getInt(KEY_HANDLE_HEIGHT, 85)
        val width = prefs.getInt(KEY_HANDLE_WIDTH, 16)
        val colorHex = prefs.getLong(KEY_HANDLE_COLOR, 0xFF0381FEL)
        val alpha = prefs.getFloat(KEY_HANDLE_ALPHA, 0.85f)
        val vibration = prefs.getBoolean(KEY_VIBRATION, true)
        val showAll = prefs.getBoolean(KEY_SHOW_ALL_APPS, true)
        val dragToPopup = prefs.getBoolean(KEY_DRAG_TO_POPUP, true)
        val overlayService = prefs.getBoolean(KEY_OVERLAY_SERVICE, false)
        val savedPanels = prefs.getString(KEY_ENABLED_PANELS, null)

        val panels = if (!savedPanels.isNullOrEmpty()) {
            savedPanels.split(",")
                .mapNotNull { name ->
                    try { PanelType.valueOf(name) } catch (e: Exception) { null }
                }
                .ifEmpty { EdgePanelSettings().enabledPanels }
        } else {
            EdgePanelSettings().enabledPanels
        }

        return EdgePanelSettings(
            handlePositionPercent = handlePos,
            isRightSide = isRight,
            handleHeightDp = height,
            handleWidthDp = width,
            handleColorHex = colorHex,
            handleAlpha = alpha,
            isVibrationEnabled = vibration,
            enabledPanels = panels,
            showAllAppsToggle = showAll,
            dragToPopupEnabled = dragToPopup,
            isOverlayServiceEnabled = overlayService
        )
    }

    fun updateSettings(newSettings: EdgePanelSettings) {
        _settings.value = newSettings
        prefs.edit()
            .putFloat(KEY_HANDLE_POS, newSettings.handlePositionPercent)
            .putBoolean(KEY_IS_RIGHT, newSettings.isRightSide)
            .putInt(KEY_HANDLE_HEIGHT, newSettings.handleHeightDp)
            .putInt(KEY_HANDLE_WIDTH, newSettings.handleWidthDp)
            .putLong(KEY_HANDLE_COLOR, newSettings.handleColorHex)
            .putFloat(KEY_HANDLE_ALPHA, newSettings.handleAlpha)
            .putBoolean(KEY_VIBRATION, newSettings.isVibrationEnabled)
            .putBoolean(KEY_SHOW_ALL_APPS, newSettings.showAllAppsToggle)
            .putBoolean(KEY_DRAG_TO_POPUP, newSettings.dragToPopupEnabled)
            .putBoolean(KEY_OVERLAY_SERVICE, newSettings.isOverlayServiceEnabled)
            .putString(KEY_ENABLED_PANELS, newSettings.enabledPanels.joinToString(",") { it.name })
            .apply()
    }

    fun updateHandlePosition(percent: Float) {
        val clamped = percent.coerceIn(0.08f, 0.92f)
        val updated = _settings.value.copy(handlePositionPercent = clamped)
        updateSettings(updated)
    }

    fun togglePanel(panel: PanelType, enabled: Boolean) {
        val current = _settings.value.enabledPanels.toMutableList()
        if (enabled && !current.contains(panel)) {
            current.add(panel)
        } else if (!enabled && current.contains(panel)) {
            // Keep at least one panel enabled
            if (current.size > 1) {
                current.remove(panel)
            }
        }
        updateSettings(_settings.value.copy(enabledPanels = current))
    }

    fun reorderPanels(panels: List<PanelType>) {
        if (panels.isNotEmpty()) {
            updateSettings(_settings.value.copy(enabledPanels = panels))
        }
    }

    companion object {
        private const val KEY_HANDLE_POS = "handle_pos"
        private const val KEY_IS_RIGHT = "is_right"
        private const val KEY_HANDLE_HEIGHT = "handle_height"
        private const val KEY_HANDLE_WIDTH = "handle_width"
        private const val KEY_HANDLE_COLOR = "handle_color"
        private const val KEY_HANDLE_ALPHA = "handle_alpha"
        private const val KEY_VIBRATION = "vibration"
        private const val KEY_SHOW_ALL_APPS = "show_all_apps"
        private const val KEY_DRAG_TO_POPUP = "drag_to_popup"
        private const val KEY_OVERLAY_SERVICE = "overlay_service"
        private const val KEY_ENABLED_PANELS = "enabled_panels"
    }
}
