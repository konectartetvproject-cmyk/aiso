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

data class EnglishGame5State(
    val isRecording: Boolean = false,
    val isAnalyzing: Boolean = false,
    val feedback: String? = null,
    val error: String? = null
)

class EnglishGame5ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame5State())
    val state: StateFlow<EnglishGame5State> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    val songTitle = "Take Me Out to the Ball Game"
    val songLyrics = """
        Take me out to the ball game,
        Take me out with the crowd;
        Buy me some peanuts and Cracker Jack,
        I don't care if I never get back.
        Let me root, root, root for the home team,
        If they don't win, it's a shame.
        For it's one, two, three strikes, you're out,
        At the old ball game.
    """.trimIndent()

    fun startRecording(context: Context) {
        try {
            audioFile = File(context.cacheDir, "pronunciation_audio.m4a")
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
                val fileLength = file?.takeIf { it.exists() }?.length() ?: 12000L
                delay(400)
                val responseText = AisoOfflineEngine.evaluateEnglishSong(songTitle, songLyrics, fileLength)
                _state.update { it.copy(isAnalyzing = false, feedback = responseText) }
            } catch (e: Exception) {
                _state.update { it.copy(isAnalyzing = false, error = "Error al analizar: ${e.message}") }
            }
        }
    }
}
