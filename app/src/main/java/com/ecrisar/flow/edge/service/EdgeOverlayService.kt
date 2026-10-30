package com.ecrisar.flow.edge.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ecrisar.flow.edge.MainActivity
import com.ecrisar.flow.edge.R
import com.ecrisar.flow.edge.data.AppRepository
import com.ecrisar.flow.edge.data.CalendarRepository
import com.ecrisar.flow.edge.data.NotesRepository
import com.ecrisar.flow.edge.data.SettingsRepository
import com.ecrisar.flow.edge.ui.edge.EdgeContainerView
import com.ecrisar.flow.edge.ui.theme.EdgePanelTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * System-Wide Overlay Service:
 * Renders the Edge Handle and Slide-Out Panel on top of all Android apps
 * using WindowManager and TYPE_APPLICATION_OVERLAY.
 */
class EdgeOverlayService : LifecycleService(), SavedStateRegistryOwner, ViewModelStoreOwner {

    private lateinit var windowManager: WindowManager
    private var overlayComposeView: ComposeView? = null
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val appViewModelStore = ViewModelStore()
    private val serviceScope = CoroutineScope(Dispatchers.Main)

    private lateinit var settingsRepo: SettingsRepository
    private lateinit var appRepo: AppRepository
    private lateinit var notesRepo: NotesRepository
    private lateinit var calendarRepo: CalendarRepository

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)

        settingsRepo = SettingsRepository(this)
        appRepo = AppRepository(this)
        notesRepo = NotesRepository(this)
        calendarRepo = CalendarRepository()

        serviceScope.launch {
            appRepo.loadApps()
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        startForegroundNotification()
        attachOverlay()
    }

    private fun startForegroundNotification() {
        val channelId = "edge_panel_overlay_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Flow Edge Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the Flow Edge handle active on your screen"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Flow Edge Active")
            .setContentText("Swipe from right edge to open quick tools & apps")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(1001, notification)
    }

    private fun attachOverlay() {
        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        overlayComposeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@EdgeOverlayService)
            setViewTreeViewModelStoreOwner(this@EdgeOverlayService)
            setViewTreeSavedStateRegistryOwner(this@EdgeOverlayService)

            setContent {
                EdgePanelTheme {
                    val settings by settingsRepo.settings.collectAsState()
                    val favoriteApps by appRepo.favoriteApps.collectAsState()
                    val allApps by appRepo.installedApps.collectAsState()
                    val notes by notesRepo.notes.collectAsState()
                    val calendarEvents by calendarRepo.events.collectAsState()

                    EdgeContainerView(
                        settings = settings,
                        favoriteApps = favoriteApps,
                        allApps = allApps,
                        notes = notes,
                        calendarEvents = calendarEvents,
                        onUpdateHandlePosition = { settingsRepo.updateHandlePosition(it) },
                        onOpenSettings = {
                            val intent = Intent(this@EdgeOverlayService, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                putExtra("open_settings", true)
                            }
                            startActivity(intent)
                        },
                        onToggleFavoriteApp = { appRepo.toggleFavorite(it) },
                        onAddNote = { t, c, col -> notesRepo.addNote(t, c, col) },
                        onDeleteNote = { notesRepo.deleteNote(it) },
                        onAddCalendarEvent = { t, tm, d -> calendarRepo.addEvent(t, tm, d) },
                        onDeleteCalendarEvent = { calendarRepo.deleteEvent(it) }
                    ) {
                        // Empty transparent background for overlay
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }

        try {
            windowManager.addView(overlayComposeView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayComposeView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        serviceScope.cancel()
        appViewModelStore.clear()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EdgeOverlayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, EdgeOverlayService::class.java)
            context.stopService(intent)
        }
    }
}
