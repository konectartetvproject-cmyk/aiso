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
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.Part
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

data class ToxicInterruptionItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String,
    val senderRole: String,
    val text: String,
    val counterMeasure: String
)

data class ToxicVideoState(
    val userTranscription: String = "",
    val isRecording: Boolean = false,
    val interruptions: List<ToxicInterruptionItem> = emptyList(),
    val isFrontCamera: Boolean = true,
    val audienceCalmScore: Int = 92,
    val error: String? = null
)

class ToxicVideoViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null
    private val _state = MutableStateFlow(ToxicVideoState())
    val state: StateFlow<ToxicVideoState> = _state.asStateFlow()

    val userTranscription = MutableStateFlow("")
    val isRecording = MutableStateFlow(false)
    val toxicInterruptions = MutableStateFlow<List<String>>(emptyList())
    val error = MutableStateFlow<String?>(null)

    private var isTtsInitialized = false
    private var accumulatedText = ""
    private var lastSentTextLength = 0
    private var periodicJob: Job? = null

    init {
        tts = TextToSpeech(application, this)
    }

    fun toggleCamera() {
        _state.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("es", "ES"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsInitialized = true
            }
        }
    }

    fun speakInterruption(text: String) {
        if (isTtsInitialized) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "toxic_tts")
        }
    }

    private fun createRecognizerIntent(): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 4000)
        }
    }

    private fun restartListeningWithoutReset() {
        if (!_state.value.isRecording) return
        try {
            speechRecognizer?.startListening(createRecognizerIntent())
        } catch (_: Exception) {}
    }

    fun startPractice() {
        if (speechRecognizer == null) {
            setupSpeechRecognizer()
        }

        accumulatedText = ""
        lastSentTextLength = 0
        _state.update {
            it.copy(
                userTranscription = "",
                isRecording = true,
                interruptions = emptyList(),
                error = null,
                audienceCalmScore = 92
            )
        }
        userTranscription.value = ""
        isRecording.value = true
        toxicInterruptions.value = emptyList()
        error.value = null

        try {
            speechRecognizer?.startListening(createRecognizerIntent())
        } catch (_: Exception) {
            // Keep session active even if emulator lacks microphone hardware
        }

        periodicJob?.cancel()
        periodicJob = viewModelScope.launch {
            delay(12_000L)
            while (isActive && _state.value.isRecording) {
                if (_state.value.interruptions.isEmpty()) {
                    generateToxicInterruption(accumulatedText.ifBlank { "propuesta de liderazgo y comunicación" })
                }
                delay(18_000L)
            }
        }
    }

    fun triggerManualInterruption() {
        val contextText = accumulatedText.ifBlank { "mi discurso ante la audiencia" }
        generateToxicInterruption(contextText)
    }

    fun stopPractice() {
        periodicJob?.cancel()
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {}
        _state.update { it.copy(isRecording = false) }
        isRecording.value = false
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
                    restartListeningWithoutReset()
                }
                override fun onError(error: Int) {
                    if (_state.value.isRecording) {
                        viewModelScope.launch {
                            delay(350)
                            restartListeningWithoutReset()
                        }
                    }
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val newText = matches[0]
                        accumulatedText = (accumulatedText + " " + newText).trim()
                        _state.update { it.copy(userTranscription = accumulatedText) }
                        userTranscription.value = accumulatedText
                        checkAndTriggerInterruption(accumulatedText)
                    }
                    restartListeningWithoutReset()
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val currentPartial = matches[0]
                        val full = (accumulatedText + " " + currentPartial).trim()
                        _state.update { it.copy(userTranscription = full) }
                        userTranscription.value = full
                        checkAndTriggerInterruption(full)
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun checkAndTriggerInterruption(currentText: String) {
        val remaining = if (lastSentTextLength <= currentText.length) {
            currentText.substring(lastSentTextLength).trim()
        } else {
            currentText
        }
        val newWords = remaining.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (newWords.size >= 12) {
            lastSentTextLength = currentText.length
            generateToxicInterruption(remaining)
        }
    }

    private fun generateToxicInterruption(spokenText: String) {
        viewModelScope.launch {
            try {
                var responseText = AisoOfflineEngine.generateToxicInterruption(spokenText)
                if (GeminiClient.isApiKeyConfigured()) {
                    val prompt = """
                        El orador está diciendo en vivo: "$spokenText".
                        Actúa como uno de los participantes tóxicos del libro "Los secretos del orador exitoso" (Ernesto el Erudito, Susana la Sniper o Nicanor el Negativo) y lanza UNA sola interrupción u objeción breve, incisiva y realista (máximo 20 palabras) sin comillas.
                    """.trimIndent()
                    val aiResp = GeminiClient.chat(
                        contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                        systemPrompt = "Simulador de audiencia hostil de AISO IA."
                    )
                    if (!aiResp.startsWith("Error:") && aiResp.isNotBlank()) {
                        responseText = aiResp.trim().removeSurrounding("\"")
                    }
                }

                val archetypes = listOf(
                    Triple(
                        "Ernesto el Erudito",
                        "Técnico y Analítico",
                        "Técnica del Capote: Valida su precisión técnica ('Un punto muy agudo') y reconduce al propósito práctico sin debatir."
                    ),
                    Triple(
                        "Susana la Sniper",
                        "Directa y Desafiante",
                        "Técnica del Capote: Mantén una sonrisa serena y postura abierta. No te justifiques; responde con un hecho concreto."
                    ),
                    Triple(
                        "Nicanor el Negativo",
                        "Escéptico y Cínico",
                        "Técnica del Capote: Reconoce la dificultad histórica pero enfócate en lo que sí está bajo nuestro control hoy."
                    ),
                    Triple(
                        "Protágoras",
                        "El Protagonista",
                        "Técnica del Capote: Agradece su comentario brevemente y devuelve la mirada al resto del público general."
                    )
                )

                val (sender, role, counterMeasure) = when {
                    responseText.contains("cifra", true) || responseText.contains("teoría", true) || responseText.contains("autor", true) -> archetypes[0]
                    responseText.contains("dinero", true) || responseText.contains("éxito", true) || responseText.contains("mágica", true) -> archetypes[1]
                    responseText.contains("país", true) || responseText.contains("tiempo", true) || responseText.contains("cambia", true) -> archetypes[2]
                    else -> archetypes[_state.value.interruptions.size % archetypes.size]
                }

                val item = ToxicInterruptionItem(
                    sender = sender,
                    senderRole = role,
                    text = responseText,
                    counterMeasure = counterMeasure
                )
                _state.update {
                    it.copy(
                        interruptions = it.interruptions + item,
                        audienceCalmScore = (it.audienceCalmScore - 3).coerceAtLeast(68)
                    )
                }
                toxicInterruptions.value = toxicInterruptions.value + responseText
                speakInterruption(responseText)
            } catch (_: Exception) {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        periodicJob?.cancel()
        try {
            speechRecognizer?.destroy()
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
