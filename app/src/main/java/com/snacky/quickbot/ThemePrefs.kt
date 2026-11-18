package com.snacky.quickbot

import android.content.Context

object ThemePrefs {
    private const val FILE = "theme_settings"
    private const val KEY_MODE = "theme_mode"

    const val MODE_SYSTEM = 0
    const val MODE_LIGHT = 1
    const val MODE_DARK = 2

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun getMode(context: Context): Int =
        prefs(context).getInt(KEY_MODE, MODE_SYSTEM)

    fun setMode(context: Context, mode: Int) {
        prefs(context).edit().putInt(KEY_MODE, mode).apply()
    }
}