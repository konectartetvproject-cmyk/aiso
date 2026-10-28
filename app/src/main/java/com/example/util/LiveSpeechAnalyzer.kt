package com.example.util

import com.example.model.AlertType
import com.example.model.LiveCoachAlert
import com.example.model.LiveDiagnosisReport
import com.example.model.SpeechStep

object LiveSpeechAnalyzer {
    // Lista canónica de los 11 Pasos del Orador Exitoso del libro
    val initialSpeechSteps: List<SpeechStep> = listOf(
        SpeechStep(
            id = 1,
            title = "1. Entrar al Escenario",
            shortName = "Entrada",
            description = "Camina con paso firme, hombros relajados y cabeza erguida antes de pronunciar la primera palabra.",
            recommendation = "Párate en el centro, respira profundamente y no hables de inmediato.",
            examplePhrases = listOf("(Silencio inicial con presencia)", "Postura erguida de poder")
        ),
        SpeechStep(
            id = 2,
            title = "2. Mirar al Público",
            shortName = "Mirada",
            description = "Contacto visual inicial recorriendo la sala en patrón Z o W para crear conexión.",
            recommendation = "Conecta con tres puntos clave del público antes de saludar.",
            examplePhrases = listOf("(Barrer la mirada visualmente)", "Conexión visual de 3 segundos")
        ),
        SpeechStep(
            id = 3,
            title = "3. Saludar",
            shortName = "Saludo",
            description = "Saludo breve, cálido y sonriente para romper el hielo.",
            recommendation = "Evita saludos largos o formales aburridos; sé empático.",
            examplePhrases = listOf("Hola a todos", "Buenos días", "Buenas tardes", "Buenas noches", "Es un gran placer acompañarlos")
        ),
        SpeechStep(
            id = 4,
            title = "4. Técnica de Inicio de Impacto",
            shortName = "Inicio Impacto",
            description = "Gancho inicial: Pregunta retórica, frase célebre, dato estadístico impactante o anécdota personal.",
            recommendation = "Despierta la curiosidad inmediatamente en los primeros 30 segundos.",
            examplePhrases = listOf(
                "¿Alguna vez se han preguntado...?",
                "¿Cuántos de ustedes han sentido...?",
                "Como decía Winston Churchill...",
                "El 93% de nuestra comunicación...",
                "Quiero contarles una breve historia..."
            )
        ),
        SpeechStep(
            id = 5,
            title = "5. Nombre del Orador",
            shortName = "Presentación",
            description = "Presenta tu nombre con convicción para fijar tu credibilidad y cercanía.",
            recommendation = "Dilo después del gancho, no antes, para no perder la atención.",
            examplePhrases = listOf("Mi nombre es...", "Soy...", "Para quienes no me conocen, soy...", "Quien les habla es...")
        ),
        SpeechStep(
            id = 6,
            title = "6. Nombre del Tema",
            shortName = "Tema",
            description = "Enuncia con claridad el tema central o la promesa de valor de tu discurso.",
            recommendation = "Indica claramente qué aprenderán o qué transformación experimentarán.",
            examplePhrases = listOf(
                "Hoy les hablaré de...",
                "El tema que nos reúne hoy es...",
                "Quiero compartir con ustedes los secretos de...",
                "Hoy vamos a descubrir cómo..."
            )
        ),
        SpeechStep(
            id = 7,
            title = "7. Desarrollo del Discurso (3 Pilares)",
            shortName = "Desarrollo",
            description = "Organiza las ideas en 3 puntos clave con ejemplos, historias o analogías.",
            recommendation = "Usa transiciones fluidas y mantén coherencia lógica entre argumentos.",
            examplePhrases = listOf(
                "En primer lugar...",
                "El segundo pilar fundamental es...",
                "Por último, el tercer elemento clave...",
                "Por ejemplo...",
                "Esto nos demuestra que..."
            )
        ),
        SpeechStep(
            id = 8,
            title = "8. Anunciar Conclusión",
            shortName = "Anuncio Cierre",
            description = "Señal de alerta para recapturar el 100% de la atención del público.",
            recommendation = "Usa frases directas de cierre para que la audiencia se prepare para lo más importante.",
            examplePhrases = listOf(
                "Para concluir...",
                "En resumen...",
                "Para finalizar esta presentación...",
                "Como conclusión...",
                "Cerrando estas reflexiones..."
            )
        ),
        SpeechStep(
            id = 9,
            title = "9. Técnica de Conclusión & Llamado a la Acción (CTA)",
            shortName = "Cierre & CTA",
            description = "Cierre memorable con llamado a la acción concreto o frase que resuene.",
            recommendation = "No termines con un simple 'y eso es todo'. Desafía a tu audiencia a actuar.",
            examplePhrases = listOf(
                "Los invito a partir de hoy a...",
                "El reto que les dejo es...",
                "Comiencen hoy mismo a dar el primer paso...",
                "Recordemos siempre que..."
            )
        ),
        SpeechStep(
            id = 10,
            title = "10. Decir Gracias",
            shortName = "Gracias",
            description = "Agradecimiento genuino, pausado y con mirada a todo el público.",
            recommendation = "Di 'Muchas gracias' con tono firme y cálido sin apresurarte.",
            examplePhrases = listOf("Muchas gracias", "Muchas gracias por su valiosa atención", "Gracias a todos")
        ),
        SpeechStep(
            id = 11,
            title = "11. Recibir Aplausos",
            shortName = "Aplausos",
            description = "Quédate en el escenario con sonrisa serena, postura relajada y disfrutando el momento.",
            recommendation = "No salgas corriendo; asiente con la cabeza y mantén el contacto visual.",
            examplePhrases = listOf("(Pausa final con sonrisa)", "Recepción de ovación con aplomo")
        )
    )

    val fillerWords = listOf(
        "ehh", "eh", "este", "emmm", "emm", "em", "o sea", "bueno", "la verdad",
        "digamos", "tipo", "como que", "me entiendes", "sabes", "nada", "obviamente",
        "básicamente", "en plan", "ajá"
    )

    fun findFillerWords(text: String): List<String> {
        val lower = text.lowercase()
        val detected = mutableListOf<String>()
        for (filler in fillerWords) {
            val pattern = if (filler.contains(" ")) {
                Regex("\\b${Regex.escape(filler)}\\b", RegexOption.IGNORE_CASE)
            } else {
                Regex("\\b${Regex.escape(filler)}+\\b", RegexOption.IGNORE_CASE)
            }
            if (pattern.containsMatchIn(lower)) {
                detected.add(filler)
            }
        }
        return detected
    }

    fun detectStepFromText(transcript: String, currentSteps: List<SpeechStep>): Pair<SpeechStep?, SpeechStep?> {
        val lower = transcript.lowercase()
        var matchedStep: SpeechStep? = null
        for (step in currentSteps) {
            if (step.detected) continue
            val isMatch = when (step.id) {
                3 -> lower.contains("hola") || lower.contains("buenos días") || lower.contains("buenos dias") ||
                        lower.contains("buenas tardes") || lower.contains("buenas noches") || lower.contains("un placer") ||
                        lower.contains("bienvenidos") || lower.contains("estimados") || lower.contains("saludo")
                4 -> lower.contains("alguna vez") || lower.contains("cuántos de ustedes") || lower.contains("se han preguntado") ||
                        lower.contains("qué pasaría") || lower.contains("sabías que") || lower.contains("como decía") ||
                        lower.contains("en palabras de") || lower.contains("historia") || lower.contains("anécdota") ||
                        lower.contains("recuerdo que") || lower.contains("según") || lower.contains("dato")
                5 -> lower.contains("mi nombre es") || lower.contains("soy ") || lower.contains("me llamo") ||
                        lower.contains("quien les habla") || lower.contains("para los que no me conocen")
                6 -> lower.contains("hoy les hablaré") || lower.contains("el tema de hoy") || lower.contains("vamos a hablar") ||
                        lower.contains("compartir con ustedes sobre") || lower.contains("en esta charla") || lower.contains("el objetivo de") ||
                        lower.contains("aprenderemos sobre")
                7 -> lower.contains("en primer lugar") || lower.contains("primer punto") || lower.contains("segundo punto") ||
                        lower.contains("tercero") || lower.contains("por ejemplo") || lower.contains("el pilar") ||
                        lower.contains("la clave") || lower.contains("además") || lower.contains("por lo tanto")
                8 -> lower.contains("para concluir") || lower.contains("en resumen") || lower.contains("para finalizar") ||
                        lower.contains("en conclusión") || lower.contains("como cierre") || lower.contains("finalmente")
                9 -> lower.contains("los invito a") || lower.contains("el llamado") || lower.contains("el reto") ||
                        lower.contains("debemos actuar") || lower.contains("comiencen hoy") || lower.contains("los desafío") ||
                        lower.contains("los animo") || lower.contains("actuemos")
                10 -> lower.contains("muchas gracias") || lower.contains("gracias por su atención") ||
                        lower.contains("agradezco su tiempo") || (lower.contains("gracias") && lower.contains("atención"))
                else -> false
            }
            if (isMatch) {
                matchedStep = step
                break
            }
        }
        val nextPending = currentSteps.firstOrNull { !it.detected && it.id != matchedStep?.id }
        return Pair(matchedStep, nextPending)
    }

    fun evaluateVolume(rmsdB: Float): Pair<String, AlertType?> {
        return when {
            rmsdB < 4.0f -> Pair("Voz muy baja / Susurro", AlertType.VOICE_VOLUME)
            rmsdB in 4.0f..10.0f -> Pair("Proyección óptima", null)
            else -> Pair("Volumen muy alto / Saturación", AlertType.VOICE_VOLUME)
        }
    }

    fun evaluateCadence(wpm: Int): Pair<String, AlertType?> {
        return when {
            wpm < 85 -> Pair("Ritmo lento o excesivas pausas", AlertType.VOICE_RHYTHM)
            wpm in 85..155 -> Pair("Ritmo elocuente y controlado", null)
            else -> Pair("Ritmo muy acelerado (Ansiedad)", AlertType.VOICE_RHYTHM)
        }
    }

    fun generateDiagnosis(
        steps: List<SpeechStep>,
        totalDurationSeconds: Long,
        totalWords: Int,
        muletillasCountMap: Map<String, Int>,
        swayingAlertsCount: Int,
        lowVolumeAlertsCount: Int,
        fastSpeechAlertsCount: Int
    ): LiveDiagnosisReport {
        val totalMuletillas = muletillasCountMap.values.sum()
        val averageWpm = if (totalDurationSeconds > 10) {
            ((totalWords.toFloat() / totalDurationSeconds) * 60).toInt()
        } else {
            110
        }
        val completedSteps = steps.filter { it.detected }
        val pendingSteps = steps.filter { !it.detected }

        var voiceScore = 100
        voiceScore -= (totalMuletillas * 6).coerceAtMost(30)
        voiceScore -= (lowVolumeAlertsCount * 4).coerceAtMost(20)
        voiceScore -= (fastSpeechAlertsCount * 5).coerceAtMost(20)
        if (averageWpm !in 90..160) voiceScore -= 10
        voiceScore = voiceScore.coerceIn(20, 100)

        var bodyScore = 100
        bodyScore -= (swayingAlertsCount * 12).coerceAtMost(40)
        bodyScore = bodyScore.coerceIn(30, 100)

        val structureRatio = completedSteps.size.toFloat() / steps.size.toFloat()
        val structureScore = (structureRatio * 100).toInt().coerceIn(10, 100)
        val overallScore = ((voiceScore * 0.35f) + (bodyScore * 0.30f) + (structureScore * 0.35f)).toInt().coerceIn(25, 100)

        val strengths = mutableListOf<String>()
        if (completedSteps.size >= 6) strengths.add("Estructura sólida: Desarrollaste ${completedSteps.size} de los 11 pasos del libro.")
        if (totalMuletillas <= 2) strengths.add("Excelente control de muletillas y elocuencia limpia.")
        if (averageWpm in 100..145) strengths.add("Cadencia equilibrada: Tu velocidad (${averageWpm} PPM) permite asimilar el mensaje.")
        if (swayingAlertsCount == 0) strengths.add("Presencia escénica estable y sin balanceo involuntario.")
        if (strengths.isEmpty()) strengths.add("Valentía y disposición para ensayar y exponer tus ideas.")

        val corrections = mutableListOf<String>()
        if (totalMuletillas > 2) {
            corrections.add("Detectamos $totalMuletillas muletillas. Sustitúyelas por pausas de 2 segundos; el silencio proyecta autoridad.")
        }
        if (swayingAlertsCount > 0) {
            corrections.add("Se identificó balanceo o movimiento involuntario. Planta ambos pies con firmeza al ancho de los hombros.")
        }
        if (averageWpm > 155) {
            corrections.add("Tu velocidad fue de $averageWpm PPM (acelerado). Habla más pausado para transmitir calma y convicción.")
        }
        if (pendingSteps.any { it.id == 4 }) {
            corrections.add("Faltó una Técnica de Inicio clara (Paso 4). Inicia con una pregunta retórica o un dato impactante.")
        }
        if (pendingSteps.any { it.id == 9 }) {
            corrections.add("No se detectó un Llamado a la Acción en el cierre (Paso 9). Todo discurso debe motivar a la acción.")
        }

        val exercises = mutableListOf<String>()
        if (totalMuletillas > 2 || averageWpm > 150) {
            exercises.add("Respiración Diafragmática de Caja (4x4): Inhala en 4s, retén en 4s, exhala en 4s y pausa en 4s antes de hablar.")
            exercises.add("Ejercicio del Lápiz en la boca para dicción precisa y reducción de titubeos.")
        }
        if (swayingAlertsCount > 0) {
            exercises.add("Práctica del 'Ancla Física': Ensayar 3 minutos manteniendo los pies clavados en el suelo y moviendo únicamente las manos en la 'Zona de Influencia'.")
        }
        if (pendingSteps.isNotEmpty()) {
            exercises.add("Estructura de 3 Pilares del libro: Practica el guión de 11 pasos escribiendo una frase para cada etapa.")
        }

        return LiveDiagnosisReport(
            overallScore = overallScore,
            bodyLanguageScore = bodyScore,
            voiceScore = voiceScore,
            structureScore = structureScore,
            durationSeconds = totalDurationSeconds,
            totalWords = totalWords,
            averageWpm = averageWpm,
            muletillasDetected = muletillasCountMap,
            totalMuletillas = totalMuletillas,
            completedSteps = completedSteps,
            pendingSteps = pendingSteps,
            strengths = strengths,
            corrections = corrections,
            recommendedBookExercises = exercises
        )
    }
}
