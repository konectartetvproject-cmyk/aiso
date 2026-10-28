package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SpeechPhase {
    NOT_STARTED,
    INTRO,
    DEVELOPMENT,
    CONCLUSION,
    FINISHED
}

class SpeechTimerViewModel : ViewModel() {
    private val _totalTimeMinutes = MutableStateFlow(15)
    val totalTimeMinutes: StateFlow<Int> = _totalTimeMinutes.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow(15 * 60)
    val remainingTimeSeconds: StateFlow<Int> = _remainingTimeSeconds.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _currentPhase = MutableStateFlow(SpeechPhase.NOT_STARTED)
    val currentPhase: StateFlow<SpeechPhase> = _currentPhase.asStateFlow()

    private val _currentHint = MutableStateFlow<String?>(null)
    val currentHint: StateFlow<String?> = _currentHint.asStateFlow()

    private var timerJob: Job? = null

    private val hints = listOf(
        "Recuerda interactuar con el público",
        "Mantén contacto visual",
        "Controla tu respiración",
        "Usa pausas activas para enfatizar",
        "Cuida tu lenguaje corporal",
        "Sonríe ligeramente",
        "Varía tu tono de voz"
    )

    fun setTotalTime(minutes: Int) {
        if (!isRunning.value) {
            _totalTimeMinutes.value = minutes
            _remainingTimeSeconds.value = minutes * 60
            _currentPhase.value = SpeechPhase.NOT_STARTED
            _currentHint.value = null
        }
    }

    fun toggleTimer() {
        if (_isRunning.value) {
            pauseTimer()
        } else {
            if (_remainingTimeSeconds.value == 0) {
                resetTimer()
            }
            startTimer()
        }
    }

    private fun startTimer() {
        _isRunning.value = true
        timerJob = viewModelScope.launch {
            while (_remainingTimeSeconds.value > 0 && _isRunning.value) {
                delay(1000)
                _remainingTimeSeconds.value -= 1
                updatePhaseAndHints()
            }
            if (_remainingTimeSeconds.value == 0) {
                _isRunning.value = false
                _currentPhase.value = SpeechPhase.FINISHED
                _currentHint.value = "¡Tiempo terminado!"
            }
        }
    }

    fun pauseTimer() {
        _isRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _remainingTimeSeconds.value = _totalTimeMinutes.value * 60
        _currentPhase.value = SpeechPhase.NOT_STARTED
        _currentHint.value = null
    }

    private fun updatePhaseAndHints() {
        val totalSec = _totalTimeMinutes.value * 60
        val remainSec = _remainingTimeSeconds.value
        val elapsedSec = totalSec - remainSec

        val introDuration = 60
        val conclusionDuration = 60

        when {
            remainSec == 0 -> {
                _currentPhase.value = SpeechPhase.FINISHED
                _currentHint.value = null
            }
            remainSec <= conclusionDuration -> {
                _currentPhase.value = SpeechPhase.CONCLUSION
                if (remainSec == conclusionDuration) {
                    _currentHint.value = "Es hora de concluir tu discurso."
                } else if (remainSec % 20 == 0) {
                    _currentHint.value = "Resume los puntos clave."
                }
            }
            elapsedSec < introDuration -> {
                _currentPhase.value = SpeechPhase.INTRO
                if (elapsedSec == 0) {
                    _currentHint.value = "Inicia con fuerza."
                }
            }
            else -> {
                _currentPhase.value = SpeechPhase.DEVELOPMENT
                val timeInDev = elapsedSec - introDuration
                if (timeInDev % 60 == 0) {
                    _currentHint.value = hints.random()
                } else if (timeInDev % 60 > 15) {
                    _currentHint.value = null
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
