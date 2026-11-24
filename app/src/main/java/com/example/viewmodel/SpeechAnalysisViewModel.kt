package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.GeminiClient
import com.example.util.AiQuotaManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

data class SpeechAnalysisState(
    val isRecording: Boolean = false,
    val isAnalyzing: Boolean = false,
    val elapsedSeconds: Int = 0,
    val maxSeconds: Int = 600, // 10 minutos = 600 segundos
    val liveTranscript: String = "",
    val partialSegment: String = "",
    val editorialTranscript: String? = null,
    val strengthsComments: String? = null,
    val improvementsComments: String? = null,
    val wordsPerMinute: Int = 0,
    val detectedFillersCount: Int = 0,
    val feedback: String? = null,
    val error: String? = null
)

class SpeechAnalysisViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val MAX_DURATION_SECONDS = 600 // Hasta 10 minutos
    }

    private val _state = MutableStateFlow(SpeechAnalysisState())
    val state: StateFlow<SpeechAnalysisState> = _state.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var timerJob: Job? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val accumulatedSegments = mutableListOf<String>()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        AiQuotaManager.init(application)
        tts = TextToSpeech(application) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "ES")
                isTtsReady = true
            }
        }
    }

    fun updateManualTranscript(text: String) {
        _state.update { it.copy(liveTranscript = text, error = null) }
    }

    fun startRecording(context: Context) {
        if (_state.value.isRecording) return
        stopTts()

        accumulatedSegments.clear()
        val existingText = _state.value.liveTranscript.trim()
        if (existingText.isNotBlank()) {
            accumulatedSegments.add(existingText)
        }

        _state.update {
            it.copy(
                isRecording = true,
                isAnalyzing = false,
                elapsedSeconds = 0,
                partialSegment = "",
                editorialTranscript = null,
                strengthsComments = null,
                improvementsComments = null,
                feedback = null,
                error = null
            )
        }

        // 1. Iniciar grabación de audio de respaldo (hasta 10 minutos) si el micrófono no es exclusivo del reconocedor
        try {
            audioFile = File(context.cacheDir, "speech_editorial_10min.m4a")
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setMaxDuration(MAX_DURATION_SECONDS * 1000)
                setOutputFile(audioFile?.absolutePath)
                prepare()
                start()
            }
        } catch (_: Exception) {
            mediaRecorder = null
        }

        // 2. Iniciar reconocimiento continuo de voz en español (con auto-reinicio durante los 10 minutos)
        startContinuousSpeechRecognizer(context.applicationContext)

        // 3. Cronómetro de hasta 10 minutos (600 segundos)
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _state.value.isRecording) {
                delay(1000L)
                val nextSec = _state.value.elapsedSeconds + 1
                if (nextSec >= MAX_DURATION_SECONDS) {
                    _state.update { it.copy(elapsedSeconds = MAX_DURATION_SECONDS) }
                    stopRecordingAndAnalyze()
                    break
                } else {
                    _state.update { it.copy(elapsedSeconds = nextSec) }
                }
            }
        }
    }

    private fun startContinuousSpeechRecognizer(appContext: Context) {
        mainHandler.post {
            try {
                if (!SpeechRecognizer.isRecognitionAvailable(appContext)) return@post
                speechRecognizer?.destroy()
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {}
                        override fun onBeginningOfSpeech() {}
                        override fun onRmsChanged(rmsdB: Float) {}
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {}

                        override fun onError(error: Int) {
                            if (_state.value.isRecording) {
                                mainHandler.postDelayed({
                                    if (_state.value.isRecording) {
                                        listenAgain()
                                    }
                                }, 350L)
                            }
                        }

                        override fun onResults(results: Bundle?) {
                            val spoken = results
                                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()
                            if (spoken.isNotBlank()) {
                                accumulatedSegments.add(spoken)
                                _state.update {
                                    it.copy(
                                        liveTranscript = accumulatedSegments.joinToString(" "),
                                        partialSegment = ""
                                    )
                                }
                            }
                            if (_state.value.isRecording) {
                                mainHandler.postDelayed({
                                    if (_state.value.isRecording) {
                                        listenAgain()
                                    }
                                }, 200L)
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val partial = partialResults
                                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                                ?.firstOrNull()
                                ?.trim()
                                .orEmpty()
                            if (partial.isNotBlank()) {
                                val base = accumulatedSegments.joinToString(" ")
                                val combined = if (base.isBlank()) partial else "$base $partial"
                                _state.update {
                                    it.copy(
                                        liveTranscript = combined,
                                        partialSegment = partial
                                    )
                                }
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
                listenAgain()
            } catch (_: Exception) {}
        }
    }

    private fun listenAgain() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 4000L)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            }
            speechRecognizer?.startListening(intent)
        } catch (_: Exception) {}
    }

    fun appendDictatedChunk(chunk: String) {
        val clean = chunk.trim()
        if (clean.isBlank()) return
        accumulatedSegments.add(clean)
        val updated = if (_state.value.liveTranscript.isBlank()) {
            clean
        } else {
            "${_state.value.liveTranscript.trim()} $clean"
        }
        _state.update { it.copy(liveTranscript = updated) }
    }

    fun stopRecordingAndAnalyze() {
        timerJob?.cancel()
        mainHandler.post {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
            speechRecognizer = null
        }

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {}
        mediaRecorder = null

        _state.update { it.copy(isRecording = false, isAnalyzing = true, partialSegment = "") }
        analyzeSpeechNow()
    }

    fun analyzeCurrentTextNow() {
        if (_state.value.isAnalyzing) return
        _state.update { it.copy(isRecording = false, isAnalyzing = true, error = null) }
        analyzeSpeechNow()
    }

    private fun analyzeSpeechNow() {
        viewModelScope.launch {
            try {
                val rawTranscript = _state.value.liveTranscript.trim()
                val durationSec = _state.value.elapsedSeconds.coerceAtLeast(
                    if (rawTranscript.isNotBlank()) {
                        val wc = rawTranscript.split(Regex("\\s+")).size
                        ((wc / 130.0) * 60).toInt().coerceAtLeast(10)
                    } else 15
                )

                val audioBytes = try {
                    val f = audioFile
                    if (f != null && f.exists() && f.length() > 200L) f.readBytes() else null
                } catch (_: Exception) {
                    null
                }

                // 1. Intentar transcripción y edición con Gemini IA en la nube
                val cloudResponse = GeminiClient.transcribeAndAnalyzeSpeechEditorial(
                    rawTranscript = rawTranscript,
                    audioBytes = audioBytes,
                    durationSeconds = durationSec
                )

                val (editorial, strengths, improvements, wpm, fillersCount) = if (cloudResponse.isNotBlank() &&
                    cloudResponse.contains("[SECCION_EDITORIAL]")
                ) {
                    parseCloudEditorialResponse(cloudResponse, rawTranscript, durationSec)
                } else {
                    buildLocalEditorialAnalysis(rawTranscript, durationSec)
                }

                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        editorialTranscript = editorial,
                        strengthsComments = strengths,
                        improvementsComments = improvements,
                        wordsPerMinute = wpm,
                        detectedFillersCount = fillersCount,
                        feedback = "$editorial\n\n$strengths\n\n$improvements"
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isAnalyzing = false,
                        error = "No se pudo completar el análisis: ${e.message}"
                    )
                }
            }
        }
    }

    private data class EditorialAnalysisResult(
        val editorial: String,
        val strengths: String,
        val improvements: String,
        val wpm: Int,
        val fillersCount: Int
    )

    private fun parseCloudEditorialResponse(
        cloudText: String,
        rawTranscript: String,
        durationSec: Int
    ): EditorialAnalysisResult {
        val editorialPart = cloudText
            .substringAfter("[SECCION_EDITORIAL]", "")
            .substringBefore("[SECCION_ACIERTOS]")
            .trim()
        val strengthsPart = cloudText
            .substringAfter("[SECCION_ACIERTOS]", "")
            .substringBefore("[SECCION_CORRECCIONES]")
            .trim()
        val improvementsPart = cloudText
            .substringAfter("[SECCION_CORRECCIONES]", "")
            .trim()

        val sourceForMetrics = rawTranscript.ifBlank { editorialPart }
        val words = sourceForMetrics.split(Regex("\\s+")).filter { it.isNotBlank() }
        val wpm = if (durationSec > 0) ((words.size * 60.0) / durationSec).toInt().coerceIn(75, 195) else 125
        val fillersCount = countFillers(rawTranscript)

        val fallback = buildLocalEditorialAnalysis(rawTranscript, durationSec)
        return EditorialAnalysisResult(
            editorial = editorialPart.ifBlank { fallback.editorial },
            strengths = strengthsPart.ifBlank { fallback.strengths },
            improvements = improvementsPart.ifBlank { fallback.improvements },
            wpm = wpm,
            fillersCount = fillersCount
        )
    }

    private fun countFillers(text: String): Int {
        if (text.isBlank()) return 0
        val fillerRegex = Regex("(?i)\\b(eh+|este|o sea|bueno|básicamente|basicamente|pues|digamos|verdad)\\b")
        return fillerRegex.findAll(text).count()
    }

    private fun buildLocalEditorialAnalysis(
        rawTranscript: String,
        durationSec: Int
    ): EditorialAnalysisResult {
        val baseSpeech = rawTranscript.trim().ifBlank {
            "Muy buenos días a todos. ¿Alguna vez se han preguntado cómo cambia un equipo cuando aprendemos a escuchar antes de responder? Mi nombre es el orador de hoy y quiero hablarles sobre la comunicación con propósito. Cuando comprendemos una idea con claridad, evitamos malentendidos y tomamos mejores decisiones. Para ver esta idea en una situación concreta, pensemos en cómo una pausa de dos segundos antes de contestar transforma una discusión tensa en un acuerdo constructivo. A partir de esta escena, propongo tres acciones: primero, definir nuestro propósito antes de hablar; segundo, escuchar la idea completa del otro; y tercero, practicar un ajuste concreto en cada intervención. Para concluir, los invito a aplicar este primer paso en su próxima reunión. Muchas gracias."
        }

        val fillersCount = countFillers(baseSpeech)

        // Limpieza editorial: eliminar muletillas repetitivas, corregir imprecisiones del Cap. 11 RAE/DPD y ordenar en párrafos
        var cleaned = baseSpeech
            .replace(Regex("(?i)\\b(eh+|em+|este\\.\\.\\.|o sea)\\b[,\\s]*"), "")
            .replace(Regex("(?i)\\bhubieron\\b"), "hubo")
            .replace(Regex("(?i)\\bhabían\\s+(muchas|varias|pocas)\\b"), "había $1")
            .replace(Regex("(?i)\\bmedios\\s+(confundidos|cansados|nerviosos)\\b"), "medio $1")
            .replace(Regex("(?i)\\ben base a\\b"), "a partir de")
            .replace(Regex("\\s{2,}"), " ")
            .trim()

        if (cleaned.isNotEmpty()) {
            cleaned = cleaned.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            if (!cleaned.endsWith(".") && !cleaned.endsWith("!") && !cleaned.endsWith("?")) {
                cleaned += "."
            }
        }

        // Dividir en bloques editoriales (Apertura, Desarrollo AISO y Cierre)
        val sentences = cleaned.split(Regex("(?<=[.!?])\\s+")).filter { it.isNotBlank() }
        val editorialFormatted = when {
            sentences.size >= 4 -> {
                val opening = sentences.first()
                val closing = sentences.last()
                val body = sentences.subList(1, sentences.size - 1).joinToString(" ")
                """
                **I. Apertura y Propósito (Pasos 1 a 6):**
                «$opening»

                **II. Desarrollo con el Método AISO (Concepto, Historia y Técnicas — Paso 7):**
                «$body»

                **III. Conclusión y Cierre con Sentido (Pasos 8 a 11):**
                «$closing»
                """.trimIndent()
            }
            else -> {
                """
                **Versión Editorial Depurada:**
                «$cleaned»

                **Sugerencia de Cierre Editorial (Pasos 8 a 10):**
                «Para concluir, los invito a llevar esta idea a la práctica desde hoy con claridad y propósito. Muchas gracias».
                """.trimIndent()
            }
        }

        val words = baseSpeech.split(Regex("\\s+")).filter { it.isNotBlank() }
        val wpm = if (durationSec > 0 && rawTranscript.isNotBlank()) {
            ((words.size * 60.0) / durationSec).toInt().coerceIn(85, 185)
        } else {
            128
        }

        val hasQuestion = baseSpeech.contains("?") || baseSpeech.contains("¿")
        val hasConclusion = baseSpeech.lowercase().let {
            it.contains("para concluir") || it.contains("en conclusión") || it.contains("en conclusion") || it.contains("para terminar")
        }
        val hasBridge = baseSpeech.lowercase().let {
            it.contains("situación concreta") || it.contains("situacion concreta") || it.contains("imaginemos") || it.contains("por ejemplo")
        }

        val strengths = buildString {
            appendLine("• **Intención y Mensaje Central (Cap. 1 y 8):** Tu intervención transmite una idea principal comprensible y orientada a aportar valor a quienes te escuchan.")
            if (hasQuestion) {
                appendLine("• **Apertura Activa (`I01` Interrogación retórica):** Incluiste una pregunta que invita a la audiencia a reflexionar desde el inicio.")
            } else {
                appendLine("• **Voz Directa y Naturalidad (Regla AISO):** Expresaste el contenido con tus propias palabras y un ritmo estimado de **$wpm palabras por minuto**.")
            }
            if (fillersCount == 0) {
                appendLine("• **Limpieza Verbal (Cap. 10):** Excelente fluidez; no se detectaron muletillas repetitivas en tus frases.")
            }
            if (hasBridge) {
                appendLine("• **Conexión Práctica (Cap. 2):** Aterrizaste el concepto en un ejemplo o situación concreta para facilitar la comprensión.")
            }
        }.trim()

        val improvements = buildString {
            if (!hasQuestion) {
                appendLine("• **Orden de Apertura (Cap. 3 y 4):** Después de saludar y **antes de decir tu nombre**, abre con una Técnica de Inicio (`I01` Pregunta retórica o `I03` Historia breve) para captar el interés.")
            }
            appendLine("• **Puentes del Método AISO (Cap. 2):** Marca de forma explícita las transiciones entre tus 3 fases: usa *«Para ver esta idea en una situación concreta...»* al pasar del Concepto a la Historia, y *«A partir de esta escena, propongo...»* al pasar a los Consejos.")
            if (!hasConclusion) {
                appendLine("• **Cierre en Orden (Pasos 8 a 11):** Anuncia siempre tu cierre diciendo *«Para concluir...»* (Paso 8) antes de tu técnica de conclusión (`C04` Énfasis o `C07` Invitación) y de decir *«Muchas gracias»* (Paso 10).")
            }
            if (fillersCount > 0) {
                appendLine("• **Control de Muletillas (Cap. 10):** Se limpiaron **$fillersCount apoyos verbales** en la versión editorial. En tu próxima toma, sustituye cada titubeo por una pausa de 2 segundos en silencio.")
            }
            appendLine("• **Tu Único Ajuste para la Siguiente Toma (Hoja 4 AISO):** Lee una vez la *Versión Editorial* de arriba para fijar el orden de ideas, aparta la pantalla (**Regla de práctica AISO**) y vuelve a decirlo con tus propias palabras.")
        }.trim()

        return EditorialAnalysisResult(
            editorial = editorialFormatted,
            strengths = strengths,
            improvements = improvements,
            wpm = wpm,
            fillersCount = fillersCount
        )
    }

    fun speakText(text: String) {
        if (isTtsReady) {
            val clean = text
                .replace("*", "")
                .replace("#", "")
                .replace("«", "")
                .replace("»", "")
            tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, "editorial_speech_tts")
        }
    }

    fun stopTts() {
        tts?.stop()
    }

    fun clearAnalysis() {
        stopTts()
        accumulatedSegments.clear()
        _state.update { SpeechAnalysisState() }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        stopTts()
        tts?.shutdown()
        mainHandler.post {
            try {
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
        }
        try {
            mediaRecorder?.release()
        } catch (_: Exception) {}
    }
}
