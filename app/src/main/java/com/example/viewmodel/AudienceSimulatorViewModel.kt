package com.example.viewmodel

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.GeminiClient
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class AudienceArchetypeOption(
    val id: String,
    val name: String,
    val badge: String,
    val description: String,
    val bookTactic: String
)

val audienceArchetypeOptions = listOf(
    AudienceArchetypeOption(
        id = "general",
        name = "Público General",
        badge = "EQUILIBRADO",
        description = "Audiencia interesada pero exigente con la claridad y aplicación práctica.",
        bookTactic = "Aplica el Método E.R.R.G.: Escucha, Reformula, Responde directo y Gira tu mirada a toda la sala."
    ),
    AudienceArchetypeOption(
        id = "ernesto",
        name = "Ernesto el Erudito",
        badge = "TÉCNICO",
        description = "Cuestiona fuentes, estadísticas y marcos teóricos buscando lucir su conocimiento.",
        bookTactic = "No compitas en ego técnico. Valida su dato ('Agradezco su precisión...') y lleva la respuesta al beneficio práctico."
    ),
    AudienceArchetypeOption(
        id = "susana",
        name = "Susana la Sniper",
        badge = "DESAFIANTE",
        description = "Lanza preguntas filosas y directas para intentar ponerte a la defensiva en público.",
        bookTactic = "Usa tu Inteligencia Intrapersonal: respira, quita el veneno al reformular la pregunta y responde con hechos sin confrontar."
    ),
    AudienceArchetypeOption(
        id = "nicanor",
        name = "Nicanor el Negativo",
        badge = "ESCÉPTICO",
        description = "Asegura que nada funcionará por falta de tiempo, presupuesto o cultura.",
        bookTactic = "No te contagies de su pesimismo. Reconoce el obstáculo real y demuestra un primer paso rápido y alcanzable hoy."
    ),
    AudienceArchetypeOption(
        id = "laura",
        name = "Laura la Líder",
        badge = "EJECUTIVA",
        description = "Directiva enfocada en retorno de inversión, métricas y ejecución inmediata.",
        bookTactic = "Ve directo al grano en los primeros 10 segundos con cifras, plazos concretos y acciones medibles."
    )
)

class AudienceSimulatorViewModel : ViewModel() {
    private val _topic = MutableStateFlow("")
    val topic: StateFlow<String> = _topic.asStateFlow()

    private val _selectedArchetype = MutableStateFlow(audienceArchetypeOptions.first())
    val selectedArchetype: StateFlow<AudienceArchetypeOption> = _selectedArchetype.asStateFlow()

    private val _currentQuestion = MutableStateFlow<String?>(null)
    val currentQuestion: StateFlow<String?> = _currentQuestion.asStateFlow()

    private val _isGeneratingQuestion = MutableStateFlow(false)
    val isGeneratingQuestion: StateFlow<Boolean> = _isGeneratingQuestion.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0L)
    val recordingSeconds: StateFlow<Long> = _recordingSeconds.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _feedback = MutableStateFlow<String?>(null)
    val feedback: StateFlow<String?> = _feedback.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var timerJob: Job? = null

    fun setTopic(newTopic: String) {
        _topic.value = newTopic
    }

    fun selectArchetype(option: AudienceArchetypeOption) {
        _selectedArchetype.value = option
    }

    fun generateQuestion() {
        val currentTopic = _topic.value.trim()
        if (currentTopic.isBlank()) {
            _error.value = "Por favor, ingresa o selecciona un tema para tu presentación."
            return
        }
        val archetype = _selectedArchetype.value
        _isGeneratingQuestion.value = true
        _error.value = null
        _currentQuestion.value = null
        _feedback.value = null
        viewModelScope.launch {
            try {
                val prompt = """
                    Actúa como un miembro del público con el perfil "${archetype.name}" (${archetype.description}) del libro "Los secretos del orador exitoso".
                    El orador acaba de exponer sobre el tema: "$currentTopic".
                    Formula UNA SOLA pregunta directa, realista y desafiante en español (máximo 35 palabras), en primera persona, sin introducciones ni comillas.
                """.trimIndent()
                val aiResponse = GeminiClient.chat(
                    contents = listOf(
                        com.example.network.Content(
                            role = "user",
                            parts = listOf(com.example.network.Part(text = prompt))
                        )
                    )
                )
                val question = if (aiResponse.isNotBlank() && !aiResponse.startsWith("Error") && !aiResponse.startsWith("Lo siento")) {
                    aiResponse
                } else {
                    AisoOfflineEngine.generateAudienceQuestion(currentTopic, archetype.name)
                }
                _currentQuestion.value = question.trim()
            } catch (e: Exception) {
                _currentQuestion.value = AisoOfflineEngine.generateAudienceQuestion(currentTopic, archetype.name)
            } finally {
                _isGeneratingQuestion.value = false
            }
        }
    }

    fun startRecording(context: Context) {
        _error.value = null
        _feedback.value = null
        _recordingSeconds.value = 0L
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
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_isRecording.value) {
                    delay(1000L)
                    _recordingSeconds.value += 1L
                }
            }
        } catch (e: Exception) {
            _error.value = "Error al iniciar la grabación: ${e.message}"
            _isRecording.value = false
        }
    }

    fun stopRecordingAndAnalyze() {
        timerJob?.cancel()
        val elapsedSec = _recordingSeconds.value.coerceAtLeast(5L)
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            _isRecording.value = false
            analyzeAnswer(elapsedSec)
        } catch (e: Exception) {
            mediaRecorder?.release()
            mediaRecorder = null
            _isRecording.value = false
            analyzeAnswer(elapsedSec)
        }
    }

    private fun analyzeAnswer(elapsedSec: Long) {
        val question = _currentQuestion.value ?: return
        val archetype = _selectedArchetype.value
        _isAnalyzing.value = true
        _feedback.value = null
        _error.value = null
        viewModelScope.launch {
            try {
                delay(400)
                val feedbackText = AisoOfflineEngine.evaluateAudienceAnswer(
                    topic = _topic.value.ifBlank { "Oratoria" },
                    question = question,
                    audioDurationSec = elapsedSec,
                    archetype = archetype.name
                )
                _feedback.value = feedbackText
            } catch (e: Exception) {
                _error.value = "Error al analizar respuesta: ${e.message}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        mediaRecorder?.release()
        mediaRecorder = null
    }
}
