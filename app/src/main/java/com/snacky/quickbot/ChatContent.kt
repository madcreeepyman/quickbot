package com.snacky.quickbot

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import android.content.Context
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun ChatContent(
    vm: ChatViewModel,
    modifier: Modifier = Modifier,
    onCopy: (String) -> Unit = {}
) {
    val messages by vm.messages.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val streamingText by vm.streamingText.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current

    LaunchedEffect(messages.size, isLoading, streamingText) {
        if (messages.isNotEmpty() || !streamingText.isNullOrEmpty() || isLoading) {
            listState.animateScrollToItem(messages.size)
        }
    }

    Column(modifier) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(
                    msg = msg,
                    onCopyClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Quickbot", msg.text))
                        onCopy(msg.text)
                    }
                )
            }
            if (!streamingText.isNullOrEmpty()) {
                item {
                    MessageBubble(
                        msg = UiMessage(role = "assistant", text = streamingText.orEmpty()),
                        onCopyClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
                                    as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Quickbot", streamingText.orEmpty()))
                            onCopy(streamingText.orEmpty())
                        }
                    )
                }
            }
            if (isLoading && streamingText.isNullOrEmpty()) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Думаю...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // ─── Капсула с полем ввода (Tonal, без обводки) ───
                Row(
                    Modifier
                        .weight(1f)
                        .heightIn(min = 42.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight(Alignment.CenterVertically),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        maxLines = 5,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
                            ) {
                                if (input.isEmpty()) {
                                    Text(
                                        "Сообщение...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                // ─── Капсула-кнопка отправки (Tonal → Filled) ───
                val hasText = input.isNotBlank() && !isLoading

                Surface(
                    onClick = {
                        val t = input.trim()
                        if (t.isNotEmpty() && !isLoading) {
                            input = ""
                            vm.send(t)
                        }
                    },
                    enabled = hasText,
                    modifier = Modifier
                        .width(72.dp)
                        .height(42.dp),
                    shape = RoundedCornerShape(21.dp),
                    color = if (hasText)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Отправить",
                            tint = if (hasText)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun MessageBubble(
    msg: UiMessage,
    onCopyClick: () -> Unit
) {
    val isUser = msg.role == "user"
    val bg = when {
        msg.isError -> MaterialTheme.colorScheme.errorContainer
        isUser -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    var showMenu by remember { mutableStateOf(false) }

    // Определяем форму: капсула для коротких, скругление для длинных
    val isShort = msg.text.length <= 40 && !msg.text.contains('\n')
    val shape = if (isShort) RoundedCornerShape(percent = 50) else RoundedCornerShape(16.dp)

    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = { showMenu = true }
            ),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box {
            Surface(
                color = bg,
                shape = shape,
                modifier = Modifier.widthIn(max = 320.dp)
            ) {
                MarkdownText(
                    markdown = msg.text,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = if (msg.isError) MaterialTheme.colorScheme.onErrorContainer
                    else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Копировать") },
                    leadingIcon = {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                    },
                    onClick = {
                        showMenu = false
                        onCopyClick()
                    }
                )
            }
        }
    }
}