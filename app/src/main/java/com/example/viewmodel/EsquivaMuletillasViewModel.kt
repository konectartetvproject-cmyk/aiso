package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EsquivaChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean
)

class EsquivaMuletillasViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<EsquivaChatMessage>>(emptyList())
    val messages: StateFlow<List<EsquivaChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var lives = 3
    private var score = 0
    private var cleanTurns = 0
    private var currentTopic = ""

    init {
        startGame()
    }

    fun startGame() {
        lives = 3
        score = 0
        cleanTurns = 0
        val (welcomeText, topic) = AisoOfflineEngine.getEsquivaInitialChallenge()
        currentTopic = topic
        _messages.value = listOf(
            EsquivaChatMessage(text = welcomeText, isUser = false)
        )
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        val userMessage = EsquivaChatMessage(text = userText, isUser = true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch {
            delay(350)
            val result = AisoOfflineEngine.handleEsquivaMuletillasTurn(
                userText = userText,
                currentLives = lives,
                currentScore = score,
                cleanTurns = cleanTurns,
                topic = currentTopic
            )
            lives = result.newLives
            score = result.newScore
            cleanTurns = result.newCleanTurns
            val botMessage = EsquivaChatMessage(text = result.replyText, isUser = false)
            _messages.value = _messages.value + botMessage
            _isLoading.value = false
        }
    }
}
