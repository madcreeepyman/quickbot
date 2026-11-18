package com.snacky.quickbot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snacky.quickbot.ui.theme.Palette

@Composable
fun PaletteSwatch(
    palette: Palette,
    selected: Boolean,
    currentDarkTheme: Boolean,
    onClick: () -> Unit
) {
    // Определяем цвет кружка
    val swatchScheme = when {
        currentDarkTheme && palette.dark != null -> palette.dark
        !currentDarkTheme && palette.light != null -> palette.light
        palette.dark != null -> palette.dark
        palette.light != null -> palette.light
        else -> null  // Системная
    }

    val primary = swatchScheme?.primary ?: MaterialTheme.colorScheme.primary
    val secondary = swatchScheme?.secondary ?: MaterialTheme.colorScheme.secondary
    val surface = swatchScheme?.surface ?: MaterialTheme.colorScheme.surface

    // Для Системной — градиент (два цвета), для остальных — обычный
    val brush = if (palette.isSystem) {
        Brush.linearGradient(listOf(primary, secondary))
    } else {
        Brush.linearGradient(listOf(primary, primary))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush)
                .then(
                    if (selected) Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ) else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (currentDarkTheme) surface else Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = palette.label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            textAlign = TextAlign.Center,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}