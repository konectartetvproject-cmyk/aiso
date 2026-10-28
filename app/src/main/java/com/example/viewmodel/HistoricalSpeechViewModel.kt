package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.HistoricalSpeech
import com.example.model.sampleHistoricalSpeeches
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HistoricalSpeechState {
    object Idle : HistoricalSpeechState()
    object Analyzing : HistoricalSpeechState()
    data class Feedback(val text: String, val transcription: String) : HistoricalSpeechState()
    data class Error(val message: String) : HistoricalSpeechState()
}

class HistoricalSpeechViewModel : ViewModel() {
    private val _speeches = MutableStateFlow(sampleHistoricalSpeeches)
    val speeches: StateFlow<List<HistoricalSpeech>> = _speeches.asStateFlow()

    private val _currentSpeech = MutableStateFlow<HistoricalSpeech?>(null)
    val currentSpeech: StateFlow<HistoricalSpeech?> = _currentSpeech.asStateFlow()

    private val _state = MutableStateFlow<HistoricalSpeechState>(HistoricalSpeechState.Idle)
    val state: StateFlow<HistoricalSpeechState> = _state.asStateFlow()

    fun selectSpeech(speechId: String) {
        _currentSpeech.value = _speeches.value.find { it.id == speechId }
        _state.value = HistoricalSpeechState.Idle
    }

    fun analyzeReading(spokenText: String) {
        val speech = _currentSpeech.value ?: return
        if (spokenText.isBlank()) {
            _state.value = HistoricalSpeechState.Error("No se pudo reconocer la voz o no hablaste.")
            return
        }
        _state.value = HistoricalSpeechState.Analyzing
        viewModelScope.launch {
            try {
                delay(400)
                val feedback = AisoOfflineEngine.evaluateHistoricalSpeech(speech, spokenText)
                _state.value = HistoricalSpeechState.Feedback(feedback, spokenText)
            } catch (e: Exception) {
                _state.value = HistoricalSpeechState.Error("Error al evaluar discurso: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = HistoricalSpeechState.Idle
    }
}
