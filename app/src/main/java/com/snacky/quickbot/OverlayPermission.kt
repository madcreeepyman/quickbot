package com.snacky.quickbot

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri

object OverlayPermission {
    fun has(context: Context): Boolean =
        Settings.canDrawOverlays(context)

    fun request(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            "package:${context.packageName}".toUri()
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}