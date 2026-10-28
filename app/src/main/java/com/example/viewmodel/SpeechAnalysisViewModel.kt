package com.example.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class SpeechAnalysisState(
    val isRecording: Boolean = false,
    val isAnalyzing: Boolean = false,
    val feedback: String? = null,
    val error: String? = null
)

class SpeechAnalysisViewModel : ViewModel() {
    private val _state = MutableStateFlow(SpeechAnalysisState())
    val state: StateFlow<SpeechAnalysisState> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    fun startRecording(context: Context) {
        try {
            audioFile = File(context.cacheDir, "speech_analysis_audio.m4a")
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
                val file = audioFile
                if (file == null || !file.exists()) {
                    _state.update { it.copy(isAnalyzing = false, error = "No se encontró el archivo de audio.") }
                    return@launch
                }
                kotlinx.coroutines.delay(500)
                val durationSec = (file.length() / 8000).coerceIn(15, 180)
                val feedback = com.example.util.AisoOfflineEngine.evaluateSpeechAudio(durationSec)
                _state.update { it.copy(isAnalyzing = false, feedback = feedback) }
            } catch (e: Exception) {
                _state.update { it.copy(isAnalyzing = false, error = "Error al analizar el audio: ${e.message}") }
            }
        }
    }
}
