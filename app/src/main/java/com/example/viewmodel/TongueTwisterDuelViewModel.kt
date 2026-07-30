package com.example.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.RetrofitClient
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
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _error.value = "Por favor, configura tu API Key de Gemini en la pestaña de Secrets."
                    _isAnalyzing.value = false
                    return@launch
                }

                val bytesP1 = audioFileP1!!.readBytes()
                val base64AudioP1 = Base64.encodeToString(bytesP1, Base64.NO_WRAP)
                
                val bytesP2 = audioFileP2!!.readBytes()
                val base64AudioP2 = Base64.encodeToString(bytesP2, Base64.NO_WRAP)

                val prompt = """
                    Eres un juez experto en oratoria evaluando un duelo de trabalenguas.
                    El trabalenguas es el siguiente:
                    "$twisterText"
                    
                    Te proporciono dos audios:
                    - El Audio 1 es del Jugador 1.
                    - El Audio 2 es del Jugador 2.
                    
                    Por favor, escucha ambos audios y evalúa la dicción, claridad, fluidez y entonación de cada uno.
                    Declara un ganador claro basándote en quién lo recitó mejor.
                    Dame tu respuesta estructurada:
                    1. Análisis Jugador 1 (Breve)
                    2. Análisis Jugador 2 (Breve)
                    3. Ganador y por qué (Breve)
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = prompt),
                                Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64AudioP1)),
                                Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64AudioP2))
                            )
                        )
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (text != null) {
                    _feedback.value = text
                } else {
                    _error.value = "No se recibió respuesta del análisis."
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión o análisis: ${e.message}"
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
