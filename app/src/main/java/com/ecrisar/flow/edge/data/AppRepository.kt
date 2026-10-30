package com.ecrisar.flow.edge.data

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import androidx.compose.ui.graphics.Color
import com.ecrisar.flow.edge.model.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("edge_panel_apps", Context.MODE_PRIVATE)

    private val _installedApps = MutableStateFlow<List<AppItem>>(emptyList())
    val installedApps: StateFlow<List<AppItem>> = _installedApps.asStateFlow()

    private val _favoriteApps = MutableStateFlow<List<AppItem>>(emptyList())
    val favoriteApps: StateFlow<List<AppItem>> = _favoriteApps.asStateFlow()

    suspend fun loadApps() = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos: List<ResolveInfo> = try {
            pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        } catch (e: Exception) {
            emptyList()
        }

        val favoritePackages = getFavoritePackageSet()

        val apps = if (resolveInfos.isNotEmpty()) {
            resolveInfos.map { resolveInfo ->
                val pkg = resolveInfo.activityInfo.packageName
                val activity = resolveInfo.activityInfo.name
                val label = resolveInfo.loadLabel(pm).toString()
                val icon = try {
                    resolveInfo.loadIcon(pm)
                } catch (e: Exception) {
                    null
                }
                val isFav = favoritePackages.contains(pkg)
                AppItem(
                    packageName = pkg,
                    activityName = activity,
                    label = label,
                    iconDrawable = icon,
                    accentColor = getHashColor(pkg),
                    isFavorite = isFav
                )
            }.sortedBy { it.label.lowercase() }
        } else {
            // Provide comprehensive fallback standard apps for testing & demo
            getSampleApps(favoritePackages)
        }

        _installedApps.value = apps
        _favoriteApps.value = apps.filter { it.isFavorite }.ifEmpty {
            apps.take(8).map { it.copy(isFavorite = true) }
        }
    }

    fun toggleFavorite(app: AppItem) {
        val currentFavs = getFavoritePackageSet().toMutableSet()
        if (currentFavs.contains(app.packageName)) {
            currentFavs.remove(app.packageName)
        } else {
            currentFavs.add(app.packageName)
        }
        saveFavoritePackageSet(currentFavs)

        val updatedInstalled = _installedApps.value.map {
            if (it.packageName == app.packageName) it.copy(isFavorite = !it.isFavorite) else it
        }
        _installedApps.value = updatedInstalled
        _favoriteApps.value = updatedInstalled.filter { it.isFavorite }
    }

    private fun getFavoritePackageSet(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, null) ?: getDefaultFavoritePackages()
    }

    private fun saveFavoritePackageSet(set: Set<String>) {
        prefs.edit().putStringSet(KEY_FAVORITES, set).apply()
    }

    private fun getDefaultFavoritePackages(): Set<String> {
        return setOf(
            "com.android.chrome",
            "com.google.android.apps.messaging",
            "com.samsung.android.dialer",
            "com.sec.android.app.camera",
            "com.sec.android.app.popupcalculator",
            "com.samsung.android.app.notes",
            "com.sec.android.gallery3d",
            "com.android.settings"
        )
    }

    private fun getSampleApps(favorites: Set<String>): List<AppItem> {
        val defaults = listOf(
            Triple("com.sec.android.app.camera", "Camera", Color(0xFFE53935)),
            Triple("com.android.chrome", "Internet", Color(0xFF1E88E5)),
            Triple("com.sec.android.gallery3d", "Gallery", Color(0xFFFB8C00)),
            Triple("com.google.android.apps.messaging", "Messages", Color(0xFF43A047)),
            Triple("com.samsung.android.dialer", "Phone", Color(0xFF00ACC1)),
            Triple("com.sec.android.app.popupcalculator", "Calculator", Color(0xFF8E24AA)),
            Triple("com.samsung.android.app.notes", "Samsung Notes", Color(0xFFFFB300)),
            Triple("com.android.settings", "Settings", Color(0xFF757575)),
            Triple("com.google.android.youtube", "YouTube", Color(0xFFD32F2F)),
            Triple("com.spotify.music", "Spotify", Color(0xFF1DB954)),
            Triple("com.google.android.apps.maps", "Maps", Color(0xFF0F9D58)),
            Triple("com.google.android.gm", "Gmail", Color(0xFFEA4335))
        )

        return defaults.map { (pkg, name, color) ->
            AppItem(
                packageName = pkg,
                label = name,
                accentColor = color,
                isFavorite = favorites.contains(pkg) || favorites.isEmpty()
            )
        }
    }

    private fun getHashColor(pkg: String): Color {
        val hash = kotlin.math.abs(pkg.hashCode())
        val palette = listOf(
            Color(0xFF0381FE),
            Color(0xFF8E24AA),
            Color(0xFF00897B),
            Color(0xFFFB8C00),
            Color(0xFFE53935),
            Color(0xFF3949AB),
            Color(0xFF43A047),
            Color(0xFFD81B60)
        )
        return palette[hash % palette.size]
    }

    companion object {
        private const val KEY_FAVORITES = "favorite_packages"
    }
}
