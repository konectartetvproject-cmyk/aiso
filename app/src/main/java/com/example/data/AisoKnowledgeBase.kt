package com.example.data

import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

enum class AisoSpecialistMode(
    val id: String,
    val title: String,
    val shortLabel: String,
    val subtitle: String,
    val badgeColorHex: Long,
    val promptDirective: String
) {
    MENTOR_360(
        id = "mentor_360",
        title = "Mentor AISO 360°",
        shortLabel = "🎓 Mentor 360°",
        subtitle = "Respuestas ágiles y personalizadas sobre los 18 capítulos",
        badgeColorHex = 0xFF005AC1,
        promptDirective = "Actúa como Mentor integral del Método AISO v07. Da diagnósticos directos, breves y accionables conectando teoría y práctica."
    ),
    SPEECH_ARCHITECT(
        id = "speech_architect",
        title = "Arquitecto de Discursos",
        shortLabel = "✍️ Crear Discurso",
        subtitle = "Diseña guiones con los 11 Pasos y el Método AISO (Cap. 2–5)",
        badgeColorHex = 0xFF6750A4,
        promptDirective = "Actúa como Arquitecto de Discursos AISO. Estructura cada idea en Inicio (I01–I10), Desarrollo (Concepto → Puente → Historia → Puente → Técnicas) y Conclusión (C01–C09), en versión breve lista para hablar sin leer."
    ),
    SCRIPT_AUDITOR(
        id = "script_auditor",
        title = "Auditor con Rúbrica",
        shortLabel = "🔍 Evaluar Guion",
        subtitle = "Evalúa textos con escala 0/1/2 y da «Un ajuste para la siguiente toma»",
        badgeColorHex = 0xFF006C4C,
        promptDirective = "Actúa como Auditor de la Rúbrica AISO (Cap. 6 y Hoja 4). Califica en escala 0/1/2 (Apertura, Desarrollo AISO, Cierre y Precisión RAE) y entrega exactamente UN ajuste concreto para la siguiente toma."
    ),
    OBJECTION_SPARRING(
        id = "objection_sparring",
        title = "Sparring y Objeciones",
        shortLabel = "🛡️ Sparring A.V.R.",
        subtitle = "Entrena preguntas difíciles, los 12 Casos y Persuasión (Cap. 13, 15, 18)",
        badgeColorHex = 0xFFB3261E,
        promptDirective = "Actúa como Entrenador de Sparring Verbal AISO (Cap. 13, 15 y 18). Ayuda al usuario a responder objeciones usando A.V.R. (Agradecer, Valorar, Redirigir), el Árbol A/B/C y las 60 técnicas de persuasión."
    ),
    NERVES_COACH(
        id = "nerves_coach",
        title = "Coach Antinervios",
        shortLabel = "🧘 Calma y Voz",
        subtitle = "Control escénico, Orador Consciente (IP/IT/IE) y Voz (Cap. 7, 10, 14)",
        badgeColorHex = 0xFF8B5000,
        promptDirective = "Actúa como Coach del Orador Consciente (Cap. 7, 10, 12 y 14). Ayuda a separar la sensación corporal de la tarea, aplicar IP01–IE06, frase de regreso y pausas respiratorias."
    )
}

enum class DeepDiveAction(
    val id: String,
    val label: String,
    val promptPrefix: String
) {
    STEP_BY_STEP(
        id = "step_by_step",
        label = "🔍 Profundizar",
        promptPrefix = "Profundiza paso a paso con detalles técnicos del libro v07 sobre"
    ),
    REAL_EXAMPLE(
        id = "real_example",
        label = "🎤 Ejemplo real",
        promptPrefix = "Dame un ejemplo real palabra por palabra listo para decir en voz alta sobre"
    ),
    PRACTICAL_EXERCISE(
        id = "practical_exercise",
        label = "🏋️ Ejercicio 2 min",
        promptPrefix = "Dame un ejercicio práctico de 2 minutos («Una toma y un ajuste») para entrenar"
    ),
    ADAPT_TO_MY_TOPIC(
        id = "adapt_topic",
        label = "🎯 Aplicar a mi tema",
        promptPrefix = "Adapta e integra directamente a mi tema y escenario actual lo anterior sobre"
    )
}

data class SpeakerProfile(
    val userName: String = "",
    val level: String = "Intermedio",
    val scenario: String = "Profesional / Liderazgo",
    val currentTopic: String = "",
    val mainGoal: String = "Hablar con impacto sin leer"
) {
    val isCustomized: Boolean
        get() = userName.isNotBlank() || currentTopic.isNotBlank() || scenario != "Profesional / Liderazgo"

    fun toPromptContext(): String {
        val namePart = if (userName.isNotBlank()) "Nombre del estudiante: $userName." else "Estudiante AISO."
        val topicPart = if (currentTopic.isNotBlank()) "Tema actual que está preparando: «$currentTopic»." else "Tema general de oratoria."
        return "$namePart Nivel: $level. Escenario/Público objetivo: $scenario. Meta principal: $mainGoal. $topicPart"
    }
}

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
        QuickTopicChip("✍️ Crear Discurso", "Hazme un discurso breve con el Método AISO", "auto_awesome"),
        QuickTopicChip("🔍 Evaluar mi Texto", "Evalúa con la Rúbrica AISO: Buenos días, hoy quiero hablarles de por qué escuchar antes de responder mejora nuestras decisiones", "edit_note"),
        QuickTopicChip("🏆 Los 11 Pasos", "Explícame de forma breve los 11 pasos para hablar en público", "flag"),
        QuickTopicChip("🧩 Método AISO", "Cómo usar el Método AISO (Concepto, Historia y Técnicas) para hablar sin leer", "architecture"),
        QuickTopicChip("🎤 10 Inicios (I01–I10)", "Cuáles son las 10 técnicas de inicio del libro (I01 a I10)", "record_voice_over"),
        QuickTopicChip("🚀 9 Cierres (C01–C09)", "Cuáles son las 9 técnicas de conclusión del libro (C01 a C09)", "rocket_launch"),
        QuickTopicChip("🛡️ A.V.R. y Objeciones", "Cómo responder preguntas difíciles con A.V.R. y los 12 Casos", "shield"),
        QuickTopicChip("⚖️ 60 Técnicas Persuasión", "Resumen de las 60 técnicas de persuasión en sus 6 momentos", "psychology"),
        QuickTopicChip("⭐ Orador Consciente", "Cuáles son las 9 herramientas del Orador Consciente (IP, IT, IE)", "star"),
        QuickTopicChip("🥊 Sparring e Improvisar", "Cómo practicar el Sparring del Orador y la improvisación", "sports_mma")
    )

    val SCENARIO_OPTIONS = listOf(
        "Profesional / Liderazgo",
        "Ventas y Negociación",
        "Académico / Tesis / Clase",
        "Conferencia Motivacional",
        "Debate y Argumentación",
        "Zoom / Podcast / Redes",
        "Brindis o Evento Social"
    )

    val LEVEL_OPTIONS = listOf(
        "Principiante (vencer nervios)",
        "Intermedio (estructurar sin leer)",
        "Avanzado (persuasión y sparring)"
    )

    /**
     * Contexto maestro de los 18 capítulos + Cuaderno de preparación de la edición verificada v07
     * de «Los secretos del orador exitoso: Vence tus miedos, habla desde el corazón» por Carlos Enrique Lopez Adames.
     */
    const val BOOK_V07_MASTER_CONTEXT = """
LIBRO OFICIAL BASE (EDICIÓN VERIFICADA v07):
Título: «Los secretos del orador exitoso: Vence tus miedos, habla desde el corazón»
Autor: Carlos Enrique Lopez Adames (Presidente fundador de AISO — Academia Internacional de Oratoria · Método AISO).

REGLAS PEDAGÓGICAS Y ÉTICAS DEL MÉTODO AISO (v07):
1. REGLA DE PRÁCTICA AISO (HABLAR SIN LEER): La preparación puede escribirse, pero al exponer se apartan papeles, fichas y pantallas para hablar con palabras propias sin memorización mecánica. Única excepción: leer los trabalenguas en ejercicios de articulación.
2. ÉTICA DE LA VERDAD: No inventar porcentajes (%) ni estadísticas sin fuente verificada. Distinguir hechos documentados de «escenas hipotéticas». Prometer solo lo que depende del propio orador.

RESUMEN DE LOS 18 CAPÍTULOS Y CUADERNO (v07):
- Cap. 1 (Propósito y valores): Definir propósito con verbo concreto (explicar, comparar, proponer, agradecer, invitar). Declaración: «Voy a hablar a... para... porque...». Valores: Honestidad, Respeto, Claridad y Servicio.
- Cap. 2 (Método AISO — Paso 7): 3 fases unidas por puentes:
  1) CONCEPTO (qué es y por qué importa) → Puente: «Para ver esta idea en una situación concreta...» →
  2) HISTORIA (hecho documentado como Newton y el manzano según William Stukeley 1752, vivencia propia o escena hipotética) → Puente: «A partir de esta escena, propongo...» →
  3) TÉCNICAS O CONSEJOS (acciones prácticas).
- Cap. 3 (Los 11 Pasos en 3 momentos):
  * I. Preparar el encuentro: 1. Entrar al escenario (sin papeles), 2. Mirar al público, 3. Saludar.
  * II. Compartir el mensaje: 4. Técnica de inicio (antes del nombre), 5. Nombre del orador, 6. Nombre del tema, 7. Desarrollo con el Método AISO (CONCEPTO → HISTORIA → TÉCNICAS).
  * III. Cerrar el encuentro: 8. Anunciar conclusión («Para concluir...»), 9. Técnica de conclusión, 10. Decir gracias, 11. Recibir aplausos con calma.
- Cap. 4 (10 Técnicas de Inicio I01–I10): Orden: Saludo → Técnica de inicio → Nombre del orador → Nombre del tema.
  I01 Interrogación retórica | I02 Frases | I03 Narrar historias (Edison, Miguel Ángel, capullo de mariposa, samurái y maestro zen, taza de té) | I04 Experiencias de vida | I05 Datos verificados con fuente | I06 Muestras de beneficios | I07 Talentos y elementos (cubo de Rubik, espejo, cantar) | I08 Palabras llamativas | I09 Actualidad | I10 Unificación.
- Cap. 5 (9 Técnicas de Conclusión C01–C09): Orden: Anunciar conclusión → Técnica de conclusión → Decir gracias → Recibir aplausos.
  C01 Dinámica (voluntaria) | C02 Expresiones | C03 Visión | C04 Énfasis | C05 Moraleja | C06 Historia personal | C07 Invitación | C08 Compromiso (solo lo que depende del orador) | C09 Unificación.
- Cap. 6 (Audiencia y Rúbrica AISO): Mapa de la audiencia (4 preguntas), Ciclo Observa → Pregunta → Adapta, 6 técnicas de interacción y Rúbrica AISO (escala 0 / 1 / 2).
- Cap. 7 (Nervios): Separar sensación de tarea; frase de regreso («La idea central de este punto es...»); diferenciar Autoestima, Confianza y Seguridad.
- Cap. 8 y 9 (Idea central e Historias): Idea en una oración; historia en 3 movimientos (situación → dificultad → decisión y aprendizaje; ej. Steve Jobs en Stanford, 2005).
- Cap. 10 y 11 (Voz, 50 Trabalenguas y Precisión RAE/DPD): Ritmo por unidades de sentido, pausas, énfasis selectivo, cuidado vocal NIDCD, práctica «Dos tomas, un ajuste» (sin objetos en la boca). Precisión RAE: «hubo varias preguntas», «estaban medio confundidos», «según / a partir de», «diferente de».
- Cap. 12 (Presencia y 8 Gestos): Revisión en dos pasadas (1ª con audio, 2ª sin sonido). 8 gestos: 1. Palmas abiertas, 2. Palma hacia abajo, 3. OK, 4. Puño cerrado, 5. Conteo con dedos, 6. Manos entrelazadas, 7. «Mírame», 8. «Cállate» (señal acordada y respetuosa).
- Cap. 13 (Persuasión ética y 60 Técnicas): Ethos, Logos y Pathos + 60 Técnicas en 6 momentos: 01. Preparar (T01, T08), 02. Descubrir (T02–T07), 03. Explicar y demostrar (T09–T27), 04. Facilitar la elección (T28–T35), En todos los momentos (T37–T40), 05. Aclarar objeciones (T36, T41–T45), 06. Cuidar el intercambio (T46–T60 + Árbol A: Aclarar ahora / B: Verificar dato / C: Excede el alcance).
- Cap. 14 (El Orador Consciente Antes/Durante/Después):
  * Intrapersonal: Diario Emocional (IP01–02), Respiración Consciente (IP03–04), Brújula de Valores (IP05–06).
  * Interpersonal: Mapa de Audiencia (IT01–02), Escucha Profunda (IT03–04), Calibrador Social (IT05–06).
  * Emocional: Pausa Estratégica (IE01–02), Alquimia Cognitiva (IE03–04), Termostato Emocional (IE05–06).
- Cap. 15 (Preguntas y 12 Casos): Recurso A.V.R. (Agradecer, Valorar, Redirigir), secuencia Observa → Comprueba → Responde → Limita y continúa, triángulo de la mirada y Casos 01 a 12.
- Cap. 16–18 y Cuaderno: Contextos virtuales/podcasts, 5 prácticas de improvisación, Sparring del Orador (3 modalidades: Defensa de la fortaleza, Pensar de pie, Caminar sobre el alambre) y 5 Hojas de trabajo.
"""

    val ITEMS = listOf(
        KnowledgeItem(
            id = "metodo_aiso_desarrollo",
            title = "El Método AISO: Concepto, Historia y Técnicas (Cap. 2)",
            category = "Método AISO (Cap. 2)",
            keywords = listOf("metodo aiso", "concepto historia tecnicas", "desarrollo del discurso", "paso 7", "como desarrollar un discurso", "tres fases", "puentes de transicion"),
            response = """
                🧩 **El Método AISO en 3 Fases (Cap. 2)**
                
                Para desarrollar tu discurso (**Paso 7**) con fluidez y **sin leer**, sigue esta ruta mental:
                
                1. **CONCEPTO:** Qué es tu idea central y por qué importa hoy.
                   - *Puente:* «Para ver esta idea en una situación concreta...»
                2. **HISTORIA:** Hecho documentado (ej. Newton, 1752), vivencia real o escena hipotética.
                   - *Puente:* «A partir de esta escena, propongo...»
                3. **TÉCNICAS:** 2 o 3 acciones concretas para aplicar hoy.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Ver el discurso modelo de Isaac Newton (Cap. 2)",
                "¿Cómo hacer los puentes de transición con naturalidad?",
                "Crear un discurso con las 3 fases sobre mi tema"
            )
        ),
        KnowledgeItem(
            id = "newton_model_speech",
            title = "Discurso Modelo de Isaac Newton (Cap. 2)",
            category = "Método AISO (Cap. 2)",
            keywords = listOf("newton", "isaac newton", "manzano", "discurso modelo", "stukeley", "ejemplo de las 3 fases"),
            response = """
                🍎 **Ejemplo Oficial AISO: Isaac Newton (Cap. 2)**
                
                • **1. Concepto:** La curiosidad activa consiste en hacernos preguntas ante lo que otros dan por sentado.
                • **2. Historia** *(Puente: «Para verlo en un hecho documentado...»)*: En 1752, William Stukeley relató cómo Newton, al ver caer una manzana en Woolsthorpe, se preguntó por qué siempre caía en línea recta hacia el centro de la Tierra.
                • **3. Técnicas** *(Puente: «A partir de esta escena, propongo...»)*: 1) Anota una pregunta diaria sobre tu trabajo; 2) Observa un problema antes de juzgarlo.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Crear un discurso con esta misma estructura para mi tema",
                "Ver las 10 Técnicas de Inicio (I01–I10)",
                "¿Cómo distinguir hecho documentado de escena hipotética?"
            )
        ),
        KnowledgeItem(
            id = "steps_11_all",
            title = "Los 11 Pasos para Hablar en Público (Cap. 3)",
            category = "Los 11 Pasos (Cap. 3)",
            keywords = listOf("11 pasos", "once pasos", "pasos para hablar en publico", "estructura del libro", "pasos del orador", "hablar sin leer", "regla de practica aiso"),
            response = """
                🏆 **Los 11 Pasos AISO en 3 Momentos (Cap. 3)**
                
                **I. Preparar:** `1` Entrar sin papeles · `2` Mirar al público · `3` Saludar.
                **II. Compartir:** `4` Técnica de inicio (`I01–I10`, *antes de tu nombre*) · `5` Nombre del orador · `6` Nombre del tema · `7` Desarrollo AISO (`CONCEPTO → HISTORIA → TÉCNICAS`).
                **III. Cerrar:** `8` Anunciar conclusión (*«Para concluir...»*) · `9` Técnica de cierre (`C01–C09`) · `10` Decir gracias · `11` Recibir aplausos con calma.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "¿Por qué la Técnica de Inicio (Paso 4) va antes de mi nombre?",
                "Ver las 10 Técnicas de Inicio (I01–I10)",
                "Ver las 9 Técnicas de Conclusión (C01–C09)"
            )
        ),
        KnowledgeItem(
            id = "step_4_before_name",
            title = "Por qué la Técnica de Inicio va antes del Nombre (Cap. 3 y 4)",
            category = "Técnicas de Inicio (Cap. 4)",
            keywords = listOf("antes de mi nombre", "antes del nombre", "por que el paso 4", "orden de inicio", "saludo tecnica nombre"),
            response = """
                🎯 **¿Por qué el Paso 4 va antes de decir tu nombre? (Cap. 3 y 4)**
                
                Si dices *«Hola, soy Juan y vengo a hablar de...»* al segundo 1, la audiencia aún está distraída y no tiene un motivo para escucharte.
                
                • **Orden AISO:** `3. Saludo → 4. Técnica de Inicio (I01–I10) → 5. Tu Nombre → 6. Tu Tema`.
                • **Efecto:** Primero despiertas curiosidad e interés con una pregunta, frase o historia breve; cuando toda la sala te mira con atención, te presentas con autoridad.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Dame 3 ejemplos listos de inicio I01, I03 e I08",
                "Cuáles son las 10 Técnicas de Inicio (I01–I10)",
                "Hazme una apertura de impacto para mi tema"
            )
        ),
        KnowledgeItem(
            id = "opening_techniques_v07",
            title = "Las 10 Técnicas de Inicio: I01 a I10 (Cap. 4)",
            category = "Técnicas de Inicio (Cap. 4)",
            keywords = listOf("tecnicas de inicio", "10 tecnicas de inicio", "diez formas de abrir", "como empezar un discurso", "inicio de impacto", "paso 4", "i01", "i02", "i03", "i04", "i05", "i06", "i07", "i08", "i09", "i10"),
            response = """
                🎤 **Las 10 Técnicas de Inicio AISO (Cap. 4)**
                *(Orden: Saludo → Técnica → Tu Nombre → Tu Tema)*
                
                • **`I01` Interrogación retórica:** Pregunta para reflexionar en silencio.
                • **`I02` Frases:** Cita verificada o propia + breve reflexión.
                • **`I03` Narrar historias:** Relato breve (Edison, la mariposa, taza de té).
                • **`I04` Experiencias de vida** | **`I05` Datos con fuente verificada**.
                • **`I06` Beneficios** | **`I07` Talentos/elementos** (cubo de Rubik, espejo).
                • **`I08` Palabras llamativas** | **`I09` Actualidad** | **`I10` Unificación**.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Cuéntame las historias del Cap. 4 (Mariposa, Samurái, Taza de té)",
                "Dame ejemplos listos para usar de I01, I06 e I08",
                "¿Cuáles son las 9 Técnicas de Conclusión (C01–C09)?"
            )
        ),
        KnowledgeItem(
            id = "cap4_stories_examples",
            title = "Historias Clásicas del Capítulo 4 (I03)",
            category = "Técnicas de Inicio (Cap. 4)",
            keywords = listOf("mariposa", "samurai", "taza de te", "edison", "miguel angel", "historias del cap 4", "capullo"),
            response = """
                📖 **Historias Breves para Abrir con `I03` (Cap. 4)**
                
                • **El capullo de mariposa:** Un hombre cortó el capullo para «ayudarla», pero la mariposa nunca pudo volar porque el esfuerzo de salir fortalecía sus alas. *(Tema: resiliencia y procesos)*.
                • **La taza de té:** Un maestro zen sirvió té hasta rebosar la taza de un visitante arrogante: *«Como esta taza, no puedo enseñarte nada si antes no vacías tus prejuicios»*. *(Tema: aprendizaje y escucha)*.
                • **El samurái y el monje:** Muestra cómo la ira nos derrota y la serenidad nos da el control.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "¿Cómo conectar esta historia con mi nombre y mi tema (Pasos 5 y 6)?",
                "Ver ejemplos de I01 (Pregunta) e I08 (Palabras llamativas)",
                "¿Cómo estructurar una historia propia en 3 movimientos (Cap. 9)?"
            )
        ),
        KnowledgeItem(
            id = "closing_techniques_v07",
            title = "Las 9 Técnicas de Conclusión: C01 a C09 (Cap. 5)",
            category = "Técnicas de Conclusión (Cap. 5)",
            keywords = listOf("tecnicas de conclusion", "9 tecnicas de conclusion", "nueve formas de cerrar", "como cerrar un discurso", "como terminar una presentacion", "paso 9", "c01", "c02", "c03", "c04", "c05", "c06", "c07", "c08", "c09"),
            response = """
                🚀 **Las 9 Técnicas de Conclusión AISO (Cap. 5)**
                *(Orden: «Para concluir...» → Técnica C01–C09 → «Muchas gracias» → Aplausos)*
                
                • **`C01` Dinámica:** Participación breve y siempre voluntaria.
                • **`C02` Expresiones:** Proverbio o cita + reflexión final.
                • **`C03` Visión:** Futuro posible y cómo construirlo.
                • **`C04` Énfasis:** Reiterar en orden los puntos clave.
                • **`C05` Moraleja** | **`C06` Historia personal**.
                • **`C07` Invitación:** Acción concreta y realizable hoy.
                • **`C08` Compromiso:** Prometer solo lo que depende de ti.
                • **`C09` Unificación:** Combinar dos cierres (ej. `C04 + C07`).
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Dame ejemplos prácticos de C04 (Énfasis), C07 (Invitación) y C09",
                "¿Qué errores debemos evitar al cerrar un discurso?",
                "Diseñar el cierre perfecto para mi tema actual"
            )
        ),
        KnowledgeItem(
            id = "audience_map_it01",
            title = "Mapa de la Audiencia y Ciclo Observa–Pregunta–Adapta (Cap. 6)",
            category = "Audiencia y Rúbrica (Cap. 6)",
            keywords = listOf("mapa de la audiencia", "4 preguntas", "cuatro preguntas", "it01", "conocer a la audiencia", "observa pregunta adapta"),
            response = """
                🗺️ **Las 4 Preguntas del Mapa de la Audiencia (`IT01`, Cap. 6)**
                
                Antes de preparar tu discurso, responde:
                1. **¿Quiénes son y qué rol tienen?** (experiencia previa y contexto).
                2. **¿Qué les preocupa o necesitan resolver hoy?**
                3. **¿Qué dudas o resistencias legítimas podrían tener?**
                4. **¿Qué acción realista pueden dar al terminar de escucharme?**
                
                🔄 *En vivo aplica el ciclo:* **Observa** señales → **Pregunta** con brevedad → **Adapta** el ritmo o ejemplo.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Ayúdame a armar el Mapa de la Audiencia para mi tema",
                "¿Cómo funciona la Rúbrica AISO (escala 0 / 1 / 2)?",
                "Ver las 9 herramientas del Orador Consciente (Cap. 14)"
            )
        ),
        KnowledgeItem(
            id = "orador_consciente_v07",
            title = "El Orador Consciente: 9 Herramientas IP, IT e IE (Cap. 14)",
            category = "El Orador Consciente (Cap. 14)",
            keywords = listOf("trinidad de la influencia", "orador consciente", "intrapersonal", "interpersonal", "inteligencia emocional", "tres inteligencias", "ip01", "ip03", "it01", "it03", "ie01", "ie03", "ie05", "termostato emocional", "alquimia cognitiva"),
            response = """
                ⭐ **El Orador Consciente: 9 Herramientas (Cap. 14)**
                
                1. 🧠 **Intrapersonal:** `IP01` Diario Emocional · `IP03` Respiración Consciente (4 tiempos) · `IP05` Brújula de Valores.
                2. 🤝 **Interpersonal:** `IT01` Mapa de Audiencia · `IT03` Escucha Profunda · `IT05` Calibrador Social.
                3. 🔥 **Emocional:** `IE01` Pausa Estratégica (2 s) · `IE03` Alquimia Cognitiva (reencuadrar hacia la tarea) · `IE05` Termostato Emocional (tu calma regula la sala).
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "¿Cómo aplicar el Termostato Emocional (IE05) ante una crítica?",
                "Dame la rutina de 2 minutos antes de salir al escenario",
                "¿Cómo usar la Pausa Estratégica (IE01) en preguntas tensas?"
            )
        ),
        KnowledgeItem(
            id = "avr_and_12_cases",
            title = "Preguntas y Objeciones: A.V.R. y los 12 Casos (Cap. 15)",
            category = "Preguntas y Manejo de Público (Cap. 15)",
            keywords = listOf("preguntas del publico", "ronda de preguntas", "avr", "a.v.r", "agradecer valorar redirigir", "12 casos", "doce casos", "situaciones dificiles", "participantes toxicos", "objeciones", "interrupciones"),
            response = """
                🛡️ **Responder Objeciones con el Recurso A.V.R. (Cap. 15)**
                
                1. **A — Agradecer:** *«Gracias por plantear esa pregunta»*.
                2. **V — Valorar:** *«Nos permite precisar un criterio clave»*.
                3. **R — Redirigir:** Conecta con tu idea central mirando a todo el grupo (*Triángulo de la mirada*).
                
                📌 **Los 12 Casos (Cap. 15):** Quien corrige detalles (`01`), encadena preguntas (`02`), ataca indirectamente (`03`), monopoliza (`04`), dice *«eso no funciona»* (`05`), murmura (`06`), ridiculiza (`07`), alza la voz (`08`), inventa datos (`09`), interrumpe en Zoom (`10`), discrepa con respeto (`11`) o calla (`12`).
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "¿Cómo manejar los Casos 01, 03 y 05 con frases exactas?",
                "Explícame el Árbol de decisión A / B / C si no sé un dato",
                "Lánzame una objeción difícil para practicar mi respuesta A.V.R."
            )
        ),
        KnowledgeItem(
            id = "decision_tree_abc",
            title = "Árbol de Decisión A / B / C ante Objeciones y Dudas (Cap. 13 y 15)",
            category = "Persuasión Responsable (Cap. 13)",
            keywords = listOf("arbol de decision", "arbol a b c", "no se la respuesta", "no se un dato", "verificar dato", "excede el alcance"),
            response = """
                🌳 **Árbol de Decisión A / B / C ante Preguntas Complejas (Cap. 13)**
                
                Nunca inventes datos ni pierdas la calma. Elige una de estas 3 rutas éticas:
                
                • **Ruta A (Aclarar ahora):** Si tienes el dato verificado, responde en 30 segundos con un ejemplo breve.
                • **Ruta B (Verificar dato):** Si no tienes la cifra exacta hoy, di con honestidad: *«Prefiero confirmar la fuente exacta y compartírsela al finalizar»*.
                • **Ruta C (Excede el alcance):** Si desvía el tema, valida su importancia y ofrécete a conversarlo al cierre.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Practiquemos un simulacro usando la Ruta B o C",
                "Ver las 60 Técnicas de Persuasión en 6 momentos",
                "¿Cómo usar el Disco Rayado sereno (T54) ante insistencias?"
            )
        ),
        KnowledgeItem(
            id = "persuasion_60_techniques",
            title = "Las 60 Técnicas de Persuasión y Manejo del Público (Cap. 13)",
            category = "Persuasión Responsable (Cap. 13)",
            keywords = listOf("60 tecnicas", "sesenta tecnicas", "persuasion", "persuadir", "ethos", "logos", "pathos", "cialdini", "disco rayado", "capitulo 13"),
            response = """
                ⚖️ **Las 60 Técnicas de Persuasión en 6 Momentos (Cap. 13)**
                
                Integran **Credibilidad (`Ethos`)**, **Evidencia (`Logos`)** y **Relevancia humana (`Pathos`)**:
                1. **Preparar (`T01, T08`):** Conocer audiencia y dominar el tema.
                2. **Descubrir (`T02–T07`):** Escucha activa y preguntas abiertas.
                3. **Explicar y demostrar (`T09–T27`):** Historias, pruebas y contrastes.
                4. **Facilitar la elección (`T28–T35`):** Opciones claras y silencio estratégico (`T35`).
                5. **Aclarar objeciones (`T36, T41–T45`):** Validar antes de reencuadrar.
                6. **Cuidar el intercambio (`T46–T60`):** Bajar tensión y límite sereno (`T54`).
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Ver las técnicas para Facilitar la Elección (T28–T35)",
                "¿Cómo aplicar T46–T60 cuando alguien sube el tono de voz?",
                "Crear un pitch persuasivo ético para mi tema"
            )
        ),
        KnowledgeItem(
            id = "nerves_confidence_v07",
            title = "Gestionar los Nervios: Autoestima, Confianza y Seguridad (Cap. 7)",
            category = "Miedo Escénico (Cap. 7)",
            keywords = listOf("miedo escenico", "nervios", "autoestima", "confianza", "seguridad", "en blanco", "quedarme en blanco", "frase de regreso", "respiracion", "capitulo 7"),
            response = """
                🧘‍♂️ **Avanzar con los Nervios sin Quedarte en Blanco (Cap. 7)**
                
                La clave AISO es **separar lo que sientes en el cuerpo de tu tarea**:
                • **Autoestima:** Valorarte sin castigarte por un tropiezo.
                • **Confianza:** Nace de preparar tus 3 bloques (`CONCEPTO → HISTORIA → TÉCNICAS`).
                • **Seguridad:** Apoyar bien los pies, respirar en 4 tiempos y mirar rostros receptivos.
                
                💡 **Frase de regreso (si pierdes el hilo):** Respira 2 s y di: *«La idea central de este punto es...»*.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Dame la rutina de 2 minutos antes de subir al escenario",
                "¿Cómo preparar mi Hoja 2 con solo palabras clave?",
                "Practicar cómo recuperarme si olvido una parte del discurso"
            )
        ),
        KnowledgeItem(
            id = "voice_and_50_twisters_v07",
            title = "Tu Voz, Precisión RAE y 50 Trabalenguas (Cap. 10 y 11)",
            category = "Voz y Precisión (Cap. 10 y 11)",
            keywords = listOf("voz", "diccion", "trabalenguas", "50 trabalenguas", "cuidado vocal", "muletillas", "ritmo", "pausas", "tono", "precision en las palabras", "rae", "capitulo 10", "capitulo 11"),
            response = """
                🗣️ **Voz, Muletillas y Precisión Léxica RAE (Cap. 10 y 11)**
                
                • **Voz con intención:** Habla por **unidades de sentido**, usa **pausas que organizan** y **énfasis selectivo**.
                • **Cero muletillas:** Elimina **una sola a la vez** en tomas de 60 s (*«Dos tomas, un ajuste»*), cambiándola por silencio.
                • **50 Trabalenguas:** Única excepción donde se lee para entrenar dicción (sin objetos en la boca).
                • **Precisión RAE (Cap. 11):** Di *«hubo varias preguntas»* (no *hubieron*) y *«estaban medio confundidos»*.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Dame 3 trabalenguas del libro con su consigna expresiva",
                "¿Cómo hacer el ejercicio 'Dos tomas, un ajuste' paso a paso?",
                "Explícame los 8 Gestos con criterio (Cap. 12)"
            )
        ),
        KnowledgeItem(
            id = "body_and_8_gestures_v07",
            title = "Presencia y los 8 Gestos con Criterio (Cap. 12)",
            category = "Presencia y Gestos (Cap. 12)",
            keywords = listOf("lenguaje corporal", "presencia", "gestos", "ocho gestos", "8 gestos", "manos", "dos pasadas", "palmas abiertas", "capitulo 12"),
            response = """
                👐 **Los 8 Gestos con Criterio y Revisión en 2 Pasadas (Cap. 12)**
                
                `1` **Palmas abiertas** (honestidad) · `2` **Palma hacia abajo** (calma) · `3` **OK** (precisión) · `4` **Puño mesurado** (determinación) · `5` **Conteo con dedos** (`1, 2, 3`) · `6` **Manos entrelazadas con soltura** (escucha) · `7` **«Mírame»** (foco visual) · `8` **«Silencio»** (solo lúdico y acordado).
                
                🎥 **Revisión en 2 pasadas:** 1.ª con sonido (voz y estructura); 2.ª sin sonido (postura y manos).
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "¿Cuándo usar y cuándo evitar cada uno de los 8 gestos?",
                "¿Cómo manejar la mirada en salas grandes vs. videollamadas?",
                "Cómo hablar ante la cámara en Zoom o Podcast (Cap. 16)"
            )
        ),
        KnowledgeItem(
            id = "sparring_improvisation_workbook_v07",
            title = "Improvisación, Sparring del Orador y Cuaderno (Cap. 16–18)",
            category = "Entrenamiento Avanzado (Cap. 16–18)",
            keywords = listOf("sparring", "sparring del orador", "improvisacion", "improvisar", "cuaderno", "hojas de trabajo", "virtual", "zoom", "podcast", "rubrica aiso", "capitulo 16", "capitulo 17", "capitulo 18"),
            response = """
                🥊 **Improvisación y Sparring del Orador (Cap. 16–18)**
                
                • **5 Prácticas de Improvisación (Cap. 17):** *1) Un objeto, una idea · 2) Misma idea, 3 audiencias · 3) Palabra inesperada · 4) Pregunta que cambia el rumbo · 5) 3 min → 1 min → 20 s*.
                • **3 Modalidades de Sparring (Cap. 18):**
                  - **M1 Defensa de la fortaleza:** Responder objeciones con A.V.R.
                  - **M2 Pensar de pie:** 60 s para armar `CONCEPTO → HISTORIA → TÉCNICAS`.
                  - **M3 Caminar sobre el alambre:** Defender postura con serenidad.
            """.trimIndent(),
            suggestedFollowUps = listOf(
                "Lánzame ahora un reto rápido de Sparring Modalidad 2",
                "Explícame las 5 Hojas del Cuaderno de Preparación AISO",
                "Claves para hablar en Zoom, Podcasts y Reels (Cap. 16)"
            )
        )
    )

    /**
     * Motor Generativo Híbrido Personalizado (Edición v07).
     * Adapta la respuesta al perfil del orador (nombre, escenario, nivel, tema actual) y al modo especialista activo,
     * manteniendo siempre mensajes breves y fáciles de leer en móvil, con opciones de profundización.
     */
    fun generatePersonalizedSmartResponse(
        userQuery: String,
        profile: SpeakerProfile = SpeakerProfile(),
        mode: AisoSpecialistMode = AisoSpecialistMode.MENTOR_360
    ): Pair<String, List<String>> {
        val raw = userQuery.trim()
        val norm = normalize(raw)
        val effectiveTopic = extractTopic(raw, profile.currentTopic)
        val greetingName = if (profile.userName.isNotBlank()) "${profile.userName}, " else ""

        // 1. Detectar si viene de un botón de acción rápida de profundización (DeepDiveAction)
        if (norm.startsWith("profundiza paso a paso") || norm.startsWith("profundiza") || norm.startsWith("amplia")) {
            return Pair(
                buildStepByStepDeepDive(effectiveTopic, profile, mode),
                listOf(
                    "Dame un ejemplo palabra por palabra sobre $effectiveTopic",
                    "Hagamos un ejercicio práctico de 2 minutos sobre esto",
                    "Crear un discurso completo con los 11 pasos sobre $effectiveTopic"
                )
            )
        }

        if (norm.startsWith("dame un ejemplo real palabra por palabra") || norm.startsWith("dame ejemplos") || norm.contains("ejemplo real")) {
            return Pair(
                buildRealExampleExpansion(effectiveTopic, profile, mode),
                listOf(
                    "¿Cómo adaptar este ejemplo a otro tipo de público?",
                    "Dame 2 cierres (C04 y C07) para rematar este ejemplo",
                    "¿Qué objeción podrían hacerme y cómo responder con A.V.R.?"
                )
            )
        }

        if (norm.startsWith("dame un ejercicio practico de 2 minutos") || norm.contains("ejercicio practico") || norm.contains("simulacro") || norm.contains("reto rapido")) {
            return Pair(
                buildPracticalExerciseExpansion(effectiveTopic, profile, mode),
                listOf(
                    "Ya hice la Toma 1, ¿cómo evalúo mi Toma 2?",
                    "Lánzame una pregunta difícil sobre $effectiveTopic",
                    "Ver los 8 Gestos con criterio (Cap. 12) para acompañar el ejercicio"
                )
            )
        }

        if (norm.startsWith("adapta e integra directamente a mi tema")) {
            return Pair(
                buildCustomFullSpeech(effectiveTopic, profile),
                listOf(
                    "Cambiar el inicio por una historia breve (I03)",
                    "Dame 3 preguntas difíciles sobre $effectiveTopic con respuesta A.V.R.",
                    "Reducir este discurso a versión ejecutiva de 60 segundos"
                )
            )
        }

        // 2. Detectar solicitud de REDACTAR / CREAR UN DISCURSO, GUION, PITCH O BRINDIS
        val isSpeechRequest = norm.contains("hazme un discurso") ||
            norm.contains("escribe un discurso") ||
            norm.contains("escribeme un discurso") ||
            norm.contains("crea un discurso") ||
            norm.contains("redactar un discurso") ||
            norm.contains("redacta un discurso") ||
            norm.contains("necesito un discurso") ||
            norm.contains("dame un discurso") ||
            norm.contains("discurso sobre") ||
            norm.contains("discurso de ") ||
            norm.contains("discurso para ") ||
            norm.contains("discurso aplicando") ||
            norm.contains("discurso breve") ||
            norm.contains("discurso completo") ||
            norm.contains("guion sobre") ||
            norm.contains("pitch") ||
            norm.contains("brindis") ||
            (mode == AisoSpecialistMode.SPEECH_ARCHITECT && raw.length > 12)

        if (isSpeechRequest) {
            return Pair(
                buildCustomFullSpeech(effectiveTopic, profile),
                listOf(
                    "Dame 3 aperturas distintas (I01, I03, I08) para este discurso",
                    "Desarrollar más la Fase 2 (Historia) con detalles escénicos",
                    "¿Qué preguntas difíciles podrían hacerme sobre $effectiveTopic?"
                )
            )
        }

        // 3. Detectar solicitud de ANALIZAR / CORREGIR / EVALUAR CON RÚBRICA AISO
        val isAnalysisRequest = norm.startsWith("analiza") ||
            norm.startsWith("corrige") ||
            norm.startsWith("mejora") ||
            norm.startsWith("revisa") ||
            norm.startsWith("evalua") ||
            norm.contains("rubrica aiso") ||
            raw.length > 210 ||
            (mode == AisoSpecialistMode.SCRIPT_AUDITOR && raw.split(" ").size >= 12)

        if (isAnalysisRequest) {
            val textToAnalyze = raw
                .replace(Regex("(?i)^(analiza|corrige|mejora|revisa|evalua)(\\s+y\\s+mejora)?(\\s+mi\\s+discurso|\\s+este\\s+texto|\\s+este\\s+discurso)?(\\s+con\\s+la\\s+rubrica\\s+aiso)?\\s*:?\\s*"), "")
                .trim()
                .ifBlank { raw }
            return Pair(
                buildDetailedSpeechAnalysis(textToAnalyze, profile),
                listOf(
                    "Reescribe mi texto aplicando los 11 pasos completos",
                    "Dame 2 cierres (C04 y C07) para mejorar el final",
                    "¿Cómo ensayarlo hoy con 'Dos tomas, un ajuste'?"
                )
            )
        }

        // 4. Buscar coincidencia exacta en los capítulos del libro v07
        val (bestItem, followUps) = findBestAnswer(raw)
        if (bestItem != null) {
            val personalizedFooter = if (profile.currentTopic.isNotBlank()) {
                "\n\n🎯 *Para tu tema «${profile.currentTopic}» (${profile.scenario}): toca **«Aplicar a mi tema»** abajo para adaptarlo al instante.*"
            } else ""
            val customFollowUps = if (profile.currentTopic.isNotBlank()) {
                listOf("Aplicar ${bestItem.category} a mi tema «${profile.currentTopic}»") + followUps.take(2)
            } else {
                followUps
            }
            return Pair(bestItem.response + personalizedFooter, customFollowUps)
        }

        // 5. Detectar modo u objetivo de OBJECIONES / VENTAS / SPARRING
        if (mode == AisoSpecialistMode.OBJECTION_SPARRING ||
            norm.contains("vender") || norm.contains("ventas") || norm.contains("cliente") ||
            norm.contains("negociar") || norm.contains("objecion") || norm.contains("precio") || norm.contains("caro")
        ) {
            return Pair(
                buildSalesAndPersuasionGuide(effectiveTopic, profile),
                listOf(
                    "Dame una respuesta exacta con A.V.R. ante «es muy caro / no funcionará»",
                    "Crear un pitch persuasivo de 60 segundos sobre $effectiveTopic",
                    "Explicar el Árbol de decisión A / B / C (Cap. 13)"
                )
            )
        }

        // 6. Detectar modo COACH ANTINERVIOS
        if (mode == AisoSpecialistMode.NERVES_COACH) {
            return Pair(
                """
                    🧘 **Guía Consciente para ${greetingName}hablar de «$effectiveTopic» (${profile.scenario})**
                    
                    1. **Antes (`IP03`):** Inhala en 4 tiempos llevando el aire al abdomen y suelta los hombros.
                    2. **Durante (`Cap. 7`):** Separa tu sensación física de tu tarea: tu única misión hoy es entregar `Concepto → Historia → Técnicas`.
                    3. **Frase de rescate:** Si hay tensión, pausa 2 segundos (`IE01`) y di: *«Lo esencial para nosotros hoy sobre $effectiveTopic es...»*.
                """.trimIndent(),
                listOf(
                    "Hazme la tarjeta de 3 palabras clave (Hoja 2) sobre $effectiveTopic",
                    "¿Cómo empezar los primeros 15 segundos con seguridad?",
                    "Aplicar el Termostato Emocional (IE05) en mi escenario"
                )
            )
        }

        // 7. Respuesta Concisa e Interactiva adaptada al perfil y tema
        return Pair(
            buildUniversalExpertResponse(effectiveTopic, profile),
            listOf(
                "Hazme un discurso con los 11 pasos sobre $effectiveTopic",
                "Dame 3 aperturas de impacto (I01, I03, I08) sobre $effectiveTopic",
                "¿Qué objeciones podrían hacerme sobre $effectiveTopic y cómo usar A.V.R.?"
            )
        )
    }

    fun generateSmartResponse(userQuery: String): Pair<String, List<String>> {
        return generatePersonalizedSmartResponse(userQuery, SpeakerProfile(), AisoSpecialistMode.MENTOR_360)
    }

    private fun extractTopic(rawQuery: String, fallbackProfileTopic: String = ""): String {
        val cleaned = rawQuery
            .replace(Regex("(?i)^(por favor\\s+|hola\\s+|aiso\\s+)?(profundiza\\s+paso\\s+a\\s+paso\\s+con\\s+detalles\\s+tecnicos\\s+del\\s+libro\\s+v07\\s+sobre\\s+|dame\\s+un\\s+ejemplo\\s+real\\s+palabra\\s+por\\s+palabra\\s+listo\\s+para\\s+decir\\s+en\\s+voz\\s+alta\\s+sobre\\s+|dame\\s+un\\s+ejercicio\\s+practico\\s+de\\s+2\\s+minutos.*?para\\s+entrenar\\s+|adapta\\s+e\\s+integra\\s+directamente\\s+a\\s+mi\\s+tema.*?sobre\\s+|profundiza\\s+en\\s+|amplia\\s+|hazme|escribe|escribeme|crea|redacta|dame|genera|prepara|explicame|explica|hablame|que decir|como hablar|necesito|quiero)\\s+(un|una|el|la|los|las)?\\s*(discurso|guion|pitch|presentacion|charla|conferencia|exposicion|brindis|palabras|resumen|argumentos|ideas)?\\s*(breve|completo|completa|corto|corta|inspirador|inspiradora|persuasivo|persuasiva|profesional)?\\s*(con\\s+el\\s+metodo\\s+aiso\\s*|aplicando\\s+los\\s+11\\s+pasos\\s*)?(sobre|de|acerca de|para|en)?\\s*"), "")
            .trim()
            .removeSuffix("?")
            .removePrefix("¿")
            .trim()
        return when {
            cleaned.length in 3..95 && !cleaned.equals("el metodo aiso", ignoreCase = true) -> cleaned
            fallbackProfileTopic.isNotBlank() -> fallbackProfileTopic
            else -> "el liderazgo y la comunicación efectiva"
        }
    }

    private fun buildCustomFullSpeech(topic: String, profile: SpeakerProfile): String {
        val capTopic = topic.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val speakerName = profile.userName.ifBlank { "[Tu Nombre]" }
        return """
            🎙️ **Guion AISO (11 Pasos) · «$capTopic»**
            *Escenario: ${profile.scenario} · (Guía tu mente, aparta el papel al hablar)*
            
            • **1–3. Entrada y Saludo:** *(Entra sin papeles, mira 2 s)*. «Muy buenos días a todos».
            • **4. Inicio (`I01`):** «¿Qué cambia realmente en nuestro entorno cuando aplicamos con criterio **$topic**?».
            • **5–6. Nombre y Tema:** «Soy **$speakerName** y hoy hablaremos sobre **$topic**».
            • **7. Desarrollo (Método AISO):**
              - **Concepto:** «**$capTopic** significa transformar buenas intenciones en decisiones claras».
              - **Historia** *(Puente: «Para verlo en una situación concreta...»)*: «Pensemos en un equipo que destraba su mayor obstáculo cuando dedica cinco minutos a escuchar antes de actuar».
              - **Técnicas** *(Puente: «A partir de esta escena, propongo...»)*: «1) Definir la prioridad en una frase; 2) Acordar un paso medible hoy».
            • **8–11. Cierre (`C07`):** «Para concluir, los invito a aplicar la primera acción hoy mismo. Muchas gracias».
        """.trimIndent()
    }

    private fun buildDetailedSpeechAnalysis(text: String, profile: SpeakerProfile): String {
        val words = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val wordCount = words.size
        val hasQuestionHook = text.contains("?") || text.contains("¿")
        val hasConclusionSignal = normalize(text).let {
            it.contains("para concluir") || it.contains("en conclusion") || it.contains("para terminar")
        }

        return """
            🔍 **Auditoría Rápida con Rúbrica AISO (Escala 0 / 1 / 2)**
            
            • **Apertura (Pasos 3–6):** ${if (hasQuestionHook) "✅ **Nivel 2** — Buen gancho inicial (`I01`)." else "⚠️ **Nivel 1** — Abre con `I01` (pregunta) o `I03` (historia) antes de decir tu nombre."}
            • **Desarrollo ($wordCount palabras):** Marca con claridad los 2 puentes entre **`CONCEPTO → HISTORIA → TÉCNICAS`**.
            • **Cierre (Pasos 8–11):** ${if (hasConclusionSignal) "✅ **Nivel 2** — Anuncia conclusión." else "⚠️ **Nivel 1** — Añade «Para concluir...» antes de tu cierre (`C01–C09`) y «Muchas gracias»."}
            
            🎯 **Un solo ajuste para tu Toma 2 (Hoja 4):**
            Aparta el texto y dilo con tus propias palabras haciendo una pausa de 2 s tras la primera frase.
        """.trimIndent()
    }

    private fun buildSalesAndPersuasionGuide(topic: String, profile: SpeakerProfile): String {
        return """
            ⚖️ **Estrategia Persuasiva AISO (Cap. 13 y 15): «$topic»**
            *Adaptada a: ${profile.scenario}*
            
            1. **Descubre (`T02–T05`):** Abre con una pregunta abierta para detectar la prioridad real del interlocutor.
            2. **Demuestra con AISO (`T09–T27`):** Explica el **Concepto**, cuenta un **Caso real verificable** y ofrece **2 opciones claras (`T30`)** aplicando el **silencio estratégico (`T35`)**.
            3. **Blindaje A.V.R.:** Si surge una duda, **A**gradece la franqueza, **V**alora el punto y **R**edirige hacia el beneficio principal.
        """.trimIndent()
    }

    private fun buildUniversalExpertResponse(topic: String, profile: SpeakerProfile): String {
        val capTopic = topic.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val userTag = if (profile.userName.isNotBlank()) " para ${profile.userName}" else ""
        return """
            🎓 **Ruta AISO$userTag: «$capTopic»**
            *(Enfoque: ${profile.scenario} · Sin leer)*
            
            1. **CONCEPTO:** Define en una oración qué es **$topic** y qué problema resuelve hoy.
            2. **HISTORIA** *(Puente: «Para verlo en una escena concreta...»)*: Relata un caso en 3 movimientos (`situación → reto → aprendizaje`).
            3. **TÉCNICAS** *(Puente: «A partir de esta escena, propongo...»)*: Entrega 2 pasos accionables y cierra con **`C07` Invitación**.
        """.trimIndent()
    }

    private fun buildStepByStepDeepDive(topic: String, profile: SpeakerProfile, mode: AisoSpecialistMode): String {
        return """
            🔎 **Profundización Paso a Paso (v07): «$topic»**
            
            1. **Hoja 1 (Propósito):** Completa mentalmente: *«Voy a hablar a [${profile.scenario}] sobre $topic para que apliquen 2 acciones hoy»*.
            2. **Hoja 2 (Mapa de 3 palabras clave):** No memorices párrafos. Recuerda solo: **1 palabra para tu Concepto**, **1 imagen para tu Historia** y **2 verbos para tus Técnicas**.
            3. **Puesta en escena (Cap. 10 y 12):** Usa **palmas abiertas** al presentar el concepto, **conteo con dedos** en las técnicas y **pausa de 2 s** antes de decir *«Para concluir...»*.
        """.trimIndent()
    }

    private fun buildRealExampleExpansion(topic: String, profile: SpeakerProfile, mode: AisoSpecialistMode): String {
        val speakerName = profile.userName.ifBlank { "[Tu Nombre]" }
        return """
            🎤 **Ejemplo Real Listo para Decir en Voz Alta**
            
            > «Muy buenos días. *(Pausa 2 s)*. ¿Cuánto tiempo ganaríamos esta semana si tomáramos decisiones más claras sobre **$topic**? (`I01`). Soy **$speakerName** y hoy veremos cómo lograrlo.
            >
            > En esencia, **$topic** consiste en priorizar lo importante con criterio (`Concepto`). Para verlo en una situación concreta, recordemos cómo un cambio de 5 minutos en la preparación evita horas de retrabajo (`Historia`). A partir de esta escena, propongo dos acciones: primero, definir el objetivo en una línea; segundo, revisar un solo indicador al cierre (`Técnicas`).
            >
            > Para concluir, los invito a probar la primera acción hoy (`C07`). Muchas gracias».
        """.trimIndent()
    }

    private fun buildPracticalExerciseExpansion(topic: String, profile: SpeakerProfile, mode: AisoSpecialistMode): String {
        return """
            🏋️ **Ejercicio Práctico de 2 Minutos («Dos Tomas, Un Ajuste»)**
            
            • **Minuto 1 (Toma 1 sin papeles):** Ponte de pie, inhala en 4 tiempos (`IP03`) y explica en 45 segundos tu **Concepto**, **Historia** y **1 Técnica** sobre **$topic** mirando a un punto fijo.
            • **Diagnóstico (15 s):** Identifica **una sola cosa** a pulir (ej. cambiar la muletilla *«ehh»* por una pausa en silencio).
            • **Minuto 2 (Toma 2 con ajuste):** Repite la idea aplicando únicamente esa mejora y cerrando con *«Para concluir... Muchas gracias»*.
        """.trimIndent()
    }

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
        if (bestItem != null && maxScore >= 18) {
            return Pair(bestItem, bestItem.suggestedFollowUps)
        }
        return Pair(null, listOf(
            "Hazme un discurso breve con el Método AISO",
            "Ver las 10 técnicas de inicio (I01–I10)",
            "¿Cómo responder objeciones con A.V.R.?"
        ))
    }

    fun getMentorMediaReview(mimeType: String?): String {
        return """
            🎬 **Revisión Rápida en Dos Pasadas (Cap. 12)**
            
            1. **Sin leer (Regla AISO):** ¿Hablaste con palabras propias mirando al frente?
            2. **Estructura:** ¿Se notó el orden `Inicio (I01–I10) → Nombre → Tema → Concepto, Historia y Técnicas → Cierre (C01–C09)`?
            3. **Un solo ajuste (Hoja 4):** Elige una sola mejora (ej. una pausa de 2 s antes de concluir) y haz una segunda toma.
        """.trimIndent()
    }

    fun getWelcomeMessage(profile: SpeakerProfile = SpeakerProfile(), mode: AisoSpecialistMode = AisoSpecialistMode.MENTOR_360): String {
        val greeting = if (profile.userName.isNotBlank()) "👋 ¡Hola, **${profile.userName}**!" else "👋 ¡Hola!"
        val topicLine = if (profile.currentTopic.isNotBlank()) {
            "\n🎯 Tema activo: **«${profile.currentTopic}»** (${profile.scenario})."
        } else {
            "\n✨ Puedes configurar **Tu Perfil y Tema** arriba para personalizar cada consejo y guion."
        }
        return """
            $greeting Soy tu **Tutor AISO IA** en modo **${mode.title}** (*Edición v07*).$topicLine
            
            Te daré respuestas **cortas, directas y al grano**, con botones en cada mensaje para **profundizar**, ver **ejemplos reales** o practicar cuando tú quieras.
        """.trimIndent()
    }

    fun normalize(text: String): String {
        val nfd = Normalizer.normalize(text.lowercase(Locale.getDefault()), Normalizer.Form.NFD)
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+").matcher(nfd).replaceAll("")
    }
}
