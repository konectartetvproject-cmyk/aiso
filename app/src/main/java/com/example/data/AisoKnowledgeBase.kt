package com.example.data

import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

data class KnowledgeItem(
    val id: String,
    val title: String,
    val category: String,
    val keywords: List<String>,
    val response: String,
    val suggestedFollowUps: List<String> = emptyList()
)

data class QuickTopicChip(
    val label: String,
    val query: String,
    val iconName: String
)

object AisoKnowledgeBase {
    val QUICK_CHIPS = listOf(
        QuickTopicChip("✍️ Analizar Discurso Escrito", "analiza mi discurso: Buenos días a todos, mi nombre es Juan y hoy quiero compartir con ustedes cómo superar el miedo al qué dirán y alcanzar nuestras metas...", "edit_note"),
        QuickTopicChip("🛡️ Preguntas del Público", "como responder preguntas del publico", "shield"),
        QuickTopicChip("❓ Si no sé la respuesta", "que hago si no se la respuesta", "help"),
        QuickTopicChip("⚠️ Pregunta hostil", "como responder pregunta agresiva o hostil", "warning"),
        QuickTopicChip("🚩 Los 11 Pasos", "cuales son los 11 pasos para hablar en publico", "flag"),
        QuickTopicChip("👥 Participantes Tóxicos", "como manejar participantes toxicos tauromaquia", "group"),
        QuickTopicChip("💨 Miedo y Respiración", "tecnicas de respiracion para el miedo escenico", "air"),
        QuickTopicChip("🎤 Técnicas de Inicio", "tecnicas de inicio para discurso", "mic"),
        QuickTopicChip("🔇 Silencio en Preguntas", "que hacer si nadie pregunta silencio", "volume_off"),
        QuickTopicChip("🗣️ Voz y Dicción", "como modular la voz y mejorar diccion", "record_voice_over"),
        QuickTopicChip("⭐ Trinidad de la Influencia", "que es la trinidad de la influencia", "star")
    )

    val ITEMS = listOf(
        KnowledgeItem(
            id = "qa_overview",
            title = "Estrategia General para Responder Preguntas del Público",
            category = "Preguntas del Público",
            keywords = listOf("preguntas del publico", "ronda de preguntas", "sesion de preguntas", "q&a", "preguntas y respuestas", "como responder preguntas", "responder al publico", "audiencia pregunta"),
            response = """
                🎙️ **El Arte de Dominar la Ronda de Preguntas (Q&A)**
                
                La sesión de preguntas no es un examen, es la oportunidad dorada para **consolidar tu liderazgo y empatía**.
                
                Usa el método de 4 pasos **E.R.R.G.** del orador exitoso:
                
                1. **Escucha Activa Total:** No interrumpas. Mira a los ojos de quien pregunta y asiente levemente.
                2. **Reformula o Parafrasea:** Repite brevemente la pregunta para que todo el auditorio la escuche y para ganar 3 segundos de reflexión: *"Si entiendo bien, tu inquietud es cómo implementar esto con poco presupuesto..."*.
                3. **Responde Directo y Conciso:** Ve al grano en los primeros 15 segundos. Evita rodeos que confundan a la audiencia.
                4. **Gira la Mirada a Toda la Sala:** No te quedes mirando solo al que preguntó. Termina mirando a todo el público para mantenerlos conectados.
                
                💡 *Regla de Oro:* Tu actitud debe ser siempre de agradecimiento, jamás de confrontación ni soberbia.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "¿Qué hago si no sé la respuesta?",
                "¿Cómo responder una pregunta agresiva?",
                "¿Qué hacer si hay un silencio incómodo?"
            )
        ),
        KnowledgeItem(
            id = "qa_dont_know_answer",
            title = "Qué responder si NO sabes la respuesta a una pregunta",
            category = "Preguntas del Público",
            keywords = listOf("no se la respuesta", "no se responder", "pregunta que no se", "si no se", "desconozco el dato", "no se la respuesta a una pregunta", "si no tengo el dato"),
            response = """
                ❓ **¿Qué hacer si te hacen una pregunta cuya respuesta NO sabes?**
                
                ⚠️ **¡NUNCA INVENTES UNA RESPUESTA!** La audiencia detecta la mentira o la duda en segundos y destruirás tu credibilidad.
                
                Aplica la técnica de la **Honestidad Estratégica**:
                
                1. **Valora la Pregunta:** *"Esa es una pregunta muy acertada y específica."*
                2. **Admite con Transparencia:** *"En este momento preciso no tengo la cifra/dato exacto en mente, y prefiero darte un dato 100% verídico antes que una aproximación."*
                3. **Compromiso de Acción:** *"Por favor acércate al finalizar para tomar tu contacto o correo, y hoy mismo te comparto esa información detallada."*
                4. **Apaláncate en la Audiencia (Alternativa experta):** *"¿Alguien aquí en el salón tiene experiencia reciente con ese caso puntual que quiera compartir en 30 segundos?"* (Esto demuestra seguridad y humildad de líder).
                
                👑 *Recuerda:* Un orador seguro no es quien lo sabe todo, sino quien sabe gestionar con elegancia lo desconocido.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "¿Cómo responder una pregunta agresiva?",
                "¿Cómo manejar a Ernesto el Erudito?"
            )
        ),
        KnowledgeItem(
            id = "qa_hostile_question",
            title = "Cómo responder preguntas agresivas o ataques personales",
            category = "Preguntas del Público",
            keywords = listOf("pregunta agresiva", "pregunta hostil", "ataque personal", "pregunta con mala fe", "quiere hacerme quedar mal", "pregunta dificil", "ofensa del publico", "pregunta toxica"),
            response = """
                🛡️ **Cómo Responder Preguntas Hostiles o de Mala Fe**
                
                Cuando alguien intenta atacarte (perfil estilo *Susana la Sniper*), su objetivo es hacerte perder los estribos o que te pongas a la defensiva.
                
                Aplica este protocolo de **Desescalamiento Inmediato**:
                
                1. **Pausa y Respiración:** Toma aire 2 segundos. No respondas en caliente. Sonríe con serenidad.
                2. **Filtra la Carga Emocional (Reformula):** Extrae el insulto y deja solo la esencia técnica.
                   - Si dicen: *"¡Tu propuesta es una tontería y nunca va a funcionar!"*
                   - Tú respondes: *"Entiendo tu preocupación sobre los desafíos de viabilidad técnica..."*
                3. **No te lo tomes como algo personal:** El ataque habla más de las frustraciones del participante que de ti.
                4. **Responde con Datos y Perspectiva:** *"Los datos del estudio muestran X resultado, aunque es natural que existan discrepancias según el contexto."*
                5. **Corta la Polémica y Gira:** No abras un debate uno a uno. Di: *"Agradezco tu punto de vista. Para avanzar con el tiempo del grupo, ¿alguien tiene otra consulta sobre el módulo B?"*
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Tácticas contra Susana la Sniper",
                "Cómo mantener la calma y respiración"
            )
        ),
        KnowledgeItem(
            id = "steps_11_all",
            title = "Los 11 Pasos para Hablar en Público (Estructura Completa)",
            category = "Los 11 Pasos",
            keywords = listOf("11 pasos", "once pasos", "pasos para hablar en publico", "metodologia de 11 pasos", "estructura del libro", "pasos del orador"),
            response = """
                🏆 **Los 11 Pasos Estructurados para Hablar en Público**
                
                Basados en el libro *"Los secretos del orador exitoso"*:
                
                1. 🚶‍♂️ **Entrar al Escenario:** Camina erguido, con paso seguro y decidido.
                2. 👀 **Mirar al Público:** No hables de inmediato. Haz una pausa de 2-3 segundos mirando a la sala para conectar.
                3. 👋 **Saludar:** Rompe el hielo con un saludo cálido y enérgico.
                4. 🎯 **Técnica de Inicio:** Usa un gancho (pregunta retórica, dato impactante, anécdota o cita).
                5. 👤 **Nombre del Orador:** Preséntate con claridad y confianza.
                6. 📌 **Nombre del Tema:** Comunica el objetivo del discurso y qué se llevará la audiencia.
                7. 💡 **Desarrollo del Discurso:** Estructura de 3 pilares lógicos con ejemplos e historias.
                8. 🔔 **Anunciar Conclusión:** Avisa a la audiencia: *"Para concluir..."* o *"En resumen..."*.
                9. 🚀 **Técnica de Conclusión:** Llamada a la acción contundente, frase célebre o historia inspiradora.
                10. 🙏 **Decir Gracias:** Agradece con humildad y respeto por la atención brindada.
                11. 👏 **Recibir Aplausos:** Mantén la postura erguida, sonríe y disfruta la ovación sin salir corriendo.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Detalle de las Técnicas de Inicio (Paso 4)",
                "Detalle de las Técnicas de Conclusión (Paso 9)",
                "Cómo entrar al escenario con seguridad"
            )
        ),
        KnowledgeItem(
            id = "toxic_all",
            title = "El Arte de la Tauromaquia Verbal: Gestionar Participantes Tóxicos",
            category = "Participantes Tóxicos",
            keywords = listOf("participantes toxicos", "tauromaquia verbal", "publico toxico", "gente dificil", "publico hostil", "perfiles toxicos", "saboteadores"),
            response = """
                🐂 **El Arte de la Tauromaquia Verbal**
                
                En toda audiencia pueden surgir participantes difíciles que buscan desestabilizarte. Tu rol como orador consciente no es aplastarlos, sino torear la situación con elegancia sin perder el control de la plaza.
                
                Los 5 Perfiles Tóxicos del libro:
                
                1. 🧐 **Ernesto el Erudito (El Sabelotodo):** Busca corregirte y demostrar que sabe más que tú.
                2. 🎯 **Susana la Sniper (La Interrogadora Hostil):** Dispara preguntas trampa para destruirte.
                3. 🎙️ **Protágoras el Protagonista (El Secuestrador):** Quiere ser el centro de atención.
                4. 😒 **Nicanor el Negativo (El Cínico Resignado):** *"Eso aquí no funciona"*.
                5. 👥 **Los Murmuradores (La Sub-audiencia Distractora):** Hablan entre ellos en voz baja y distraen.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Cómo manejar a Ernesto el Erudito",
                "Cómo manejar a Susana la Sniper",
                "Cómo manejar a Nicanor el Negativo"
            )
        ),
        KnowledgeItem(
            id = "breathing_box",
            title = "Respiración Diafragmática / Respiración de Caja (Box Breathing)",
            category = "Miedo Escénico",
            keywords = listOf("respiracion", "respiracion diafragmatica", "respiracion de caja", "box breathing", "como calmarme", "ejercicio de respiracion", "ansiedad"),
            response = """
                🧘‍♂️ **Técnica de Respiración de Caja (Box Breathing 4x4x4x4)**
                
                Es la técnica que usan los oradores profesionales y fuerzas de élite para regular el sistema nervioso parasimpático en menos de 2 minutos antes de salir al escenario:
                
                1. **Inhala por la nariz (4 segundos):** Lleva el aire hacia el abdomen (respiración diafragmática), no infles solo el pecho.
                2. **Retén el aire (4 segundos):** Mantén la calma interna con los pulmones llenos.
                3. **Exhala por la boca suavemente (4 segundos):** Suelta todo el aire sintiendo cómo descienden los hombros.
                4. **Sostén en vacío (4 segundos):** Quédate con los pulmones vacíos en absoluta paz.
                
                Repite 4 ciclos completos antes de tomar el micrófono. Tus pulsaciones bajarán de inmediato y tu voz ganará resonancia y estabilidad.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "La Voz como Instrumento",
                "Pasos 1 y 2: Entrada al Escenario"
            )
        )
    )

    fun findBestAnswer(userQuery: String): Pair<KnowledgeItem?, List<String>> {
        val cleanQuery = normalize(userQuery)
        if (cleanQuery.isBlank()) {
            return Pair(null, emptyList())
        }
        val tokens = cleanQuery.split(Pattern.compile("\\s+")).filter { it.length > 2 }
        
        var bestItem: KnowledgeItem? = null
        var maxScore = 0
        for (item in ITEMS) {
            var score = 0
            for (kw in item.keywords) {
                val cleanKw = normalize(kw)
                if (cleanQuery.contains(cleanKw)) {
                    score += 50
                }
                val kwTokens = cleanKw.split(Pattern.compile("\\s+")).filter { it.length > 2 }
                for (t in tokens) {
                    if (kwTokens.contains(t)) {
                        score += 8
                    }
                }
            }
            val cleanTitle = normalize(item.title)
            for (t in tokens) {
                if (cleanTitle.contains(t)) {
                    score += 10
                }
            }
            if (score > maxScore) {
                maxScore = score
                bestItem = item
            }
        }
        if (bestItem != null && maxScore >= 10) {
            return Pair(bestItem, bestItem.suggestedFollowUps)
        }
        return Pair(null, listOf(
            "¿Cómo responder preguntas del público?",
            "¿Qué hago si no sé la respuesta a una pregunta?",
            "¿Cuáles son los 11 pasos para hablar en público?",
            "¿Cómo manejar a los participantes tóxicos?",
            "Técnica de respiración para calmar los nervios"
        ))
    }

    fun getMentorMediaReview(mimeType: String?): String {
        return """
            🎬 **¡He recibido tu práctica de oratoria!**
            
            Como tu mentor AISO IA, aquí tienes la guía para evaluar tu presentación según los **11 Pasos** y *"Los secretos del orador exitoso"*:
            
            1. 🚶‍♂️ **Entrada y Pausa Inicial:** ¿Caminaste erguido y esperaste 3 segundos en silencio mirando a la audiencia antes de hablar?
            2. 🎯 **Técnica de Inicio:** ¿Comenzaste con una pregunta retórica, un dato impactante o una anécdota?
            3. 🗣️ **Voz y Matices:** ¿Variaste el tono y el volumen o hablaste en línea recta? Recuerda hacer una pausa dramática de 2 segundos antes de tu idea principal.
            4. 👐 **Manos y Gestos:** ¿Tus manos estuvieron visibles sobre la cintura y abiertas hacia el público?
            5. 🚀 **Cierre:** ¿Finalizaste con una llamada a la acción inspiradora y esperaste los aplausos con una sonrisa?
            
            ⭐ *Tu voz es tu instrumento más poderoso. Cada ensayo te acerca más a la maestría.*
        """.trimIndent()
    }

    fun getWelcomeMessage(): String {
        return """
            👋 ¡Hola! Soy **AISO IA**, tu mentor experto en oratoria y comunicación persuasiva.
            
            Tengo integrado todo el conocimiento de **"Los secretos del orador exitoso"** y una guía completa para **manejar preguntas del público y objeciones difíciles**, funcionando **100% en tu dispositivo y sin ningún costo**.
            
            ¿En qué te gustaría entrenar hoy?
            * ✍️ **Analizar Discurso Escrito:** Envía o pega el texto de tu discurso y AISO lo evaluará según los 11 pasos, detectará muletillas y te entregará una versión optimizada.
            * 🛡️ **Manejo de Preguntas Difíciles del Público**
            * 🏆 **Los 11 Pasos para Hablar en Público**
            * 🐂 **Tauromaquia Verbal y Participantes Tóxicos**
            * 🧘‍♂️ **Superar el Miedo Escénico**
            * 🗣️ **Uso de la Voz y Presencia Escénica**
            
            Escribe tu pregunta o toca uno de los temas sugeridos abajo.
        """.trimIndent()
    }

    fun normalize(text: String): String {
        val nfd = Normalizer.normalize(text.lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(nfd).replaceAll("")
    }
}
