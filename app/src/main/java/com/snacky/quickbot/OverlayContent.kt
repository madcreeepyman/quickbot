package com.snacky.quickbot

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource               // ← ДОБАВИТЬ
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun OverlayContent(vm: ChatViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Получаем строку заранее для использования в корутине
    val copiedText = stringResource(R.string.chat_copied)

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.app_name), // ← ИЗМЕНЕНО
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { (context as? OverlayService)?.stopSelf() }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.overlay_close) // ← ИЗМЕНЕНО
                    )
                }
            }
            ChatContent(
                vm = vm,
                modifier = Modifier.weight(1f),
                onCopy = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = copiedText, // ← ИЗМЕНЕНО
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }

        // Snackbar поверх всего, внизу
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
        )
    }
}