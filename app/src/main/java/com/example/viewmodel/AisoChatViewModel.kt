package com.example.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AisoKnowledgeBase
import com.example.data.AisoSpecialistMode
import com.example.data.DeepDiveAction
import com.example.data.SpeakerProfile
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.InlineData
import com.example.network.Part
import com.example.util.AiQuotaManager
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val id: String = UUID.randomUUID().toString(),
    val mediaUri: Uri? = null,
    val mediaType: String? = null,
    val mediaName: String? = null,
    val topicContext: String = "",
    val modeBadge: String = "",
    val suggestedFollowUps: List<String> = emptyList()
) {
    val isUser: Boolean
        get() = isFromUser
}

class AisoChatViewModel : ViewModel() {
    private val _speakerProfile = MutableStateFlow(SpeakerProfile())
    val speakerProfile: StateFlow<SpeakerProfile> = _speakerProfile.asStateFlow()

    private val _specialistMode = MutableStateFlow(AisoSpecialistMode.MENTOR_360)
    val specialistMode: StateFlow<AisoSpecialistMode> = _specialistMode.asStateFlow()

    private val _messages = MutableStateFlow(
        listOf(
            ChatMessage(
                text = AisoKnowledgeBase.getWelcomeMessage(),
                isFromUser = false,
                topicContext = "el Método AISO y los 11 Pasos",
                modeBadge = AisoSpecialistMode.MENTOR_360.shortLabel,
                suggestedFollowUps = listOf(
                    "Explícame en breve los 11 pasos AISO",
                    "Hazme un discurso corto con el Método AISO",
                    "¿Cómo vencer los nervios y no quedarme en blanco?"
                )
            )
        )
    )
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateSpeakerProfile(newProfile: SpeakerProfile) {
        val oldProfile = _speakerProfile.value
        _speakerProfile.value = newProfile

        if (oldProfile != newProfile) {
            val summaryParts = mutableListOf<String>()
            if (newProfile.userName.isNotBlank()) summaryParts.add("Orador: **${newProfile.userName}**")
            summaryParts.add("Escenario: **${newProfile.scenario}**")
            summaryParts.add("Nivel: **${newProfile.level}**")
            if (newProfile.currentTopic.isNotBlank()) summaryParts.add("Tema activo: **«${newProfile.currentTopic}»**")

            val topicForSuggestions = newProfile.currentTopic.ifBlank { "mi próxima presentación" }
            _messages.value = _messages.value + ChatMessage(
                text = "✅ **Perfil AISO actualizado**\n${summaryParts.joinToString(" · ")}\n\nA partir de ahora adaptaré cada guion, ejemplo y consejo a tu contexto.",
                isFromUser = false,
                topicContext = topicForSuggestions,
                modeBadge = _specialistMode.value.shortLabel,
                suggestedFollowUps = listOf(
                    "Hazme un discurso breve con los 11 pasos sobre $topicForSuggestions",
                    "Dame 3 inicios de impacto (I01, I03, I08) para $topicForSuggestions",
                    "¿Qué preguntas difíciles podrían hacerme en ${newProfile.scenario}?"
                )
            )
        }
    }

    fun selectSpecialistMode(mode: AisoSpecialistMode) {
        if (_specialistMode.value == mode) return
        _specialistMode.value = mode
        val topic = _speakerProfile.value.currentTopic.ifBlank { "liderazgo y comunicación" }

        val modeFollowUps = when (mode) {
            AisoSpecialistMode.MENTOR_360 -> listOf(
                "Explícame en breve los 11 pasos AISO",
                "¿Cuáles son las 9 herramientas del Orador Consciente?",
                "Cómo usar el Método AISO para hablar sin leer"
            )
            AisoSpecialistMode.SPEECH_ARCHITECT -> listOf(
                "Crea un discurso breve con los 11 pasos sobre $topic",
                "Dame 3 aperturas distintas (I01, I03, I08) sobre $topic",
                "Diseña un cierre memorable (C07 + C09) sobre $topic"
            )
            AisoSpecialistMode.SCRIPT_AUDITOR -> listOf(
                "Evalúa con la Rúbrica AISO: Buenos días, hoy quiero proponerles...",
                "¿Qué evalúa la escala 0 / 1 / 2 de la Rúbrica AISO?",
                "¿Cuáles son los errores de precisión léxica RAE (Cap. 11)?"
            )
            AisoSpecialistMode.OBJECTION_SPARRING -> listOf(
                "Lánzame una objeción difícil sobre $topic para responder con A.V.R.",
                "¿Cómo manejar a quien interrumpe o dice «eso nunca funciona»?",
                "Explícame el Árbol de decisión A / B / C (Cap. 13)"
            )
            AisoSpecialistMode.NERVES_COACH -> listOf(
                "Dame la rutina de 2 minutos antes de subir a hablar",
                "¿Qué hacer exactamente si me quedo en blanco?",
                "Cómo aplicar el Termostato Emocional (IE05) bajo presión"
            )
        }

        _messages.value = _messages.value + ChatMessage(
            text = "${mode.shortLabel} **Modo activado: ${mode.title}**\n*${mode.subtitle}*.\n\n¿Qué quieres trabajar ahora mismo?",
            isFromUser = false,
            topicContext = topic,
            modeBadge = mode.shortLabel,
            suggestedFollowUps = modeFollowUps
        )
    }

    fun triggerDeepDive(sourceMessage: ChatMessage, action: DeepDiveAction) {
        if (_isLoading.value) return
        val profile = _speakerProfile.value
        val activeTopic = when {
            profile.currentTopic.isNotBlank() && action == DeepDiveAction.ADAPT_TO_MY_TOPIC -> profile.currentTopic
            sourceMessage.topicContext.isNotBlank() -> sourceMessage.topicContext
            profile.currentTopic.isNotBlank() -> profile.currentTopic
            else -> "el Método AISO"
        }

        val syntheticQuery = when (action) {
            DeepDiveAction.STEP_BY_STEP -> "${action.promptPrefix} $activeTopic"
            DeepDiveAction.REAL_EXAMPLE -> "${action.promptPrefix} $activeTopic"
            DeepDiveAction.PRACTICAL_EXERCISE -> "${action.promptPrefix} $activeTopic"
            DeepDiveAction.ADAPT_TO_MY_TOPIC -> "${action.promptPrefix} $activeTopic en el escenario ${profile.scenario}"
        }

        sendMessage(
            userText = syntheticQuery,
            displayLabelOverride = "${action.label}: «$activeTopic»"
        )
    }

    private fun buildDynamicSystemInstruction(
        profile: SpeakerProfile,
        mode: AisoSpecialistMode,
        isDeepDiveRequest: Boolean
    ): String {
        val wordLimitRule = if (isDeepDiveRequest) {
            "2. EXTENSIÓN DE PROFUNDIZACIÓN (MÁXIMO 130 PALABRAS): Como el usuario pidió profundizar, entrega pasos concretos o el ejemplo exacto en 3 viñetas muy claras y escaneables, sin rodeos."
        } else {
            "2. BREVEDAD OBLIGATORIA (MÁXIMO 85 PALABRAS): Tus respuestas iniciales DEBEN ser cortas, directas, potentes y fáciles de leer en el celular (máximo 2 o 3 viñetas breves). NUNCA envíes bloques largos de texto; el usuario tiene botones debajo para profundizar si lo desea."
        }

        return """
Eres el Tutor Oficial de AISO IA, mentor experto basado estrictamente en la edición verificada v07 del libro «Los secretos del orador exitoso: Vence tus miedos, habla desde el corazón» de Carlos Enrique Lopez Adames.

PERFIL PERSONALIZADO DEL USUARIO ACTUAL:
${profile.toPromptContext()}

MODO ESPECIALISTA ACTIVO:
${mode.title} — ${mode.promptDirective}

REGLAS DE ORO DE FORMATO Y LONGITUD:
1. FIDELIDAD TOTAL A LA EDICIÓN v07: Usa los códigos y conceptos exactos del libro (`I01–I10`, `C01–C09`, `CONCEPTO → HISTORIA → TÉCNICAS`, los `11 Pasos`, `A.V.R.`, `IP01–IE06`, `T01–T60`, Rúbrica `0/1/2` y «Dos tomas, un ajuste»).
$wordLimitRule
3. PERSONALIZACIÓN REAL: Dirígete al usuario por su nombre si lo configuró, y adapta los ejemplos a su escenario (${profile.scenario}) y a su tema actual (${profile.currentTopic.ifBlank { "el tema de su consulta" }}).
4. REGLA DE HABLAR SIN LEER: Recuerda que los guiones sirven para ordenar la mente, pero al exponer se aparta el papel.
5. OPCIONES PARA PROFUNDIZAR: Al final de tu respuesta, agrega SIEMPRE una línea exacta con el marcador `[SUGERENCIAS]` seguida de 3 opciones cortas (separadas por `|`) para que el usuario elija en qué profundizar. Ejemplo:
[SUGERENCIAS] Ver ejemplo real palabra por palabra | Aplicar a mi escenario de ${profile.scenario} | Practicar objeciones con A.V.R.

${AisoKnowledgeBase.BOOK_V07_MASTER_CONTEXT}
""".trimIndent()
    }

    fun sendMessage(
        userText: String,
        context: Context? = null,
        mediaUri: Uri? = null,
        mediaType: String? = null,
        mediaName: String? = null,
        displayLabelOverride: String? = null
    ) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty() && mediaUri == null) return

        val profile = _speakerProfile.value
        val mode = _specialistMode.value

        val userMsg = ChatMessage(
            text = displayLabelOverride ?: if (trimmed.isNotEmpty()) trimmed else "Analiza este archivo multimedia de mi práctica de oratoria.",
            isFromUser = true,
            mediaUri = mediaUri,
            mediaType = mediaType,
            mediaName = mediaName
        )
        val previousMessages = _messages.value
        _messages.value = previousMessages + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            try {
                if (mediaUri != null && context != null) {
                    val mediaAnalysis = analyzeMediaWithGeminiOrFallback(context, mediaUri, mediaType, trimmed, profile, mode)
                    val (cleanText, parsedFollowUps) = parseFollowUpsFromReply(
                        rawReply = mediaAnalysis,
                        fallbackQuery = trimmed.ifEmpty { "evaluar mi práctica" },
                        profile = profile,
                        mode = mode
                    )
                    _messages.value = _messages.value + ChatMessage(
                        text = cleanText,
                        isFromUser = false,
                        topicContext = profile.currentTopic.ifBlank { "mi práctica en audio/video" },
                        modeBadge = mode.shortLabel,
                        suggestedFollowUps = parsedFollowUps
                    )
                } else {
                    val isDeepDive = displayLabelOverride != null ||
                        trimmed.startsWith("Profundiza", ignoreCase = true) ||
                        trimmed.startsWith("Dame un ejemplo real", ignoreCase = true) ||
                        trimmed.startsWith("Dame un ejercicio", ignoreCase = true)

                    val contents = previousMessages.takeLast(8).map { msg ->
                        Content(
                            role = if (msg.isUser) "user" else "model",
                            parts = listOf(Part(text = msg.text))
                        )
                    } + Content(
                        role = "user",
                        parts = listOf(Part(text = trimmed))
                    )

                    val systemInstruction = buildDynamicSystemInstruction(profile, mode, isDeepDive)
                    val cloudReply = if (AiQuotaManager.canMakeRequest()) {
                        withContext(Dispatchers.IO) {
                            GeminiClient.chat(
                                contents = contents,
                                systemPrompt = systemInstruction
                            )
                        }
                    } else {
                        ""
                    }

                    val (cleanText, parsedFollowUps) = if (cloudReply.isNotBlank()) {
                        AiQuotaManager.recordTextUsage(systemInstruction + "\n" + trimmed, cloudReply)
                        parseFollowUpsFromReply(cloudReply, trimmed, profile, mode)
                    } else {
                        AisoKnowledgeBase.generatePersonalizedSmartResponse(trimmed, profile, mode)
                    }

                    val inferredTopic = inferTopicFromQuery(trimmed, profile.currentTopic)
                    _messages.value = _messages.value + ChatMessage(
                        text = cleanText,
                        isFromUser = false,
                        topicContext = inferredTopic,
                        modeBadge = mode.shortLabel,
                        suggestedFollowUps = parsedFollowUps
                    )
                }
            } catch (e: Exception) {
                val (smartFallback, followUps) = AisoKnowledgeBase.generatePersonalizedSmartResponse(trimmed, profile, mode)
                _messages.value = _messages.value + ChatMessage(
                    text = smartFallback,
                    isFromUser = false,
                    topicContext = inferTopicFromQuery(trimmed, profile.currentTopic),
                    modeBadge = mode.shortLabel,
                    suggestedFollowUps = followUps
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun inferTopicFromQuery(query: String, profileTopic: String): String {
        val cleaned = query
            .replace(Regex("(?i)^(profundiza.*?sobre|dame un ejemplo.*?sobre|dame un ejercicio.*?entrenar|adapta.*?sobre|hazme un discurso.*?sobre|explicame|como usar|cuales son)\\s*"), "")
            .trim()
            .removeSuffix("?")
            .removePrefix("¿")
            .trim()
        return when {
            cleaned.length in 4..65 -> cleaned
            profileTopic.isNotBlank() -> profileTopic
            else -> "el Método AISO"
        }
    }

    private fun parseFollowUpsFromReply(
        rawReply: String,
        fallbackQuery: String,
        profile: SpeakerProfile,
        mode: AisoSpecialistMode
    ): Pair<String, List<String>> {
        val marker = "[SUGERENCIAS]"
        if (rawReply.contains(marker)) {
            val parts = rawReply.split(marker, limit = 2)
            val mainText = parts[0].trim()
            val rawSuggestions = parts.getOrNull(1)
                ?.split("|")
                ?.map { it.trim().removePrefix("-").removePrefix("•").trim() }
                ?.filter { it.length in 5..95 }
                ?.take(3)
                .orEmpty()

            if (rawSuggestions.isNotEmpty()) {
                return Pair(mainText, rawSuggestions)
            }
            return Pair(mainText, getDefaultFollowUpsFor(fallbackQuery, profile, mode))
        }
        return Pair(rawReply.trim(), getDefaultFollowUpsFor(fallbackQuery, profile, mode))
    }

    private fun getDefaultFollowUpsFor(
        query: String,
        profile: SpeakerProfile,
        mode: AisoSpecialistMode
    ): List<String> {
        val (_, followUps) = AisoKnowledgeBase.generatePersonalizedSmartResponse(query, profile, mode)
        return followUps.ifEmpty {
            listOf(
                "Hazme un discurso breve con el Método AISO",
                "Ver las 10 técnicas de inicio (I01–I10)",
                "¿Cómo responder preguntas difíciles con A.V.R.?"
            )
        }
    }

    private suspend fun analyzeMediaWithGeminiOrFallback(
        context: Context,
        uri: Uri,
        mimeType: String?,
        userPrompt: String,
        profile: SpeakerProfile,
        mode: AisoSpecialistMode
    ): String = withContext(Dispatchers.IO) {
        if (!AiQuotaManager.canMakeRequest()) {
            return@withContext AisoKnowledgeBase.getMentorMediaReview(mimeType)
        }

        val prompt = buildString {
            append("Evalúa este archivo con la Rúbrica AISO (0/1/2) y la Revisión en dos pasadas (Cap. 12). ")
            append("Sé muy breve (máximo 90 palabras) y da un único ajuste concreto para la Toma 2. ")
            if (userPrompt.isNotBlank()) {
                append("Contexto del estudiante: $userPrompt")
            }
        }

        try {
            val safeMime = mimeType ?: context.contentResolver.getType(uri) ?: "application/octet-stream"
            val bytes = readBytesLimited(context.contentResolver.openInputStream(uri), maxBytes = 6 * 1024 * 1024)
            if (bytes != null && bytes.isNotEmpty()) {
                val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)
                val contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = prompt),
                            Part(inlineData = InlineData(mimeType = safeMime, data = base64Data))
                        )
                    )
                )
                val systemPrompt = buildDynamicSystemInstruction(profile, mode, isDeepDiveRequest = false)
                val reply = GeminiClient.chat(contents = contents, systemPrompt = systemPrompt)
                if (reply.isNotBlank()) {
                    AiQuotaManager.recordTextUsage(prompt, reply)
                    return@withContext reply
                }
            }
        } catch (_: Exception) {
            // Fallback local
        }
        return@withContext AisoKnowledgeBase.getMentorMediaReview(mimeType)
    }

    private fun readBytesLimited(inputStream: InputStream?, maxBytes: Int): ByteArray? {
        if (inputStream == null) return null
        return inputStream.use { input ->
            val buffer = ByteArrayOutputStream()
            val data = ByteArray(4096)
            var total = 0
            var nRead: Int
            while (input.read(data, 0, data.size).also { nRead = it } != -1) {
                total += nRead
                if (total > maxBytes) break
                buffer.write(data, 0, nRead)
            }
            buffer.toByteArray()
        }
    }

    fun clearChat() {
        val profile = _speakerProfile.value
        val mode = _specialistMode.value
        val topic = profile.currentTopic.ifBlank { "el Método AISO" }
        _messages.value = listOf(
            ChatMessage(
                text = AisoKnowledgeBase.getWelcomeMessage(profile, mode),
                isFromUser = false,
                topicContext = topic,
                modeBadge = mode.shortLabel,
                suggestedFollowUps = listOf(
                    "Explícame en breve los 11 pasos AISO",
                    "Hazme un discurso corto sobre $topic",
                    "¿Cómo vencer los nervios y no quedarme en blanco?"
                )
            )
        )
    }
}
