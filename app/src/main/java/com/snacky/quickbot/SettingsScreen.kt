package com.snacky.quickbot

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.snacky.quickbot.ui.theme.allPalettes
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.core.net.toUri

// Вспомогательная функция для перевода названий палитр
@Composable
fun paletteLabel(paletteId: String): String {
    return when (paletteId) {
        "system" -> stringResource(R.string.palette_system)
        "mono" -> stringResource(R.string.palette_mono)
        "mint" -> stringResource(R.string.palette_mint)
        "indigo" -> stringResource(R.string.palette_indigo)
        "terracotta" -> stringResource(R.string.palette_terracotta)
        "rose" -> stringResource(R.string.palette_rose)
        "aqua" -> stringResource(R.string.palette_aqua)
        "banana" -> stringResource(R.string.palette_banana)
        else -> paletteId // fallback
    }
}

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
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
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
            Text(stringResource(R.string.theme_title), style = MaterialTheme.typography.titleMedium)

            val themeOptions = listOf(
                ThemePrefs.MODE_DARK to stringResource(R.string.theme_dark),
                ThemePrefs.MODE_SYSTEM to stringResource(R.string.theme_system),
                ThemePrefs.MODE_LIGHT to stringResource(R.string.theme_light)
            )
            val selectedThemeIndex = themeOptions.indexOfFirst { it.first == themeMode }
                .coerceAtLeast(0)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                themeOptions.forEachIndexed { index, (mode, label) ->
                    val isSelected = index == selectedThemeIndex
                    val isFirst = index == 0
                    val isLast = index == themeOptions.size - 1

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
            Text(stringResource(R.string.palette_title), style = MaterialTheme.typography.titleMedium)

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
                        // ИЗМЕНЕНО: передаем переведенное название вместо palette.label
                        labelText = paletteLabel(palette.id),
                        onClick = {
                            paletteId = palette.id
                            PalettePrefs.setPalette(context, palette.id)
                        }
                    )
                }
            }

            Text(
                stringResource(R.string.palette_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider()

            // МОДЕЛЬ
            Text(stringResource(R.string.model_title), style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text(stringResource(R.string.model_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { SecurePrefs.saveModel(context, model.trim()) },
                    enabled = model.isNotBlank()
                ) {
                    Text(stringResource(R.string.save))
                }

                FilledTonalButton(
                    onClick = {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            "https://openrouter.ai/models".toUri()
                        )
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.visit_openrouter))
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Text(
                stringResource(R.string.model_hint),
                style = MaterialTheme.typography.bodySmall
            )

            HorizontalDivider()

            // ПОТОКОВЫЕ ОТВЕТЫ
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(stringResource(R.string.streaming_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringResource(R.string.streaming_desc),
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
            Text(stringResource(R.string.overlay_title), style = MaterialTheme.typography.titleMedium)
            Text(
                stringResource(if (overlayGranted) R.string.overlay_granted else R.string.overlay_denied),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(onClick = {
                OverlayPermission.request(context)
                overlayGranted = OverlayPermission.has(context)
            }) {
                Text(stringResource(R.string.overlay_button))
            }

            HorizontalDivider()

            // API-КЛЮЧ
            Text(stringResource(R.string.api_key_title), style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onClearKey,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(stringResource(R.string.delete_key))
            }
        }
    }
}