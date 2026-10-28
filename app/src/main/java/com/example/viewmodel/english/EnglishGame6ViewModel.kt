package com.example.viewmodel.english

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EnglishGame6State(
    val isRecording: Boolean = false,
    val currentIndex: Int = 0,
    val feedback: String? = null,
    val isCompleted: Boolean = false,
    val currentHeardText: String = ""
)

class EnglishGame6ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame6State())
    val state: StateFlow<EnglishGame6State> = _state.asStateFlow()

    val practiceItems = listOf(
        "Hello",
        "Water",
        "Beautiful",
        "Challenge",
        "Through",
        "How are you today",
        "I would like a cup of coffee",
        "The weather is very nice",
        "She sells seashells by the seashore"
    )

    fun startRecording() {
        _state.update { it.copy(isRecording = true, feedback = null, currentHeardText = "") }
    }

    fun stopRecording() {
        _state.update { it.copy(isRecording = false) }
    }

    fun onTextHeard(text: String, isFinal: Boolean) {
        if (!_state.value.isRecording) return
        
        _state.update { it.copy(currentHeardText = text) }
        if (isFinal) {
            checkPronunciation(text)
        }
    }

    private fun checkPronunciation(heardText: String) {
        val currentState = _state.value
        if (currentState.currentIndex >= practiceItems.size) return
        
        val targetText = practiceItems[currentState.currentIndex].lowercase()
        val heardLower = heardText.lowercase()
        val cleanTarget = targetText.replace(Regex("[^a-z ]"), "").trim()
        val cleanHeard = heardLower.replace(Regex("[^a-z ]"), "").trim()

        if (cleanHeard.contains(cleanTarget) || cleanTarget.contains(cleanHeard) && cleanHeard.length > cleanTarget.length * 0.8) {
            if (currentState.currentIndex + 1 >= practiceItems.size) {
                _state.update { it.copy(
                    isCompleted = true,
                    isRecording = false,
                    feedback = "¡Excelente! Has completado todas las frases."
                )}
            } else {
                _state.update { it.copy(
                    currentIndex = it.currentIndex + 1,
                    feedback = "¡Muy bien! Siguiente frase.",
                    currentHeardText = ""
                )}
            }
        } else {
            _state.update { it.copy(
                feedback = "Escuché: \"$heardText\". Inténtalo de nuevo."
            )}
        }
    }
    
    fun reset() {
        _state.value = EnglishGame6State()
    }
}
