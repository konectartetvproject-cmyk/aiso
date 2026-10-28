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

data class Quote(val text: String, val author: String)

data class EnglishGame11State(
    val isRecording: Boolean = false,
    val isAnalyzing: Boolean = false,
    val feedback: String? = null,
    val error: String? = null,
    val currentIndex: Int = 0,
    val isCompleted: Boolean = false
)

class EnglishGame11ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame11State())
    val state: StateFlow<EnglishGame11State> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    val quotes = listOf(
        Quote("To be, or not to be, that is the question.", "William Shakespeare"),
        Quote("I have a dream that one day this nation will rise up.", "Martin Luther King Jr."),
        Quote("The only thing we have to fear is fear itself.", "Franklin D. Roosevelt"),
        Quote("That's one small step for man, one giant leap for mankind.", "Neil Armstrong"),
        Quote("Stay hungry, stay foolish.", "Steve Jobs")
    )

    fun startRecording(context: Context) {
        try {
            audioFile = File(context.cacheDir, "quote_audio.m4a")
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
                if (currentState.currentIndex >= quotes.size) return@launch
                val currentQuote = quotes[currentState.currentIndex].text
                val file = audioFile
                val fileLength = file?.takeIf { it.exists() }?.length() ?: 9000L
                delay(400)
                val responseText = AisoOfflineEngine.evaluateEnglishQuote(currentQuote, fileLength)
                _state.update { it.copy(isAnalyzing = false, feedback = responseText) }
            } catch (e: Exception) {
                _state.update { it.copy(isAnalyzing = false, error = "Error al analizar: ${e.message}") }
            }
        }
    }

    fun nextQuote() {
        _state.update { 
            if (it.currentIndex + 1 < quotes.size) {
                it.copy(currentIndex = it.currentIndex + 1, feedback = null, error = null)
            } else {
                it.copy(isCompleted = true, feedback = null, error = null)
            }
        }
    }
}
