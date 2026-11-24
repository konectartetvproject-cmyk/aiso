package com.example.model

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class Lesson(
    val id: String,
    val areaId: String,
    val areaName: String,
    val level: Int,
    val title: String,
    val description: String,
    val content: String,
    val anecdote: String,
    val questions: List<QuizQuestion>,
    val icon: String
)

val aisoLessons = listOf(
    // AREA 1: Propósito, Método AISO y los 11 Pasos (Cap. 1, 2 y 3)
    Lesson(
        id = "1_1",
        areaId = "1",
        areaName = "1. El Método AISO y la Regla Sin Leer (Cap. 1–3)",
        level = 1,
        title = "Propósito y Método AISO",
        description = "Desarrolla tu discurso con Concepto, Historia y Técnicas.",
        content = "En «Los secretos del orador exitoso» (Edición v07 — Carlos Enrique Lopez Adames), todo discurso parte de una Declaración de Propósito con un verbo concreto (explicar, comparar, proponer, agradecer o invitar).\n\nEl desarrollo del discurso (Paso 7) se organiza con el Método AISO en tres fases consecutivas conectadas por puentes de transición:\n1. CONCEPTO: Qué es la idea central, qué la caracteriza y por qué importa.\n2. HISTORIA: Un hecho documentado, vivencia propia o escena hipotética que haga visible el concepto (Puente: «Para ver esta idea en una situación concreta...»).\n3. TÉCNICAS O CONSEJOS: Acciones concretas que quien escucha pueda practicar (Puente: «A partir de esta escena, propongo...»).\n\nRegla de práctica AISO: La preparación puede ser escrita, pero al exponer está prohibido leer hojas, fichas o teleprompter; se habla con palabras propias.",
        anecdote = "El manzano de Isaac Newton (Cap. 2): En 1752, William Stukeley documentó su conversación con Newton sobre cómo, en 1666, ver caer una manzana en su jardín lo llevó a preguntarse por qué caía siempre perpendicularmente hacia el centro de la Tierra. El libro destaca que no existe evidencia histórica de que le golpeara la cabeza, enseñándonos a narrar con rigor y honestidad.",
        questions = listOf(
            QuizQuestion(
                text = "¿Cuáles son las tres fases del Método AISO para desarrollar el discurso en el Paso 7?",
                options = listOf(
                    "Saludo, Lectura y Despedida",
                    "CONCEPTO → HISTORIA → TÉCNICAS O CONSEJOS",
                    "Pregunta, Cita y Estadística",
                    "Improvisación, Debate y Cierre"
                ),
                correctAnswerIndex = 1,
                explanation = "El Método AISO organiza el desarrollo del discurso en tres fases claras: CONCEPTO, HISTORIA y TÉCNICAS O CONSEJOS, unidas por puentes de transición."
            ),
            QuizQuestion(
                text = "¿Qué establece la Regla de práctica AISO respecto al uso de papeles o pantallas?",
                options = listOf(
                    "Leer todo el discurso palabra por palabra",
                    "La preparación puede escribirse, pero al hablar se apartan el papel, las fichas y el teleprompter para expresarlo con palabras propias",
                    "Memorizar mecánicamente cada coma del texto",
                    "Solo hablar sin preparación previa"
                ),
                correctAnswerIndex = 1,
                explanation = "En la Academia AISO está prohibido leer durante la exposición (con la única excepción de los trabalenguas en el entrenamiento vocal)."
            )
        ),
        icon = "star"
    ),
    Lesson(
        id = "1_2",
        areaId = "1",
        areaName = "1. El Método AISO y la Regla Sin Leer (Cap. 1–3)",
        level = 2,
        title = "Los 11 Pasos en 3 Momentos",
        description = "El orden exacto desde entrar al escenario hasta recibir aplausos.",
        content = "El Capítulo 3 agrupa los 11 pasos para hablar en público en tres momentos:\n\n• Momento I — Preparar el encuentro: 1. Entrar al escenario (apartar textos), 2. Mirar al público (distribuir atención con naturalidad), 3. Saludar.\n• Momento II — Compartir el mensaje: 4. Técnica de inicio (I01–I10, antes de decir tu nombre), 5. Nombre del orador, 6. Nombre del tema, 7. Desarrollo del discurso con el Método AISO.\n• Momento III — Cerrar el encuentro: 8. Anunciar conclusión («Para concluir...»), 9. Técnica de conclusión (C01–C09), 10. Decir gracias, 11. Recibir aplausos con serenidad.",
        anecdote = "Escuchar antes de responder (Discurso modelo del Cap. 2): En una escena hipotética sobre la entrega de un proyecto en viernes, un equipo evita un conflicto mayor cuando decide escuchar la explicación completa antes de emitir un juicio.",
        questions = listOf(
            QuizQuestion(
                text = "En el orden obligatorio de apertura AISO (Pasos 3 a 6), ¿cuándo dice su nombre el orador?",
                options = listOf(
                    "Antes de saludar",
                    "Después del saludo y de la Técnica de inicio (Paso 4), justo antes del Nombre del tema (Paso 6)",
                    "Al final del discurso",
                    "No dice su nombre"
                ),
                correctAnswerIndex = 1,
                explanation = "El orden de apertura AISO es: Saludo (Paso 3) → Técnica de inicio (Paso 4) → Nombre del orador (Paso 5) → Nombre del tema (Paso 6)."
            )
        ),
        icon = "star"
    ),
    // AREA 2: Voz, Precisión y Presencia (Cap. 10, 11 y 12)
    Lesson(
        id = "2_1",
        areaId = "2",
        areaName = "2. Voz, Precisión Léxica y Presencia (Cap. 10–12)",
        level = 1,
        title = "Voz y 50 Trabalenguas",
        description = "Ritmo por unidades de sentido, pausas y precisión en las palabras.",
        content = "El Capítulo 10 trabaja la velocidad por unidades de sentido, las pausas que organizan, el tono e intención, el énfasis selectivo y el cuidado vocal (hidratación y descanso según el NIDCD). Los 50 Trabalenguas AISO se practican con consigna expresiva (Intención, Ritmo, Tono y Articulación), siendo la única excepción donde se permite leer, y siempre sin colocar objetos en la boca.\n\nEl Capítulo 11 enseña la precisión léxica según la RAE y el DPD: usar «hubo varias preguntas» (impersonal en singular), «estaban medio confundidos» (invariable), «según / a partir de» y «diferente de».",
        anecdote = "Práctica «Dos tomas, un ajuste» (Cap. 10): En lugar de intentar corregir diez cosas a la vez y bloquearse, el estudiante graba un minuto hablando sin leer, escucha la grabación e identifica un solo ajuste concreto (por ejemplo, sustituir una muletilla por una pausa silenciosa) en la segunda toma.",
        questions = listOf(
            QuizQuestion(
                text = "Según el Capítulo 11 (Precisión en las palabras — RAE/DPD), ¿cuál de estas expresiones es la correcta?",
                options = listOf(
                    "Hubieron varias preguntas y estaban medios confundidos",
                    "Hubo varias preguntas y estaban medio confundidos",
                    "Habían muchas personas en base a la lista",
                    "Hubieron muchas dudas diferentes a lo previsto"
                ),
                correctAnswerIndex = 1,
                explanation = "El verbo 'haber' impersonal va siempre en singular ('hubo varias preguntas') y 'medio' como adverbio es invariable ('medio confundidos')."
            )
        ),
        icon = "mic"
    ),
    Lesson(
        id = "2_2",
        areaId = "2",
        areaName = "2. Voz, Precisión Léxica y Presencia (Cap. 10–12)",
        level = 2,
        title = "Los 8 Gestos y Dos Pasadas",
        description = "El cuerpo al servicio del mensaje y revisión en dos pasadas.",
        content = "El Capítulo 12 propone explorar con criterio 8 gestos: 1. Palmas abiertas, 2. Palma hacia abajo, 3. Gesto de OK, 4. Puño cerrado (con mesura), 5. Conteo con los dedos, 6. Manos entrelazadas (con soltura al escuchar), 7. Gesto de «mírame», y 8. Gesto de «cállate» / silencio acordado con respeto.\n\nPara evaluar tu práctica, aplica la Revisión en Dos Pasadas: la 1.ª pasada con sonido (para revisar estructura y voz) y la 2.ª pasada sin audio (para observar postura, manos y mirada).",
        anecdote = "Discurso de Steve Jobs en Stanford (2005 — Cap. 9): Jobs organizó su mensaje en relatos claros con tres movimientos (situación → dificultad → decisión y aprendizaje), manteniendo su presencia serena al servicio de cada idea.",
        questions = listOf(
            QuizQuestion(
                text = "¿En qué consiste la revisión de grabación en «dos pasadas» del Capítulo 12?",
                options = listOf(
                    "Grabar dos discursos distintos el mismo día",
                    "Ver la grabación primero con sonido (estructura y voz) y una segunda vez sin sonido (postura, mirada y gestos)",
                    "Leer el discurso dos veces rápido",
                    "Pedir dos opiniones en redes sociales"
                ),
                correctAnswerIndex = 1,
                explanation = "La segunda pasada en silencio permite observar con claridad qué comunican los pies, las manos y la mirada sin distraerse con el audio."
            )
        ),
        icon = "mic"
    ),
    // AREA 3: Persuasión, Orador Consciente y Manejo de Público (Cap. 13, 14 y 15)
    Lesson(
        id = "3_1",
        areaId = "3",
        areaName = "3. Persuasión, Orador Consciente y A.V.R. (Cap. 13–15)",
        level = 1,
        title = "Las 60 Técnicas y el Orador Consciente",
        description = "Ethos, Logos, Pathos y las 9 herramientas IP / IT / IE.",
        content = "El Capítulo 13 une Credibilidad (Ethos), Argumento (Logos) y Relevancia humana (Pathos) a través de 60 Técnicas organizadas en 6 momentos: 01. Preparar, 02. Descubrir, 03. Explicar y demostrar, 04. Facilitar la elección, 05. Aclarar objeciones y 06. Cuidar el intercambio (con el Árbol de decisión A / B / C).\n\nEl Capítulo 14 entrena al Orador Consciente en tres dimensiones (Antes, Durante y Después): Intrapersonal (Diario Emocional IP01–02, Respiración Consciente IP03–04, Brújula de Valores IP05–06), Interpersonal (Mapa de la Audiencia IT01–02, Escucha Profunda IT03–04, Calibrador Social IT05–06) y Emocional (Pausa Estratégica IE01–02, Alquimia Cognitiva IE03–04 y Termostato Emocional IE05–06).",
        anecdote = "Martin Luther King Jr. (1963 — Cap. 13): En su discurso en Washington combinó razones verificables, principios éticos y una visión compartida de dignidad humana.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué significa actuar como un «Termostato Emocional» (IE05–IE06) en el Capítulo 14?",
                options = listOf(
                    "Subir el volumen y discutir si alguien del público se altera",
                    "Regular el propio tono, ritmo y serenidad para estabilizar el clima de la sala en vez de reaccionar como un termómetro",
                    "Ignorar todas las preguntas del público",
                    "Terminar la sesión inmediatamente ante cualquier duda"
                ),
                correctAnswerIndex = 1,
                explanation = "El termómetro solo refleja la tensión externa; el termostato regula la serenidad propia para ayudar a equilibrar el ambiente."
            )
        ),
        icon = "person"
    ),
    Lesson(
        id = "3_2",
        areaId = "3",
        areaName = "3. Persuasión, Orador Consciente y A.V.R. (Cap. 13–15)",
        level = 2,
        title = "A.V.R., los 12 Casos y el Sparring",
        description = "Responde objeciones con respeto y entrena con el Sparring del Orador.",
        content = "El Capítulo 15 presenta el recurso A.V.R. (Agradecer, Valorar y Redirigir), la secuencia de 4 pasos (Observa → Comprueba → Responde → Limita y continúa), el triángulo de la mirada y los 12 Casos prácticos de situaciones difíciles.\n\nEl Capítulo 18 cierra con el Sparring del Orador en 3 modalidades: 1. Tema preparado («La defensa de la fortaleza»), 2. Preparación breve («Pensar de pie») y 3. Desacuerdo acordado («Caminar sobre el alambre»).",
        anecdote = "El samurái y el maestro zen (Cap. 4 y 15): La verdadera fortaleza en el intercambio no está en reaccionar con impulso, sino en la pausa consciente que conserva la claridad y el respeto.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué significan las siglas del recurso A.V.R. en el Capítulo 15?",
                options = listOf(
                    "Atacar, Vencer y Rebatir",
                    "Agradecer, Valorar y Redirigir",
                    "Anotar, Verificar y Repetir",
                    "Aplaudir, Volver y Retirarse"
                ),
                correctAnswerIndex = 1,
                explanation = "A.V.R. consiste en Agradecer la intervención, Valorar el punto útil y Redirigir hacia el propósito y el grupo."
            )
        ),
        icon = "person"
    )
)
