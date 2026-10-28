package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Affirmation
import com.example.model.sampleAffirmations
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AffirmationState {
    object Idle : AffirmationState()
    object Analyzing : AffirmationState()
    data class Feedback(val text: String, val transcription: String) : AffirmationState()
    data class Error(val message: String) : AffirmationState()
}

class AffirmationViewModel : ViewModel() {
    private val _affirmations = MutableStateFlow(sampleAffirmations)
    val affirmations: StateFlow<List<Affirmation>> = _affirmations.asStateFlow()

    private val _currentAffirmation = MutableStateFlow<Affirmation?>(null)
    val currentAffirmation: StateFlow<Affirmation?> = _currentAffirmation.asStateFlow()

    private val _state = MutableStateFlow<AffirmationState>(AffirmationState.Idle)
    val state: StateFlow<AffirmationState> = _state.asStateFlow()

    fun selectAffirmation(id: String) {
        _currentAffirmation.value = _affirmations.value.find { it.id == id }
        _state.value = AffirmationState.Idle
    }

    fun analyzeReading(spokenText: String) {
        val affirmation = _currentAffirmation.value ?: return
        if (spokenText.isBlank()) {
            _state.value = AffirmationState.Error("No se pudo reconocer la voz o no hablaste.")
            return
        }
        _state.value = AffirmationState.Analyzing
        viewModelScope.launch {
            try {
                delay(400)
                val feedback = AisoOfflineEngine.evaluateAffirmationReading(affirmation, spokenText)
                _state.value = AffirmationState.Feedback(feedback, spokenText)
            } catch (e: Exception) {
                _state.value = AffirmationState.Error("Error al evaluar afirmación: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = AffirmationState.Idle
    }
}
