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

            if (audioFile?.exists() == true) {
                analyzeAudio(audioFile!!, twisterText, instruction)
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
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _error.value = "Por favor, configura tu API Key de Gemini en la pestaña de Secrets."
                    _isAnalyzing.value = false
                    return@launch
                }

                val bytes = file.readBytes()
                val base64Audio = Base64.encodeToString(bytes, Base64.NO_WRAP)

                val prompt = """
                    Eres un experto profesor de oratoria. El usuario acaba de grabar un audio recitando este trabalenguas:
                    "$twisterText"
                    
                    La instrucción adicional era: "$instruction"
                    
                    Por favor, escucha el audio y analiza su dicción, claridad y entonación. ¿Cumplió con la instrucción?
                    Dame una retroalimentación breve (1 párrafo), directa y constructiva.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = prompt),
                                Part(inlineData = InlineData(mimeType = "audio/mp4", data = base64Audio))
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
    }

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
