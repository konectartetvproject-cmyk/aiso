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
    ),
    // AREA 4: Técnicas de Inicio y Conclusión
    Lesson(
        id = "4_1",
        areaId = "4",
        areaName = "4. Técnicas de Inicio y Conclusión",
        level = 1,
        title = "Ganchos de Inicio",
        description = "Fórmulas exactas para atrapar la atención.",
        content = "El inicio de un discurso es crucial para captar la atención. Puedes usar técnicas como la Interrogación Retórica (formular una pregunta sin esperar respuesta para hacer reflexionar), narrar una historia histórica o personal, mostrar Datos estadísticos alarmantes o sorprendentes, o usar Palabras Llamativas que generen intriga inmediata.",
        anecdote = "Miguel Ángel y el bloque de mármol: Una vez un niño vio a Miguel Ángel golpear un gran bloque de mármol. Al preguntarle por qué lo hacía, él contestó: 'Porque hay un ángel adentro y le estoy ayudando a salir'. Esta es una narrativa poderosa que se usa frecuentemente como técnica de inicio para hablar sobre el potencial oculto.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué es una técnica de inicio por 'Interrogación retórica'?",
                options = listOf("Hacer preguntas de examen", "Formular una pregunta sin esperar respuesta para hacer pensar", "Pedirle a alguien que responda obligatoriamente", "Ninguna de las anteriores"),
                correctAnswerIndex = 1,
                explanation = "Consiste en formular una pregunta sin esperar respuesta para reforzar un punto de vista e incentivar la reflexión."
            )
        ),
        icon = "star"
    ),
    Lesson(
        id = "4_2",
        areaId = "4",
        areaName = "4. Técnicas de Inicio y Conclusión",
        level = 2,
        title = "Cierres Memorables",
        description = "Cerrar con impacto y unificación.",
        content = "La conclusión es la última oportunidad de reforzar tu mensaje. Técnicas efectivas incluyen la Dinámica (hacer que el público se levante y repita una frase de poder), la Visión (invitarlos a cerrar los ojos e imaginar un futuro posible si aplican tu mensaje), o el Énfasis (basado en la regla de tres elementos para reiterar los puntos principales). También existe la 'Unificación', que es combinar varias técnicas a la vez.",
        anecdote = "La moraleja del águila criada como gallina: Un granjero crió a un águila junto a sus gallinas, haciéndole creer que no podía volar. Un día, vio a un halcón y descubrió su verdadera naturaleza. Esta fábula se usa en cierres memorables para inspirar a la audiencia a romper las creencias limitantes y 'volar'.",
        questions = listOf(
            QuizQuestion(
                text = "¿En qué consiste la técnica de conclusión por 'Visión'?",
                options = listOf("Hablar del pasado", "Mostrar imágenes", "Invitar al público a imaginar un futuro posible si aplican el mensaje", "Cerrar los ojos"),
                correctAnswerIndex = 2,
                explanation = "Consiste en mostrarle al público la visión de un futuro posible si aplican lo que se les ha dicho."
            )
        ),
        icon = "star"
    ),
    // AREA 5: El Arte de la Palabra
    Lesson(
        id = "5_1",
        areaId = "5",
        areaName = "5. El Arte de la Palabra (Persuadir y Convencer)",
        level = 1,
        title = "La Vía Emocional",
        description = "Conectar emocionalmente (Persuasión).",
        content = "Persuadir es el camino hacia el Corazón. Apela a las emociones, utiliza el storytelling y se basa en los principios del psicólogo Robert Cialdini, como la Reciprocidad (dar valor primero), Autoridad (credibilidad), Prueba Social (mostrar que otros ya lo hacen), y Simpatía. Persuadir requiere vulnerabilidad, empatía y hablar con genuina pasión.",
        anecdote = "La emoción de Martin Luther King Jr.: En su discurso en Washington, King no se limitó a exponer datos de la segregación; pintó un cuadro vívido y cargado de emotividad usando metáforas como 'el oscuro y desolado valle'. Su repetición del mantra 'I have a dream' apeló directamente al corazón colectivo de la nación.",
        questions = listOf(
            QuizQuestion(
                text = "¿Cuál es la diferencia principal entre persuadir y convencer?",
                options = listOf("Son exactamente lo mismo", "Persuadir apela a las emociones; convencer apela a la razón", "Persuadir es lógico; convencer es emocional", "Solo se puede persuadir a los niños"),
                correctAnswerIndex = 1,
                explanation = "La persuasión va de la mente al corazón (emociones, storytelling), mientras que la convicción sigue la ruta de la razón (argumentos lógicos)."
            )
        ),
        icon = "mic"
    ),
    Lesson(
        id = "5_2",
        areaId = "5",
        areaName = "5. El Arte de la Palabra (Persuadir y Convencer)",
        level = 2,
        title = "La Vía Lógica",
        description = "Conectar lógicamente (Convicción).",
        content = "La Convicción es la ruta hacia la Razón. Su objetivo es que la audiencia acepte la verdad a través de argumentos lógicos, datos duros, y refutación proactiva de contraargumentos. Se utiliza mucho la técnica del Framing (Encuadre): por ejemplo, un 'Encuadre de Pérdida' resalta lo que el público perderá si no actúa (la aversión a la pérdida es un gran motivador). El orador experto entrelaza historias que conmueven con datos que demuestran.",
        anecdote = "La convicción implacable en Stanford: Steve Jobs, a pesar de usar historias personales, utilizó una lógica implacable. Su conclusión sobre el cáncer no fue sentimental, sino una exhortación racional: 'Vuestro tiempo es limitado, así que no lo malgastéis viviendo la vida de otro'. Utilizó su propia experiencia como evidencia irrefutable.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué es el 'Encuadre de Pérdida' en la argumentación lógica?",
                options = listOf("Perder el hilo del discurso", "Mostrar a la audiencia lo que podría perder si no sigue tu consejo", "Un chiste que nadie entiende", "Olvidar los datos"),
                correctAnswerIndex = 1,
                explanation = "Muestra lo que la audiencia podría perder; la aversión a la pérdida es un poderoso motivador lógico (Logos)."
            )
        ),
        icon = "mic"
    ),
    // AREA 6: La Trinidad de la Influencia
    Lesson(
        id = "6_1",
        areaId = "6",
        areaName = "6. La Trinidad de la Influencia",
        level = 1,
        title = "El Dominio del Yo",
        description = "Inteligencia Intrapersonal e Interpersonal.",
        content = "1. Inteligencia Intrapersonal: Es el Dominio del Yo (autoconciencia y autodisciplina). Nos permite usar el 'escudo de teflón' para no tomarnos las cosas como ataques personales y salir al escenario como dueños de nuestra energía.\n\n2. Inteligencia Interpersonal: Es el Puente hacia el Otro. Implica empatía, el radar social y la escucha profunda para percibir y comprender los estados de ánimo de la audiencia.",
        anecdote = "El 'radar social' y la escucha profunda: Se relata cómo los grandes líderes, antes de emitir un juicio, hacen un escaneo silencioso de la sala. Si la audiencia muestra signos de confusión, un orador con alta inteligencia interpersonal detiene su discurso y dice: 'Permítanme explicar esto de otra manera', conectando inmediatamente.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué inteligencia te permite usar el 'escudo de teflón' para no tomarte nada personal?",
                options = listOf("Inteligencia Interpersonal", "Inteligencia Intrapersonal", "Inteligencia Emocional", "Inteligencia Lógica"),
                correctAnswerIndex = 1,
                explanation = "La Inteligencia Intrapersonal (autoconciencia, autodisciplina) te ayuda a actuar como protagonista y no como víctima."
            )
        ),
        icon = "person"
    ),
    Lesson(
        id = "6_2",
        areaId = "6",
        areaName = "6. La Trinidad de la Influencia",
        level = 2,
        title = "La Sinergia Maestra",
        description = "Inteligencia Emocional y Termostato.",
        content = "3. Inteligencia Emocional: Es la integración de las capacidades intra e interpersonales en tiempo real. Funciona como el 'Termostato Emocional' de la sala. En vez de ser un termómetro que solo refleja si el ambiente está apático o agresivo, el orador funciona como un termostato: establece la temperatura deseada e inyecta la energía necesaria para transformar el clima de la interacción.",
        anecdote = "La Pausa Consciente: Cuando un participante hostil ataca, el orador emocionalmente inteligente utiliza una 'Pausa de 2 segundos'. En ese instante, su corteza prefrontal toma el control sobre el cerebro reptiliano, permitiéndole responder con una sonrisa medida y un tono calmado que desarma instantáneamente la agresividad.",
        questions = listOf(
            QuizQuestion(
                text = "En la Inteligencia Emocional, ¿qué es el 'Termostato Emocional'?",
                options = listOf("Un aparato del aire acondicionado", "Ser una víctima del humor de la sala", "Posicionarte como un líder que establece e inyecta la energía que desea en el ambiente", "Medir la fiebre del público"),
                correctAnswerIndex = 2,
                explanation = "Un termostato establece la temperatura que desea. Como orador, tú inyectas la energía para cambiar el ambiente a tu favor."
            )
        ),
        icon = "person"
    ),
    // AREA 7: Tauromaquia Verbal
    Lesson(
        id = "7_1",
        areaId = "7",
        areaName = "7. Tauromaquia Verbal",
        level = 1,
        title = "Gestionando Interrupciones",
        description = "Ernesto el Erudito y Susana la Sniper.",
        content = "La 'Tauromaquia Verbal' es el arte de gestionar perfiles tóxicos con elegancia.\n\n- Susana la Sniper: Lanza preguntas trampa para acorralarte. Estrategia: Mantén la calma, reformula la pregunta a tu favor, responde directo y pivota tu mirada al resto del público.\n\n- Ernesto el Erudito: Te interrumpe para demostrar que sabe más que tú. Estrategia A.V.R. (Agradece, Valora, Redirige). Al valorar su aporte ('Aprecio tu profundo conocimiento'), satisfaces su ego sin ceder el control de la presentación.",
        anecdote = "La experiencia del Autor con el Doctor: Un alumno intentó sabotear la clase usando sus vastos títulos médicos para atacar al facilitador. El autor aplicó la 'respuesta blanda que calma la ira', agradeciendo públicamente sus aportes y reencuadrando sus críticas como contribuciones valiosas, lo que neutralizó por completo la hostilidad del Doctor.",
        questions = listOf(
            QuizQuestion(
                text = "¿Qué estrategia usar con 'Ernesto el Erudito' (Sabelotodo)?",
                options = listOf("Agradecer, Valorar su conocimiento y Redirigir/Aparcar", "Pelear con él con otros datos", "Decirle que no sabe nada", "Llamar a seguridad"),
                correctAnswerIndex = 0,
                explanation = "La estrategia A.V.R. (Agradece, Valora, Redirige) lo desarma validando su necesidad de reconocimiento sin ceder tu tarima."
            )
        ),
        icon = "star"
    ),
    Lesson(
        id = "7_2",
        areaId = "7",
        areaName = "7. Tauromaquia Verbal",
        level = 2,
        title = "Controlando el Caos",
        description = "Protágoras, Nicanor y Los Murmuradores.",
        content = "- Protágoras el Protagonista: Hace monólogos interminables. Estrategia: Intercepta educadamente, Valida su punto y Aparca el tema para continuarlo en el receso.\n\n- Nicanor el Negativo: El cínico que dice que nada funcionará. Estrategia: Reconoce el desafío y gira la responsabilidad al grupo preguntando cómo superarían esa barrera.\n\n- Los Murmuradores: Tienen conversaciones paralelas. Estrategia Escalonada: 1) Pausa visual (quédate en silencio y míralos), 2) Proximidad (camina hacia ellos), 3) Intervención amable ('¿Tienen alguna pregunta para compartir?').",
        anecdote = "El Escudo de la Serenidad: Un orador en una sala corporativa hostil fue bombardeado por 'Nicanores'. En lugar de luchar solo, sonrió con seguridad y dijo: 'Tienen razón, es un reto enorme. Como equipo directivo, ¿cuál proponen que sea nuestro primer paso para resolverlo?'. Automáticamente, los que criticaban tuvieron que proponer soluciones, cambiando el caos por colaboración.",
        questions = listOf(
            QuizQuestion(
                text = "¿Cómo manejar a 'Los Murmuradores'?",
                options = listOf("Gritarles que se callen", "Escalada gradual: Pausa visual, Proximidad, e Intervención directa amable", "Lanzarles un borrador", "Salir corriendo"),
                correctAnswerIndex = 1,
                explanation = "Usa la escalada: primero silencio y mirarlos, luego acercarte físicamente, y si falla, invitarlos amablemente a compartir su punto."
            )
        ),
        icon = "star"
    )
)
