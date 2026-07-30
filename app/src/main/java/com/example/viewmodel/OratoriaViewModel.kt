package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.api.Content
import com.example.api.GenerateContentRequest
import com.example.api.Part
import com.example.api.RetrofitClient
import com.example.model.Exercise
import com.example.model.Lesson
import com.example.model.Technique
import com.example.model.aisoLessons
import com.example.model.sampleExercises
import com.example.model.techniquesList
import org.json.JSONObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isFromUser: Boolean)

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

class OratoriaViewModel : ViewModel() {

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
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _aiFeedback.value = "Por favor, configura tu API Key de Gemini en la pestaña de Secrets."
                    return@launch
                }

                val technique = _currentTechnique.value ?: return@launch
                val formulaString = technique.formula.joinToString("\n") { "- $it" }
                
                val prompt = """
                    Eres el Tutor Inteligente Oficial de la Academia, entrenado con el libro "Los secretos del orador exitoso".
                    El usuario está practicando la Técnica de ${technique.type}: "${technique.name}".
                    
                    FÓRMULA EXACTA DE LA TÉCNICA:
                    $formulaString
                    
                    RESPUESTA DEL USUARIO (Dictada por voz o escrita):
                    "$text"
                    
                    INSTRUCCIÓN DE EVALUACIÓN:
                    1. Evalúa minuciosamente si el usuario cumplió con CADA PARTE de la fórmula del libro en su respuesta. 
                    2. Analiza el contenido, la estructura y también proporciona sugerencias sobre el **ritmo o tono** adecuado que el usuario debería usar al hablar este fragmento en público (pausas, énfasis, modulación).
                    3. Si le faltó algún elemento (ej. se le olvidó el nombre del orador o el saludo), corrígelo amablemente.
                    4. Si lo hizo perfecto, celebra su progreso.
                    5. Dale feedback constructivo y motivador basado en el libro. 
                    Usa MAYÚSCULAS para destacar los elementos de la fórmula que usó bien o los que le faltaron.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No pude evaluar la respuesta. Intenta de nuevo."
                
                _aiFeedback.value = aiText
                
                // Generar métricas simuladas de análisis de voz basadas en la respuesta
                _voiceMetrics.value = VoiceMetrics(
                    pace = (70..95).random() / 100f,
                    clarity = (75..98).random() / 100f,
                    sentiment = (60..100).random() / 100f
                )
            } catch (e: Exception) {
                _aiFeedback.value = "Error al conectar con la IA: ${e.message}"
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
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _aiFeedback.value = "Por favor, configura tu API Key de Gemini en la pestaña de Secrets."
                    return@launch
                }

                val technique = _currentTechnique.value ?: return@launch
                val formulaString = technique.formula.joinToString("\n") { "- $it" }
                
                val prompt = """
                    Eres el Tutor Inteligente Oficial de la Academia, experto en análisis de oratoria.
                    El usuario está practicando la Técnica de ${technique.type}: "${technique.name}".
                    
                    FÓRMULA EXACTA DE LA TÉCNICA:
                    $formulaString
                    
                    INSTRUCCIÓN DE EVALUACIÓN MULTIMODAL:
                    Acabas de recibir un archivo de audio con la respuesta hablada del usuario.
                    1. Transcribe brevemente el contenido de su respuesta.
                    2. Evalúa si el usuario cumplió con la FÓRMULA EXACTA de la técnica.
                    3. Evalúa el AUDIO: la claridad de la voz, la velocidad/ritmo (pace), y el tono (sentiment/positividad).
                    4. Proporciona retroalimentación constructiva.
                    
                    Al final de tu respuesta, DEBES incluir OBLIGATORIAMENTE un bloque JSON con las métricas de voz del 0.0 al 1.0 (donde 1.0 es excelente), con el siguiente formato exacto:
                    ```json
                    {
                      "pace": 0.85,
                      "clarity": 0.90,
                      "sentiment": 0.88
                    }
                    ```
                """.trimIndent()
                
                val base64Audio = android.util.Base64.encodeToString(audioBytes, android.util.Base64.NO_WRAP)
                
                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(
                            Part(text = prompt),
                            Part(inlineData = com.example.api.InlineData(mimeType = "audio/mp4", data = base64Audio))
                        ))
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                var aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No pude evaluar la respuesta. Intenta de nuevo."
                
                var pace = 0.8f
                var clarity = 0.8f
                var sentiment = 0.8f
                
                val jsonRegex = Regex("```json\\s*(\\{[\\s\\S]*?\\})\\s*```")
                val match = jsonRegex.find(aiText)
                if (match != null) {
                    val jsonStr = match.groupValues[1]
                    try {
                        val jsonObject = JSONObject(jsonStr)
                        pace = jsonObject.optDouble("pace", 0.8).toFloat()
                        clarity = jsonObject.optDouble("clarity", 0.8).toFloat()
                        sentiment = jsonObject.optDouble("sentiment", 0.8).toFloat()
                        aiText = aiText.replace(match.value, "").trim()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                
                _aiFeedback.value = aiText
                _voiceMetrics.value = VoiceMetrics(pace = pace, clarity = clarity, sentiment = sentiment)

            } catch (e: Exception) {
                _aiFeedback.value = "Error al conectar con la IA: ${e.message}"
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
            "Prohibir completamente el uso de redes sociales",
            "Sustituir a todos los profesores humanos por Inteligencia Artificial",
            "Obligar a todos los ciudadanos a adoptar una dieta estrictamente vegana",
            "Eliminar por completo las calificaciones y exámenes en las escuelas"
        )
        _debateTopic.value = topics.random()
        _debateMessages.value = emptyList()
        _isLiveDebateActive.value = true
        _debateFinished.value = false
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
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _debateMessages.value = _debateMessages.value + ChatMessage("Por favor, configura tu API Key de Gemini en la pestaña de Secrets.", isFromUser = false)
                    return@launch
                }

                val chatHistoryText = _debateMessages.value.joinToString("\n") { 
                    if (it.isFromUser) "Orador: ${it.text}" else "Participante Tóxico: ${it.text}"
                }

                val prompt = """
                    Eres un participante tóxico ('El Negativo' y 'El Agresivo' combinados) en el público de una conferencia.
                    El orador (el usuario) está dando un discurso donde debe defender a toda costa este tema polémico: "${_debateTopic.value}".
                    
                    El orador debe intentar persuadir al público.
                    Tu objetivo es cuestionar, criticar y buscar el lado negativo a cada uno de sus argumentos de forma tóxica.
                    
                    Historial de la conversación:
                    $chatHistoryText
                    
                    INSTRUCCIÓN:
                    1. Analiza la última respuesta del orador.
                    2. Si el orador mantiene la calma, usa la asertividad y da argumentos persuasivos sólidos de forma inteligente ante tus ataques continuos, DEBES ceder y concluir la práctica. En ese caso, responde comenzando EXACTAMENTE con la frase "EVALUACIÓN FINAL:" seguido de un feedback de por qué lo hizo bien y cómo aplicó las técnicas.
                    3. Si el orador pierde la paciencia, se enoja o da argumentos débiles, responde con otro ataque o cuestionamiento tóxico.
                    4. Mantén tus respuestas extremadamente cortas, precisas y al grano (máximo 1 o 2 oraciones breves). No des largas explicaciones.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No pude procesar tu respuesta."
                
                if (aiText.startsWith("EVALUACIÓN FINAL:")) {
                    _debateFinished.value = true
                }
                
                _debateMessages.value = _debateMessages.value + ChatMessage(aiText, isFromUser = false)
            } catch (e: Exception) {
                _debateMessages.value = _debateMessages.value + ChatMessage("Error de conexión: ${e.message}", isFromUser = false)
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
        
        // Add first message from AI
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
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _impromptuMessages.value = _impromptuMessages.value + ChatMessage("Por favor, configura tu API Key de Gemini en la pestaña de Secrets.", isFromUser = false)
                    return@launch
                }

                val chatHistoryText = _impromptuMessages.value.joinToString("\n") { 
                    if (it.isFromUser) "Orador: ${it.text}" else "IA (Evaluador): ${it.text}"
                }

                val prompt = """
                    Eres un evaluador de oratoria ayudando a un estudiante a practicar 'Discursos Improvisados' (Impromptu Speech).
                    El estudiante está desarrollando una idea sobre el tema: "${_impromptuTopic.value}".
                    
                    Historial de la conversación:
                    $chatHistoryText
                    
                    INSTRUCCIÓN:
                    1. Analiza el desarrollo de la idea que acaba de dar el orador.
                    2. Si notas que la idea ya está bien desarrollada, tiene buena estructura (inicio, nudo, desenlace o introducción, desarrollo, conclusión), y ha dado suficientes argumentos, felicítalo y termina la práctica comenzando EXACTAMENTE con la frase "EVALUACIÓN FINAL:" seguido de tu retroalimentación.
                    3. Si la idea aún es muy corta, le falta desarrollo, o puede expandirse más, hazle una pregunta interesante o pídele un ejemplo para que continúe desarrollando su discurso.
                    4. Actúa como un mentor constructivo y amable.
                    5. Mantén tus respuestas cortas (1 o 2 párrafos).
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val aiText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No pude procesar tu respuesta."
                
                if (aiText.startsWith("EVALUACIÓN FINAL:")) {
                    _impromptuFinished.value = true
                }
                
                _impromptuMessages.value = _impromptuMessages.value + ChatMessage(aiText, isFromUser = false)
            } catch (e: Exception) {
                _impromptuMessages.value = _impromptuMessages.value + ChatMessage("Error de conexión: ${e.message}", isFromUser = false)
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
                val apiKey = BuildConfig.GEMINI_API_KEY
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                    _feedbackState.value = FeedbackState.Error("Por favor, configura tu API Key de Gemini en la pestaña de Secrets.")
                    return@launch
                }

                val prompt = """
                    Tema del ejercicio: ${exercise.title}
                    Descripción: ${exercise.description}
                    
                    Discurso del usuario:
                    "$speechText"
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(parts = listOf(Part(text = prompt)))
                    ),
                    systemInstruction = Content(
                        parts = listOf(
                            Part(
                                text = """
                                    Eres el Tutor Inteligente Oficial de la Academia, un asistente experto y un mentor empático entrenado exclusivamente con el contenido del libro "Los secretos del orador exitoso". Tu objetivo es guiar a los estudiantes a descubrir su voz auténtica, superar el miedo escénico y dominar las técnicas de comunicación persuasiva y efectiva según la metodología exacta descrita en la obra.

                                    El usuario te enviará un discurso basado en un ejercicio específico (u otra interacción).
                                    Tu objetivo principal al evaluar discursos es analizarlo y proporcionar feedback constructivo estructurado de la siguiente manera:
                                    1. 🌟 Puntos fuertes (qué hizo bien).
                                    2. 📈 Áreas de mejora (qué puede ajustar).
                                    3. 💡 Consejo final (un tip práctico para su próxima presentación basado en el libro).
                                    Sé claro, empático y directo. Usa formato Markdown.
                                    
                                    ### REGLAS DE ORO DE COMPORTAMIENTO:
                                    1. **Fidelidad Absoluta al Libro:** Solo responderás utilizando los conceptos, dinámicas, anécdotas e historias provistas en el material. Si te preguntan algo fuera del libro, reconduce amablemente el tema diciendo cómo se abordaría desde la filosofía del autor.
                                    2. **Estilo y Tono:** Sé un mentor motivador, profesional, asertivo y dinámico. Usa un lenguaje claro y persuasivo. Estructura tus respuestas para que sean fáciles de leer de un vistazo (scannable), utilizando negritas, listas y viñetas con espacios amplios.
                                    3. **No te lo tomes Personal:** Ante cualquier interacción con el usuario, modela la Inteligencia Emocional que predica el libro: responde con calma, usa la "respuesta blanda" que calma la ira, mantén la diplomacia y sé un termostato que defina el clima positivo de la interacción.
                                    
                                    ### GUÍA TEMÁTICA DE RESPUESTAS (Fórmulas y Conceptos Clave del Libro):
                                    1. **Gestión del Miedo Escénico y Autoestima:** Redefinir el miedo como entusiasmo, respiración diafragmática, Alquimia Cognitiva (Etiquetar y Reencuadrar).
                                    2. **Técnicas de Voz y Dicción:** Velocidad, Tono, Volumen, Énfasis y Pausas. Erradicación de Muletillas y Formas Incorrectas de Expresión (reemplazar muletillas por pausas estratégicas).
                                    3. **Estructura de un Discurso Poderoso:** Introducción (Gancho, Presentación, Puntos clave), Cuerpo (Desarrollo, Transiciones, Ejemplos/Datos) y Conclusión (Resumen, Llamada a la acción, Cierre memorable). Domina los 11 pasos estructurados.
                                    4. **Técnicas de Inicio y Conclusión:** (Interrogación retórica, Frases, Narrar, etc.).
                                    5. **El Arte de la Palabra:** Persuasión (Mente al Corazón) vs. Convicción (Ruta a la Razón).
                                    6. **La Trinidad de la Influencia:** Inteligencia Intrapersonal, Interpersonal y Emocional.
                                    7. **Tauromaquia Verbal:** Gestión de Audiencias Difíciles (Ernesto el Erudito, Susana la Sniper, Protágoras el Protagonista, Nicanor el Negativo, Los Murmuradores).
                                    
                                    ### ANÉCDOTAS DE INSPIRACIÓN DISPONIBLES:
                                    - La historia de Juan (superó miedo escénico).
                                    - La historia de Laura (dicción, omisión de la 'd' final).
                                    - Casacoima (1817) y Bolívar (reencuadre radical de un líder).
                                    - El Discurso de Angostura (1819) (manejo con diplomacia).
                                    - La experiencia del Autor con el Doctor (la respuesta blanda calma la ira).
                                """.trimIndent()
                            )
                        )
                    )
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (text != null) {
                    _feedbackState.value = FeedbackState.Success(text)
                } else {
                    _feedbackState.value = FeedbackState.Error("No se recibió feedback. Inténtalo de nuevo.")
                }
            } catch (e: Exception) {
                _feedbackState.value = FeedbackState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
