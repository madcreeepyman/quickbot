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

    private val TAG = "QuickbotDB"

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
                Log.d(TAG, "Loaded ${entities.size} messages from DB")
                _messages.value = entities.map { UiMessage(it.role, it.text, it.isError) }
            }
        }
    }

    fun send(text: String) {
        val ctx = getApplication<Application>()
        val apiKey = SecurePrefs.getKey(ctx) ?: return
        val model = SecurePrefs.getModel(ctx)
        val streamingEnabled = SecurePrefs.isStreamingEnabled(ctx)

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
                    dao.insertMessage(
                        ChatMessageEntity(
                            role = "assistant",
                            text = "Ошибка: ${error?.message ?: "Неизвестная ошибка"}",
                            isError = true
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send message", e)
                val partial = _streamingText.value.orEmpty()
                if (partial.isNotBlank()) {
                    try {
                        dao.insertMessage(ChatMessageEntity(role = "assistant", text = partial))
                    } catch (dbError: Exception) {
                        Log.e(TAG, "Failed to save partial response", dbError)
                    }
                }
                try {
                    dao.insertMessage(
                        ChatMessageEntity(
                            role = "assistant",
                            text = "Ошибка: ${e.message ?: "Неизвестная ошибка"}",
                            isError = true
                        )
                    )
                } catch (dbError: Exception) {
                    Log.e(TAG, "Failed to save error message", dbError)
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
                Log.d(TAG, "Cleared all messages")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear messages", e)
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
