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

class TongueTwisterDuelViewModel : ViewModel() {
    private val _isRecordingP1 = MutableStateFlow(false)
    val isRecordingP1: StateFlow<Boolean> = _isRecordingP1.asStateFlow()

    private val _isRecordingP2 = MutableStateFlow(false)
    val isRecordingP2: StateFlow<Boolean> = _isRecordingP2.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFileP1: File? = null
    private var audioFileP2: File? = null

    val hasAudioP1 = MutableStateFlow(false)
    val hasAudioP2 = MutableStateFlow(false)

    fun startRecording(context: Context, player: Int) {
        _error.value = null
        try {
            val fileName = if (player == 1) "duel_p1.mp4" else "duel_p2.mp4"
            val file = File(context.cacheDir, fileName)
            
            if (player == 1) audioFileP1 = file else audioFileP2 = file
            
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
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            if (player == 1) _isRecordingP1.value = true else _isRecordingP2.value = true
        } catch (e: Exception) {
            _error.value = "Error al iniciar la grabación J$player: ${e.message}"
            if (player == 1) _isRecordingP1.value = false else _isRecordingP2.value = false
        }
    }

    fun stopRecording(player: Int) {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            if (player == 1) {
                _isRecordingP1.value = false
                hasAudioP1.value = audioFileP1?.exists() == true
            } else {
                _isRecordingP2.value = false
                hasAudioP2.value = audioFileP2?.exists() == true
            }
        } catch (e: Exception) {
            _error.value = "Error al detener la grabación J$player: ${e.message}"
            if (player == 1) _isRecordingP1.value = false else _isRecordingP2.value = false
        }
    }

    fun analyzeDuel(twisterText: String) {
        if (audioFileP1?.exists() != true || audioFileP2?.exists() != true) {
            _error.value = "Ambos jugadores deben grabar su audio."
            return
        }
        _isAnalyzing.value = true
        _feedback.value = null
        _error.value = null
        viewModelScope.launch {
            try {
                kotlinx.coroutines.delay(450)
                val len1 = audioFileP1?.length() ?: 0L
                val len2 = audioFileP2?.length() ?: 0L
                val feedback = com.example.util.AisoOfflineEngine.evaluateTongueTwisterDuel(twisterText, len1, len2)
                _feedback.value = feedback
            } catch (e: Exception) {
                _error.value = "Error al evaluar duelo: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
    
    fun clearFeedback() {
        _feedback.value = null
        hasAudioP1.value = false
        hasAudioP2.value = false
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
