package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DictionPracticeViewModel(application: Application) : AndroidViewModel(application) {
    private var speechRecognizer: SpeechRecognizer? = null

    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun startListening() {
        if (speechRecognizer == null) {
            setupSpeechRecognizer()
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        _transcription.value = ""
        _error.value = null
        _feedback.value = null
        _isRecording.value = true
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _error.value = "Error al iniciar el micrófono: ${e.message}"
            _isRecording.value = false
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isRecording.value = false
        if (_transcription.value.isNotBlank()) {
            analyzeTranscription(_transcription.value)
        }
    }

    private fun analyzeTranscription(text: String) {
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

                val prompt = """
                    Eres un profesor experto en oratoria. El estudiante ha realizado una práctica de dicción.
                    Esta es la transcripción de lo que dijo: "$text".
                    
                    Analiza la transcripción y bríndale consejos específicos y prácticos para mejorar:
                    1. Su dicción y pronunciación (¿Se nota alguna palabra mal articulada o repetitiva?).
                    2. Su tono y ritmo (Infiere por la estructura y longitud de las oraciones).
                    3. Su volumen y proyección (consejos generales basados en el texto).
                    
                    Por favor, dame tu retroalimentación en viñetas claras y alentadoras.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = prompt)
                            )
                        )
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (responseText != null) {
                    _feedback.value = responseText
                } else {
                    _error.value = "No se recibió respuesta del análisis."
                }
            } catch (e: Exception) {
                _error.value = "Error de análisis: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    private fun setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    _isRecording.value = false
                    if (_transcription.value.isNotBlank()) {
                        analyzeTranscription(_transcription.value)
                    }
                }
                override fun onError(error: Int) {
                    _isRecording.value = false
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                        SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                        SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Tiempo de espera de red agotado"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No se reconoció el habla"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
                        SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de espera agotado"
                        else -> "Error desconocido"
                    }
                    _error.value = errorMessage
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        _transcription.value = matches[0]
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        _transcription.value = matches[0]
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } else {
            _error.value = "El reconocimiento de voz no está disponible en este dispositivo."
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
    }
}
