package com.snacky.quickbot

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.setup_description),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = key,
                onValueChange = { key = it.trim() },
                label = { Text(stringResource(R.string.setup_api_key_label)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it.trim() },
                label = { Text(stringResource(R.string.setup_model_label)) },
                placeholder = { Text(stringResource(R.string.setup_model_placeholder)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.setup_model_hint),
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
                Text(stringResource(R.string.setup_save))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.setup_key_hint),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}