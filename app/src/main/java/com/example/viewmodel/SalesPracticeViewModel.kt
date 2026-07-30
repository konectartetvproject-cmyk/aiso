package com.example.viewmodel

import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.gemini.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

data class SalesState(
    val isConfiguring: Boolean = true,
    val contextInfo: String = "",
    val isRecording: Boolean = false,
    val isWaitingForAI: Boolean = false,
    val chatHistory: List<CSChatMessage> = emptyList(),
    val isFinished: Boolean = false,
    val feedback: String? = null
)

class SalesPracticeViewModel : ViewModel() {
    private val _state = MutableStateFlow(SalesState())
    val state: StateFlow<SalesState> = _state.asStateFlow()

    private var tts: TextToSpeech? = null

    fun updateContextInfo(info: String) {
        _state.update { it.copy(contextInfo = info) }
    }

    fun startPractice() {
        _state.update {
            it.copy(
                isConfiguring = false,
                isFinished = false,
                chatHistory = listOf(),
                feedback = null
            )
        }
        // AI speaks first
        triggerAIResponse()
    }

    fun toggleRecording() {
        _state.update { it.copy(isRecording = !it.isRecording) }
    }

    fun onUserSpeechResult(text: String, bitmap: Bitmap?) {
        if (text.isBlank()) return
        
        _state.update {
            val newHistory = it.chatHistory + CSChatMessage(role = "user", text = text)
            it.copy(chatHistory = newHistory, isRecording = false, isWaitingForAI = true)
        }

        triggerAIResponse(bitmap)
    }

    private fun triggerAIResponse(bitmap: Bitmap? = null) {
        val currentState = _state.value
        viewModelScope.launch {
            try {
                val systemInstruction = "Eres un cliente potencial interactuando con un vendedor. El producto o servicio que te intentan vender es: ${currentState.contextInfo}. Actúa de manera realista, mostrando interés pero también planteando objeciones o dudas sobre precio, utilidad o alternativas. Mantén tus respuestas conversacionales, breves (1-3 oraciones) y naturales. Si el vendedor es muy convincente, puedes aceptar la compra."
                
                val contents = currentState.chatHistory.map {
                    Content(
                        role = it.role,
                        parts = listOf(Part(text = it.text))
                    )
                }.toMutableList()

                if (bitmap != null && contents.isNotEmpty()) {
                    val lastContent = contents.last()
                    val base64Image = bitmapToBase64(bitmap)
                    val updatedParts = lastContent.parts.toMutableList()
                    updatedParts.add(Part(inlineData = InlineData(mimeType = "image/jpeg", data = base64Image)))
                    contents[contents.size - 1] = lastContent.copy(parts = updatedParts)
                }

                val request = GenerateContentRequest(
                    systemInstruction = Content(role = "user", parts = listOf(Part(text = systemInstruction))),
                    contents = if (contents.isEmpty()) {
                        // AI starts the conversation
                        listOf(Content(role = "user", parts = listOf(Part(text = "Hola, he escuchado sobre su producto/servicio. ¿De qué se trata?"))))
                    } else contents
                )

                val response = GeminiRetrofitClient.service.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )
                
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No entendí."
                
                _state.update {
                    it.copy(
                        chatHistory = it.chatHistory + CSChatMessage(role = "model", text = aiText),
                        isWaitingForAI = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(isWaitingForAI = false) }
            }
        }
    }

    fun finishPractice() {
        _state.update { it.copy(isFinished = true, isWaitingForAI = true) }
        viewModelScope.launch {
            try {
                val transcript = _state.value.chatHistory.joinToString("\n") { "${if (it.role == "user") "Vendedor" else "Cliente"}: ${it.text}" }
                val prompt = "Eres un coach experto en ventas. El vendedor acaba de terminar un roleplay. Esta es la transcripción de la conversación:\n$transcript\n\nDame una evaluación detallada y consejos constructivos para el vendedor, destacando sus puntos fuertes, manejo de objeciones y cierre de ventas."
                
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(role = "user", parts = listOf(Part(text = prompt)))
                    )
                )

                val response = GeminiRetrofitClient.service.generateContent(
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = request
                )
                
                val feedback = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Buen trabajo."
                
                _state.update { it.copy(feedback = feedback, isWaitingForAI = false) }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(feedback = "Error al obtener retroalimentación.", isWaitingForAI = false) }
            }
        }
    }
    
    fun setTts(textToSpeech: TextToSpeech) {
        tts = textToSpeech
    }
    
    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    
    override fun onCleared() {
        tts?.shutdown()
        super.onCleared()
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        val scaled = Bitmap.createScaledBitmap(bitmap, 480, 640, true)
        scaled.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
