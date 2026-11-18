package com.snacky.quickbot.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.snacky.quickbot.PalettePrefs
import com.snacky.quickbot.ThemePrefs

private val FallbackDark = darkColorScheme()
private val FallbackLight = lightColorScheme()

@Composable
fun QuickbotTheme(
    themeMode: Int = ThemePrefs.MODE_SYSTEM,
    paletteId: String = PalettePrefs.getPalette(LocalContext.current),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()

    val darkTheme = when (themeMode) {
        ThemePrefs.MODE_LIGHT -> false
        ThemePrefs.MODE_DARK -> true
        else -> systemDark
    }

    val palette = findPalette(paletteId)

    val colorScheme = when {
        // Системная (Monet) — только Android 12+
        palette.isSystem && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)

        // Системная на старых версиях — фолбэк на Монохром
        palette.isSystem -> {
            val fallback = findPalette("mono")
            (if (darkTheme) fallback.dark else fallback.light)
                ?: (if (darkTheme) FallbackDark else FallbackLight)
        }

        // Обычная палитра
        else -> {
            val scheme = if (darkTheme) palette.dark else palette.light
            scheme ?: if (darkTheme) FallbackDark else FallbackLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}