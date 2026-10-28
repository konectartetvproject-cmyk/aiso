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
    // TÉCNICAS DE INICIO
    Technique(
        id = "inicio_1",
        type = "Inicio",
        name = "Interrogación retórica",
        purpose = "Captar la atención de la audiencia, reforzar un punto de vista e incentivar la reflexión desde el primer segundo.",
        formula = listOf("Saludo", "Pregunta retórica", "Nombre del orador", "Nombre del tema"),
        example = "**Buenas tardes a todos.** **¿Creen que una persona puede ser realmente feliz sin amar lo que hace?** Mi nombre es **[Tu Nombre]** y hoy hablaremos sobre **el poder de encontrar la pasión en nuestras vidas**."
    ),
    Technique(
        id = "inicio_2",
        type = "Inicio",
        name = "Frases",
        purpose = "Establecer autoridad o inspiración apoyándose en personajes célebres y reflexionar sobre ello.",
        formula = listOf("Saludo", "Frase de autor", "Nombre del autor", "Reflexión", "Nombre del orador", "Nombre del tema"),
        example = "**Hola a todos.** **'La excelencia moral es el resultado del hábito'.** **Aristóteles.** **Esto nos enseña que nuestras virtudes se construyen a través de la práctica diaria.** Mi nombre es **[Tu Nombre]** y el tema de hoy es **cómo la disciplina diaria nos lleva al éxito**."
    ),
    Technique(
        id = "inicio_3",
        type = "Inicio",
        name = "Narrar (Historias/Anécdotas)",
        purpose = "Conectar emocionalmente contando una historia o anécdota que ilustre el tema.",
        formula = listOf("Saludo", "Narración (historia, cuento, anécdota)", "Reflexión del mensaje", "Nombre del orador", "Nombre del tema"),
        example = "**Buenos días a todos.** **Cuando Thomas Edison inventó la bombilla, realizó más de mil intentos. Al ser cuestionado, respondió: 'No son fracasos, he conseguido saber formas de cómo no se debe hacer'.** **Esta anécdota nos enseña que los errores son parte del camino.** Mi nombre es **[Tu Nombre]** y hoy hablaremos de **la perseverancia**."
    ),
    Technique(
        id = "inicio_4",
        type = "Inicio",
        name = "Experiencias de vida",
        purpose = "Construir credibilidad y empatía a través de una experiencia personal.",
        formula = listOf("Saludo", "Anécdota o experiencia de vida", "Conclusión de la experiencia", "Nombre del orador", "Nombre del tema"),
        example = "**Buenos días.** **Recuerdo que, hace unos años, tenía un miedo paralizante a hablar en público.** **Ese momento me demostró el inmenso poder de la oratoria.** Soy **[Tu Nombre]** y el tema de hoy es **cómo convertir el miedo escénico en tu mayor fortaleza**."
    ),
    Technique(
        id = "inicio_5",
        type = "Inicio",
        name = "Datos",
        purpose = "Captar el interés inmediato utilizando cifras o estadísticas relevantes y alarmantes.",
        formula = listOf("Saludo", "Dato o estadística", "Breve reflexión", "Nombre del orador", "Nombre del tema"),
        example = "**Buenas noches.** **Se ha demostrado que el 93% de la comunicación es no verbal.** **Esto significa que nuestro cuerpo habla más que las palabras.** Mi nombre es **[Tu Nombre]** y hoy les enseñaré a **dominar el lenguaje corporal**."
    ),
    Technique(
        id = "inicio_6",
        type = "Inicio",
        name = "Muestras",
        purpose = "Mostrar las posibles ventajas o beneficios del tema para despertar el interés de lo que ganarán al escuchar.",
        formula = listOf("Saludo", "Muestra de beneficios", "Nombre del orador", "Nombre del tema"),
        example = "**Saludos.** **¿Les gustaría generar ingresos desde la comodidad de su hogar?** Mi nombre es **[Tu Nombre]** y hoy les hablaré sobre **cómo crear un negocio en línea exitoso**."
    ),
    Technique(
        id = "inicio_7",
        type = "Inicio",
        name = "Talentos y elementos",
        purpose = "Utilizar la creatividad, talentos del orador o elementos físicos para impactar visual o auditivamente.",
        formula = listOf("Saludo", "Uso de un talento o elemento para captar la atención", "Conexión con el tema del discurso", "Nombre del orador", "Nombre del tema"),
        example = "**Hola a todos.** **[El orador sube al escenario con un cubo de Rubik sin resolver] Este cubo es como un problema en la vida, parece imposible, pero con pasos correctos se logra.** Mi nombre es **[Tu Nombre]** y hoy les hablaré sobre **cómo resolver problemas complejos**."
    ),
    Technique(
        id = "inicio_8",
        type = "Inicio",
        name = "Palabras llamativas",
        purpose = "Romper el patrón con palabras fuertes o inesperadas para capturar la atención total.",
        formula = listOf("Saludo", "Palabra o frase llamativa", "Explicación del mensaje", "Nombre del orador", "Nombre del tema"),
        example = "**Muy buenas tardes.** **Soy un fracaso, y no tengo problema en decirlo.** **La verdad es que cada fracaso me ha enseñado una lección invaluable.** Soy **[Tu Nombre]** y el tema de hoy es **cómo los fracasos son maestros**."
    ),
    Technique(
        id = "inicio_9",
        type = "Inicio",
        name = "Actualidad",
        purpose = "Conectar el discurso con un hecho o noticia reciente para demostrar relevancia.",
        formula = listOf("Saludo", "Menciona un hecho o noticia actual", "Conexión con el tema del discurso", "Nombre del orador", "Nombre del tema"),
        example = "**Hola a todos.** **Recientemente, el lanzamiento del último iPhone ha generado gran expectativa.** **Detrás de ese éxito, hay años de innovación.** Mi nombre es **[Tu Nombre]** y hoy les hablaré sobre **la innovación como clave del éxito**."
    ),
    Technique(
        id = "inicio_10",
        type = "Inicio",
        name = "Unificación",
        purpose = "Combinar dos o más técnicas de inicio para maximizar el impacto.",
        formula = listOf("Saludo", "Uso de varias técnicas de inicio", "Nombre del orador", "Nombre del tema"),
        example = "**Buenas noches.** **¿Se han preguntado qué pasaría si superaran sus miedos? [Interrogación] Hace unos años mi mayor miedo era hablar en público... [Experiencia]** Mi nombre es **[Tu Nombre]** y hoy les hablaré sobre **cómo convertir el miedo en poder**."
    ),
    // TÉCNICAS DE CONCLUSIÓN
    Technique(
        id = "conclusion_1",
        type = "Conclusión",
        name = "Dinámica",
        purpose = "Involucrar activamente al público al final para generar energía y recordación.",
        formula = listOf("Anunciar la conclusión", "Pedirle al público que participe en una dinámica", "Agradecer al público", "Recibir los aplausos"),
        example = "**Para concluir,** **quiero que todos se levanten y repitan conmigo: 'Yo quiero, yo puedo, yo logro'.** **Muchas gracias por acompañarme.** **[Aplausos]**"
    ),
    Technique(
        id = "conclusion_2",
        type = "Conclusión",
        name = "Expresiones",
        purpose = "Cerrar con broche de oro utilizando una cita o proverbio profundo.",
        formula = listOf("Anunciar la conclusión", "Cita o frase", "Breve comentario o reflexión", "Agradecer al público"),
        example = "**Para concluir, quiero dejarles con una frase de Buda:** **'El dolor es inevitable, pero el sufrimiento es opcional'.** **Esta frase nos enseña que sufrir es una elección.** **Gracias.**"
    ),
    Technique(
        id = "conclusion_3",
        type = "Conclusión",
        name = "Visión",
        purpose = "Inspirar a la audiencia proyectándolos a un futuro deseado y posible.",
        formula = listOf("Anunciar la conclusión", "Invitar al público a visualizar un futuro", "Agradecer al público"),
        example = "**Para terminar,** **quiero que se imaginen 5 años en el futuro viviendo la vida que han soñado. Ese futuro es posible si toman acción hoy.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_4",
        type = "Conclusión",
        name = "Énfasis",
        purpose = "Fijar el mensaje reiterando los tres elementos más importantes (regla de tres).",
        formula = listOf("Anunciar la conclusión", "Reiterar los puntos principales", "Agradecer al público"),
        example = "**En conclusión,** **recuerden siempre estas tres cosas: preparación, práctica y pasión.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_5",
        type = "Conclusión",
        name = "Moraleja",
        purpose = "Dejar una lección clara mediante una fábula o historia corta.",
        formula = listOf("Anunciar la conclusión", "Contar la historia", "Explicar la moraleja", "Agradecer al público"),
        example = "**Para terminar, les dejo la fábula del águila criada con gallinas...** **La moraleja es que puedes decidir ser un águila, sin importar tu entorno.** **Gracias.**"
    ),
    Technique(
        id = "conclusion_6",
        type = "Conclusión",
        name = "Historia personal",
        purpose = "Cerrar con autenticidad y vulnerabilidad a través de una vivencia propia.",
        formula = listOf("Anunciar la conclusión", "Contar la historia personal", "Agradecer al público"),
        example = "**En resumen, les contaré una historia. Hace unos años perdí mi empleo... y descubrí mi pasión por la oratoria.** **Gracias a todos.**"
    ),
    Technique(
        id = "conclusion_7",
        type = "Conclusión",
        name = "Invitación",
        purpose = "Llamar a la acción (Call to Action) directa y clara.",
        formula = listOf("Anunciar la conclusión", "Invitar a la audiencia a realizar una acción", "Agradecer al público"),
        example = "**Para terminar,** **los invito a que, al salir, demuestren su afecto a sus seres queridos.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_8",
        type = "Conclusión",
        name = "Compromiso",
        purpose = "Generar un pacto bidireccional entre el orador y la audiencia.",
        formula = listOf("Anunciar la conclusión", "Hacer un compromiso con la audiencia", "Agradecer al público"),
        example = "**En conclusión,** **me comprometo a que si trabajan duro, el éxito llegará y estaré aquí para guiarlos.** **Muchas gracias.**"
    ),
    Technique(
        id = "conclusion_9",
        type = "Conclusión",
        name = "Unificación",
        purpose = "Potenciar el cierre integrando varias técnicas de conclusión a la vez.",
        formula = listOf("Anunciar la conclusión", "Combinar varias técnicas de conclusión", "Agradecer al público"),
        example = "**Para finalizar, (Énfasis + Historia personal) recuerden que la clave está en enfoque, disciplina y perseverancia. Cuando yo era joven aprendí que eran la clave.** **Gracias a todos.**"
    ),
    // LOS 11 PASOS
    Technique(
        id = "pasos_11",
        type = "Metodología",
        name = "Los 11 Pasos para Hablar en Público",
        purpose = "Una guía paso a paso desde que entras al escenario hasta que recibes los aplausos para asegurar el éxito de tu presentación.",
        formula = listOf(
            "1. Entrar al Escenario: Camina con confianza y una postura erguida para establecer el tono de la presentación.",
            "2. Mirar al Público: Haz contacto visual con la audiencia para crear una conexión y demostrar que estás comprometido.",
            "3. Saludar: Un saludo sencillo ayuda a romper el hielo y establecer una relación amigable.",
            "4. Técnica de Inicio: Usa una anécdota, pregunta o cita para captar la atención del público desde el principio.",
            "5. Nombre del Orador: Preséntate claramente para establecer tu identidad y credibilidad.",
            "6. Nombre del Tema: Introduce el tema del discurso para que la audiencia sepa qué esperar.",
            "7. Desarrollo del Discurso: Organiza el contenido de manera lógica y utiliza ejemplos para mantener el interés.",
            "8. Anunciar Conclusión: Avisa a la audiencia que estás por terminar para que presten atención a tus últimas palabras.",
            "9. Técnica de Conclusión: Cierra el discurso de manera efectiva con un resumen, una llamada a la acción o una cita poderosa.",
            "10. Decir Gracias: Agradece al público por su atención para cerrar la presentación de manera respetuosa.",
            "11. Recibir Aplausos: Mantén una postura relajada y sonríe para disfrutar y reconocer el esfuerzo de tu presentación."
        ),
        example = "**Ejemplo Completo Aplicando los 11 Pasos:**\n\n**[1. Entrar al Escenario, 2. Mirar al Público]**\n**[3. Saludar]:** ¡Muy buenas noches a todos!\n**[4. Técnica de Inicio]:** ¿Alguna vez han sentido que el miedo los paraliza justo antes de tomar una decisión importante?\n**[5. Nombre del Orador]:** Mi nombre es [Tu Nombre]...\n**[6. Nombre del Tema]:** ...y hoy vamos a hablar sobre cómo transformar el miedo en nuestro mayor aliado.\n**[7. Desarrollo]:** Cuando entendemos que el miedo es solo una alerta y no un muro, nuestra perspectiva cambia. [Desarrollas tus puntos principales...]\n**[8. Anunciar Conclusión]:** Para terminar con esta idea...\n**[9. Técnica de Conclusión]:** Los invito a que mañana, cuando sientan miedo, den un paso al frente de todos modos.\n**[10. Decir Gracias]:** Muchas gracias.\n**[11. Recibir Aplausos]**"
    )
)
