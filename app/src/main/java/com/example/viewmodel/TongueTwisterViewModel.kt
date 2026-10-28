package com.example.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class TongueTwisterViewModel : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    fun startRecording(context: Context) {
        _error.value = null
        _feedback.value = null
        try {
            audioFile = File(context.cacheDir, "tongue_twister.mp4")
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
            _isRecording.value = true
        } catch (e: Exception) {
            _error.value = "Error al iniciar la grabación: ${e.message}"
            _isRecording.value = false
        }
    }

    fun stopRecordingAndAnalyze(twisterText: String, instruction: String) {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _isRecording.value = false
            val file = audioFile
            if (file != null && file.exists()) {
                analyzeAudio(file, twisterText, instruction)
            } else {
                _error.value = "No se pudo guardar el archivo de audio."
            }
        } catch (e: Exception) {
            _error.value = "Error al detener la grabación: ${e.message}"
            _isRecording.value = false
        }
    }

    private fun analyzeAudio(file: File, twisterText: String, instruction: String) {
        _isAnalyzing.value = true
        _feedback.value = null
        _error.value = null
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(400)
                val feedback = com.example.util.AisoOfflineEngine.evaluateTongueTwister(twisterText, instruction, file.length())
                _feedback.value = feedback
            } catch (e: Exception) {
                _error.value = "Error al analizar trabalenguas: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
    
    fun clearFeedback() {
        _feedback.value = null
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
