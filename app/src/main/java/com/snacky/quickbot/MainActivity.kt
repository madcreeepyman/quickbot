package com.snacky.quickbot

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.snacky.quickbot.ui.theme.QuickbotTheme

class MainActivity : ComponentActivity() {

    private val notifPermLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* не критично */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifPermLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            AppRoot()
        }
    }
}

@Composable
private fun AppRoot() {
    val context = LocalContext.current
    var hasKey by remember { mutableStateOf(SecurePrefs.getKey(context) != null) }
    var screen by remember { mutableStateOf("chat") }

    // Перечитываем themeMode и paletteId при каждой смене экрана
    val themeMode by remember(screen) { mutableIntStateOf(ThemePrefs.getMode(context)) }
    val paletteId by remember(screen) { mutableStateOf(PalettePrefs.getPalette(context)) }

    QuickbotTheme(themeMode = themeMode, paletteId = paletteId) {
        Surface {
            when {
                !hasKey -> SetupScreen(onSave = { key, model ->
                    SecurePrefs.saveKey(context, key)
                    SecurePrefs.saveModel(context, model)
                    hasKey = true
                })
                screen == "settings" -> SettingsScreen(
                    onBack = { screen = "chat" },
                    onClearKey = {
                        SecurePrefs.clearKey(context)
                        hasKey = false
                        screen = "chat"
                    }
                )
                else -> ChatScreen(onOpenSettings = { screen = "settings" })
            }
        }
    }
}