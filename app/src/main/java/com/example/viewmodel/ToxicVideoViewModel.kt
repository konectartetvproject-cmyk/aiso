package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
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
import java.util.Locale

class ToxicVideoViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null

    private val _userTranscription = MutableStateFlow("")
    val userTranscription: StateFlow<String> = _userTranscription.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _toxicInterruptions = MutableStateFlow<List<String>>(emptyList())
    val toxicInterruptions: StateFlow<List<String>> = _toxicInterruptions.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private var isTtsInitialized = false
    private var accumulatedText = ""
    private var lastSentTextLength = 0

    init {
        tts = TextToSpeech(application, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("es", "ES"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                _error.value = "Lenguaje TTS no soportado"
            } else {
                isTtsInitialized = true
            }
        } else {
            _error.value = "Error al inicializar TTS"
        }
    }

    fun startPractice() {
        if (speechRecognizer == null) {
            setupSpeechRecognizer()
        }
        
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Keep listening longer
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
        }

        _userTranscription.value = ""
        accumulatedText = ""
        lastSentTextLength = 0
        _toxicInterruptions.value = emptyList()
        _error.value = null
        _isRecording.value = true
        
        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            _error.value = "Error al iniciar el micrófono: ${e.message}"
            _isRecording.value = false
        }
    }

    fun stopPractice() {
        speechRecognizer?.stopListening()
        _isRecording.value = false
        tts?.stop()
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
                    // Try to restart if still supposedly recording
                    if (_isRecording.value) {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                            }
                            speechRecognizer?.startListening(intent)
                        } catch (e: Exception) {
                            _isRecording.value = false
                        }
                    }
                }
                override fun onError(error: Int) {
                    if (_isRecording.value) {
                        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            // Just restart listening
                            startPractice()
                            return
                        }
                    }
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val newText = matches[0]
                        accumulatedText += " " + newText
                        _userTranscription.value = accumulatedText
                        checkAndTriggerInterruption(accumulatedText)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val currentPartial = matches[0]
                        _userTranscription.value = accumulatedText + " " + currentPartial
                        checkAndTriggerInterruption(accumulatedText + " " + currentPartial)
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        } else {
            _error.value = "El reconocimiento de voz no está disponible en este dispositivo."
        }
    }

    private fun checkAndTriggerInterruption(currentText: String) {
        val newWords = currentText.substring(lastSentTextLength).trim().split(" ")
        // Trigger interruption roughly every 15-20 words
        if (newWords.size > 15) {
            val textToSend = currentText.substring(lastSentTextLength).trim()
            lastSentTextLength = currentText.length
            generateToxicInterruption(textToSend)
        }
    }

    private fun generateToxicInterruption(spokenText: String) {
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _error.value = "Por favor, configura tu API Key de Gemini."
                    return@launch
                }

                val prompt = """
                    Eres un participante de la audiencia EXTREMADAMENTE tóxico, sabelotodo y molesto.
                    El orador está dando un discurso y acaba de decir esto: "$spokenText".
                    
                    INTERRÚMPELO con un comentario grosero, contradictorio o irrelevante. 
                    Sé sarcástico. NO saludes. 
                    Debe ser MUY CORTO (1 o 2 oraciones máximo), ya que estás gritando desde el público.
                    Responde en español.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (responseText != null) {
                    val currentList = _toxicInterruptions.value.toMutableList()
                    currentList.add(responseText)
                    _toxicInterruptions.value = currentList

                    if (isTtsInitialized) {
                        tts?.speak(responseText, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                }
            } catch (e: Exception) {
                // Ignore API errors to keep the flow uninterrupted, or log them
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
    }
}
