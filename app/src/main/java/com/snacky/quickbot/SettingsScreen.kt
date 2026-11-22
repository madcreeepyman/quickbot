package com.snacky.quickbot

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll          // ← добавь эту строку
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.snacky.quickbot.ui.theme.allPalettes
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onClearKey: () -> Unit
) {
    val context = LocalContext.current
    var themeMode by remember { mutableIntStateOf(ThemePrefs.getMode(context)) }
    var paletteId by remember { mutableStateOf(PalettePrefs.getPalette(context)) }
    var model by remember { mutableStateOf(SecurePrefs.getModel(context)) }
    var overlayGranted by remember { mutableStateOf(OverlayPermission.has(context)) }
    var streamingEnabled by remember { mutableStateOf(SecurePrefs.isStreamingEnabled(context)) }

    val currentDark = when (themeMode) {
        ThemePrefs.MODE_LIGHT -> false
        ThemePrefs.MODE_DARK -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ТЕМА
            Text("Тема", style = MaterialTheme.typography.titleMedium)

            val themeOptions = listOf(
                ThemePrefs.MODE_DARK to "Тёмная",
                ThemePrefs.MODE_SYSTEM to "Системная",
                ThemePrefs.MODE_LIGHT to "Светлая"
            )
            val selectedThemeIndex = themeOptions.indexOfFirst { it.first == themeMode }
                .coerceAtLeast(0)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp) // Зазор 2dp по стандарту Connected
            ) {
                themeOptions.forEachIndexed { index, (mode, label) ->
                    val isSelected = index == selectedThemeIndex
                    val isFirst = index == 0
                    val isLast = index == themeOptions.size - 1

                    // Форма: внешние углы 24dp, внутренние 4dp
                    val shape = when {
                        isFirst -> RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 4.dp, bottomEnd = 4.dp)
                        isLast -> RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp, topEnd = 24.dp, bottomEnd = 24.dp)
                        else -> RoundedCornerShape(4.dp)
                    }

                    Surface(
                        onClick = {
                            themeMode = mode
                            ThemePrefs.setMode(context, mode)
                        },
                        shape = shape,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // ПАЛИТРА
            Text("Цвет оформления", style = MaterialTheme.typography.titleMedium)

            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                allPalettes.forEach { palette ->
                    PaletteSwatch(
                        palette = palette,
                        selected = palette.id == paletteId,
                        currentDarkTheme = currentDark,
                        onClick = {
                            paletteId = palette.id
                            PalettePrefs.setPalette(context, palette.id)
                        }
                    )
                }
            }

            Text(
                "Системная палитра работает только на Android 12 и выше " +
                        "На более старых версиях используется Монохром",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // МОДЕЛЬ
            Text("Модель OpenRouter", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Model ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Button(
                onClick = { SecurePrefs.saveModel(context, model.trim()) },
                enabled = model.isNotBlank()
            ) {
                Text("Сохранить")
            }
            Text(
                "Например: openai/gpt-oss-120b:free, anthropic/claude-3.5-sonnet",
                style = MaterialTheme.typography.bodySmall
            )

// Ссылка на OpenRouter
            FilledTonalButton (
                onClick = {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        "https://openrouter.ai/models".toUri()
                    )
                    context.startActivity(intent)
                }
            ) {
                Text("Список моделей на сайте OpenRouter")
            }

            HorizontalDivider()

            // ПОТОКОВЫЕ ОТВЕТЫ
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Потоковая выдача", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Показывать ответ по мере генерации",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = streamingEnabled,
                    onCheckedChange = { enabled ->
                        streamingEnabled = enabled
                        SecurePrefs.setStreamingEnabled(context, enabled)
                    }
                )
            }

            HorizontalDivider()

            // ОВЕРЛЕЙ
            Text("Оверлей ассистента", style = MaterialTheme.typography.titleMedium)
            Text(
                if (overlayGranted) "Разрешение предоставлено"
                else "Разрешение не предоставлено — оверлей работать не будет",
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = {
                OverlayPermission.request(context)
                overlayGranted = OverlayPermission.has(context)
            }) {
                Text("Выдать или отозвать разрешение")
            }

            HorizontalDivider()

            // API-КЛЮЧ
            Text("API-ключ", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onClearKey,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Удалить ключ")
            }
        }
    }
}