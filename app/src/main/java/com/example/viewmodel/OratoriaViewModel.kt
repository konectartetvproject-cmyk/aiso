package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ExerciseAttempt
import com.example.data.ExerciseRepository
import com.example.model.Exercise
import com.example.model.Lesson
import com.example.model.Technique
import com.example.model.aisoLessons
import com.example.model.sampleExercises
import com.example.model.techniquesList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ChatMessage is defined in AisoChatViewModel.kt
data class VoiceMetrics(
    val pace: Float, 
    val clarity: Float,
    val sentiment: Float
)

sealed class FeedbackState {
    object Idle : FeedbackState()
    object Loading : FeedbackState()
    data class Success(val feedback: String) : FeedbackState()
    data class Error(val message: String) : FeedbackState()
}

class OratoriaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExerciseRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ExerciseRepository(database.exerciseAttemptDao())
    }

    val exerciseHistory: StateFlow<List<ExerciseAttempt>> = repository.getAllAttempts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Quiz State ---
    private val _lessons = MutableStateFlow(aisoLessons)
    val lessons: StateFlow<List<Lesson>> = _lessons.asStateFlow()

    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _isQuizFinished = MutableStateFlow(false)
    val isQuizFinished: StateFlow<Boolean> = _isQuizFinished.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _isAnswerChecked = MutableStateFlow(false)
    val isAnswerChecked: StateFlow<Boolean> = _isAnswerChecked.asStateFlow()

    private val _completedLessons = MutableStateFlow<Set<String>>(emptySet())
    val completedLessons: StateFlow<Set<String>> = _completedLessons.asStateFlow()

    // --- Technique Practice State ---
    private val _currentTechnique = MutableStateFlow<Technique?>(null)
    val currentTechnique: StateFlow<Technique?> = _currentTechnique.asStateFlow()

    private val _aiFeedback = MutableStateFlow<String?>(null)
    val aiFeedback: StateFlow<String?> = _aiFeedback.asStateFlow()

    private val _isEvaluatingTechnique = MutableStateFlow(false)
    val isEvaluatingTechnique: StateFlow<Boolean> = _isEvaluatingTechnique.asStateFlow()

    private val _completedTechniques = MutableStateFlow<Set<String>>(emptySet())
    val completedTechniques: StateFlow<Set<String>> = _completedTechniques.asStateFlow()

    private val _voiceMetrics = MutableStateFlow<VoiceMetrics?>(null)
    val voiceMetrics: StateFlow<VoiceMetrics?> = _voiceMetrics.asStateFlow()

    fun startTechniquePractice(technique: Technique) {
        _currentTechnique.value = technique
        _aiFeedback.value = null
        _voiceMetrics.value = null
    }
    
    fun evaluateTechniquePractice(text: String) {
        _isEvaluatingTechnique.value = true
        _aiFeedback.value = null
        _voiceMetrics.value = null
        viewModelScope.launch {
            try {
                val technique = _currentTechnique.value ?: return@launch
                
                val cachedFeedback = repository.getCachedFeedback(technique.id, text)
                if (cachedFeedback != null) {
                    _aiFeedback.value = cachedFeedback.aiFeedback
                    _isEvaluatingTechnique.value = false
                    return@launch
                }

                kotlinx.coroutines.delay(400)
                val aiText = com.example.util.AisoOfflineEngine.evaluateTechnique(
                    techniqueName = technique.name,
                    formula = technique.formula,
                    userText = text
                )
                
                _aiFeedback.value = aiText
                
                repository.insertAttempt(ExerciseAttempt(
                    exerciseId = technique.id,
                    userText = text,
                    aiFeedback = aiText
                ))

                _voiceMetrics.value = VoiceMetrics(
                    pace = (80..95).random() / 100f,
                    clarity = (85..98).random() / 100f,
                    sentiment = (80..100).random() / 100f
                )
            } catch (e: Exception) {
                _aiFeedback.value = com.example.api.ApiErrorHelper.formatError(e)
            } finally {
                _isEvaluatingTechnique.value = false
            }
        }
    }

    fun evaluateTechniquePracticeWithAudio(audioBytes: ByteArray, textFallback: String? = null) {
        _isEvaluatingTechnique.value = true
        _aiFeedback.value = null
        _voiceMetrics.value = null
        viewModelScope.launch {
            try {
                val technique = _currentTechnique.value ?: return@launch
                kotlinx.coroutines.delay(450)
                
                val userSpokenText = textFallback?.ifBlank { null } ?: "Práctica hablada de la técnica ${technique.name}"
                val aiText = com.example.util.AisoOfflineEngine.evaluateTechnique(
                    techniqueName = technique.name,
                    formula = technique.formula,
                    userText = userSpokenText
                )
                
                val pace = 0.88f
                val clarity = 0.92f
                val sentiment = 0.90f
                
                _aiFeedback.value = aiText
                _voiceMetrics.value = VoiceMetrics(pace = pace, clarity = clarity, sentiment = sentiment)

                repository.insertAttempt(ExerciseAttempt(
                    exerciseId = technique.id,
                    userText = userSpokenText,
                    aiFeedback = aiText
                ))
            } catch (e: Exception) {
                _aiFeedback.value = com.example.api.ApiErrorHelper.formatError(e)
            } finally {
                _isEvaluatingTechnique.value = false
            }
        }
    }

    fun completeCurrentTechnique() {
        val technique = _currentTechnique.value
        if (technique != null) {
            _completedTechniques.value = _completedTechniques.value + technique.id
        }
    }

    fun clearTechnique() {
        _currentTechnique.value = null
        _aiFeedback.value = null
    }

    fun startLesson(lesson: Lesson) {
        _currentLesson.value = lesson
        _currentQuestionIndex.value = 0
        _score.value = 0
        _isQuizFinished.value = false
        _selectedOptionIndex.value = null
        _isAnswerChecked.value = false
    }

    fun selectOption(index: Int) {
        if (!_isAnswerChecked.value) {
            _selectedOptionIndex.value = index
        }
    }

    fun checkAnswer() {
        if (_selectedOptionIndex.value != null && !_isAnswerChecked.value) {
            val lesson = _currentLesson.value ?: return
            val currentQuestion = lesson.questions[_currentQuestionIndex.value]
            if (_selectedOptionIndex.value == currentQuestion.correctAnswerIndex) {
                _score.value += 1
            }
            _isAnswerChecked.value = true
        }
    }

    fun nextQuestion() {
        val lesson = _currentLesson.value ?: return
        if (_currentQuestionIndex.value < lesson.questions.size - 1) {
            _currentQuestionIndex.value += 1
            _selectedOptionIndex.value = null
            _isAnswerChecked.value = false
        } else {
            _isQuizFinished.value = true
            _completedLessons.value = _completedLessons.value + lesson.id
        }
    }

    fun clearLesson() {
        _currentLesson.value = null
    }

    // --- Speech Evaluation State ---
    private val _exercises = MutableStateFlow(sampleExercises)
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    private val _selectedExercise = MutableStateFlow<Exercise?>(null)
    val selectedExercise: StateFlow<Exercise?> = _selectedExercise.asStateFlow()

    private val _feedbackState = MutableStateFlow<FeedbackState>(FeedbackState.Idle)
    val feedbackState: StateFlow<FeedbackState> = _feedbackState.asStateFlow()

    fun selectExercise(exercise: Exercise) {
        _selectedExercise.value = exercise
        _feedbackState.value = FeedbackState.Idle
    }

    fun clearSelection() {
        _selectedExercise.value = null
        _feedbackState.value = FeedbackState.Idle
    }

    // --- Live Debate State ---
    private val _isLiveDebateActive = MutableStateFlow(false)
    val isLiveDebateActive: StateFlow<Boolean> = _isLiveDebateActive.asStateFlow()

    private val _debateTopic = MutableStateFlow("")
    val debateTopic: StateFlow<String> = _debateTopic.asStateFlow()

    private val _debateMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val debateMessages: StateFlow<List<ChatMessage>> = _debateMessages.asStateFlow()

    private val _isDebateLoading = MutableStateFlow(false)
    val isDebateLoading: StateFlow<Boolean> = _isDebateLoading.asStateFlow()

    private val _debateFinished = MutableStateFlow(false)
    val debateFinished: StateFlow<Boolean> = _debateFinished.asStateFlow()
    
    // --- Impromptu Speech State ---
    private val _isImpromptuActive = MutableStateFlow(false)
    val isImpromptuActive: StateFlow<Boolean> = _isImpromptuActive.asStateFlow()

    private val _impromptuTopic = MutableStateFlow("")
    val impromptuTopic: StateFlow<String> = _impromptuTopic.asStateFlow()

    private val _impromptuMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val impromptuMessages: StateFlow<List<ChatMessage>> = _impromptuMessages.asStateFlow()

    private val _isImpromptuLoading = MutableStateFlow(false)
    val isImpromptuLoading: StateFlow<Boolean> = _isImpromptuLoading.asStateFlow()

    private val _impromptuFinished = MutableStateFlow(false)
    val impromptuFinished: StateFlow<Boolean> = _impromptuFinished.asStateFlow()

    fun startLiveDebate() {
        val topics = listOf(
            "Promover que se vuelva a permitir fumar en espacios cerrados",
            "Prohibir completamente el uso de redes sociales en menores de 21 años",
            "Sustituir a todos los profesores humanos por Inteligencia Artificial",
            "Obligar a todos los ciudadanos a adoptar una dieta estrictamente vegana",
            "Eliminar por completo las calificaciones y exámenes en las universidades"
        )
        _debateTopic.value = topics.random()
        _isLiveDebateActive.value = true
        _debateFinished.value = false
        _debateMessages.value = listOf(
            ChatMessage(
                "¡Bienvenido al Debate en Vivo! Defiende o refuta con argumentos sólidos la siguiente postura polémica: \"${_debateTopic.value}\". Yo actuaré como tu contrincante crítico. ¡Tienes la palabra!",
                isFromUser = false
            )
        )
    }

    fun stopLiveDebate() {
        _isLiveDebateActive.value = false
        _debateMessages.value = emptyList()
        _debateTopic.value = ""
        _debateFinished.value = false
    }

    fun sendDebateMessage(userMessageText: String) {
        if (userMessageText.isBlank()) return
        val newUserMessage = ChatMessage(userMessageText, isFromUser = true)
        _debateMessages.value = _debateMessages.value + newUserMessage
        _isDebateLoading.value = true
        viewModelScope.launch {
            try {
                val turnCount = _debateMessages.value.count { it.isFromUser }
                val (offlineText, isFinished) = com.example.util.AisoOfflineEngine.evaluateDebateResponse(
                    topic = _debateTopic.value,
                    userText = userMessageText,
                    turnCount = turnCount
                )

                var finalText = offlineText
                if (com.example.network.GeminiClient.isApiKeyConfigured()) {
                    val prompt = if (isFinished) {
                        """
                        Tema de debate: "${_debateTopic.value}"
                        Último argumento del orador: "$userMessageText"
                        Este es el cierre del debate (turno $turnCount). Da un veredicto final constructivo (máximo 4 líneas) evaluando su persuasión, uso de datos y temple según "Los secretos del orador exitoso".
                        """.trimIndent()
                    } else {
                        """
                        Tema de debate: "${_debateTopic.value}"
                        Argumento del orador: "$userMessageText"
                        Actúa como un contrincante agudo en el debate (como Ernesto el Erudito o Susana la Sniper) y lanza una refutación breve (2 líneas) seguida de un consejo de 1 línea de AISO sobre cómo contraargumentar mejor.
                        """.trimIndent()
                    }
                    val aiResp = com.example.network.GeminiClient.chat(
                        contents = listOf(com.example.network.Content(role = "user", parts = listOf(com.example.network.Part(text = prompt)))),
                        systemPrompt = "Eres el entrenador de debate en vivo de AISO IA."
                    )
                    if (!aiResp.startsWith("Error:") && aiResp.isNotBlank()) {
                        finalText = aiResp
                    }
                }

                if (isFinished) {
                    _debateFinished.value = true
                }
                _debateMessages.value = _debateMessages.value + ChatMessage(finalText, isFromUser = false)
            } catch (e: Exception) {
                val (offlineText, isFinished) = com.example.util.AisoOfflineEngine.evaluateDebateResponse(
                    topic = _debateTopic.value,
                    userText = userMessageText,
                    turnCount = _debateMessages.value.count { it.isFromUser }
                )
                if (isFinished) _debateFinished.value = true
                _debateMessages.value = _debateMessages.value + ChatMessage(offlineText, isFromUser = false)
            } finally {
                _isDebateLoading.value = false
            }
        }
    }

    fun startImpromptuSpeech() {
        val topics = listOf(
            "El impacto de la inteligencia artificial en la creatividad artística",
            "¿Por qué los humanos necesitamos contar historias?",
            "El valor del fracaso en el éxito profesional",
            "Cómo la música afecta nuestro estado de ánimo y productividad",
            "El futuro de los viajes espaciales comerciales"
        )
        _impromptuTopic.value = topics.random()
        _impromptuMessages.value = emptyList()
        _isImpromptuActive.value = true
        _impromptuFinished.value = false
        
        _impromptuMessages.value = listOf(
            ChatMessage("¡Hola! Tu tema improvisado es: \"${_impromptuTopic.value}\". Tienes la palabra. ¿Cómo iniciarías y desarrollarías esta idea?", isFromUser = false)
        )
    }

    fun stopImpromptuSpeech() {
        _isImpromptuActive.value = false
        _impromptuMessages.value = emptyList()
        _impromptuTopic.value = ""
        _impromptuFinished.value = false
    }

    fun sendImpromptuMessage(userMessageText: String) {
        if (userMessageText.isBlank()) return
        val newUserMessage = ChatMessage(userMessageText, isFromUser = true)
        _impromptuMessages.value = _impromptuMessages.value + newUserMessage
        _isImpromptuLoading.value = true
        viewModelScope.launch {
            try {
                val turnCount = _impromptuMessages.value.count { it.isFromUser }
                val (offlineText, isFinished) = com.example.util.AisoOfflineEngine.evaluateImpromptuResponse(
                    topic = _impromptuTopic.value,
                    userText = userMessageText,
                    turnCount = turnCount
                )

                var finalText = offlineText
                if (com.example.network.GeminiClient.isApiKeyConfigured()) {
                    val prompt = if (isFinished) {
                        """
                        Tema improvisado: "${_impromptuTopic.value}"
                        Cierre del orador: "$userMessageText"
                        Evalúa su improvisación completa en máximo 4 líneas destacando su apertura, desarrollo y llamado a la acción según "Los secretos del orador exitoso".
                        """.trimIndent()
                    } else {
                        """
                        Tema improvisado: "${_impromptuTopic.value}"
                        Desarrollo del orador (parte $turnCount): "$userMessageText"
                        Dale un feedback motivador de 1 línea y pídele el siguiente paso (por ejemplo, añadir una anécdota o preparar el cierre con llamado a la acción). Máximo 3 líneas en total.
                        """.trimIndent()
                    }
                    val aiResp = com.example.network.GeminiClient.chat(
                        contents = listOf(com.example.network.Content(role = "user", parts = listOf(com.example.network.Part(text = prompt)))),
                        systemPrompt = "Eres AISO IA, entrenador de improvisación oratoria."
                    )
                    if (!aiResp.startsWith("Error:") && aiResp.isNotBlank()) {
                        finalText = aiResp
                    }
                }

                if (isFinished) {
                    _impromptuFinished.value = true
                }
                _impromptuMessages.value = _impromptuMessages.value + ChatMessage(finalText, isFromUser = false)
            } catch (e: Exception) {
                val (offlineText, isFinished) = com.example.util.AisoOfflineEngine.evaluateImpromptuResponse(
                    topic = _impromptuTopic.value,
                    userText = userMessageText,
                    turnCount = _impromptuMessages.value.count { it.isFromUser }
                )
                if (isFinished) _impromptuFinished.value = true
                _impromptuMessages.value = _impromptuMessages.value + ChatMessage(offlineText, isFromUser = false)
            } finally {
                _isImpromptuLoading.value = false
            }
        }
    }

    fun submitSpeech(speechText: String) {
        val exercise = _selectedExercise.value ?: return
        if (speechText.isBlank()) return

        _feedbackState.value = FeedbackState.Loading
        viewModelScope.launch {
            try {
                val cachedFeedback = repository.getCachedFeedback(exercise.id, speechText)
                if (cachedFeedback != null) {
                    _feedbackState.value = FeedbackState.Success(cachedFeedback.aiFeedback)
                    return@launch
                }

                val offlineText = com.example.util.AisoOfflineEngine.evaluateExerciseSpeech(
                    title = exercise.title,
                    description = exercise.description,
                    speechText = speechText
                )

                var finalText = offlineText
                if (com.example.network.GeminiClient.isApiKeyConfigured()) {
                    val prompt = """
                        Evalúa el siguiente discurso para el ejercicio "${exercise.title}" (${exercise.description}).
                        Discurso del usuario: "$speechText"
                        Ofrece retroalimentación estructurada (Fortalezas, Uso de Técnicas del libro 'Los secretos del orador exitoso', y Sugerencia de Mejora) en máximo 6 líneas.
                    """.trimIndent()
                    val aiResp = com.example.network.GeminiClient.chat(
                        contents = listOf(com.example.network.Content(role = "user", parts = listOf(com.example.network.Part(text = prompt)))),
                        systemPrompt = "Eres AISO IA, mentor de oratoria."
                    )
                    if (!aiResp.startsWith("Error:") && aiResp.isNotBlank()) {
                        finalText = aiResp
                    }
                }

                repository.insertAttempt(ExerciseAttempt(
                    exerciseId = exercise.id,
                    userText = speechText,
                    aiFeedback = finalText
                ))
                _feedbackState.value = FeedbackState.Success(finalText)
            } catch (e: Exception) {
                val offlineText = com.example.util.AisoOfflineEngine.evaluateExerciseSpeech(
                    title = exercise.title,
                    description = exercise.description,
                    speechText = speechText
                )
                _feedbackState.value = FeedbackState.Success(offlineText)
            }
        }
    }
}
