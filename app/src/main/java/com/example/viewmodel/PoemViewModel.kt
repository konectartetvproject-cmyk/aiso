package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.Poem
import com.example.model.samplePoems
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PoemState {
    object Idle : PoemState()
    object Analyzing : PoemState()
    data class Feedback(val text: String, val transcription: String) : PoemState()
    data class Error(val message: String) : PoemState()
}

class PoemViewModel : ViewModel() {
    private val _poems = MutableStateFlow(samplePoems)
    val poems: StateFlow<List<Poem>> = _poems.asStateFlow()

    private val _currentPoem = MutableStateFlow<Poem?>(null)
    val currentPoem: StateFlow<Poem?> = _currentPoem.asStateFlow()

    private val _state = MutableStateFlow<PoemState>(PoemState.Idle)
    val state: StateFlow<PoemState> = _state.asStateFlow()

    fun selectPoem(poemId: String) {
        _currentPoem.value = _poems.value.find { it.id == poemId }
        _state.value = PoemState.Idle
    }

    fun analyzeReading(spokenText: String) {
        val poem = _currentPoem.value ?: return
        if (spokenText.isBlank()) {
            _state.value = PoemState.Error("No se pudo reconocer la voz o no hablaste.")
            return
        }
        _state.value = PoemState.Analyzing
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(400)
                val feedback = com.example.util.AisoOfflineEngine.evaluatePoemDeclamation(
                    poemTitle = poem.title,
                    poemAuthor = poem.author,
                    poemContent = poem.content,
                    spokenText = spokenText
                )
                _state.value = PoemState.Feedback(feedback, spokenText)
            } catch (e: Exception) {
                _state.value = PoemState.Error("Error al analizar declamación: ${e.message}")
            }
        }
    }

    fun resetState() {
        _state.value = PoemState.Idle
    }
}
