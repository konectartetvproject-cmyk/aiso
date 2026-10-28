package com.example.util

import com.example.model.SpeechStep
import java.util.Locale

data class WrittenSpeechAnalysisResult(
    val score: Int,
    val wordCount: Int,
    val estimatedDurationSeconds: Int,
    val fillerWordsFound: Map<String, Int>,
    val detectedSteps: List<SpeechStep>,
    val missingSteps: List<SpeechStep>,
    val strengths: List<String>,
    val weaknesses: List<String>,
    val recommendations: List<String>,
    val rewrittenSpeech: String
)

object WrittenSpeechAnalyzer {
    private val analysisKeywords = listOf(
        "analiza mi discurso", "revisa mi discurso", "corrige mi discurso", "evalua mi discurso",
        "evalúa mi discurso", "mi discurso:", "este es mi discurso", "analizar discurso",
        "revisar discurso", "corregir discurso", "feedback de mi discurso", "qué tal mi discurso",
        "que tal mi discurso", "analiza este texto", "analiza este discurso"
    )

    fun isSpeechAnalysisRequest(text: String): Boolean {
        val lower = text.lowercase(Locale.getDefault()).trim()
        if (analysisKeywords.any { lower.contains(it) }) return true
        val words = lower.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size >= 40) {
            val oratoryIndicators = listOf(
                "buenos dias", "buenos días", "buenas tardes", "buenas noches", "estimados",
                "hoy les hablare", "hoy les hablaré", "mi nombre es", "para concluir",
                "en conclusion", "en conclusión", "en resumen", "muchas gracias",
                "primero", "segundo", "pilar", "por que", "por qué", "los invito a"
            )
            val matchCount = oratoryIndicators.count { lower.contains(it) }
            if (matchCount >= 2) return true
        }
        return false
    }

    fun extractCleanSpeech(text: String): String {
        var clean = text
        for (kw in analysisKeywords) {
            val regex = Regex("(?i)^.*?$kw[:\\s]*")
            clean = clean.replace(regex, "")
        }
        return clean.trim().ifBlank { text.trim() }
    }

    fun analyze(speechText: String): WrittenSpeechAnalysisResult {
        val cleanSpeech = extractCleanSpeech(speechText)
        val lower = cleanSpeech.lowercase(Locale.getDefault())
        val words = cleanSpeech.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val wordCount = words.size
        val estimatedDurationSeconds = if (wordCount > 0) ((wordCount / 130.0f) * 60).toInt() else 0

        val fillerWordsList = LiveSpeechAnalyzer.fillerWords
        val foundFillers = mutableMapOf<String, Int>()
        for (filler in fillerWordsList) {
            val pattern = if (filler.contains(" ")) {
                Regex("\\b${Regex.escape(filler)}\\b", RegexOption.IGNORE_CASE)
            } else {
                Regex("\\b${Regex.escape(filler)}+\\b", RegexOption.IGNORE_CASE)
            }
            val count = pattern.findAll(lower).count()
            if (count > 0) {
                foundFillers[filler] = count
            }
        }
        val totalFillers = foundFillers.values.sum()

        val allSteps = LiveSpeechAnalyzer.initialSpeechSteps
        val detected = mutableListOf<SpeechStep>()
        val missing = mutableListOf<SpeechStep>()

        val hasGreeting = lower.contains("hola") || lower.contains("buenos días") || lower.contains("buenos dias") ||
                lower.contains("buenas tardes") || lower.contains("buenas noches") || lower.contains("un placer") ||
                lower.contains("bienvenidos") || lower.contains("estimados")
        if (hasGreeting) detected.add(allSteps[2]) else missing.add(allSteps[2])

        val hasHook = lower.contains("?") || lower.contains("¿") || lower.contains("alguna vez") ||
                lower.contains("cuántos de ustedes") || lower.contains("cuantos de ustedes") ||
                lower.contains("sabían que") || lower.contains("sabias que") || lower.contains("como decía") ||
                lower.contains("en palabras de") || lower.contains("recuerdo que") || lower.contains("imaginen") ||
                lower.contains("historia") || lower.contains("anécdota") || lower.contains("dato") || lower.contains("estudio")
        if (hasHook) detected.add(allSteps[3]) else missing.add(allSteps[3])

        val hasSpeakerIntro = lower.contains("mi nombre es") || lower.contains("soy ") || lower.contains("me llamo") ||
                lower.contains("quien les habla") || lower.contains("para los que no me conocen")
        if (hasSpeakerIntro) detected.add(allSteps[4]) else missing.add(allSteps[4])

        val hasTopic = lower.contains("hoy les hablaré") || lower.contains("hoy les hablare") ||
                lower.contains("el tema de hoy") || lower.contains("vamos a hablar") ||
                lower.contains("compartir con ustedes sobre") || lower.contains("aprenderemos") ||
                lower.contains("el objetivo de esta charla") || lower.contains("quiero hablarles")
        if (hasTopic) detected.add(allSteps[5]) else missing.add(allSteps[5])

        val hasDevelopment = lower.contains("en primer lugar") || lower.contains("primer punto") ||
                lower.contains("segundo") || lower.contains("tercero") || lower.contains("por ejemplo") ||
                lower.contains("además") || lower.contains("ademas") || lower.contains("el pilar") ||
                lower.contains("la clave") || lower.contains("por qué") || lower.contains("lo más importante")
        if (hasDevelopment) detected.add(allSteps[6]) else missing.add(allSteps[6])

        val hasAnnounceClosing = lower.contains("para concluir") || lower.contains("en resumen") ||
                lower.contains("para finalizar") || lower.contains("en conclusión") || lower.contains("en conclusion") ||
                lower.contains("finalmente") || lower.contains("como cierre") || lower.contains("cerrando")
        if (hasAnnounceClosing) detected.add(allSteps[7]) else missing.add(allSteps[7])

        val hasCTA = lower.contains("los invito a") || lower.contains("el llamado") || lower.contains("el reto") ||
                lower.contains("debemos actuar") || lower.contains("comiencen hoy") || lower.contains("los desafío") ||
                lower.contains("los animo") || lower.contains("recordemos que") || lower.contains("los exhorto") ||
                lower.contains("actuemos") || lower.contains("la decisión es")
        if (hasCTA) detected.add(allSteps[8]) else missing.add(allSteps[8])

        val hasThanks = lower.contains("muchas gracias") || lower.contains("gracias por su") ||
                lower.contains("agradezco su") || (lower.contains("gracias") && lower.contains("atención"))
        if (hasThanks) detected.add(allSteps[9]) else missing.add(allSteps[9])

        var score = 40
        if (hasHook) score += 15
        if (hasGreeting) score += 5
        if (hasSpeakerIntro) score += 5
        if (hasTopic) score += 10
        if (hasDevelopment) score += 15
        if (hasAnnounceClosing) score += 5
        if (hasCTA) score += 15
        if (hasThanks) score += 5
        score -= (totalFillers * 4).coerceAtMost(20)
        if (wordCount < 40) score -= 15
        score = score.coerceIn(25, 100)

        val strengths = mutableListOf<String>()
        if (hasHook) strengths.add("Apertura estimulante: Empleas un gancho que despierta curiosidad (pregunta, anécdota o dato).")
        if (hasDevelopment) strengths.add("Desarrollo hilado: Utilizas conectores lógicos que guían al oyente de un argumento a otro.")
        if (hasCTA) strengths.add("Llamado a la Acción claro: No dejas el discurso en teoría, impulsas a la audiencia a dar el paso.")
        if (totalFillers == 0) strengths.add("Texto pulcro y sin muletillas registradas.")
        if (wordCount in 80..350) strengths.add("Extensión ideal: Conciso, directo al grano y fácil de recordar.")
        if (strengths.isEmpty()) strengths.add("Claridad de intención y autenticidad en el mensaje planteado.")

        val weaknesses = mutableListOf<String>()
        if (!hasHook) weaknesses.add("Falta de Técnica de Inicio (Paso 4): Iniciar en frío reduce hasta un 60% la retención en los primeros 30 segundos.")
        if (!hasTopic) weaknesses.add("Promesa del tema difusa (Paso 6): La audiencia necesita saber qué ganará escuchándote.")
        if (!hasDevelopment) weaknesses.add("Estructura poco definida (Paso 7): Aplica la Regla de los 3 Pilares del libro.")
        if (!hasAnnounceClosing) weaknesses.add("Cierre abrupto (Paso 8): Te faltó anunciar la conclusión ('Para concluir...', 'En resumen...').")
        if (!hasCTA) weaknesses.add("Sin Llamado a la Acción (Paso 9): Como enseña el libro, un discurso sin llamado a la acción se olvida fácilmente.")
        if (totalFillers > 0) weaknesses.add("Muletillas detectadas ($totalFillers): Aparecen expresiones como ${foundFillers.keys.joinToString(", ")}.")

        val recommendations = mutableListOf<String>()
        recommendations.add("Regla de la Trinidad de la Influencia: Conecta la mente del público con datos (logos), emociones con historias (pathos) y tu autoridad con postura (ethos).")
        if (!hasHook) recommendations.add("Añade una pregunta retórica inicial antes de decir tu nombre.")
        if (!hasCTA) recommendations.add("Termina con un desafío claro: 'Los invito a que hoy mismo den el primer paso hacia...'")
        recommendations.add("Respiración Diafragmática de Caja (4x4): Al ensayar este texto en voz alta, haz una pausa de 2 segundos en cada punto para oxigenarte.")

        val topicSubject = if (hasTopic) "este mensaje" else "la superación y el liderazgo"
        val rewritten = buildString {
            append("✍️ **VERSIÓN OPTIMIZADA POR AISO (11 PASOS DEL LIBRO):**\n\n")
            append("1. **[Técnica de Inicio - Gancho]:**\n")
            if (hasHook) {
                append("\"¿Alguna vez se han preguntado qué separa a quienes sueñan de quienes realmente logran transformar su realidad?\"\n\n")
            } else {
                append("\"¿Cuántos de ustedes han sentido el deseo ardiente de marcar una verdadera diferencia, pero el miedo al qué dirán los ha frenado?\"\n\n")
            }
            append("2. **[Saludo y Presentación]:**\n")
            append("\"¡Muy buenos días a todos! Mi nombre es [Tu Nombre], y es un auténtico privilegio compartir este momento con ustedes.\"\n\n")
            append("3. **[Nombre del Tema & Promesa]:**\n")
            append("\"Hoy quiero revelarles los secretos fundamentales detrás de $topicSubject, y cómo pueden aplicarlos hoy mismo para potenciar sus vidas.\"\n\n")
            append("4. **[Cuerpo - Regla de los 3 Pilares]:**\n")
            append("• **Primer Pilar (El Fundamento):** Lo primero que debemos comprender es que la convicción nace de la preparación y el diálogo interno positivo.\n")
            append("• **Segundo Pilar (La Estrategia):** No basta con tener buenas intenciones; requerimos disciplina y enfoque diario, hablando siempre desde el corazón.\n")
            append("• **Tercer Pilar (La Trascendencia):** Cuando comunicamos para servir a los demás y no para alimentar el ego, la audiencia conecta con nuestra verdad.\n\n")
            append("5. **[Anuncio de Conclusión]:**\n")
            append("\"Para concluir esta presentación, quiero que se lleven una sola idea grabada en el alma:\"\n\n")
            append("6. **[Cierre & Llamado a la Acción (CTA)]:**\n")
            append("\"Los invito a partir de hoy a dar ese paso valiente que han estado postergando. No esperen las condiciones perfectas; creen ustedes la oportunidad.\"\n\n")
            append("7. **[Agradecimiento & Recepción de Aplausos]:**\n")
            append("\"¡Muchas gracias por su atención y nos vemos en la cima!\" *(Pausa, sonrisa cálida y contacto visual).*")
        }

        return WrittenSpeechAnalysisResult(
            score = score,
            wordCount = wordCount,
            estimatedDurationSeconds = estimatedDurationSeconds,
            fillerWordsFound = foundFillers,
            detectedSteps = detected,
            missingSteps = missing,
            strengths = strengths,
            weaknesses = weaknesses,
            recommendations = recommendations,
            rewrittenSpeech = rewritten
        )
    }

    fun formatAnalysisToMarkdown(result: WrittenSpeechAnalysisResult): String {
        return buildString {
            append("📊 **DIAGNÓSTICO DEL DISCURSO ESCRITO (MENTOR AISO)**\n\n")
            val scoreEmoji = when {
                result.score >= 85 -> "🏆"
                result.score >= 70 -> "⭐"
                else -> "📈"
            }
            append("$scoreEmoji **Calificación General: ${result.score}/100**\n")
            append("⏱️ **Métricas:** ${result.wordCount} palabras | ~${result.estimatedDurationSeconds / 60}m ${result.estimatedDurationSeconds % 60}s de exposición oral recomendada (130 PPM).\n\n")
            append("📋 **Auditoría de los 11 Pasos del Libro:**\n")
            append("✅ **Pasos Detectados (${result.detectedSteps.size}):** ${result.detectedSteps.joinToString(", ") { it.shortName }}\n")
            if (result.missingSteps.isNotEmpty()) {
                append("⚠️ **Pasos Faltantes (${result.missingSteps.size}):** ${result.missingSteps.joinToString(", ") { it.shortName }}\n")
            }
            append("\n")
            if (result.fillerWordsFound.isNotEmpty()) {
                append("🚫 **Muletillas Escritas (${result.fillerWordsFound.values.sum()}):** ")
                append(result.fillerWordsFound.entries.joinToString(", ") { "'${it.key}' (${it.value})" })
                append("\n\n")
            }
            append("💪 **Fortalezas del Discurso:**\n")
            result.strengths.forEach { append("• $it\n") }
            append("\n")
            if (result.weaknesses.isNotEmpty()) {
                append("🔧 **Patrones Débiles a Corregir:**\n")
                result.weaknesses.forEach { append("• $it\n") }
                append("\n")
            }
            append("💡 **Recomendaciones de 'Los secretos del orador exitoso':**\n")
            result.recommendations.forEach { append("• $it\n") }
            append("\n")
            append(result.rewrittenSpeech)
        }
    }
}
