package com.example.viewmodel

import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            try {
                delay(400)
                val historyPairs = _state.value.chatHistory.map { it.role to it.text }
                val aiText = if (historyPairs.isEmpty()) {
                    "Hola, he escuchado sobre su propuesta comercial. ¿De qué se trata exactamente y qué beneficios ofrece?"
                } else {
                    com.example.util.AisoOfflineEngine.generateCustomerRoleplayTurn(historyPairs, isSales = true)
                }
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
                delay(450)
                val historyPairs = _state.value.chatHistory.map { it.role to it.text }
                val feedback = com.example.util.AisoOfflineEngine.evaluateCustomerRoleplay(historyPairs, isSales = true)
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
}
