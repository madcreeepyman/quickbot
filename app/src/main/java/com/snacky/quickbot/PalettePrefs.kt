package com.snacky.quickbot

import android.content.Context
import androidx.core.content.edit

object PalettePrefs {
    private const val FILE = "theme_settings"
    private const val KEY_PALETTE = "palette_id"

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun getPalette(context: Context): String =
        prefs(context).getString(KEY_PALETTE, "system") ?: "system"

    fun setPalette(context: Context, paletteId: String) {
        prefs(context).edit { putString(KEY_PALETTE, paletteId) }
    }
}