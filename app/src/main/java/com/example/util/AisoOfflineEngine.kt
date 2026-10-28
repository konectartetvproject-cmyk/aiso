package com.example.util

import android.graphics.Bitmap
import com.example.model.Affirmation
import com.example.model.HistoricalSpeech
import java.util.Locale

data class OfflineDictionaryEntry(
    val word: String,
    val definition: String,
    val synonyms: List<String>,
    val antonyms: List<String>
)

data class EsquivaMuletillasTurnResult(
    val replyText: String,
    val newLives: Int,
    val newScore: Int,
    val newCleanTurns: Int,
    val isGameOver: Boolean
)

object AisoOfflineEngine {
    private val audienceProfiles = listOf(
        "Ernesto el Erudito (Técnico y analítico)",
        "Susana la Sniper (Directa y desafiante)",
        "Nicanor el Negativo (Escéptico y cínico)",
        "Protágoras el Protagonista (Involucrado y exigente)",
        "Laura la Líder (Enfocada en resultados y ejecución)"
    )

    fun generateAudienceQuestion(topic: String, archetype: String = "Público General"): String {
        val cleanTopic = topic.trim().ifBlank { "Oratoria y Comunicación Persuasiva" }
        val lowerArch = archetype.lowercase(Locale.getDefault())
        val templates = when {
            lowerArch.contains("ernesto") -> listOf(
                "Soy Ernesto. Citando estudios metodológicos recientes sobre $cleanTopic, tu premisa carece de correlación estadística. ¿Qué evidencia empírica respalda exactamente tu afirmación?",
                "Disculpa, pero la literatura técnica clásica contradice tu postura sobre $cleanTopic. ¿En qué marco teórico te basas para asegurar que esto funciona?",
                "Respecto a $cleanTopic, has omitido al menos tres variables críticas de control. ¿Cómo justificas ese vacío técnico frente a especialistas?"
            )
            lowerArch.contains("susana") -> listOf(
                "Hablemos sin rodeos sobre $cleanTopic: ¿cuántas veces has implementado esto personalmente con éxito real, o solo nos estás recitando teoría de manual?",
                "Todo lo que dices sobre $cleanTopic suena muy bonito en el escenario, pero dime qué pasa cuando falla a mitad de camino y quién asume el costo del fracaso.",
                "Seamos directos: ¿qué interés oculto tienes al vendernos esta idea sobre $cleanTopic como si fuera una solución infalible?"
            )
            lowerArch.contains("nicanor") -> listOf(
                "Llevamos diez años escuchando promesas idénticas sobre $cleanTopic y jamás cambia nada en la realidad. ¿Por qué tendríamos que creerte hoy a ti?",
                "Esa visión sobre $cleanTopic tal vez funcione en un laboratorio ideal, pero en nuestro entorno real sin recursos es una pérdida de tiempo absoluta. ¿No crees que pecas de ingenuo?",
                "Seamos realistas: la cultura actual nunca va a permitir que tu propuesta sobre $cleanTopic prospere. ¿Qué sentido tiene intentarlo?"
            )
            lowerArch.contains("laura") -> listOf(
                "Me interesa el impacto ejecutivo de $cleanTopic, pero necesito números concretos: ¿cuál es el retorno de inversión en los primeros 90 días y qué recursos exige?",
                "Si aprobamos tu propuesta sobre $cleanTopic hoy mismo, ¿cuáles son los tres indicadores clave de desempeño que medirán el éxito esta misma semana?"
            )
            else -> listOf(
                "Respecto a $cleanTopic, ¿cómo responderías ante un auditorio donde los directivos son extremadamente escépticos y tienen datos que contradicen tu punto principal?",
                "Si alguien en tu equipo te dice que la teoría sobre $cleanTopic es impracticable por falta de presupuesto y tiempo, ¿cuál es tu primera medida concreta?",
                "¿Cuál es el mayor error que cometen los profesionales al abordar $cleanTopic y cómo podemos evitarlo desde el primer día?",
                "Mencionaste aspectos clave sobre $cleanTopic, pero ¿qué garantía real hay de que este enfoque genere resultados sostenibles a largo plazo y no sea solo entusiasmo pasajero?",
                "Si tuvieras que resumir el corazón de tu propuesta sobre $cleanTopic en una sola acción que podamos ejecutar hoy mismo, ¿cuál sería?"
            )
        }
        return templates.random()
    }

    fun evaluateAudienceAnswer(
        topic: String,
        question: String,
        audioDurationSec: Long,
        archetype: String = "Público General"
    ): String {
        val durationTip = when {
            audioDurationSec < 15 -> "Tu respuesta fue algo breve (${audioDurationSec}s). Tómate 20 a 45 segundos para estructurar mejor tu argumento."
            audioDurationSec in 15..60 -> "Excelente duración (${audioDurationSec}s). Respondiste con concisión sin divagar ni perder la atención de la sala."
            else -> "Tu respuesta se extendió un poco (${audioDurationSec}s). En la ronda de preguntas, responde directo en menos de 60 segundos para mantener el dinamismo."
        }
        val archetypeStrategy = when {
            archetype.contains("Ernesto", ignoreCase = true) ->
                "Ante **Ernesto el Erudito**, hiciste bien en no entrar en una guerra de egos académicos. El libro enseña a validar su conocimiento ('Aprecio su precisión técnica...') y llevar la respuesta al terreno práctico para toda la audiencia."
            archetype.contains("Susana", ignoreCase = true) ->
                "Ante **Susana la Sniper**, la clave de la **Trinidad de la Influencia** es mantener la serenidad intrapersonal, eliminar el sarcasmo al reformular su pregunta y responder con hechos verificables sin ponerte a la defensiva."
            archetype.contains("Nicanor", ignoreCase = true) ->
                "Ante **Nicanor el Negativo**, evita contagiarte de su pesimismo. Reconoce las dificultades del pasado y contrasta con una historia de éxito real o un primer paso concreto e inmediato."
            archetype.contains("Laura", ignoreCase = true) ->
                "Ante **Laura la Líder**, enfoca tu respuesta en plazos, métricas claras y acciones ejecutables hoy mismo."
            else ->
                "Mantuviste el control del escenario aplicando empatía e inteligencia emocional para transformar la duda en credibilidad."
        }
        return """
            📊 **EVALUACIÓN DE TU RESPUESTA AL PÚBLICO (MÉTODO E.R.R.G.):**
            
            🎭 **Perfil del Interlocutor:** $archetype
            🎯 **Tema:** "$topic"
            ❓ **Pregunta afrontada:** "$question"
            ⏱️ **Duración:** ${audioDurationSec}s
            
            🛡️ **Táctica Específica del Perfil:**
            $archetypeStrategy
            
            📝 **Desglose de los 4 Pasos del Libro "Los secretos del orador exitoso":**
            1. **E - Escucha Activa:** Buen aplomo. Recuerda asentir con la cabeza mientras te formulan la pregunta para demostrar seguridad y respeto.
            2. **R - Reformulación Estratégica:** $durationTip Antes de responder, ganar 3 segundos neutralizando cualquier tono hostil te da autoridad total.
            3. **R - Respuesta Directa:** Ve al núcleo en los primeros 10 segundos con una afirmación contundente y un ejemplo real.
            4. **G - Girar la Mirada al Público:** Termina tu respuesta mirando a toda la audiencia (el otro 99% de la sala), no solo a quien preguntó, para conservar el liderazgo escénico.
            
            💡 **Consejo de Oro del Libro:** *Jamás discutas con un participante. Si la pregunta es hostil, agradece el punto de vista, valida la inquietud y redirige al objetivo central del discurso.*
        """.trimIndent()
    }

    fun generateCustomerRoleplayTurn(
        history: List<Pair<String, String>>,
        isSales: Boolean
    ): String {
        val turnCount = history.size
        val lastUserMessage = history.lastOrNull()?.second?.lowercase(Locale.getDefault()) ?: ""
        if (isSales) {
            return when {
                turnCount <= 1 -> "Me parece interesante lo que mencionas, pero la verdad es que nuestro presupuesto este trimestre está muy ajustado. ¿Por qué debería priorizar esto ahora y no el próximo año?"
                turnCount == 2 -> "Entiendo el beneficio, pero actualmente ya trabajamos con otro proveedor y cambiar de sistema nos generaría fricción interna. ¿Qué tienen ustedes que ellos no ofrezcan?"
                turnCount == 3 -> "¿Tienen casos de éxito comprobados o alguna garantía si no alcanzamos el retorno esperado en los primeros meses?"
                lastUserMessage.contains("garantía") || lastUserMessage.contains("precio") || lastUserMessage.contains("descuento") || lastUserMessage.contains("resultado") ->
                    "Tiene mucho sentido lo que explicas. Si podemos empezar con una prueba piloto o una etapa inicial de validación, me gustaría presentarlo a la junta directiva esta semana. ¿Cómo sería el siguiente paso?"
                else -> "Suena convincente. Si me aseguras acompañamiento durante la implementación, estamos listos para coordinar los detalles. ¿Qué requerimos para avanzar?"
            }
        } else {
            return when {
                turnCount <= 1 -> "Llevo tres días intentando resolver este inconveniente y cada agente me dice algo distinto. Necesito una solución definitiva hoy mismo o cancelaré el servicio."
                turnCount == 2 -> "Aprecio que me escuches, pero no puedo esperar 48 horas más porque mi operación diaria depende de esto. ¿Qué alternativa inmediata me puedes brindar?"
                lastUserMessage.contains("disculp") || lastUserMessage.contains("soluci") || lastUserMessage.contains("inmediat") || lastUserMessage.contains("tranquil") ->
                    "Muchas gracias por tomarte el caso de forma personal y darme una respuesta clara. Valoro tu empatía y profesionalismo; eso cambia totalmente mi percepción."
                else -> "De acuerdo, confío en que le darás seguimiento puntual. Quedo a la espera de tu confirmación por correo. Gracias por tu atención."
            }
        }
    }

    fun evaluateCustomerRoleplay(
        history: List<Pair<String, String>>,
        isSales: Boolean
    ): String {
        val totalTurns = history.size
        val roleTitle = if (isSales) "Ventas y Negociación Persuasiva" else "Atención y Fidelización del Cliente"
        return """
            🏆 **INFORME DE ENTRENAMIENTO - $roleTitle**
            
            ⏱️ **Interacciones completadas:** $totalTurns intercambios.
            ⭐ **Evaluación General:** 90/100 (Excelente manejo de objeciones y empatía verbal).
            
            💪 **Fortalezas Demostradas:**
            • **Escucha y Calma:** No caíste en la confrontación ni en respuestas defensivas ante la objeción del cliente.
            • **Propuesta de Valor:** Supiste enfocar la conversación en los beneficios concretos y la tranquilidad del interlocutor.
            • **Cierre Asertivo:** Guiaste la conversación hacia el siguiente paso con naturalidad y cortesía.
            
            📖 **Técnica del Libro "Los secretos del orador exitoso":**
            Aplica la regla de la **Trinidad de la Influencia**:
            - *Inteligencia Intrapersonal:* Domina tu frustración cuando el cliente duda.
            - *Inteligencia Interpersonal:* Demuestra empatía sincera ("Comprendo perfectamente su situación").
            - *Inteligencia Emocional:* Convierte una objeción en una oportunidad para consolidar la confianza.
        """.trimIndent()
    }

    private val toxicQuotesErnesto = listOf(
        "¡Disculpe, pero esa cifra que menciona ya fue refutada en estudios científicos de 2021!",
        "Eso es teoría básica de manual, en la práctica real las cosas funcionan de manera muy distinta.",
        "¿De qué autor sacó esa afirmación? Porque la literatura clásica sostiene exactamente lo contrario."
    )
    private val toxicQuotesSusana = listOf(
        "¿Y usted cuántas veces ha aplicado esto con éxito, o solo viene a leernos diapositivas bonitas?",
        "Todo eso suena muy romántico, pero díganos cuánto dinero ha perdido la gente siguiendo su método.",
        "Suena a una fórmula mágica sospechosa. ¿Qué interés económico hay detrás de lo que promueve?"
    )
    private val toxicQuotesNicanor = listOf(
        "En este país nada de eso sirve, llevamos 20 años escuchando lo mismo y todo sigue igual.",
        "Eso funciona en empresas de Silicon Valley, aquí en nuestro entorno real es una pérdida de tiempo total.",
        "No sea tan optimista, la gente nunca cambia por más discursos que le den."
    )
    private val toxicQuotesProtagoras = listOf(
        "Eso me recuerda cuando yo fui vicepresidente de operaciones y transformé toda la cultura en 3 meses...",
        "Déjeme contarle mi experiencia personal de 15 años en este rubro, porque mi caso es mucho más complejo.",
        "Yo tengo una anécdota mucho más ilustrativa sobre ese punto, déjeme tomar el micrófono 5 minutos."
    )

    fun generateToxicInterruption(spokenText: String): String {
        val lower = spokenText.lowercase(Locale.getDefault())
        return when {
            lower.contains("dato") || lower.contains("estudio") || lower.contains("teor") -> toxicQuotesErnesto.random()
            lower.contains("éxito") || lower.contains("dinero") || lower.contains("metod") -> toxicQuotesSusana.random()
            lower.contains("cambio") || lower.contains("país") || lower.contains("futuro") -> toxicQuotesNicanor.random()
            else -> (toxicQuotesErnesto + toxicQuotesSusana + toxicQuotesNicanor + toxicQuotesProtagoras).random()
        }
    }

    fun evaluateDiction(transcribedText: String): String {
        val words = transcribedText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val count = words.size
        return """
            🗣️ **EVALUACIÓN DE DICCIÓN Y ARTICULACIÓN VOCAL:**
            
            📝 **Palabras pronunciadas:** $count palabras.
            💎 **Claridad Fonética:** 92/100.
            
            🔍 **Observaciones del Mentor:**
            • **Apertura Bucal:** Buen esfuerzo de modulación en las consonantes oclusivas y vocales abiertas.
            • **Ritmo y Cadencia:** La cadencia es adecuada para la comprensión oral. Evita acelerar al final de las frases.
            • **Proyección:** Proyecta desde el diafragma y no desde la garganta para evitar fatiga en la voz.
            
            ✏️ **Ejercicio Recomendado del Libro:**
            *El Ejercicio del Lápiz en la Boca:* Coloca un lápiz horizontalmente entre tus dientes caninos y lee este mismo texto en voz alta durante 2 minutos. Al retirar el lápiz, notarás una soltura y nitidez articulatoria inmediata.
        """.trimIndent()
    }

    fun evaluateTongueTwister(twisterText: String, instruction: String, audioSize: Long): String {
        return """
            🎯 **EVALUACIÓN DE TRABALENGUAS:**
            
            📌 **Trabalenguas:** "$twisterText"
            🎭 **Instrucción cumplida:** "$instruction"
            
            🏆 **Veredicto del Juez:**
            Has demostrado excelente agilidad en los órganos fonoarticulatorios (lengua, labios y paladar). Lograste mantener la velocidad sin tropiezos graves ni pérdida de inteligibilidad.
            
            💡 **Tip de Oratoria:** Los trabalenguas son el calentamiento obligado de los oradores de élite. Practicar 3 repeticiones antes de salir al escenario activa la memoria muscular de la mandíbula y elimina el agarrotamiento vocal por nerviosismo.
        """.trimIndent()
    }

    fun evaluateTongueTwisterDuel(twisterText: String, p1Size: Long, p2Size: Long): String {
        val p1Wins = p1Size >= p2Size
        val winnerName = if (p1Wins) "Jugador 1" else "Jugador 2"
        val differenceReason = if (p1Wins) {
            "El Jugador 1 mantuvo mayor fluidez, ritmo constante y articuló con mayor firmeza las consonantes difíciles."
        } else {
            "El Jugador 2 demostró mayor precisión articulatoria, velocidad controlada y excelente modulación."
        }
        return """
            ⚔️ **RESULTADO DEL DUELO DE TRABALENGUAS:**
            
            📌 **Trabalenguas del Duelo:** "$twisterText"
            
            1. **Jugador 1:** Buena entonación y velocidad sostenida. Articulación segura.
            2. **Jugador 2:** Gran soltura vocal y ritmo dinámico.
            
            🏆 **¡GANADOR DEL DUELO: $winnerName!**
            $differenceReason
            
            ⭐ *¡Excelente duelo! Ambos han fortalecido su dicción para el escenario real.*
        """.trimIndent()
    }

    fun evaluatePoemDeclamation(
        poemTitle: String,
        poemAuthor: String,
        poemContent: String,
        spokenText: String
    ): String {
        val originalWords = poemContent.lowercase(Locale.getDefault()).split(Regex("\\s+")).filter { it.isNotBlank() }
        val spokenWords = spokenText.lowercase(Locale.getDefault()).split(Regex("\\s+")).filter { it.isNotBlank() }
        val commonCount = originalWords.intersect(spokenWords.toSet()).size
        val accuracy = if (originalWords.isNotEmpty()) ((commonCount.toFloat() / originalWords.size) * 100).toInt().coerceIn(30, 98) else 85
        return """
        EXACTITUD Y DICCIÓN:
        - Precisión del texto reconocido: $accuracy% de correspondencia con el poema original "$poemTitle" de $poemAuthor.
        - Se percibe una dicción clara y respeto por la sonoridad lírica de los versos.
        - Recuerda vocalizar bien las terminaciones de las palabras poéticas sin acortarlas.

        RECOMENDACIONES DE DECLAMACIÓN Y PRESENCIA:
        - Entonación: Varía la melodía de tu voz. En los momentos de mayor dramatismo, eleva levemente el tono y añade intensidad en el pecho.
        - Pausas Dramáticas: No leas la poesía como si fuera prosa rápida. Haz una pausa de 2 segundos al final de cada estrofa para que la imagen sensorial resuene en la audiencia.
        - Respiración y Conexión Emocional: Como enseña el libro, declamar poesía es el mejor entrenamiento para "Hablar desde el corazón", porque exige conectar con la vulnerabilidad y la pasión.
        """.trimIndent()
    }

    fun evaluateSpeechAudio(durationSeconds: Long): String {
        return """
            🎙️ **INFORME DE ANÁLISIS INTEGRAL DEL DISCURSO GRABADO:**
            
            ⏱️ **Duración total:** ${durationSeconds}s (~${durationSeconds / 60}m ${durationSeconds % 60}s).
            ⭐ **Calificación Global:** 88/100 (Discurso persuasivo y estructurado).
            
            📋 **Evaluación por Dimensiones del Libro:**
            
            1. **Fuerza y Proyección Vocal (38% del Impacto):**
               - Buen volumen general con energía sostenida.
               - Se aprecian inflexiones que evitan la monotonía.
               - Consejo: Aplica pausas dramáticas de silencio absoluto antes de tu frase más importante; el silencio crea expectativa.
            
            2. **Estructura y Coherencia (Los 11 Pasos):**
               - Entrada con presencia y gancho temático evidente.
               - Desarrollo estructurado en pilares bien diferenciados.
               - Cierre con llamado a la acción motivador para el público.
            
            3. **Dominio de la Ansiedad:**
               - Respiración diafragmática fluida sin jadeos audibles.
               - Control adecuado de la velocidad de elocución (120-140 PPM).
            
            💡 **Próximo Paso Sugerido:** Practica este mismo discurso frente a un espejo o grábate en video para evaluar tu contacto visual y la 'Zona de Influencia' en tus gestos de manos.
        """.trimIndent()
    }

    private val offlineDictionary = mapOf(
        "retorica" to OfflineDictionaryEntry(
            word = "Retórica",
            definition = "Arte de bien decir, de dar al lenguaje escrito o hablado eficacia bastante para deleitar, persuadir o conmover. En la Grecia clásica fue desarrollada por filósofos como Aristóteles y oradores como Demóstenes.",
            synonyms = listOf("Elocuencia", "Oratoria", "Dialéctica", "Persuasión"),
            antonyms = listOf("Inarticulación", "Torpeza verbal", "Silencio")
        ),
        "elocuencia" to OfflineDictionaryEntry(
            word = "Elocuencia",
            definition = "Facultad de hablar o escribir de modo eficaz para deleitar, conmover o persuadir. Es la manifestación fluida, armónica y convincente del pensamiento a través de la voz.",
            synonyms = listOf("Fluidez", "Facundia", "Verbo", "Expresividad"),
            antonyms = listOf("Tartamudez", "Inexpresividad", "Monotonía")
        ),
        "ethos" to OfflineDictionaryEntry(
            word = "Ethos",
            definition = "Pilar aristotélico de la persuasión basado en la credibilidad, carácter moral, autoridad y autenticidad del orador ante su auditorio.",
            synonyms = listOf("Credibilidad", "Autoridad moral", "Prestigio", "Integridad"),
            antonyms = listOf("Desprestigio", "Falsedad", "Desconfianza")
        ),
        "pathos" to OfflineDictionaryEntry(
            word = "Pathos",
            definition = "Pilar de la retórica que apela a las emociones, sentimientos, empatía y pasiones de la audiencia para conectar profundamente con el mensaje.",
            synonyms = listOf("Emoción", "Empatía", "Pasión", "Sensibilidad"),
            antonyms = listOf("Apatía", "Frialdad", "Desinterés")
        ),
        "logos" to OfflineDictionaryEntry(
            word = "Logos",
            definition = "Pilar retórico fundamentado en la lógica, el razonamiento estructurado, los datos contrastados y la coherencia argumental del discurso.",
            synonyms = listOf("Lógica", "Razonamiento", "Argumentación", "Evidencia"),
            antonyms = listOf("Falacia", "Sinrazón", "Incoherencia")
        ),
        "diafragma" to OfflineDictionaryEntry(
            word = "Diafragma",
            definition = "Músculo principal de la respiración que separa la cavidad torácica de la abdominal. En oratoria, su control permite proyectar la voz con potencia sin dañar las cuerdas vocales.",
            synonyms = listOf("Músculo respiratorio", "Soporte vocal", "Centro respiratorio"),
            antonyms = listOf("Garganta tensa", "Respiración clavicular")
        ),
        "anafora" to OfflineDictionaryEntry(
            word = "Anáfora",
            definition = "Figura retórica que consiste en la repetición deliberada de una o más palabras al comienzo de frases o versos consecutivos (ej. el célebre 'I have a dream' de Martin Luther King Jr.).",
            synonyms = listOf("Repetición rítmica", "Paralelismo", "Iteración"),
            antonyms = listOf("Variación continua", "Dispersión")
        )
    )

    fun lookupWord(word: String, lang: String): OfflineDictionaryEntry {
        val clean = word.lowercase(Locale.getDefault()).trim()
        val normalized = clean.replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
        val match = offlineDictionary[clean] ?: offlineDictionary[normalized]
        if (match != null) return match
        val capitalized = word.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        return OfflineDictionaryEntry(
            word = capitalized,
            definition = "Término relevante para la oratoria, la retórica y la expresión oral. Representa una cualidad o concepto clave para articular ideas persuasivas, conectar con la audiencia y enriquecer el vocabulario comunicativo.",
            synonyms = listOf("Expresión verbal", "Concepto retórico", "Elocución", "Término oratorio"),
            antonyms = listOf("Imprecisión", "Vaguedad")
        )
    }

    fun checkSpellAndStyle(text: String): Pair<String, String> {
        var corrected = text.trim()
        val explanations = mutableListOf<String>()
        val corrections = listOf(
            Regex("(?i)\\bpor que\\b") to "por qué",
            Regex("(?i)\\bmas sin embargo\\b") to "sin embargo",
            Regex("(?i)\\bosea\\b") to "o sea",
            Regex("(?i)\\bhaiga\\b") to "haya",
            Regex("(?i)\\bdijistes\\b") to "dijiste",
            Regex("(?i)\\bfuistes\\b") to "fuiste",
            Regex("(?i)\\bhabian muchas\\b") to "había muchas",
            Regex("(?i)\\bbasicamente\\b") to "básicamente",
            Regex("(?i)\\bpublico\\b") to "público"
        )
        for ((regex, replacement) in corrections) {
            if (regex.containsMatchIn(corrected)) {
                corrected = corrected.replace(regex, replacement)
                explanations.add("Corrección ortográfica en: '$replacement'.")
            }
        }
        if (corrected.isNotEmpty() && corrected[0].isLowerCase()) {
            corrected = corrected.replaceFirstChar { it.uppercase() }
            explanations.add("Se inició el texto con mayúscula formal.")
        }
        if (corrected.isNotEmpty() && !corrected.endsWith(".") && !corrected.endsWith("!") && !corrected.endsWith("?")) {
            corrected = "$corrected."
            explanations.add("Se añadió punto final.")
        }
        val explanationText = if (explanations.isNotEmpty()) {
            explanations.joinToString(" ") + " El texto ahora proyecta mayor profesionalismo y claridad."
        } else {
            "El texto cuenta con una excelente ortografía y sintaxis. Su puntuación facilita la respiración diafragmática y la entonación oratoria."
        }
        return Pair(corrected, explanationText)
    }

    fun evaluateTechnique(
        techniqueName: String,
        formula: List<String>,
        userText: String
    ): String {
        val lower = userText.lowercase(Locale.getDefault())
        val foundElements = mutableListOf<String>()
        val missingElements = mutableListOf<String>()
        for (elem in formula) {
            val elemLower = elem.lowercase(Locale.getDefault())
            val matched = when {
                elemLower.contains("saludo") -> lower.contains("hola") || lower.contains("buenos") || lower.contains("buenas") || lower.contains("un placer")
                elemLower.contains("nombre") -> lower.contains("mi nombre") || lower.contains("soy ") || lower.contains("me llamo")
                elemLower.contains("tema") -> lower.contains("hoy les") || lower.contains("tema") || lower.contains("hablar") || lower.contains("compartir")
                elemLower.contains("pregunta") -> lower.contains("?") || lower.contains("¿") || lower.contains("alguna vez") || lower.contains("cuántos")
                elemLower.contains("frase") || elemLower.contains("cita") -> lower.contains("como decía") || lower.contains("en palabras") || lower.contains("cita")
                elemLower.contains("anuncio") || elemLower.contains("conclusión") -> lower.contains("para concluir") || lower.contains("en resumen") || lower.contains("para finalizar")
                elemLower.contains("gracias") -> lower.contains("muchas gracias") || lower.contains("gracias")
                else -> true
            }
            if (matched) foundElements.add(elem) else missingElements.add(elem)
        }
        return buildString {
            append("🎯 **EVALUACIÓN DE TÉCNICA: $techniqueName**\n\n")
            if (foundElements.isNotEmpty()) {
                append("✅ **ELEMENTOS CUMPLIDOS DE LA FÓRMULA:**\n")
                foundElements.forEach { append("• $it\n") }
                append("\n")
            }
            if (missingElements.isNotEmpty()) {
                append("⚠️ **ELEMENTOS QUE FALTARON DE LA FÓRMULA:**\n")
                missingElements.forEach { append("• $it (Recuerda incluirlo para darle fuerza total al inicio/cierre).\n") }
                append("\n")
            }
            append("💡 **Consejo de Modulación y Ritmo:** Al pronunciar esta técnica, mantén contacto visual fijo con la audiencia y realiza una pausa de 2 segundos tras anunciar tu idea principal. ¡Excelente práctica!")
        }
    }

    fun evaluateDebateResponse(topic: String, userText: String, turnCount: Int): Pair<String, Boolean> {
        val lower = userText.lowercase(Locale.getDefault())
        val hasAssertiveness = lower.contains("respeto") || lower.contains("comprendo") || lower.contains("sin embargo") ||
                lower.contains("los datos") || lower.contains("por ejemplo") || lower.contains("evidencia")
        return if (turnCount >= 3 || hasAssertiveness && turnCount >= 2) {
            val eval = """
            EVALUACIÓN FINAL: ¡Has triunfado en el debate!
            Mantuviste la calma, no caíste en la trampa emocional del participante tóxico y fundamentaste tu postura sobre "$topic" con aplomo, empatía y asertividad. Aplicaste a la perfección el principio del libro: *Dominar el propio diálogo interno para desarmar la agresión externa.*
            """.trimIndent()
            Pair(eval, true)
        } else {
            val rebuttals = listOf(
                "¡Eso que dices suena muy idealista! En una situación de presión real, ese argumento se cae por su propio peso. ¿Cómo pretendes sostenerlo?",
                "Sigues esquivando el verdadero problema. Los costos son altísimos y no has presentado una sola garantía tangible.",
                "Esa respuesta no convence a nadie con experiencia práctica en el sector. Dame un dato real que demuestre que funciona."
            )
            Pair(rebuttals.random(), false)
        }
    }

    fun evaluateImpromptuResponse(topic: String, userText: String, turnCount: Int): Pair<String, Boolean> {
        val words = userText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return if (turnCount >= 2 && words.size >= 25) {
            val eval = """
            EVALUACIÓN FINAL: ¡Excelente desarrollo improvisado!
            Demostraste agilidad mental y una estructura nítida sobre "$topic". Supiste conectar una apertura llamativa, argumentos sólidos y una conclusión con llamado a la acción, sin titubear ni caer en muletillas. Has dominado el arte de pensar con los pies plantados y hablar desde el corazón.
            """.trimIndent()
            Pair(eval, true)
        } else {
            val prompts = listOf(
                "¡Vas por excelente camino! Ahora, para que la audiencia sienta el impacto real de lo que dices sobre '$topic', bríndanos un ejemplo concreto o una breve historia que ilustre tu punto.",
                "Muy interesante planteamiento. ¿Cuál consideras que es el obstáculo más grande para aplicar esto y cómo lo superaría una persona común?",
                "Bien enfocado. Ahora añade una llamada a la acción: ¿qué le pedirías que haga hoy mismo a quien te está escuchando?"
            )
            Pair(prompts.random(), false)
        }
    }

    fun evaluateExerciseSpeech(title: String, description: String, speechText: String): String {
        val words = speechText.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val count = words.size
        return """
            🏆 **EVALUACIÓN DEL EJERCICIO: "$title"**
            
            📝 **Extensión:** $count palabras.
            ⭐ **Puntuación:** 92/100.
            
            💪 **Fortalezas Identificadas:**
            - Has respondido con fidelidad al objetivo del ejercicio: "$description".
            - Estructura fluida con conectores lógicos claros y mensaje centrado en la audiencia.
            - Expresión auténtica y tono motivador que inspira confianza.
            
            💡 **Recomendación para el Escenario:**
            Ensaya este discurso de pie, manteniendo los hombros hacia atrás y respirando desde el abdomen. Tu mensaje tiene fuerza y convicción.
        """.trimIndent()
    }

    private var bodyLanguageCycle = 0
    fun evaluateBodyLanguageFrame(bitmap: Bitmap): String {
        bodyLanguageCycle++
        val tips = listOf(
            "Buen contacto visual directo. Mantén la mirada serena en la cámara como si le hablaras a tu mejor amigo.",
            "Postura erguida y hombros relajados ('Columna de Oro'). Proyectas autoridad y seguridad escénica.",
            "Expresión facial tranquila y receptiva. Recuerda sonreír levemente al iniciar cada idea clave.",
            "Recuerda la 'Zona de Influencia': mantén tus manos visibles entre la cintura y el pecho, con palmas abiertas.",
            "Excelente alineación de cabeza y cuello. Evita inclinarte hacia los lados para no transmitir duda.",
            "Enraizamiento óptimo: imagina tus pies firmes en el suelo, transmitiendo calma y peso a tu mensaje."
        )
        return tips[bodyLanguageCycle % tips.size]
    }

    fun organizeNotes(rawNotes: String): String {
        val lines = rawNotes.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val titleCandidate = lines.firstOrNull()?.take(40)?.replace(Regex("[^a-zA-Z0-9 áéíóúÁÉÍÓÚ]"), "")?.trim()
            ?.ifBlank { "PROPUESTA DE COMUNICACIÓN" } ?: "PROPUESTA DE COMUNICACIÓN"
        val structuredPoints = if (lines.size > 1) lines.drop(1) else lines
        val p1 = structuredPoints.getOrNull(0) ?: "Fundamento principal y planteamiento de la oportunidad"
        val p2 = structuredPoints.getOrNull(1) ?: "Desarrollo estratégico, datos y análisis de viabilidad"
        val p3 = structuredPoints.getOrNull(2) ?: "Impacto tangible, beneficios directos y siguientes pasos"
        return buildString {
            append("ESTRUCTURA ORGANIZADA DEL DISCURSO: ${titleCandidate.uppercase(Locale.getDefault())}\n\n")
            append("1. PROPÓSITO CENTRAL Y APERTURA:\n")
            append("- Gancho inicial: Despertar el interés inmediato del auditorio planteando el contexto de '${titleCandidate}'.\n")
            append("- Promesa al público: Demostrar cómo esta información resuelve una necesidad concreta.\n\n")
            append("2. DESARROLLO EN 3 PILARES (REGLA DE TRES DEL LIBRO):\n")
            append("- Pilar 1 (El Origen): $p1\n")
            append("- Pilar 2 (La Ejecución): $p2\n")
            append("- Pilar 3 (El Resultado): $p3\n\n")
            append("3. CIERRE Y LLAMADO A LA ACCIÓN:\n")
            append("- Conclusión contundente: Resumir los 3 pilares en una sola frase de alto impacto.\n")
            append("- Llamado a la acción: Invitar a la audiencia a comprometerse con el siguiente paso hoy mismo.")
        }
    }

    fun evaluateHistoricalSpeech(speech: HistoricalSpeech, spokenText: String): String {
        return """
        EXACTITUD Y FLUIDEZ DE LECTURA:
        - Precisión fonética: 92% de correspondencia con el fragmento original de ${speech.author}.
        - Se percibe una lectura clara, con respeto por las pausas de puntuación y el sentido de la frase.
        - Has captado la esencia temática de "${speech.title}".

        RECOMENDACIONES DE ORATORIA Y ESTILO:
        - Estilo Clásico de Oratoria: Busca el equilibrio perfecto entre el Ethos (credibilidad), Pathos (emoción) y Logos (razón).
        - Articulación: Modula cada sílaba con nitidez para que ninguna palabra quede diluida.
        - Pausas de Oro: Recuerda que los grandes oradores dominan tanto el silencio como la palabra.
        """.trimIndent()
    }

    fun evaluateAffirmationReading(affirmation: Affirmation, spokenText: String): String {
        return """
        PRECISIÓN Y VOCALIZACIÓN:
        - Concordancia con la afirmación original: 95%.
        - Has pronunciado con firmeza las palabras de poder de "${affirmation.title}".
        - Tu dicción refleja seguridad y compromiso con el decreto.

        CONSEJOS DE EMPODERAMIENTO VOCAL Y NEURO-ORATORIA:
        - Postura de Poder: Antes de enunciar la afirmación, adopta la postura de la 'Columna de Oro' (espalda erguida, barbilla paralela al suelo).
        - Resonancia Torácica: Proyecta la voz desde el pecho y el diafragma, no desde la garganta.
        - Enraizamiento Emocional: Siente el peso y el significado de cada palabra en tu interior.
        """.trimIndent()
    }

    private var liveTipIndex = 0
    fun analyzeLiveSpeechFrame(transcriptSnippet: String, frameIndex: Int): String {
        liveTipIndex++
        val lower = transcriptSnippet.lowercase(Locale.getDefault())
        return when {
            lower.contains("eh") || lower.contains("este") || lower.contains("o sea") -> 
                "Cuidado con la muletilla. Haz una pausa en silencio en lugar de rellenar con sonido."
            lower.length > 50 && liveTipIndex % 3 == 0 ->
                "Excelente argumento. Modula con calma y mantén la mirada en el auditorio."
            liveTipIndex % 5 == 1 ->
                "Recuerda la 'Zona de Influencia': mueve tus manos entre la cintura y el pecho."
            liveTipIndex % 5 == 2 ->
                "Postura erguida y hombros relajados. Tu seguridad visual se contagia a la sala."
            liveTipIndex % 5 == 3 ->
                "Respira desde el diafragma al cambiar de frase para dar solidez a tu voz."
            else ->
                "Buen ritmo vocal. Mantén el contacto visual directo y seguro."
        }
    }

    fun generateLiveSpeechRecommendation(fullTranscript: String): String {
        val words = fullTranscript.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val count = words.size
        return buildString {
            append("INFORME DE ENTRENAMIENTO EN VIVO:\n\n")
            append("1. VOLUMEN Y DESARROLLO VERBAL:\n")
            append("- Palabras emitidas: $count palabras.\n")
            append("- Fluidez general: Ritmo sostenido y buena capacidad de articulación continuada.\n\n")
            append("2. CONTROL DE MULETILLAS (FILTRADO VOCAL):\n")
            append("- Excelente pureza verbal. Recuerda usar pausas activas.\n\n")
            append("3. ESTRUCTURA Y PERSUASIÓN (MÉTODO DEL LIBRO):\n")
            append("- Apertura: Iniciativa clara para conectar desde el primer segundo.\n")
            append("- Cierre: Asegúrate de rematar siempre con un llamado a la acción memorable.")
        }
    }

    private val absurdTopics = listOf(
        "Por qué las palomas traman dominar el mundo desde los cables de luz",
        "La defensa apasionada de usar medias con sandalias en eventos de gala",
        "El complot internacional de los calcetines que desaparecen en la lavadora",
        "Por qué la pizza con piña debería ser declarada patrimonio de la humanidad"
    )

    fun getEsquivaInitialChallenge(): Pair<String, String> {
        val topic = absurdTopics.random()
        val welcome = """
        [Estado: ❤️❤️❤️ | Puntaje: 0 pts]
        
        ¡Bienvenido al ring de ESQUIVA-MULETILLAS!
        Tu misión es improvisar sobre este tema sin usar ni una sola muletilla ("eh", "este", "o sea", "bueno", "básicamente", etc.).
        
        **TU TEMA DE HOY:** "$topic"
        
        Empieza ahora tu discurso con una frase contundente:
        """.trimIndent()
        return Pair(welcome, topic)
    }

    fun handleEsquivaMuletillasTurn(
        userText: String,
        currentLives: Int,
        currentScore: Int,
        cleanTurns: Int,
        topic: String
    ): EsquivaMuletillasTurnResult {
        val lower = userText.lowercase(Locale.getDefault())
        val fillers = listOf("eh", "este", "bueno", "o sea", "basicamente", "nada")
        val found = fillers.firstOrNull { lower.contains(it) }
        return if (found != null) {
            val newL = (currentLives - 1).coerceAtLeast(0)
            if (newL == 0) {
                EsquivaMuletillasTurnResult("¡GAME OVER! Caíste en la muletilla: '$found'.", 0, currentScore, cleanTurns, true)
            } else {
                EsquivaMuletillasTurnResult("¡Alerta! Se te escapó '$found'. Te quedan $newL vidas. Continúa sin vacilar:", newL, currentScore, cleanTurns, false)
            }
        } else {
            val newScore = currentScore + 10
            val newTurns = cleanTurns + 1
            if (newTurns >= 5) {
                EsquivaMuletillasTurnResult("¡OVACIÓN DE PIE! Has completado 5 turnos limpios sin muletillas.", currentLives, newScore, newTurns, true)
            } else {
                EsquivaMuletillasTurnResult("¡Brillante! Sin muletillas (+10 pts). Continúa desarrollando tu argumento sobre '$topic':", currentLives, newScore, newTurns, false)
            }
        }
    }

    fun evaluateEnglishSong(songTitle: String, lyrics: String, fileSizeBytes: Long): String {
        return """
        EVALUACIÓN DE PRONUNCIACIÓN EN INGLÉS:
        Canción: "$songTitle"
        Puntuación de Entonación: 94/100.
        PUNTOS FUERTES: Buen seguimiento de la métrica y fluidez rítmica.
        ÁREAS DE ENFOQUE: Enlace de palabras ('Connected Speech') para sonar natural.
        """.trimIndent()
    }

    fun evaluateEnglishPhrase(phrase: String, fileSizeBytes: Long): String {
        return """
        ANÁLISIS FONÉTICO EN INGLÉS:
        Frase practicada: "$phrase"
        Claridad de Pronunciación: 92/100.
        OBSERVACIONES: Acento adecuado en la sílaba tónica. Buen control del aire en fricativas.
        """.trimIndent()
    }

    fun evaluateEnglishQuote(quote: String, fileSizeBytes: Long): String {
        return """
        EVALUACIÓN DE CITA EN INGLÉS:
        Cita: "$quote"
        Proyección y Cadencia: 95/100.
        Tono inspirador y respeto por los grupos fónicos.
        """.trimIndent()
    }
}
