package com.snacky.quickbot

import android.app.Activity
import android.os.Bundle
import androidx.core.content.ContextCompat

class AssistActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (OverlayPermission.has(this)) {
            ContextCompat.startForegroundService(
                this, android.content.Intent(this, OverlayService::class.java)
            )
        } else {
            OverlayPermission.request(this)
        }
        finish()
    }
}