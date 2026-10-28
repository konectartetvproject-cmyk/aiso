package com.example.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AisoKnowledgeBase
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.Part
import com.example.util.AiQuotaManager
import com.example.util.QuotaInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

enum class MessageSender {
    USER, ASSISTANT
}

data class ChatMessage(
    val text: String = "",
    val isFromUser: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender = if (isFromUser) MessageSender.USER else MessageSender.ASSISTANT,
    val timestamp: Long = System.currentTimeMillis()
)

class AisoChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _connectionStatus = MutableStateFlow<String?>(null)
    val connectionStatus: StateFlow<String?> = _connectionStatus.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    val quotaInfo: StateFlow<QuotaInfo> = AiQuotaManager.quotaState
        .stateIn(viewModelScope, SharingStarted.Eagerly, AiQuotaManager.quotaState.value)

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
                isTtsReady = true
            }
        }

        _messages.value = listOf(
            ChatMessage(
                text = "¡Hola! Soy **AISO**, tu mentor personal de oratoria y elocuencia basado en *Los secretos del orador exitoso*. ¿En qué reto de comunicación o presentación te puedo guiar hoy?",
                sender = MessageSender.ASSISTANT
            )
        )
    }

    fun setPaidKeyMode(enabled: Boolean) {
        AiQuotaManager.setPaidKeyMode(enabled)
    }

    fun resetQuota() {
        AiQuotaManager.resetQuota()
    }

    fun testModelConnection() {
        if (_isTestingConnection.value) return
        _isTestingConnection.value = true
        viewModelScope.launch {
            _connectionStatus.value = GeminiClient.testConnection()
            _isTestingConnection.value = false
        }
    }

    fun speakText(text: String) {
        if (isTtsReady && tts != null) {
            val clean = text.replace("*", "").replace("#", "").replace("`", "")
            tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "aiso_chat_tts")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty() || _isLoading.value) return

        val userMsg = ChatMessage(
            text = trimmed,
            sender = MessageSender.USER
        )
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            try {
                if (!AiQuotaManager.canMakeRequest()) {
                    val (item, _) = AisoKnowledgeBase.findBestAnswer(trimmed)
                    val offlineMsg = (item?.response ?: "Has alcanzado el límite de gasto de $2.00 USD para consultas en vivo.") + 
                        "\n\n*(Límite de uso de $2.00 USD alcanzado. Respuesta provista desde la base de conocimiento local sin costo adicional)*"
                    _messages.value = _messages.value + ChatMessage(
                        text = offlineMsg,
                        sender = MessageSender.ASSISTANT
                    )
                    _isLoading.value = false
                    return@launch
                }

                AiQuotaManager.recordUsage(inputTokens = 150, outputTokens = 120)

                val systemPrompt = """
                    Eres AISO IA, el mentor y tutor de oratoria, comunicación persuasiva e inteligencia escénica, basado estrictamente en el libro 'Los secretos del orador exitoso'.
                    REGLA OBLIGATORIA DE LONGITUD: Tus respuestas DEBEN SER CORTAS, DIRECTAS Y CONCISAS (máximo 2 a 3 oraciones o viñetas muy breves, menos de 75 palabras en total).
                    Sin introducciones largas ni relleno. Ve directo al grano con un consejo práctico, técnica o ejercicio accionable (ej. respiración diafragmática, regla de los 7 segundos, Trinidad de la Influencia, anclaje de poder).
                """.trimIndent()

                val apiContents = _messages.value.takeLast(6).map { msg ->
                    Content(
                        role = if (msg.sender == MessageSender.USER) "user" else "model",
                        parts = listOf(Part(text = msg.text))
                    )
                }

                val aiResponse = GeminiClient.chat(apiContents, systemPrompt)

                val finalResponse = if (aiResponse.isNotBlank() && !aiResponse.startsWith("Error")) {
                    aiResponse
                } else {
                    // Fallback con conocimiento offline de AisoKnowledgeBase
                    val (item, _) = AisoKnowledgeBase.findBestAnswer(trimmed)
                    item?.response ?: """
                        Excelente consulta sobre oratoria. 
                        
                        Recuerda estos 3 principios fundamentales:
                        1. **Conexión visual:** No mires al vacío ni al suelo; enfócate en personas específicas durante 3 a 5 segundos.
                        2. **Voz diafragmática:** Respira por la nariz inflando el abdomen, no el pecho.
                        3. **Pausas de poder:** Antes de responder o cambiar de idea, guarda 2 segundos de silencio. Demuestra control absoluto.
                    """.trimIndent()
                }

                _messages.value = _messages.value + ChatMessage(
                    text = finalResponse,
                    sender = MessageSender.ASSISTANT
                )
            } catch (e: Exception) {
                val (item, _) = AisoKnowledgeBase.findBestAnswer(trimmed)
                val fallbackText = item?.response ?: "Recuerda que la elocuencia se practica día a día. Mantén la calma, respira diafragmáticamente y proyecta con convicción."
                _messages.value = _messages.value + ChatMessage(
                    text = fallbackText,
                    sender = MessageSender.ASSISTANT
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
