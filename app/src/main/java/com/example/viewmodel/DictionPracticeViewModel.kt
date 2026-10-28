package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.Part
import com.example.util.AisoOfflineEngine
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

    fun updateTranscription(text: String) {
        _transcription.value = text
        _error.value = null
    }

    fun analyzeCurrentText() {
        val text = _transcription.value.trim()
        if (text.isNotBlank()) {
            analyzeTranscription(text)
        }
    }

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
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _isRecording.value = false
        if (_transcription.value.isNotBlank()) {
            analyzeTranscription(_transcription.value)
        }
    }

    private fun analyzeTranscription(text: String) {
        if (_isAnalyzing.value) return
        _isAnalyzing.value = true
        _feedback.value = null
        _error.value = null
        viewModelScope.launch {
            try {
                val offlineFeedback = AisoOfflineEngine.evaluateDiction(text)
                var finalFeedback = offlineFeedback

                if (GeminiClient.isApiKeyConfigured()) {
                    val prompt = """
                        Evalúa la dicción, articulación fonética, ritmo y claridad de esta lectura o frase pronunciada por el orador:
                        "$text"
                        Incluye consejos prácticos del libro "Los secretos del orador exitoso" (como el ejercicio del lápiz en la boca o el cuidado de las consonantes finales como hacía Laura con la 'd' final en 'ciudad' y 'libertad'). Máximo 5 líneas.
                    """.trimIndent()
                    val aiResp = GeminiClient.chat(
                        contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                        systemPrompt = "Eres AISO IA, entrenador experto en dicción y técnica vocal."
                    )
                    if (!aiResp.startsWith("Error:") && aiResp.isNotBlank()) {
                        finalFeedback = aiResp
                    }
                }

                _feedback.value = finalFeedback
            } catch (e: Exception) {
                _feedback.value = AisoOfflineEngine.evaluateDiction(text)
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
                }
                override fun onError(error: Int) {
                    _isRecording.value = false
                    if (_transcription.value.isNotBlank()) {
                        analyzeTranscription(_transcription.value)
                    } else {
                        _error.value = "No se detectó voz. Puedes intentar grabar de nuevo o elegir una frase de práctica abajo."
                    }
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        _transcription.value = matches[0]
                        analyzeTranscription(matches[0])
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
            _error.value = "El reconocimiento de voz directo no está disponible. Usa las frases de práctica o escribe tu lectura."
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
    }
}
