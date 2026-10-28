package com.example.viewmodel.english

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class EnglishGame7State(
    val isRecording: Boolean = false,
    val isAnalyzing: Boolean = false,
    val feedback: String? = null,
    val error: String? = null,
    val currentIndex: Int = 0,
    val isCompleted: Boolean = false
)

class EnglishGame7ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame7State())
    val state: StateFlow<EnglishGame7State> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    val phrases = listOf(
        "I am looking forward to seeing you.",
        "Could you please repeat that?",
        "What time is our meeting scheduled for?",
        "It was a pleasure doing business with you.",
        "I appreciate your help with this project."
    )

    fun startRecording(context: Context) {
        try {
            audioFile = File(context.cacheDir, "phrase_audio.m4a")
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            _state.update { it.copy(isRecording = true, feedback = null, error = null) }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Error al iniciar grabación: ${e.message}") }
        }
    }

    fun stopRecordingAndAnalyze() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _state.update { it.copy(isRecording = false, isAnalyzing = true) }
            analyzeAudio()
        } catch (e: Exception) {
            _state.update { it.copy(isRecording = false, error = "Error al detener grabación: ${e.message}") }
        }
    }

    private fun analyzeAudio() {
        viewModelScope.launch {
            try {
                val currentState = _state.value
                if (currentState.currentIndex >= phrases.size) return@launch
                val currentPhrase = phrases[currentState.currentIndex]
                val file = audioFile
                val fileLength = file?.takeIf { it.exists() }?.length() ?: 8000L
                delay(400)
                val responseText = AisoOfflineEngine.evaluateEnglishPhrase(currentPhrase, fileLength)
                _state.update { it.copy(isAnalyzing = false, feedback = responseText) }
            } catch (e: Exception) {
                _state.update { it.copy(isAnalyzing = false, error = "Error al analizar: ${e.message}") }
            }
        }
    }

    fun nextPhrase() {
        _state.update { 
            if (it.currentIndex + 1 < phrases.size) {
                it.copy(currentIndex = it.currentIndex + 1, feedback = null, error = null)
            } else {
                it.copy(isCompleted = true, feedback = null, error = null)
            }
        }
    }
}
