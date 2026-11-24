package com.example.model

data class Technique(
    val id: String,
    val type: String,
    val name: String,
    val purpose: String,
    val formula: List<String>,
    val example: String
)

val techniquesList = listOf(
    // TÉCNICAS DE INICIO (CAPÍTULO 4 — CÓDIGOS I01 A I10)
    Technique(
        id = "inicio_1",
        type = "Inicio",
        name = "I01 · Interrogación retórica",
        purpose = "Invitar a la audiencia a pensar en silencio sobre el tema desde el primer instante, antes de presentar tu nombre y el título del discurso.",
        formula = listOf("1. Saludo (Paso 3)", "2. Pregunta retórica (Paso 4)", "3. Nombre del orador (Paso 5)", "4. Nombre del tema (Paso 6)"),
        example = "**Buenas tardes a todos.** **¿Qué cambia en un equipo cuando escuchamos la idea completa antes de responder?** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **el valor de escuchar con atención antes de decidir**."
    ),
    Technique(
        id = "inicio_2",
        type = "Inicio",
        name = "I02 · Frases",
        purpose = "Abrir con una cita comprobada de un autor reconocido (o una frase propia reflexiva) y explicar brevemente su relación con el propósito del discurso.",
        formula = listOf("1. Saludo", "2. Frase o cita verificada", "3. Nombre del autor", "4. Breve reflexión conectada al propósito", "5. Nombre del orador", "6. Nombre del tema"),
        example = "**Muy buenos días.** **«La excelencia no es un acto aislado, sino un hábito construido con constancia».** **Esta reflexión inspirada en la ética clásica nos recuerda que la práctica diaria transforma nuestra capacidad de comunicar.** Mi nombre es **[Tu Nombre]** y el tema de hoy es **cómo entrenar hábitos de claridad al hablar en público**."
    ),
    Technique(
        id = "inicio_3",
        type = "Inicio",
        name = "I03 · Narrar historias o anécdotas",
        purpose = "Hacer visible el sentido del tema mediante un relato breve documentado o tradicional (como Edison, Miguel Ángel, el capullo de la mariposa, el samurái o la taza de té).",
        formula = listOf("1. Saludo", "2. Narración breve (situación → dificultad → aprendizaje)", "3. Reflexión conectada al tema", "4. Nombre del orador", "5. Nombre del tema"),
        example = "**Buenos días a todos.** **Cuando un visitante le preguntó a un maestro zen por sus enseñanzas mientras lo interrumpía sin parar, el maestro sirvió té hasta que la taza rebosó y dijo: «Como esta taza, si vienes lleno de certezas no queda espacio para aprender algo nuevo».** **Escuchar requiere abrir espacio.** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **la escucha profunda en el trabajo en equipo**."
    ),
    Technique(
        id = "inicio_4",
        type = "Inicio",
        name = "I04 · Experiencias de vida",
        purpose = "Construir cercanía y honestidad compartiendo una vivencia propia verdadera cuya lección conduzca directamente al tema.",
        formula = listOf("1. Saludo", "2. Experiencia personal real", "3. Aprendizaje obtenido", "4. Nombre del orador", "5. Nombre del tema"),
        example = "**Buenos días.** **En mi primera exposición frente a un grupo intenté leer palabra por palabra una hoja y perdí el contacto con quienes me escuchaban; cuando aparté el papel y expliqué la idea con mis propias palabras, la conversación fluyó con naturalidad.** Soy **[Tu Nombre]** y el tema de hoy es **cómo prepararte para hablar desde el corazón y sin leer**."
    ),
    Technique(
        id = "inicio_5",
        type = "Inicio",
        name = "I05 · Datos verificados",
        purpose = "Sustentar la importancia del tema con un dato o estadística real, indicando siempre su fuente, fecha y qué significa para quienes escuchan (sin inventar porcentajes).",
        formula = listOf("1. Saludo", "2. Dato verificado con fuente y contexto", "3. Explicación de su relevancia para el grupo", "4. Nombre del orador", "5. Nombre del tema"),
        example = "**Buenas tardes.** **Según las guías de salud vocal del Instituto Nacional de la Sordera y Otros Trastornos de la Comunicación (NIDCD), forzar la voz sin pausas ni hidratación es una de las causas más frecuentes de fatiga vocal en docentes y oradores.** **Cuidar el instrumento es parte de cuidar el mensaje.** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **el cuidado y uso consciente de la voz**."
    ),
    Technique(
        id = "inicio_6",
        type = "Inicio",
        name = "I06 · Muestras de beneficios",
        purpose = "Explicar con honestidad qué utilidad práctica o qué herramienta concreta podrá aplicar el público al finalizar la intervención, sin promesas mágicas.",
        formula = listOf("1. Saludo", "2. Utilidad o beneficio realizable para la audiencia", "3. Nombre del orador", "4. Nombre del tema"),
        example = "**Saludos a todos.** **Contar con una estructura clara de tres pasos permite organizar cualquier intervención en pocos minutos sin depender de leer un papel.** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **cómo desarrollar discursos claros con el Método AISO**."
    ),
    Technique(
        id = "inicio_7",
        type = "Inicio",
        name = "I07 · Talentos y elementos",
        purpose = "Integrar con pertinencia una habilidad expresiva (cantar, declamar) o un objeto visible (un cubo de Rubik, un espejo) al servicio directo de la idea.",
        formula = listOf("1. Saludo", "2. Uso breve del talento o elemento físico", "3. Puente de sentido con el tema", "4. Nombre del orador", "5. Nombre del tema"),
        example = "**Hola a todos.** **[Muestra un cubo de Rubik desordenado] Resolver este cubo al azar parece imposible, pero cuando comprendemos la secuencia de movimientos cada pieza encuentra su lugar.** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **cómo ordenar problemas complejos paso a paso**."
    ),
    Technique(
        id = "inicio_8",
        type = "Inicio",
        name = "I08 · Palabras llamativas",
        purpose = "Despertar la atención con una afirmación breve e inesperada que se explica de inmediato con sentido y pleno respeto hacia el público.",
        formula = listOf("1. Saludo", "2. Expresión llamativa", "3. Aclaración inmediata del sentido", "4. Nombre del orador", "5. Nombre del tema"),
        example = "**Muy buenas tardes.** **El papel puede ser el mayor obstáculo de un buen mensaje.** **No porque escribir sea malo, sino porque leer frente al público nos separa de quienes nos escuchan.** Soy **[Tu Nombre]** y hoy hablaremos sobre **la libertad de exponer con palabras propias**."
    ),
    Technique(
        id = "inicio_9",
        type = "Inicio",
        name = "I09 · Actualidad",
        purpose = "Vincular el inicio del discurso con un hecho o acontecimiento reciente confirmado para mostrar la vigencia práctica del tema.",
        formula = listOf("1. Saludo", "2. Hecho o noticia reciente confirmada", "3. Conexión directa con el propósito", "4. Nombre del orador", "5. Nombre del tema"),
        example = "**Buenos días a todos.** **En los últimos meses, el aumento de reuniones híbridas y virtuales ha cambiado la forma en que los equipos toman acuerdos.** **Comunicar con brevedad y mirar a la cámara hoy es indispensable.** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **cómo adaptar tu mensaje a contextos virtuales**."
    ),
    Technique(
        id = "inicio_10",
        type = "Inicio",
        name = "I10 · Unificación",
        purpose = "Combinar con fluidez dos o más técnicas de inicio en una sola apertura antes de presentar tu nombre y el tema.",
        formula = listOf("1. Saludo", "2. Combinación de dos técnicas de inicio (p. ej., I01 + I04)", "3. Nombre del orador", "4. Nombre del tema"),
        example = "**Buenas noches.** **¿Qué hacemos cuando una pregunta inesperada cambia el rumbo de nuestra presentación? [I01 Interrogación] Hace un año viví esa situación en una reunión técnica y aprendí el valor de hacer una pausa antes de contestar [I04 Experiencia].** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **cómo responder preguntas difíciles con serenidad**."
    ),

    // TÉCNICAS DE CONCLUSIÓN (CAPÍTULO 5 — CÓDIGOS C01 A C09)
    Technique(
        id = "conclusion_1",
        type = "Conclusión",
        name = "C01 · Dinámica",
        purpose = "Cerrar con una actividad breve y voluntaria que refuerce el aprendizaje, ofreciendo siempre una alternativa respetuosa para quien prefiera observar.",
        formula = listOf("1. Anunciar la conclusión (Paso 8)", "2. Invitar de forma voluntaria a la dinámica (con alternativa de observación)", "3. Decir gracias (Paso 10)", "4. Recibir aplausos (Paso 11)"),
        example = "**Para concluir,** **quienes deseen hacerlo pueden levantar la mano o elegir en silencio cuál de estas tres acciones aplicarán mañana en su equipo.** **Muchas gracias.** **[Recibir aplausos con serenidad]**"
    ),
    Technique(
        id = "conclusion_2",
        type = "Conclusión",
        name = "C02 · Expresiones",
        purpose = "Cerrar el discurso con una cita verificada, proverbio o frase síntesis acompañada de una breve reflexión final.",
        formula = listOf("1. Anunciar la conclusión", "2. Cita, proverbio o expresión final", "3. Breve comentario que cierre la idea central", "4. Decir gracias"),
        example = "**Para concluir, recordemos esta idea:** **«Hablar con claridad es una forma de respeto hacia el tiempo de los demás».** **Que cada intervención nuestra esté siempre al servicio de quienes nos escuchan.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_3",
        type = "Conclusión",
        name = "C03 · Visión",
        purpose = "Invitar al público a imaginar un escenario futuro posible explicando qué decisiones concretas ayudan a construirlo, sin prometer resultados mágicos.",
        formula = listOf("1. Anunciar la conclusión", "2. Describir un futuro posible y las condiciones para construirlo", "3. Decir gracias"),
        example = "**Para terminar,** **imaginemos cómo cambiarán nuestras reuniones del próximo trimestre si en cada una definimos el propósito en una oración y escuchamos antes de responder. Ese avance depende de la constancia con que practiquemos hoy.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_4",
        type = "Conclusión",
        name = "C04 · Énfasis",
        purpose = "Fijar en la memoria de la audiencia las ideas esenciales del discurso repitiéndolas con una estructura breve, ordenada y paralela.",
        formula = listOf("1. Anunciar la conclusión", "2. Reiterar en orden los puntos principales (Concepto, Historia y Técnicas)", "3. Decir gracias"),
        example = "**En conclusión,** **llevémonos estas tres ideas claras: comprender el concepto antes de hablar, apoyarnos en historias honestas y practicar un ajuste concreto en cada ensayo.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_5",
        type = "Conclusión",
        name = "C05 · Moraleja",
        purpose = "Sintetizar la enseñanza del discurso mediante una fábula o relato breve cuya lección deje clara la idea principal.",
        formula = listOf("1. Anunciar la conclusión", "2. Relato breve o fábula final", "3. Explicación de la moraleja conectada al tema", "4. Decir gracias"),
        example = "**Para terminar, recordemos la imagen del capullo de la mariposa:** **el esfuerzo por salir del capullo es precisamente lo que fortalece sus alas para volar.** **No evitemos la práctica: cada ensayo fortalece nuestra propia voz.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_6",
        type = "Conclusión",
        name = "C06 · Historia personal",
        purpose = "Cerrar con autenticidad retomando o compartiendo una vivencia propia honesta que muestre el valor práctico del mensaje.",
        formula = listOf("1. Anunciar la conclusión", "2. Vivencia personal breve conectada al propósito", "3. Decir gracias"),
        example = "**En conclusión,** **cada vez que aparto mis apuntes antes de hablar y confío en el recorrido que preparé, descubro que conversar con el público es mucho más valioso que leer un texto perfecto.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_7",
        type = "Conclusión",
        name = "C07 · Invitación",
        purpose = "Proponer a la audiencia una acción concreta, cercana y realizable, respetando siempre su autonomía para decidir.",
        formula = listOf("1. Anunciar la conclusión", "2. Proponer una acción concreta y realizable", "3. Decir gracias"),
        example = "**Para concluir,** **los invito a que hoy mismo elijan una idea importante de su trabajo y la practiquen en voz alta durante un minuto sin leer ningún papel.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_8",
        type = "Conclusión",
        name = "C08 · Compromiso",
        purpose = "Declarar al cierre una promesa realista sobre aquello que depende directamente de ti como orador.",
        formula = listOf("1. Anunciar la conclusión", "2. Declarar un compromiso propio verificable", "3. Decir gracias"),
        example = "**En conclusión,** **mi compromiso con ustedes es enviarles hoy mismo la hoja de preparación y quedar disponible al finalizar para resolver cualquier duda puntual.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_9",
        type = "Conclusión",
        name = "C09 · Unificación",
        purpose = "Combinar de forma armónica dos técnicas de conclusión (por ejemplo, C04 Énfasis + C07 Invitación) hacia un mismo mensaje final.",
        formula = listOf("1. Anunciar la conclusión", "2. Combinar dos técnicas de conclusión", "3. Decir gracias", "4. Recibir aplausos"),
        example = "**Para finalizar [Paso 8], recordemos nuestras tres claves de hoy: propósito claro, escucha atenta y práctica sin leer [C04 Énfasis]. Los invito a aplicar la primera de ellas en su próxima conversación [C07 Invitación].** **Muchas gracias [Paso 10].**"
    ),

    // METODOLOGÍA AISO v07 (CAPÍTULOS 2, 3, 13 Y 14)
    Technique(
        id = "metodo_aiso",
        type = "Metodología",
        name = "El Método AISO (Cap. 2)",
        purpose = "Desarrollar cualquier discurso (Paso 7) en tres fases claras conectadas por puentes de transición, hablando con palabras propias y sin leer.",
        formula = listOf(
            "1. Fase 1 — CONCEPTO: Qué es la idea central, qué la caracteriza y por qué importa.",
            "2. Puente 1: «Para ver esta idea en una situación concreta...»",
            "3. Fase 2 — HISTORIA: Hecho documentado (como Newton y el manzano), vivencia propia o escena hipotética en 3 movimientos (situación → dificultad → decisión y aprendizaje).",
            "4. Puente 2: «A partir de esta escena, propongo estas acciones concretas...»",
            "5. Fase 3 — TÉCNICAS O CONSEJOS: Acciones prácticas que quien escucha puede aplicar (sin leer el papel)."
        ),
        example = "**Ejemplo del Método AISO (Paso 7):**\n\n**[Fase 1: CONCEPTO]:** Escuchar antes de responder consiste en comprender la idea completa del otro antes de dar nuestro veredicto.\n**[Puente 1]:** Para ver esta idea en una situación concreta, imaginemos esta escena cotidiana...\n**[Fase 2: HISTORIA]:** En una entrega de viernes, dos áreas discuten por un retraso; cuando hacen una pausa de dos minutos para escuchar qué necesita cada parte, descubren el ajuste exacto a tiempo.\n**[Puente 2]:** A partir de esta escena, propongo dos técnicas prácticas...\n**[Fase 3: TÉCNICAS]:** Primero, escucha sin interrumpir hasta el final; segundo, confirma en una frase lo que entendiste antes de proponer tu solución."
    ),
    Technique(
        id = "pasos_11",
        type = "Metodología",
        name = "Los 11 Pasos AISO (Cap. 3)",
        purpose = "Recorrido completo en tres momentos (Preparar el encuentro, Compartir el mensaje y Cerrar el encuentro) cumpliendo la Regla de práctica AISO de hablar sin leer.",
        formula = listOf(
            "Momento I — Preparar el encuentro: 1. Entrar al escenario (apartar textos), 2. Mirar al público, 3. Saludar.",
            "Momento II — Compartir el mensaje: 4. Técnica de inicio (I01–I10, antes del nombre), 5. Nombre del orador, 6. Nombre del tema, 7. Desarrollo del discurso con el Método AISO (CONCEPTO → HISTORIA → TÉCNICAS O CONSEJOS).",
            "Momento III — Cerrar el encuentro: 8. Anunciar conclusión («Para concluir...»), 9. Técnica de conclusión (C01–C09), 10. Decir gracias, 11. Recibir aplausos con serenidad."
        ),
        example = "**Ejemplo Completo con los 11 Pasos AISO (v07):**\n\n**[1. Entrar sin papeles, 2. Mirar al público]**\n**[3. Saludar]:** ¡Muy buenos días a todos!\n**[4. Técnica de Inicio I01]:** ¿Qué diferencia a una idea que se olvida de una idea que se comprende y se aplica?\n**[5. Nombre del Orador]:** Mi nombre es [Tu Nombre]...\n**[6. Nombre del Tema]:** ...y hoy hablaremos sobre cómo estructurar un mensaje claro con el Método AISO.\n**[7. Desarrollo AISO]:** [Concepto → Puente → Historia → Puente → Técnicas o Consejos expresados con palabras propias].\n**[8. Anunciar Conclusión]:** Para concluir...\n**[9. Técnica de Conclusión C07]:** Los invito a ensayar su próxima idea en voz alta y sin leer.\n**[10. Decir Gracias]:** Muchas gracias.\n**[11. Recibir Aplausos con calma]**"
    ),
    Technique(
        id = "orador_consciente",
        type = "Metodología",
        name = "El Orador Consciente (Cap. 14)",
        purpose = "Entrenar las 9 herramientas de inteligencia Intrapersonal (IP), Interpersonal (IT) y Emocional (IE) Antes, Durante y Después de hablar.",
        formula = listOf(
            "Intrapersonal (IP): Diario Emocional (IP01–02), Ancla de la Respiración Consciente (IP03–04) y Brújula de los Valores Personales (IP05–06).",
            "Interpersonal (IT): Mapa de la Audiencia (IT01–02), Escucha Profunda (IT03–04) y Calibrador Social (IT05–06).",
            "Emocional (IE): Pausa Estratégica (IE01–02), Etiquetar y Reencuadrar / Alquimia Cognitiva (IE03–04) y Termostato Emocional (IE05–06)."
        ),
        example = "**Aplicación en escena:**\n- **Antes:** Hago mi respiración consciente en 4 tiempos (`IP03`) y reviso las 4 preguntas del Mapa de la Audiencia (`IT01`).\n- **Durante:** Si surge una objeción tensa, aplico la Pausa Estratégica de 2 segundos (`IE02`), escucho sin interrumpir (`IT04`) y regulo mi tono como un Termostato Emocional (`IE06`).\n- **Después:** Registro en la Hoja 4 qué funcionó y cuál será mi único ajuste para la siguiente práctica (`IP02`)."
    )
)
