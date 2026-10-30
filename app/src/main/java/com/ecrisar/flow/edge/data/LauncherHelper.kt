package com.ecrisar.flow.edge.data

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.ecrisar.flow.edge.model.AppItem

object LauncherHelper {

    /**
     * Launch an application standard fullscreen mode.
     */
    fun launchApp(context: Context, app: AppItem) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Opening ${app.label}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to launch ${app.label}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Launch an application in Samsung Pop-Up View / Android Freeform Windowing mode.
     * Sets launch bounds centered on screen with FLAG_ACTIVITY_MULTIPLE_TASK & FLAG_ACTIVITY_NEW_TASK.
     */
    fun launchAppInPopupView(
        context: Context,
        app: AppItem,
        bounds: Rect? = null
    ): Boolean {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                ?: return false

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

            val displayMetrics = context.resources.displayMetrics
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels

            val launchBounds = bounds ?: Rect(
                (screenWidth * 0.15f).toInt(),
                (screenHeight * 0.20f).toInt(),
                (screenWidth * 0.85f).toInt(),
                (screenHeight * 0.80f).toInt()
            )

            val options = ActivityOptions.makeBasic()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                options.setLaunchBounds(launchBounds)
            }

            // Attempt Samsung / Android Freeform windowing mode reflection (WINDOWING_MODE_FREEFORM = 5)
            try {
                val method = ActivityOptions::class.java.getMethod("setLaunchWindowingMode", Int::class.javaPrimitiveType)
                method.invoke(options, 5) // WINDOWING_MODE_FREEFORM
            } catch (ignored: Throwable) {
                // Not accessible or non-freeform OEM, fallback to setLaunchBounds
            }

            context.startActivity(intent, options.toBundle())
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to normal launch
            launchApp(context, app)
            return false
        }
    }

    /**
     * Checks if the app has permission to draw overlays (SYSTEM_ALERT_WINDOW).
     */
    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    /**
     * Intent to open the System Overlay Permission Settings.
     */
    fun getOverlayPermissionIntent(context: Context): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } else {
            Intent()
        }
    }
}
