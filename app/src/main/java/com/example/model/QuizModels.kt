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
    // AREA 1: Miedo Escénico y Autoestima
    Lesson(
        id = "1_1",
        areaId = "1",
        areaName = "1. Gestión del Miedo Escénico y Autoestima",
        level = 1,
        title = "Fundamentos del Miedo",
        description = "Reencuadra el miedo como entusiasmo.",
        content = "El miedo a hablar en público es una barrera común, pero los oradores exitosos se distinguen por su capacidad de transformar ese miedo en una fuente de energía. El miedo se manifiesta como nerviosismo y autocrítica, pero la clave es centrarse en la construcción de la confianza.\n\nLa Alquimia Cognitiva es un proceso de dos pasos: primero debes etiquetar la emoción (ej: 'Estoy sintiendo ansiedad'), y luego debes reencuadrarla positivamente (ej: 'Mi cuerpo se está preparando y llenando de energía para el escenario'). No se trata de eliminar el miedo, sino de redirigir esa energía hacia el entusiasmo y la pasión.",
        anecdote = "La historia de Juan: Un hombre con miedo escénico paralizante que soñaba con ser orador. Se inscribió en un curso y, mediante técnicas de respiración y enfrentando el miedo gradualmente, aprendió a transformar su ansiedad en energía. Cuando dio su primera charla regional, el público lo ovacionó, demostrando que con preparación el miedo se convierte en una herramienta poderosa.",
        questions = listOf(
            QuizQuestion(
                text = "Según el libro, ¿cuál es el primer paso de la Alquimia Cognitiva ante el miedo?",
                options = listOf("Ignorarlo", "Etiquetar la emoción", "Respirar rápido", "Huir del escenario"),
                correctAnswerIndex = 1,
                explanation = "El Paso 1 de la Alquimia Cognitiva es etiquetar la emoción (ej: 'Estoy sintiendo ansiedad') para luego reencuadrarla."
            )
        ),
        icon = "star"
    ),
    Lesson(
        id = "1_2",
        areaId = "1",
        areaName = "1. Gestión del Miedo Escénico y Autoestima",
        level = 2,
        title = "Respiración y Anclaje",
        description = "Técnicas avanzadas de respiración y control.",
        content = "Otra técnica clave es la respiración cuadrada o en caja (inhalar, retener, exhalar y mantener vacío, todo en 4 tiempos), que te devuelve al control parasimpático. La 'respiración del bebé' o diafragmática, donde el abdomen se expande al inhalar y se contrae al exhalar, es la forma más natural y eficiente de proyectar la voz sin forzar las cuerdas vocales.",
        anecdote = "El líder bajo fiebre en Casacoima (1817): Simón Bolívar, acorralado en un pantano hostil y sufriendo de fiebre, utilizó un reencuadre radical de la realidad para vender a sus oficiales una visión detallada de un futuro victorioso, transformando la desesperanza del momento en un ímpetu imparable.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué es la 'respiración cuadrada' o 'en caja'?",
                options = listOf("Inhalar, retener, exhalar y mantener vacío, todo en 4 tiempos", "Respirar muy rápido 4 veces", "Inflar el pecho como una caja", "Aguantar la respiración 4 minutos"),
                correctAnswerIndex = 0,
                explanation = "Es inhalar contando hasta 4, retener 4, exhalar en 4 y mantener los pulmones vacíos en 4. Ayuda a calmar los nervios."
            )
        ),
        icon = "star"
    ),
    // AREA 2: Técnicas de Voz y Dicción
    Lesson(
        id = "2_1",
        areaId = "2",
        areaName = "2. Técnicas de Voz y Dicción",
        level = 1,
        title = "Claridad y Articulación",
        description = "Aprende a usar tu instrumento vocal y erradicar muletillas.",
        content = "Tu voz es tu herramienta más poderosa como orador. Dominar la voz es crucial para transmitir tu mensaje con impacto y credibilidad. El tono, el volumen, el ritmo y la entonación son elementos clave para conectar con tu audiencia.\n\nLa dicción es la técnica de articular las palabras de manera correcta. Una mala dicción puede hacer que la audiencia pierda el interés y no se entienda el mensaje. Ejercicios como colocar un lápiz horizontalmente entre tus dientes y leer un texto en voz alta te fuerzan a articular más.",
        anecdote = "La historia de Laura: Laura era una joven tímida que solía omitir la 'd' final en palabras como 'ciudad' o 'libertad' y hablaba demasiado rápido. A través de la práctica con trabalenguas, el ejercicio del lápiz en la boca y el control de su respiración diafragmática, transformó su dicción y logró conectar emocionalmente con audiencias internacionales.",
        questions = listOf(
            QuizQuestion(
                text = "¿Cuál es una técnica recomendada para mejorar la articulación?",
                options = listOf("Gritar", "El lápiz en la boca", "Hablar muy rápido", "Susurrar siempre"),
                correctAnswerIndex = 1,
                explanation = "El ejercicio del lápiz en la boca es ideal para forzar y mejorar la articulación y dicción."
            )
        ),
        icon = "mic"
    ),
    Lesson(
        id = "2_2",
        areaId = "2",
        areaName = "2. Técnicas de Voz y Dicción",
        level = 2,
        title = "Pausas y Modulación",
        description = "Control del ritmo, tono y silencios estratégicos.",
        content = "Es fundamental eliminar las muletillas ('este', 'bueno', 'o sea', 'ajá'). Estas distraen a la audiencia y dan una sensación de inseguridad. La mejor forma de eliminarlas es reemplazarlas por pausas estratégicas, lo cual también genera suspenso y permite a la audiencia procesar la información. El tono debe modularse según el mensaje: un tono bajo para autoridad y seriedad, y uno alto para entusiasmo.",
        anecdote = "El poder del silencio en Martin Luther King Jr.: Su discurso 'I Have a Dream' es un ejemplo magistral del uso de pausas estratégicas y modulación. King no solo variaba su ritmo para construir un clímax emocional, sino que utilizaba silencios precisos para dejar que sus palabras más poderosas resonaran en la mente de las multitudes.",
        questions = listOf(
            QuizQuestion(
                text = "¿Cómo se deben eliminar las muletillas?",
                options = listOf("Reemplazándolas por pausas estratégicas", "Hablando más rápido", "Usando más gestos", "Bajando el volumen"),
                correctAnswerIndex = 0,
                explanation = "Las muletillas se deben reemplazar por pausas, las cuales además generan expectativa."
            )
        ),
        icon = "mic"
    ),
    // AREA 3: Estructura de un Discurso Poderoso
    Lesson(
        id = "3_1",
        areaId = "3",
        areaName = "3. Estructura de un Discurso Poderoso",
        level = 1,
        title = "Anatomía Básica",
        description = "La anatomía de una presentación en 11 pasos.",
        content = "Un buen discurso tiene una estructura clara. Una presentación exitosa se construye sobre tres pilares principales: una introducción sólida (Gancho, Presentación, Puntos clave), un cuerpo bien desarrollado (Desarrollo, Transiciones, Ejemplos/Datos) y una conclusión memorable (Resumen, Llamada a la acción, Cierre).\n\nLos primeros pasos para hablar en público son: 1. Entrar al Escenario (con confianza), 2. Mirar al Público (crear conexión visual), 3. Saludar, 4. Técnica de Inicio (anécdota o pregunta para captar atención), 5. Nombre del Orador, 6. Nombre del Tema.",
        anecdote = "El Discurso de Angostura (1819): Simón Bolívar estructuró este discurso como un viaje de identidad. Pasó del dolor del pasado y la crisis del presente, hacia una visión detallada del futuro. Lo hizo manejando con extrema diplomacia y humildad estratégica a una audiencia fragmentada de líderes militares con grandes egos.",
        questions = listOf(
            QuizQuestion(
                text = "¿Cuál es el paso número 1 de los 11 pasos estructurados?",
                options = listOf("Saludar", "Entrar al Escenario con confianza", "Decir el tema", "Recibir aplausos"),
                correctAnswerIndex = 1,
                explanation = "El primer paso es entrar al escenario caminando con confianza y una postura erguida para establecer el tono."
            )
        ),
        icon = "person"
    ),
    Lesson(
        id = "3_2",
        areaId = "3",
        areaName = "3. Estructura de un Discurso Poderoso",
        level = 2,
        title = "Desarrollo y Cierre",
        description = "Transiciones, datos y cierre estructurado.",
        content = "El desarrollo del discurso (Paso 7) requiere organizar el contenido lógicamente usando transiciones suaves, ejemplos y datos estadísticos para mantener el interés. Luego, el Paso 8 es Anunciar la Conclusión, alertando al público de que el final se acerca. Finalmente, el Paso 9 es la Técnica de Conclusión, el 10 decir Gracias, y el 11 recibir los aplausos manteniendo una postura relajada y sonriendo.",
        anecdote = "El discurso estructurado en Stanford: Steve Jobs construyó un puente maestro entre lógica y emoción al dividir su famoso discurso en tres historias estructuradas: 'conectar los puntos', 'el amor y la pérdida' y 'la muerte'. Esta organización impecable facilitó el seguimiento y amplificó el impacto final.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué se debe hacer justo antes de decir la técnica de conclusión?",
                options = listOf("Huir", "Anunciar la Conclusión", "Un chiste", "Pedir preguntas"),
                correctAnswerIndex = 1,
                explanation = "El paso 8 es Anunciar la Conclusión, para avisar a la audiencia que estás por terminar y presten atención."
            )
        ),
        icon = "person"
    )
)
