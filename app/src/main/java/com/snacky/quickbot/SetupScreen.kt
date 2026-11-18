package com.snacky.quickbot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SetupScreen(onSave: (apiKey: String, model: String) -> Unit) {
    var key by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }

    Box(
        Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier.widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Quickbot", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                "Вставь свой API-ключ OpenRouter. Он будет храниться только на устройстве в зашифрованном виде.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = key,
                onValueChange = { key = it.trim() },
                label = { Text("OpenRouter API Key") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it.trim() },
                label = { Text("Model ID (опционально)") },
                placeholder = { Text("openai/gpt-4o-mini") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Оставь пустым для использования openai/gpt-4o-mini. " +
                        "Список моделей: openrouter.ai/models",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    val finalModel = model.ifBlank { "openai/gpt-4o-mini" }
                    onSave(key, finalModel)
                },
                enabled = key.startsWith("sk-or-"),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Ключи OpenRouter начинаются с sk-or-",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}