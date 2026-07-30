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

class AudienceSimulatorViewModel : ViewModel() {

    private val _topic = MutableStateFlow("")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _currentQuestion = MutableStateFlow<String?>(null)
    val currentQuestion: StateFlow<String?> = _currentQuestion.asStateFlow()

    private val _isGeneratingQuestion = MutableStateFlow(false)
    val isGeneratingQuestion: StateFlow<Boolean> = _isGeneratingQuestion.asStateFlow()

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

    fun setTopic(newTopic: String) {
        _topic.value = newTopic
    }

    fun generateQuestion() {
        if (_topic.value.isBlank()) {
            _error.value = "Por favor, ingresa un tema."
            return
        }

        _isGeneratingQuestion.value = true
        _error.value = null
        _currentQuestion.value = null
        _feedback.value = null

        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _error.value = "Por favor, configura tu API Key de Gemini en la pestaña de Secrets."
                    _isGeneratingQuestion.value = false
                    return@launch
                }

                val prompt = """
                    Eres un miembro de la audiencia en una conferencia sobre el tema: "${_topic.value}".
                    Genera UNA pregunta inesperada, desafiante pero realista, que le harías al orador.
                    Solo escribe la pregunta, sin introducciones ni saludos.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (text != null) {
                    _currentQuestion.value = text.trim()
                } else {
                    _error.value = "No se pudo generar la pregunta."
                }
            } catch (e: Exception) {
                _error.value = "Error al generar pregunta: ${e.message}"
            } finally {
                _isGeneratingQuestion.value = false
            }
        }
    }

    fun startRecording(context: Context) {
        _error.value = null
        _feedback.value = null
        try {
            audioFile = File(context.cacheDir, "audience_answer.mp4")
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

    fun stopRecordingAndAnalyze() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _isRecording.value = false

            if (audioFile?.exists() == true) {
                analyzeAudio(audioFile!!)
            } else {
                _error.value = "No se pudo guardar el archivo de audio."
            }
        } catch (e: Exception) {
            _error.value = "Error al detener la grabación: ${e.message}"
            _isRecording.value = false
        }
    }

    private fun analyzeAudio(file: File) {
        val question = _currentQuestion.value ?: return

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
                    Eres un experto profesor de oratoria evaluando la respuesta de un orador a una pregunta del público.
                    El tema de la conferencia era: "${_topic.value}".
                    La pregunta inesperada que se le hizo fue: "$question"
                    
                    El usuario acaba de grabar un audio con su respuesta.
                    Por favor, escucha el audio y evalúa:
                    1. La calidad de la respuesta (¿Fue coherente, evasiva, sólida?).
                    2. La dicción y fluidez (¿Se trabó, usó muletillas, habló con claridad?).
                    
                    Dame una retroalimentación estructurada y constructiva en viñetas.
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

    override fun onCleared() {
        super.onCleared()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
