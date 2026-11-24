package com.snacky.quickbot

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiMessage(val role: String, val text: String, val isError: Boolean = false)

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val tag = "QuickbotDB"

    private val db = AppDatabase.getDatabase(app)
    private val dao = db.chatDao()

    private val _messages = MutableStateFlow<List<UiMessage>>(emptyList())
    val messages: StateFlow<List<UiMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _streamingText = MutableStateFlow<String?>(null)
    val streamingText: StateFlow<String?> = _streamingText.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllMessages().collect { entities ->
                Log.d(tag, "Loaded ${entities.size} messages from DB")
                _messages.value = entities.map { UiMessage(it.role, it.text, it.isError) }
            }
        }
    }

    fun send(text: String) {
        val ctx = getApplication<Application>()
        val apiKey = SecurePrefs.getKey(ctx) ?: return
        val model = SecurePrefs.getModel(ctx)
        val streamingEnabled = SecurePrefs.isStreamingEnabled(ctx)

        // Получаем строки заранее
        val errorPrefix = ctx.getString(R.string.error_prefix)
        val unknownError = ctx.getString(R.string.error_unknown)

        _isLoading.value = true
        _streamingText.value = if (streamingEnabled) "" else null

        viewModelScope.launch {
            val history = _messages.value
                .filter { !it.isError }
                .map { ChatMessage(it.role, it.text) } + ChatMessage("user", text)

            try {
                dao.insertMessage(ChatMessageEntity(role = "user", text = text))

                val result = if (streamingEnabled) {
                    OpenRouterClient.sendStreaming(apiKey, model, history) { delta ->
                        _streamingText.value = (_streamingText.value.orEmpty()) + delta
                    }
                } else {
                    OpenRouterClient.send(apiKey, model, history)
                }

                if (result.isSuccess) {
                    val reply = result.getOrThrow()
                    dao.insertMessage(ChatMessageEntity(role = "assistant", text = reply))
                } else {
                    val partial = _streamingText.value.orEmpty()
                    if (partial.isNotBlank()) {
                        dao.insertMessage(ChatMessageEntity(role = "assistant", text = partial))
                    }
                    val error = result.exceptionOrNull()
                    // ИЗМЕНЕНО:
                    val errorMessage = error?.message ?: unknownError
                    dao.insertMessage(
                        ChatMessageEntity(
                            role = "assistant",
                            text = String.format(errorPrefix, errorMessage),
                            isError = true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Failed to send message", e)
                val partial = _streamingText.value.orEmpty()
                if (partial.isNotBlank()) {
                    try {
                        dao.insertMessage(ChatMessageEntity(role = "assistant", text = partial))
                    } catch (dbError: Exception) {
                        Log.e(tag, "Failed to save partial response", dbError)
                    }
                }
                try {
                    // ИЗМЕНЕНО:
                    val errorMessage = e.message ?: unknownError
                    dao.insertMessage(
                        ChatMessageEntity(
                            role = "assistant",
                            text = String.format(errorPrefix, errorMessage),
                            isError = true
                        )
                    )
                } catch (dbError: Exception) {
                    Log.e(tag, "Failed to save error message", dbError)
                }
            } finally {
                _streamingText.value = null
                _isLoading.value = false
            }
        }
    }

    fun clear() {
        viewModelScope.launch {
            try {
                dao.clearAll()
                Log.d(tag, "Cleared all messages")
            } catch (e: Exception) {
                Log.e(tag, "Failed to clear messages", e)
            }
        }
    }
}

class ChatViewModelFactory(
    private val app: Application
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}