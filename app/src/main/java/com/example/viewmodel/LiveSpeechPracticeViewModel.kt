package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.util.AisoOfflineEngine
import com.example.util.LiveSpeechAnalyzer
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class LiveSpeechState(
    val isRecording: Boolean = false,
    val isAnalyzing: Boolean = false,
    val currentTip: String = "Preparado. Pulsa el micrófono para iniciar tu discurso...",
    val tipCategory: String = "ENFOQUE",
    val allTips: List<String> = emptyList(),
    val globalRecommendation: String? = null,
    val fullTranscript: String = "",
    val elapsedSeconds: Int = 0,
    val wordCount: Int = 0,
    val paceWpm: Int = 0,
    val fillerCount: Int = 0,
    val detectedFillers: List<String> = emptyList(),
    val isFrontCamera: Boolean = true,
    val showGuideOverlay: Boolean = true,
    val isTorchOn: Boolean = false,
    val isTtsEnabled: Boolean = false,
    val audioRmsDb: Float = 0f,
    val activeStepName: String = "Paso 1: Entrada y Presencia",
    val showSummaryDialog: Boolean = false,
    val speechScore: Int = 92
)

class LiveSpeechPracticeViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val _state = MutableStateFlow(LiveSpeechState())
    val state: StateFlow<LiveSpeechState> = _state.asStateFlow()

    private var frameCount = 0
    private var lastAnalyzedTextIndex = 0
    private var timerJob: Job? = null

    private var committedTranscript = ""
    private var currentHypothesis = ""
    private var currentSteps = LiveSpeechAnalyzer.initialSpeechSteps

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val commonFillers = listOf(
        "eh", "ehh", "este", "bueno", "o sea", "verdad", "básicamente", "como tal", "nada", "entonces", "digamos", "tipo"
    )

    init {
        try {
            tts = TextToSpeech(application, this)
        } catch (_: Exception) {}
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                tts?.language = Locale("es", "ES")
                tts?.setSpeechRate(1.05f)
                tts?.setPitch(1.0f)
                isTtsInitialized = true
            } catch (_: Exception) {}
        }
    }

    fun toggleCamera() {
        _state.update { it.copy(isFrontCamera = !it.isFrontCamera, isTorchOn = false) }
    }

    fun toggleGuideOverlay() {
        _state.update { it.copy(showGuideOverlay = !it.showGuideOverlay) }
    }

    fun toggleTorch() {
        _state.update { it.copy(isTorchOn = !it.isTorchOn) }
    }

    fun toggleTts() {
        _state.update { 
            val newTtsState = !it.isTtsEnabled
            if (!newTtsState) {
                tts?.stop()
            }
            it.copy(isTtsEnabled = newTtsState) 
        }
    }

    fun dismissSummaryDialog() {
        _state.update { it.copy(showSummaryDialog = false) }
    }

    fun onRmsChanged(rmsdB: Float) {
        val normalized = rmsdB.coerceIn(0f, 10f)
        _state.update { it.copy(audioRmsDb = normalized) }
    }

    fun toggleRecording() {
        if (_state.value.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        frameCount = 0
        lastAnalyzedTextIndex = 0
        committedTranscript = ""
        currentHypothesis = ""
        currentSteps = LiveSpeechAnalyzer.initialSpeechSteps
        _state.update { 
            it.copy(
                isRecording = true, 
                currentTip = "Hablando... mantén los hombros relajados y proyecta tu voz hacia la audiencia.",
                tipCategory = "PRESENCIA",
                allTips = emptyList(), 
                globalRecommendation = null,
                fullTranscript = "",
                elapsedSeconds = 0,
                wordCount = 0,
                paceWpm = 0,
                fillerCount = 0,
                detectedFillers = emptyList(),
                activeStepName = "Paso 1: Entrada y Presencia",
                showSummaryDialog = false
            ) 
        }
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _state.update { curr ->
                    if (!curr.isRecording) return@update curr
                    val newSec = curr.elapsedSeconds + 1
                    val wpm = if (newSec > 3 && curr.wordCount > 0) {
                        ((curr.wordCount.toFloat() / newSec) * 60).toInt().coerceAtMost(300)
                    } else curr.paceWpm
                    curr.copy(elapsedSeconds = newSec, paceWpm = wpm)
                }
            }
        }
    }

    fun onPartialSpeech(partialText: String) {
        if (partialText.isBlank() || !_state.value.isRecording) return
        currentHypothesis = partialText.trim()
        val combined = if (committedTranscript.isEmpty()) currentHypothesis else "$committedTranscript $currentHypothesis"
        val words = combined.split(Regex("\\s+")).filter { it.isNotBlank() }
        val count = words.size
        val wpm = if (_state.value.elapsedSeconds > 3 && count > 0) {
            ((count.toFloat() / _state.value.elapsedSeconds) * 60).toInt().coerceAtMost(300)
        } else _state.value.paceWpm
        _state.update { curr ->
            curr.copy(
                fullTranscript = combined,
                wordCount = count,
                paceWpm = wpm
            )
        }
    }

    fun onFinalSpeech(finalText: String) {
        if (finalText.isBlank() || !_state.value.isRecording) return
        val cleanFinal = finalText.trim()
        committedTranscript = if (committedTranscript.isEmpty()) cleanFinal else "$committedTranscript $cleanFinal"
        currentHypothesis = ""
        val words = committedTranscript.split(Regex("\\s+")).filter { it.isNotBlank() }
        val count = words.size
        val lower = committedTranscript.lowercase(Locale.getDefault())
        val foundFillers = commonFillers.filter { filler ->
            lower.contains(Regex("\\b${Regex.escape(filler)}\\b"))
        }
        val wpm = if (_state.value.elapsedSeconds > 3 && count > 0) {
            ((count.toFloat() / _state.value.elapsedSeconds) * 60).toInt().coerceAtMost(300)
        } else _state.value.paceWpm

        val (matchedStep, nextStep) = LiveSpeechAnalyzer.detectStepFromText(committedTranscript, currentSteps)
        val stepTitle = matchedStep?.title ?: nextStep?.title ?: _state.value.activeStepName

        _state.update { curr ->
            curr.copy(
                fullTranscript = committedTranscript,
                wordCount = count,
                paceWpm = wpm,
                fillerCount = foundFillers.size,
                detectedFillers = foundFillers,
                activeStepName = stepTitle
            )
        }
    }

    fun onNewText(text: String) {
        onFinalSpeech(text)
    }

    fun analyzeFrame(bitmap: Bitmap) {
        val currentState = _state.value
        if (!currentState.isRecording || currentState.isAnalyzing) return
        val transcriptToAnalyze = currentState.fullTranscript.substring(lastAnalyzedTextIndex).trim()
        if (transcriptToAnalyze.isEmpty()) return
        lastAnalyzedTextIndex = currentState.fullTranscript.length
        frameCount++
        
        _state.update { it.copy(isAnalyzing = true) }
        viewModelScope.launch {
            try {
                delay(150)
                val tip = AisoOfflineEngine.analyzeLiveSpeechFrame(transcriptToAnalyze, frameCount)
                val category = when {
                    tip.contains("Columna", ignoreCase = true) || tip.contains("Postura", ignoreCase = true) -> "POSTURA"
                    tip.contains("Zona de Influencia", ignoreCase = true) || tip.contains("manos", ignoreCase = true) -> "GESTOS"
                    tip.contains("mirada", ignoreCase = true) || tip.contains("contacto", ignoreCase = true) -> "MIRADA"
                    tip.contains("muletilla", ignoreCase = true) -> "MULETILLAS"
                    tip.contains("diafragma", ignoreCase = true) || tip.contains("ritmo", ignoreCase = true) -> "VOZ"
                    else -> "ORATORIA"
                }
                _state.update { 
                    it.copy(
                        currentTip = tip,
                        tipCategory = category,
                        allTips = it.allTips + tip,
                        isAnalyzing = false
                    ) 
                }
                if (_state.value.isTtsEnabled && isTtsInitialized) {
                    val spokenTip = when {
                        tip.contains("muletilla", ignoreCase = true) -> "Pausa de silencio en vez de muletilla."
                        tip.contains("Columna", ignoreCase = true) -> "Alinea tu columna y barbilla."
                        tip.contains("manos", ignoreCase = true) -> "Abre las manos en el plexo solar."
                        tip.contains("mirada", ignoreCase = true) -> "Mantén la mirada en el público."
                        else -> tip
                    }
                    tts?.speak(spokenTip, TextToSpeech.QUEUE_FLUSH, null, "LIVE_TIP")
                }
            } catch (e: Exception) {
                _state.update { it.copy(isAnalyzing = false) }
            }
        }
    }

    private fun stopRecording() {
        timerJob?.cancel()
        tts?.stop()
        val curr = _state.value
        val baseScore = 92
        val pacePenalty = when {
            curr.paceWpm in 115..155 -> 0
            curr.paceWpm in 90..180 -> 4
            else -> 8
        }
        val fillerPenalty = (curr.fillerCount * 3).coerceAtMost(15)
        val calculatedScore = (baseScore - pacePenalty - fillerPenalty).coerceIn(70, 98)
        _state.update { 
            it.copy(
                isRecording = false, 
                currentTip = "Discurso completado con éxito.",
                speechScore = calculatedScore,
                showSummaryDialog = true
            ) 
        }
        generateGlobalRecommendation()
    }

    private fun generateGlobalRecommendation() {
        val transcript = _state.value.fullTranscript
        if (transcript.trim().isEmpty()) {
            _state.update { it.copy(globalRecommendation = "No se detectó discurso grabado.") }
            return
        }
        viewModelScope.launch {
            try {
                delay(300)
                val recommendation = AisoOfflineEngine.generateLiveSpeechRecommendation(transcript)
                _state.update { it.copy(globalRecommendation = recommendation) }
            } catch (e: Exception) {
                _state.update { it.copy(globalRecommendation = "Error al generar informe.") }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
    }
}
