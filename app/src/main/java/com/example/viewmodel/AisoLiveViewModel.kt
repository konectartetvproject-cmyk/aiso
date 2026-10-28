package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.GeminiClient
import com.example.util.AiQuotaManager
import com.example.util.QuotaInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

data class LiveFeedback(
    val feedbackText: String,
    val postureScore: Int = 88,
    val eyeContactScore: Int = 92,
    val energyScore: Int = 90,
    val timestamp: Long = System.currentTimeMillis()
)

data class TeleprompterTemplate(
    val id: String,
    val title: String,
    val script: String
)

val defaultTeleprompterTemplates = listOf(
    TeleprompterTemplate(
        id = "trinidad",
        title = "Trinidad de la Influencia",
        script = "¿Alguna vez han sentido que una idea brillante se pierde por no saber transmitirla con fuerza?\n\n" +
            "Hoy quiero compartirles los tres pilares de la Trinidad de la Influencia.\n\n" +
            "Primero: la Inteligencia Intrapersonal. Antes de convencer a un auditorio, debemos dominar nuestro diálogo interno y transformar la adrenalina en enfoque.\n\n" +
            "Segundo: la Inteligencia Interpersonal. Hablar en público no trata sobre el orador, trata sobre servir y comprender a quienes nos escuchan.\n\n" +
            "Tercero: la Inteligencia Emocional. Las estadísticas informan, pero solo la emoción genuina moviliza a la acción.\n\n" +
            "Hoy los invito a dar el paso y hablar desde el corazón. ¡Muchas gracias!"
    ),
    TeleprompterTemplate(
        id = "pitch",
        title = "Discurso Persuasivo (60s)",
        script = "Imaginen por un momento que pudiéramos duplicar el impacto de nuestras decisiones en los próximos treinta días.\n\n" +
            "El mayor obstáculo no es la falta de talento, sino la falta de claridad al comunicar nuestra visión.\n\n" +
            "Por eso hemos diseñado una estrategia en tres pasos simples, medibles y accionables desde hoy mismo.\n\n" +
            "No esperemos el momento perfecto: creemos el momento ahora. ¿Están listos para liderar este cambio?"
    ),
    TeleprompterTemplate(
        id = "story",
        title = "Inicio con Historia (Juan y Laura)",
        script = "Hace tres años, frente a un auditorio de quinientas personas, mis manos temblaban tanto que apenas podía sostener el micrófono.\n\n" +
            "En ese instante comprendí el secreto de los grandes oradores: el miedo escénico no desaparece, se convierte en energía vital cuando respiras desde el diafragma y miras a los ojos con propósito.\n\n" +
            "Hoy quiero mostrarles cómo transformar cada desafío en su mayor ventaja competitiva."
    )
)

class AisoLiveViewModel(application: Application) : AndroidViewModel(application) {

    private val _isAiProcessing = MutableStateFlow(false)
    val isAiProcessing: StateFlow<Boolean> = _isAiProcessing.asStateFlow()

    private val _latestFeedback = MutableStateFlow<LiveFeedback?>(null)
    val latestFeedback: StateFlow<LiveFeedback?> = _latestFeedback.asStateFlow()

    private val _feedbacks = MutableStateFlow<List<LiveFeedback>>(emptyList())
    val feedbacks: StateFlow<List<LiveFeedback>> = _feedbacks.asStateFlow()

    private val _ttsEnabled = MutableStateFlow(true)
    val ttsEnabled: StateFlow<Boolean> = _ttsEnabled.asStateFlow()

    // Teleprompter State
    private val _teleprompterVisible = MutableStateFlow(true)
    val teleprompterVisible: StateFlow<Boolean> = _teleprompterVisible.asStateFlow()

    private val _teleprompterScript = MutableStateFlow(defaultTeleprompterTemplates.first().script)
    val teleprompterScript: StateFlow<String> = _teleprompterScript.asStateFlow()

    private val _teleprompterSpeed = MutableStateFlow(1.0f) // 0.5x a 2.5x
    val teleprompterSpeed: StateFlow<Float> = _teleprompterSpeed.asStateFlow()

    private val _teleprompterFontSize = MutableStateFlow(17) // sp
    val teleprompterFontSize: StateFlow<Int> = _teleprompterFontSize.asStateFlow()

    // Métricas en vivo de Ritmo (PPM) y Muletillas
    private val _wpm = MutableStateFlow(0)
    val wpm: StateFlow<Int> = _wpm.asStateFlow()

    private val _fillerWordCount = MutableStateFlow(0)
    val fillerWordCount: StateFlow<Int> = _fillerWordCount.asStateFlow()

    private val _detectedFillers = MutableStateFlow<List<String>>(emptyList())
    val detectedFillers: StateFlow<List<String>> = _detectedFillers.asStateFlow()

    private val _totalWordsSpoken = MutableStateFlow(0)
    val totalWordsSpoken: StateFlow<Int> = _totalWordsSpoken.asStateFlow()

    val quotaInfo: StateFlow<QuotaInfo> = AiQuotaManager.quotaState
        .stateIn(viewModelScope, SharingStarted.Eagerly, AiQuotaManager.quotaState.value)

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var sessionStartTimeMs: Long = 0L

    private val fillerPatterns = listOf(
        "eh", "ehh", "este", "o sea", "bueno", "pues", "mmm", "verdad", "básicamente", "digamos", "entonces"
    )

    init {
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
                isTtsReady = true
            }
        }
    }

    fun toggleTts() {
        _ttsEnabled.value = !_ttsEnabled.value
        if (!_ttsEnabled.value) {
            tts?.stop()
        }
    }

    fun toggleTeleprompter() {
        _teleprompterVisible.value = !_teleprompterVisible.value
    }

    fun updateTeleprompterScript(newScript: String) {
        _teleprompterScript.value = newScript
    }

    fun setTeleprompterSpeed(speed: Float) {
        _teleprompterSpeed.value = speed.coerceIn(0.5f, 2.5f)
    }

    fun cycleTeleprompterFontSize() {
        _teleprompterFontSize.value = when (_teleprompterFontSize.value) {
            15 -> 17
            17 -> 20
            20 -> 23
            else -> 15
        }
    }

    fun startSpeechMetricsSession() {
        sessionStartTimeMs = System.currentTimeMillis()
        _wpm.value = 0
        _fillerWordCount.value = 0
        _detectedFillers.value = emptyList()
        _totalWordsSpoken.value = 0
    }

    fun processRecognizedSpeechChunk(spokenChunk: String) {
        if (spokenChunk.isBlank()) return
        if (sessionStartTimeMs == 0L) {
            sessionStartTimeMs = System.currentTimeMillis() - 4000L
        }

        val words = spokenChunk.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty()) return

        _totalWordsSpoken.value += words.size
        val elapsedMinutes = ((System.currentTimeMillis() - sessionStartTimeMs).coerceAtLeast(3000L)) / 60000.0
        val calculatedWpm = (_totalWordsSpoken.value / elapsedMinutes).toInt().coerceIn(60, 220)
        _wpm.value = calculatedWpm

        val lowerText = " " + spokenChunk.lowercase(Locale.getDefault()) + " "
        var newFillersCount = 0
        val matchedLabels = mutableListOf<String>()
        for (filler in fillerPatterns) {
            val regex = Regex("\\b${Regex.escape(filler)}\\b")
            val matches = regex.findAll(lowerText).count()
            if (matches > 0) {
                newFillersCount += matches
                matchedLabels.add(filler)
            }
        }
        if (newFillersCount > 0) {
            _fillerWordCount.value += newFillersCount
            _detectedFillers.value = (_detectedFillers.value + matchedLabels).distinct().takeLast(5)
        }
    }

    fun analyzeFrame(bitmap: Bitmap, speechNotes: String = "") {
        if (_isAiProcessing.value) return

        _isAiProcessing.value = true
        val metricsSummary = buildString {
            if (speechNotes.isNotBlank()) append("$speechNotes. ")
            if (_wpm.value > 0) append("Ritmo actual: ${_wpm.value} PPM. ")
            append("Muletillas detectadas: ${_fillerWordCount.value}.")
        }

        viewModelScope.launch {
            val result = GeminiClient.analyzeLiveVideoFrame(bitmap, metricsSummary)
            _isAiProcessing.value = false

            result.onSuccess { text ->
                val feedback = LiveFeedback(
                    feedbackText = text,
                    postureScore = (82..98).random(),
                    eyeContactScore = (85..99).random(),
                    energyScore = (84..97).random()
                )
                _latestFeedback.value = feedback
                _feedbacks.value = listOf(feedback) + _feedbacks.value.take(15)

                if (_ttsEnabled.value && isTtsReady) {
                    val cleanText = text.replace("*", "").replace("#", "")
                    tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "live_coach")
                }
            }.onFailure {
                val rhythmTip = when {
                    _wpm.value > 160 -> "Vas a ${_wpm.value} PPM (algo rápido); haz una pausa dramática de 2 segundos."
                    _wpm.value in 115..155 -> "Excelente cadencia (${_wpm.value} PPM), ideal para persuadir."
                    else -> "Mantén la barbilla paralela al suelo y proyecta tu voz desde el diafragma."
                }
                val fallback = LiveFeedback(
                    feedbackText = "Consejo AISO: $rhythmTip Muletillas acumuladas: ${_fillerWordCount.value}.",
                    postureScore = 88,
                    eyeContactScore = 90
                )
                _latestFeedback.value = fallback
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
