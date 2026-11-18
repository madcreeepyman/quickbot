package com.snacky.quickbot

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(val role: String, val content: String)

object OpenRouterClient {
    private const val URL = "https://openrouter.ai/api/v1/chat/completions"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun buildRequest(
        apiKey: String,
        model: String,
        history: List<ChatMessage>,
        stream: Boolean
    ): Request {
        val messages = JSONArray()
        history.forEach {
            messages.put(JSONObject().apply {
                put("role", it.role)
                put("content", it.content)
            })
        }
        val body = JSONObject().apply {
            put("model", model)
            put("messages", messages)
            if (stream) put("stream", true)
        }.toString()

        return Request.Builder()
            .url(URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "https://localhost")
            .addHeader("X-Title", "Quickbot")
            .post(body.toRequestBody("application/json".toMediaType()))
            .build()
    }

    suspend fun send(
        apiKey: String,
        model: String,
        history: List<ChatMessage>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest(apiKey, model, history, stream = false)
            client.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("HTTP ${response.code}: $text")
                    )
                }
                val content = JSONObject(text)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                Result.success(content)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendStreaming(
        apiKey: String,
        model: String,
        history: List<ChatMessage>,
        onDelta: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        var call: okhttp3.Call? = null
        var cancellationHandle: kotlinx.coroutines.DisposableHandle? = null
        try {
            call = client.newCall(buildRequest(apiKey, model, history, stream = true))
            cancellationHandle = currentCoroutineContext()[Job]?.invokeOnCompletion { cause ->
                if (cause != null) call?.cancel()
            }

            call!!.execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    return@withContext Result.failure(
                        Exception("HTTP ${response.code}: $errorBody")
                    )
                }

                val source = response.body?.source()
                    ?: return@withContext Result.failure(Exception("Пустой ответ сервера"))
                val answer = StringBuilder()

                while (!source.exhausted()) {
                    currentCoroutineContext().ensureActive()
                    val line = source.readUtf8Line() ?: break
                    if (!line.startsWith("data:")) continue

                    val payload = line.removePrefix("data:").trim()
                    if (payload.isEmpty()) continue
                    if (payload == "[DONE]") break

                    val delta = try {
                        val event = JSONObject(payload)
                        val choices = event.optJSONArray("choices")
                        val choice = choices?.optJSONObject(0)
                        val content = choice?.optJSONObject("delta")?.opt("content")
                        content as? String
                    } catch (_: Exception) {
                        null
                    }

                    if (!delta.isNullOrEmpty()) {
                        answer.append(delta)
                        onDelta(delta)
                    }
                }
                Result.success(answer.toString())
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            cancellationHandle?.dispose()
        }
    }
}
