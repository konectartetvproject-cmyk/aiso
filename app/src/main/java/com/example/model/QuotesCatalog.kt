package com.example.model

import com.example.viewmodel.OratoryQuote

object QuotesCatalog {

    val categories = listOf(
        "Todas",
        // Categorías originales intactas
        "Persuasión",
        "Presencia Escénica",
        "Dicción y Claridad",
        "Voz y Respiración",
        "Conexión Emocional",
        "Estructura del Mensaje",
        "Liderazgo",
        // Nuevas categorías agregadas
        "Libros y Literatura",
        "Cine y Películas",
        "Filosofía y Estoicismo",
        "Frases Típicas y Refranes",
        "Discursos Históricos",
        "Favoritas ❤️"
    )

    val allQuotes: List<OratoryQuote> = listOf(
        // ==========================================
        // 1. PERSUASIÓN (Categoría Original + Expansión)
        // ==========================================
        OratoryQuote(
            id = "q1",
            text = "El secreto del orador no radica en lo que dice, sino en la convicción con la que hace vibrar el corazón de su audiencia.",
            author = "Marco Tulio Cicerón",
            category = "Persuasión",
            source = "De Oratore (Sobre el Orador)",
            usageTip = "Ideal para abrir una conferencia sobre liderazgo o comunicación auténtica."
        ),
        OratoryQuote(
            id = "q2",
            text = "No trates de convencer a la mente sin antes haber conquistado el corazón.",
            author = "Blaise Pascal",
            category = "Persuasión",
            source = "Pensamientos (Pensées)",
            usageTip = "Úsala antes de presentar una historia emotiva que respalde tus datos técnicos."
        ),
        OratoryQuote(
            id = "q3",
            text = "La persuasión es a menudo más eficaz que la fuerza bruta; una idea bien expresada desarma cualquier prejuicio.",
            author = "Esopo",
            category = "Persuasión",
            source = "Fábulas de Esopo (El viento y el sol)",
            usageTip = "Excelente metáfora para negociaciones, acuerdos o presentaciones de ventas."
        ),
        OratoryQuote(
            id = "p4",
            text = "El carácter del orador (Ethos), la emoción que despierta (Pathos) y la solidez de su argumento (Logos) son las tres columnas de toda persuasión.",
            author = "Aristóteles",
            category = "Persuasión",
            source = "Arte Retórica",
            usageTip = "Perfecta para estructurar una exposición académica o un debate formal."
        ),
        OratoryQuote(
            id = "p5",
            text = "Cuando Cicerón hablaba, la gente decía: '¡Qué bien habla!'. Pero cuando Demóstenes hablaba, la gente gritaba: '¡Marchemos!'.",
            author = "Plutarco",
            category = "Persuasión",
            source = "Vidas Paralelas",
            usageTip = "Úsala en tu cierre (Peroración) para pasar de la reflexión al llamado a la acción."
        ),
        OratoryQuote(
            id = "p6",
            text = "La única forma de influir en otra persona es hablar acerca de lo que ella quiere y demostrarle cómo conseguirlo.",
            author = "Dale Carnegie",
            category = "Persuasión",
            source = "Cómo ganar amigos e influir sobre las personas",
            usageTip = "Recuérdala al diseñar tu promesa de valor al inicio de un pitch comercial."
        ),
        OratoryQuote(
            id = "p7",
            text = "La Trinidad de la Influencia nace cuando alineas tu Inteligencia Intrapersonal, tu empatía Interpersonal y tu dominio Emocional en el escenario.",
            author = "Los secretos del orador exitoso",
            category = "Persuasión",
            source = "Capítulo: La Trinidad de la Influencia",
            usageTip = "Cita clave para explicar por qué la técnica vocal sin inteligencia emocional no basta."
        ),
        OratoryQuote(
            id = "p8",
            text = "El orador verdadero es un hombre de bien experto en el arte de hablar: 'Vir bonus dicendi peritus'.",
            author = "Marco Fabio Quintiliano",
            category = "Persuasión",
            source = "Institutio Oratoria",
            usageTip = "Ideal para discursos sobre ética profesional, derecho o servicio público."
        ),
        OratoryQuote(
            id = "p9",
            text = "La elocuencia es la pintura del pensamiento; y así, los que después de haber pintado añaden todavía más trazos, hacen un borrón en lugar de un cuadro.",
            author = "Blaise Pascal",
            category = "Persuasión",
            source = "Pensamientos",
            usageTip = "Gran frase para defender la simplicidad de un proyecto o estrategia."
        ),
        OratoryQuote(
            id = "p10",
            text = "Quien sabe escuchar posee la mitad de la victoria antes de pronunciar su primer argumento.",
            author = "Baltasar Gracián",
            category = "Persuasión",
            source = "Oráculo manual y arte de prudencia",
            usageTip = "Excelente para rondas de preguntas y respuestas o mesas de diálogo."
        ),

        // ==========================================
        // 2. PRESENCIA ESCÉNICA (Categoría Original + Expansión)
        // ==========================================
        OratoryQuote(
            id = "q4",
            text = "Hay dos tipos de oradores: los que están nerviosos y los que mienten. El coraje es hacer vibrar los nervios a tu favor.",
            author = "Mark Twain",
            category = "Presencia Escénica",
            source = "Aforismos y Discursos",
            usageTip = "Genial para romper el hielo con humor si notas tensión en la sala."
        ),
        OratoryQuote(
            id = "q5",
            text = "El valor no es la ausencia de miedo, sino el juicio de que algo más es más importante que el temor a ser juzgado.",
            author = "Nelson Mandela",
            category = "Presencia Escénica",
            source = "El largo camino hacia la libertad",
            usageTip = "Poderosa para motivar equipos ante cambios difíciles o incertidumbre."
        ),
        OratoryQuote(
            id = "q6",
            text = "Pisa el escenario como si fueras el anfitrión en tu propia casa y el público fueran tus invitados más esperados.",
            author = "Los secretos del orador exitoso",
            category = "Presencia Escénica",
            source = "Capítulo: Dominio Escénico",
            usageTip = "Repítela mentalmente 30 segundos antes de subir al estrado."
        ),
        OratoryQuote(
            id = "pe4",
            text = "Ganas fuerza, coraje y confianza en cada experiencia en la que realmente te detienes a mirar al miedo de frente.",
            author = "Eleanor Roosevelt",
            category = "Presencia Escénica",
            source = "You Learn by Living",
            usageTip = "Ideal para discursos de graduación, mentoría o superación personal."
        ),
        OratoryQuote(
            id = "pe5",
            text = "El cerebro humano es un órgano maravilloso: empieza a funcionar en el momento en que te levantas y no se detiene hasta que subes al escenario.",
            author = "George Jessel",
            category = "Presencia Escénica",
            source = "Clásico del humor retórico",
            usageTip = "Úsala como apertura autocrítica ligera para empatizar de inmediato con el público."
        ),
        OratoryQuote(
            id = "pe6",
            text = "El miedo escénico no es tu enemigo: es adrenalina pura esperando convertirse en magnetismo, brillo en los ojos y firmeza en la voz.",
            author = "Los secretos del orador exitoso",
            category = "Presencia Escénica",
            source = "Capítulo: Transformando el Miedo en Poder",
            usageTip = "Úsala en talleres de comunicación para reencuadrar la ansiedad."
        ),
        OratoryQuote(
            id = "pe7",
            text = "El éxito es aprender a ir de fracaso en fracaso sin desesperarse y sin perder jamás el entusiasmo.",
            author = "Winston Churchill",
            category = "Presencia Escénica",
            source = "Reflexiones de Estado",
            usageTip = "Cierre contundente para charlas de emprendimiento y resiliencia."
        ),
        OratoryQuote(
            id = "pe8",
            text = "Lo que eres habla tan fuerte que el auditorio no puede escuchar lo que dices si tu cuerpo contradice a tu voz.",
            author = "Ralph Waldo Emerson",
            category = "Presencia Escénica",
            source = "Cartas y objetivos sociales",
            usageTip = "Úsala al hablar de coherencia, liderazgo con el ejemplo y lenguaje no verbal."
        ),
        OratoryQuote(
            id = "pe9",
            text = "Antes de que tu lengua pronuncie la primera sílaba, tus hombros erguidos, tu mirada serena y tus primeros tres segundos de silencio ya dieron el discurso.",
            author = "Los secretos del orador exitoso",
            category = "Presencia Escénica",
            source = "Capítulo: El Lenguaje del Cuerpo",
            usageTip = "Aplícala en cada entrada al escenario: plantarse, mirar en W, sonreír y hablar."
        ),
        OratoryQuote(
            id = "pe10",
            text = "Haz siempre aquello que temes hacer; allí donde tiembla tu voz es exactamente donde comienza tu crecimiento.",
            author = "Ralph Waldo Emerson",
            category = "Presencia Escénica",
            source = "Ensayos: Confianza en uno mismo",
            usageTip = "Excelente para invitar a tu audiencia a salir de su zona de confort."
        ),

        // ==========================================
        // 3. DICCIÓN Y CLARIDAD (Categoría Original + Expansión)
        // ==========================================
        OratoryQuote(
            id = "q7",
            text = "Habla claramente si hablas en absoluto; talla cada palabra antes de dejarla caer con aplomo.",
            author = "Oliver Wendell Holmes",
            category = "Dicción y Claridad",
            source = "Urania: A Rhymed Lesson",
            usageTip = "Recuerda vocalizar las consonantes finales para proyectar autoridad."
        ),
        OratoryQuote(
            id = "dc2",
            text = "Cuando le preguntaron a Demóstenes cuáles eran las tres cosas más importantes en la oratoria, respondió: 'Primero, la pronunciación y entrega; segundo, la entrega; tercero, la entrega'.",
            author = "Demóstenes",
            category = "Dicción y Claridad",
            source = "Citado por Cicerón en Brutus",
            usageTip = "Ideal para destacar que el 'cómo' se pronuncia transforma el 'qué' se dice."
        ),
        OratoryQuote(
            id = "dc3",
            text = "Pronuncia el discurso, te lo ruego, con soltura y buen tono en la lengua; sin aserrar el aire con la mano, sino con templanza y fuego contenido.",
            author = "William Shakespeare",
            category = "Dicción y Claridad",
            source = "Hamlet (Consejos de Hamlet a los actores, Acto III)",
            usageTip = "Cita magistral sobre articulación limpia y naturalidad escénica."
        ),
        OratoryQuote(
            id = "dc4",
            text = "El ejercicio del lápiz entre los dientes obliga a tu lengua y labios a esforzarse el doble; cuando lo retiras, tu dicción fluye como cristal tallado.",
            author = "Los secretos del orador exitoso",
            category = "Dicción y Claridad",
            source = "Capítulo: Gimnasia Articulatoria",
            usageTip = "Realiza este ejercicio durante 2 minutos antes de cualquier presentación clave."
        ),
        OratoryQuote(
            id = "dc5",
            text = "La diferencia entre la palabra casi correcta y la palabra exacta es la diferencia entre la chispa de una luciérnaga y el golpe de un relámpago.",
            author = "Mark Twain",
            category = "Dicción y Claridad",
            source = "Carta a George Bainton (1888)",
            usageTip = "Úsala para resaltar el poder de la precisión léxica y el vocabulario rico."
        ),
        OratoryQuote(
            id = "dc6",
            text = "Quien piensa claro, habla claro; la confusión en los labios casi siempre nace de una idea sin pulir en la mente.",
            author = "Quintiliano",
            category = "Dicción y Claridad",
            source = "Institutio Oratoria",
            usageTip = "Excelente para abrir talleres de redacción de discursos o argumentación."
        ),
        OratoryQuote(
            id = "dc7",
            text = "No se debe hablar solo para que el público pueda entendernos, sino de modo tan nítido que le sea imposible no entendernos.",
            author = "Marco Fabio Quintiliano",
            category = "Dicción y Claridad",
            source = "Institutio Oratoria (Libro VIII)",
            usageTip = "Regla de oro para exposiciones técnicas, médicas, legales o financieras."
        ),
        OratoryQuote(
            id = "dc8",
            text = "Las palabras son como las hojas: cuando abundan demasiado, rara vez se encuentra buen fruto debajo de ellas.",
            author = "Alexander Pope",
            category = "Dicción y Claridad",
            source = "Ensayo sobre la crítica",
            usageTip = "Úsala para introducir una propuesta directa, concisa y sin rodeos."
        ),
        OratoryQuote(
            id = "dc9",
            text = "Una buena articulación permite que incluso un susurro se escuche con nitidez en la última fila del auditorio.",
            author = "Konstantin Stanislavski",
            category = "Dicción y Claridad",
            source = "La construcción del personaje",
            usageTip = "Recuerda que la claridad depende de los labios y la lengua, no de gritar."
        ),

        // ==========================================
        // 4. VOZ Y RESPIRACIÓN (Categoría Original + Expansión)
        // ==========================================
        OratoryQuote(
            id = "q8",
            text = "La voz humana es el instrumento más bello del mundo, pero también el más difícil de tocar sin disciplina y aire diafragmático.",
            author = "Richard Strauss",
            category = "Voz y Respiración",
            source = "Escritos sobre música y ópera",
            usageTip = "Ideal para abrir módulos sobre técnica vocal, canto o locución."
        ),
        OratoryQuote(
            id = "q9",
            text = "No temas al silencio. Una pausa oportuna es el arma más elocuente y dominante de la retórica.",
            author = "Mark Twain",
            category = "Voz y Respiración",
            source = "Discursos y ensayos de Mark Twain",
            usageTip = "Haz una pausa de 2 segundos justo antes de decir la palabra 'pausa'."
        ),
        OratoryQuote(
            id = "vr3",
            text = "La música no está en las notas, sino en el silencio que vibra entre ellas; igual ocurre con el gran discurso.",
            author = "Wolfgang Amadeus Mozart",
            category = "Voz y Respiración",
            source = "Epistolario musical",
            usageTip = "Úsala para demostrar cómo el ritmo y el silencio crean expectativa."
        ),
        OratoryQuote(
            id = "vr4",
            text = "Respira desde el diafragma en cuatro tiempos: quien gobierna su aliento, gobierna su ritmo cardíaco y la atención de la sala.",
            author = "Los secretos del orador exitoso",
            category = "Voz y Respiración",
            source = "Capítulo: Respiración de Caja",
            usageTip = "Guía práctica para evitar que la voz tiemble en el primer minuto."
        ),
        OratoryQuote(
            id = "vr5",
            text = "El tono de la voz es el rostro del alma; puedes engañar con las palabras, pero jamás con la vibración de tu garganta.",
            author = "Séneca",
            category = "Voz y Respiración",
            source = "Cartas a Lucilio",
            usageTip = "Excelente para hablar sobre autenticidad, sinceridad y liderazgo."
        ),
        OratoryQuote(
            id = "vr6",
            text = "En la música y en la palabra hablada, el silencio es el marco dorado donde descansa la idea principal.",
            author = "Miles Davis",
            category = "Voz y Respiración",
            source = "Autobiografía",
            usageTip = "Reemplaza cualquier muletilla ('eh', 'este') por una pausa limpia."
        ),
        OratoryQuote(
            id = "vr7",
            text = "Una voz monótona duerme hasta a la verdad más brillante; varía tu volumen, tu velocidad y tu cadencia como una sinfonía.",
            author = "Los secretos del orador exitoso",
            category = "Voz y Respiración",
            source = "Capítulo: Matices y Musicalidad Vocal",
            usageTip = "Baja el volumen para crear intimidad y súbelo para llamar a la acción."
        ),
        OratoryQuote(
            id = "vr8",
            text = "Las palabras significan más de lo que está escrito en el papel; se necesita la voz humana para infundirles sus matices más profundos.",
            author = "Maya Angelou",
            category = "Voz y Respiración",
            source = "Yo sé por qué canta el pájaro enjaulado",
            usageTip = "Ideal para explicar por qué un discurso leído sin alma pierde su fuerza."
        ),
        OratoryQuote(
            id = "vr9",
            text = "Calla o di algo que sea mejor que el silencio; y cuando hables, que tu aliento sostenga la firmeza de tu pensamiento.",
            author = "Pitágoras",
            category = "Voz y Respiración",
            source = "Sentencias pitagóricas",
            usageTip = "Perfecta para enseñar el valor de las pausas estratégicas."
        ),

        // ==========================================
        // 5. CONEXIÓN EMOCIONAL (Categoría Original + Expansión)
        // ==========================================
        OratoryQuote(
            id = "q10",
            text = "La gente olvidará lo que dijiste, olvidará lo que hiciste, pero nunca olvidará cómo los hiciste sentir.",
            author = "Maya Angelou",
            category = "Conexión Emocional",
            source = "Entrevistas y conferencias",
            usageTip = "Una de las citas más universales para cerrar una charla sobre servicio o liderazgo."
        ),
        OratoryQuote(
            id = "q11",
            text = "Las palabras tienen el poder de crear y de sanar. Cuando nacen de la verdad y la empatía, transforman al oyente.",
            author = "Buda",
            category = "Conexión Emocional",
            source = "Enseñanzas del Dhammapada",
            usageTip = "Ideal para discursos de conciliación, paz o cultura organizacional."
        ),
        OratoryQuote(
            id = "q12",
            text = "Para conmover al auditorio, debes haberte conmovido primero tú mismo con la causa que defiendes.",
            author = "Horacio",
            category = "Conexión Emocional",
            source = "Arte Poética (Epístola a los Pisones)",
            usageTip = "Úsala para explicar la empatía genuina al contar una historia personal."
        ),
        OratoryQuote(
            id = "ce4",
            text = "Cuando alguien te escucha de verdad sin juzgarte, sin tratar de tomar la responsabilidad por ti, se siente increíblemente liberador.",
            author = "Carl Rogers",
            category = "Conexión Emocional",
            source = "El proceso de convertirse en persona",
            usageTip = "Gran apertura para charlas sobre escucha activa, coaching o atención al cliente."
        ),
        OratoryQuote(
            id = "ce5",
            text = "La vulnerabilidad en el escenario no es debilidad; es la medida más exacta de tu valentía y el puente más corto hacia tu audiencia.",
            author = "Brené Brown",
            category = "Conexión Emocional",
            source = "El poder de ser vulnerable",
            usageTip = "Perfecta antes de compartir un fracaso o lección aprendida (como la historia de Juan en el libro)."
        ),
        OratoryQuote(
            id = "ce6",
            text = "Un dato estadístico informa al cerebro, pero la historia de una sola persona con nombre y apellido moviliza a toda una multitud.",
            author = "Los secretos del orador exitoso",
            category = "Conexión Emocional",
            source = "Capítulo: El Arte del Storytelling",
            usageTip = "Combina siempre una cifra importante con un caso humano concreto."
        ),
        OratoryQuote(
            id = "ce7",
            text = "Las palabras amables pueden ser cortas y fáciles de decir, pero sus ecos son verdaderamente infinitos.",
            author = "Madre Teresa de Calcuta",
            category = "Conexión Emocional",
            source = "Discursos humanitarios",
            usageTip = "Hermoso cierre para eventos de voluntariado, gratitud o reconocimientos."
        ),
        OratoryQuote(
            id = "ce8",
            text = "La música expresa aquello que no puede decirse con palabras y aquello sobre lo que es imposible permanecer en silencio.",
            author = "Víctor Hugo",
            category = "Conexión Emocional",
            source = "William Shakespeare (Ensayo)",
            usageTip = "Úsala para hablar de la cadencia y musicalidad emocional de un discurso."
        ),
        OratoryQuote(
            id = "ce9",
            text = "Ante un participante tóxico como 'Susana la Sniper' o 'Nicanor el Negativo', tu mayor escudo no es el ataque, sino la empatía estratégica y la calma inquebrantable.",
            author = "Los secretos del orador exitoso",
            category = "Conexión Emocional",
            source = "Capítulo: Manejo de Audiencias Difíciles",
            usageTip = "Recuérdala cuando enfrentes objeciones hostiles: valida la emoción y redirige al tema."
        ),

        // ==========================================
        // 6. ESTRUCTURA DEL MENSAJE (Categoría Original + Expansión)
        // ==========================================
        OratoryQuote(
            id = "q13",
            text = "Si no puedes explicarlo de forma sencilla, significa que no lo entiendes lo suficientemente bien.",
            author = "Albert Einstein",
            category = "Estructura del Mensaje",
            source = "Atribuido en conferencias científicas",
            usageTip = "Excelente antes de traducir un concepto técnico complejo a una analogía cotidiana."
        ),
        OratoryQuote(
            id = "q14",
            text = "Sé breve, sé claro y sé memorable. Comienza con una chispa, sustenta con roca firme y culmina con un fuego.",
            author = "Dale Carnegie",
            category = "Estructura del Mensaje",
            source = "El camino fácil y rápido para hablar eficazmente",
            usageTip = "Resume las tres partes de todo discurso: apertura magnética, desarrollo sólido y cierre épico."
        ),
        OratoryQuote(
            id = "q15",
            text = "Un buen discurso debe ser como una falda corta: lo bastante largo para cubrir el tema, pero lo bastante corto para mantener el interés.",
            author = "Winston Churchill",
            category = "Estructura del Mensaje",
            source = "Anécdotas parlamentarias",
            usageTip = "Frase célebre clásica para prometer brevedad al inicio de una intervención."
        ),
        OratoryQuote(
            id = "ec3",
            text = "He escrito esta carta más larga de lo habitual porque no tuve el tiempo suficiente para hacerla más corta.",
            author = "Blaise Pascal",
            category = "Estructura del Mensaje",
            source = "Cartas Provinciales (Carta XVI)",
            usageTip = "Úsala para destacar el esfuerzo de síntesis y precisión en una presentación ejecutiva."
        ),
        OratoryQuote(
            id = "ec4",
            text = "La simplicidad es la máxima sofisticación.",
            author = "Leonardo da Vinci",
            category = "Estructura del Mensaje",
            source = "Cuadernos de notas",
            usageTip = "Perfecta para presentar diseños, productos o estrategias minimalistas."
        ),
        OratoryQuote(
            id = "ec5",
            text = "Diles lo que les vas a decir, díselo con claridad y pasión, y luego diles lo que les acabas de decir.",
            author = "Aristóteles (Regla clásica de oro)",
            category = "Estructura del Mensaje",
            source = "Tradición retórica clásica",
            usageTip = "La fórmula infalible de Exordio (Introducción), Cuerpo y Peroración (Conclusión)."
        ),
        OratoryQuote(
            id = "ec6",
            text = "Todo discurso memorable necesita un gancho magnético en los primeros 30 segundos: una interrogación retórica, un dato impactante o una historia vivida.",
            author = "Los secretos del orador exitoso",
            category = "Estructura del Mensaje",
            source = "Capítulo: Técnicas de Inicio Impactante",
            usageTip = "Nunca empieces diciendo 'Bueno, hoy les vengo a hablar de...'; inicia con un gancho."
        ),
        OratoryQuote(
            id = "ec7",
            text = "Si me dan diez minutos para hablar, necesito una semana de preparación; si me dan una hora, puedo empezar ahora mismo.",
            author = "Woodrow Wilson",
            category = "Estructura del Mensaje",
            source = "Discursos presidenciales",
            usageTip = "Ilustra por qué los pitches cortos (Elevator Pitch) requieren máxima estructura."
        ),
        OratoryQuote(
            id = "ec8",
            text = "La claridad es la cortesía del filósofo y la primera obligación del orador ante el tiempo de su público.",
            author = "José Ortega y Gasset",
            category = "Estructura del Mensaje",
            source = "¿Qué es filosofía?",
            usageTip = "Cita elegante para conferencias universitarias, jurídicas o científicas."
        ),
        OratoryQuote(
            id = "ec9",
            text = "La mente humana retiene en grupos de tres: 'Sangre, esfuerzo, lágrimas y sudor'; 'Libertad, igualdad, fraternidad'; 'Vine, vi, vencí'.",
            author = "Los secretos del orador exitoso",
            category = "Estructura del Mensaje",
            source = "Capítulo: El Poder del Tricolon (Regla de Tres)",
            usageTip = "Agrupa siempre tus argumentos principales en tres pilares."
        ),

        // ==========================================
        // 7. LIDERAZGO (Categoría Original + Expansión)
        // ==========================================
        OratoryQuote(
            id = "q16",
            text = "Aquellos que tienen la valentía de creer que pueden cambiar el mundo con sus palabras, son quienes realmente lo hacen.",
            author = "Steve Jobs",
            category = "Liderazgo",
            source = "Campaña Think Different (1997)",
            usageTip = "Cierre electrizante para presentaciones de innovación, tecnología o visión de futuro."
        ),
        OratoryQuote(
            id = "q17",
            text = "Tengo un sueño: que un día mis palabras edifiquen puentes donde otros levantaron muros de intolerancia.",
            author = "Martin Luther King Jr.",
            category = "Liderazgo",
            source = "Inspirado en los discursos por los Derechos Civiles",
            usageTip = "Poderosa llamada a la unidad, empatía y liderazgo valiente."
        ),
        OratoryQuote(
            id = "q18",
            text = "El orador no busca el aplauso del momento, sino la semilla que florece en la mente del oyente días después.",
            author = "Los secretos del orador exitoso",
            category = "Liderazgo",
            source = "Epílogo: El Legado de la Palabra",
            usageTip = "Enfoca tu discurso en el valor que se lleva el público, no en tu propio ego."
        ),
        OratoryQuote(
            id = "li2",
            text = "Nuestras vidas comienzan a terminar el día en que guardamos silencio sobre las cosas que realmente importan.",
            author = "Martin Luther King Jr.",
            category = "Liderazgo",
            source = "Sermones en Selma, Alabama (1965)",
            usageTip = "Ideal para hablar de responsabilidad ética y toma de postura."
        ),
        OratoryQuote(
            id = "li3",
            text = "Sé tú mismo el cambio que deseas ver en el mundo; tu ejemplo es el discurso más elocuente que jamás pronunciarás.",
            author = "Mahatma Gandhi",
            category = "Liderazgo",
            source = "Escritos sobre liderazgo y acción no violenta",
            usageTip = "Inspira coherencia entre lo que un líder dice y lo que hace."
        ),
        OratoryQuote(
            id = "li4",
            text = "En la vida no hay nada que temer, solo hay cosas que comprender. Ahora es el momento de comprender más, para que podamos temer menos.",
            author = "Marie Curie",
            category = "Liderazgo",
            source = "Escritos científicos y biográficos",
            usageTip = "Excelente para exposiciones sobre ciencia, educación, cambio o incertidumbre."
        ),
        OratoryQuote(
            id = "li5",
            text = "No es el crítico quien cuenta, ni el que señala cómo tropieza el hombre fuerte, sino el que está realmente en la arena, con el rostro manchado de polvo y sudor.",
            author = "Theodore Roosevelt",
            category = "Liderazgo",
            source = "Discurso 'El Hombre en la Arena' (La Sorbona, 1910)",
            usageTip = "Una de las citas más potentes de la historia para reconocer el esfuerzo de un equipo."
        ),
        OratoryQuote(
            id = "li7",
            text = "Si tus acciones inspiran a otros a soñar más, aprender más, hacer más y convertirse en algo más, entonces eres un líder.",
            author = "John Quincy Adams",
            category = "Liderazgo",
            source = "Alocuciones presidenciales",
            usageTip = "Cita clásica para aperturas o cierres de convenciones de liderazgo."
        ),
        OratoryQuote(
            id = "li8",
            text = "Un líder es un repartidor de esperanza.",
            author = "Napoleón Bonaparte",
            category = "Liderazgo",
            source = "Máximas y pensamientos",
            usageTip = "Frase corta, contundente y fácil de recordar para momentos de crisis."
        ),

        // ==========================================
        // 8. LIBROS Y LITERATURA UNIVERSAL (Nueva Categoría)
        // ==========================================
        OratoryQuote(
            id = "lib1",
            text = "He aquí mi secreto, que no puede ser más simple: solo con el corazón se puede ver bien; lo esencial es invisible a los ojos.",
            author = "Antoine de Saint-Exupéry",
            category = "Libros y Literatura",
            source = "Libro: El Principito (1943)",
            usageTip = "Ideal para invitar al público a mirar más allá de las cifras superficiales."
        ),
        OratoryQuote(
            id = "lib2",
            text = "Cambiar el mundo, amigo Sancho, no es locura ni utopía, sino justicia.",
            author = "Miguel de Cervantes (Tradición quijotesca)",
            category = "Libros y Literatura",
            source = "Inspirado en Don Quijote de la Mancha",
            usageTip = "Magnífica para cerrar discursos de causas sociales, derecho o grandes proyectos."
        ),
        OratoryQuote(
            id = "lib3",
            text = "El que lee mucho y anda mucho, ve mucho y sabe mucho.",
            author = "Miguel de Cervantes",
            category = "Libros y Literatura",
            source = "Libro: Don Quijote de la Mancha (Segunda Parte, Cap. XXV)",
            usageTip = "Excelente para charlas sobre aprendizaje continuo, cultura y preparación del orador."
        ),
        OratoryQuote(
            id = "lib4",
            text = "La vida no es la que uno vivió, sino la que uno recuerda y cómo la recuerda para contarla.",
            author = "Gabriel García Márquez",
            category = "Libros y Literatura",
            source = "Libro: Vivir para contarla (2002)",
            usageTip = "Apertura extraordinaria para una charla basada en storytelling o biografía."
        ),
        OratoryQuote(
            id = "lib5",
            text = "Las estirpes condenadas a cien años de soledad no tenían una segunda oportunidad sobre la tierra.",
            author = "Gabriel García Márquez",
            category = "Libros y Literatura",
            source = "Libro: Cien años de soledad (1967)",
            usageTip = "Úsala como metáfora sobre la urgencia de aprovechar las oportunidades hoy."
        ),
        OratoryQuote(
            id = "lib6",
            text = "Pero si el pensamiento corrompe el lenguaje, el lenguaje también puede corromper el pensamiento.",
            author = "George Orwell",
            category = "Libros y Literatura",
            source = "Ensayo: La política y el idioma inglés / 1984",
            usageTip = "Cita brillante para hablar del poder de las palabras, el pensamiento crítico y la ética."
        ),
        OratoryQuote(
            id = "lib7",
            text = "La suprema excelencia consiste en quebrar la resistencia del adversario sin siquiera tener que combatir.",
            author = "Sun Tzu",
            category = "Libros y Literatura",
            source = "Libro: El Arte de la Guerra (Cap. III)",
            usageTip = "Aplícala en debates y negociaciones: persuadir sin humillar al interlocutor."
        ),
        OratoryQuote(
            id = "lib8",
            text = "Amigos, romanos, compatriotas, prestadme vuestros oídos; vengo a enterrar a César, no a ensalzarlo.",
            author = "William Shakespeare",
            category = "Libros y Literatura",
            source = "Obra: Julio César (Discurso de Marco Antonio, Acto III)",
            usageTip = "El ejemplo más famoso de la literatura sobre cómo ganar a una audiencia hostil."
        ),
        OratoryQuote(
            id = "lib9",
            text = "Nosotros pocos, nosotros felices pocos, nosotros banda de hermanos; porque quien hoy vierta su sangre conmigo será mi hermano.",
            author = "William Shakespeare",
            category = "Libros y Literatura",
            source = "Obra: Enrique V (Arenga del Día de San Crispín, Acto IV)",
            usageTip = "Modelo supremo de discurso motivacional para unir a un equipo antes de un gran reto."
        ),
        OratoryQuote(
            id = "lib10",
            text = "Siempre imaginé que el Paraíso sería algún tipo de biblioteca.",
            author = "Jorge Luis Borges",
            category = "Libros y Literatura",
            source = "Poema de los dones",
            usageTip = "Ideal para eventos culturales, educativos, ferias del libro o inauguraciones."
        ),
        OratoryQuote(
            id = "lib11",
            text = "Andábamos sin buscarnos, pero sabiendo que andábamos para encontrarnos.",
            author = "Julio Cortázar",
            category = "Libros y Literatura",
            source = "Libro: Rayuela (Cap. 7)",
            usageTip = "Hermosa cita para brindis, alianzas estratégicas o historias de conexión humana."
        ),
        OratoryQuote(
            id = "lib12",
            text = "Aun la noche más oscura terminará y el sol saldrá de nuevo.",
            author = "Víctor Hugo",
            category = "Libros y Literatura",
            source = "Libro: Los Miserables",
            usageTip = "Cierre esperanzador para discursos en tiempos de dificultad o reconstrucción."
        ),
        OratoryQuote(
            id = "lib13",
            text = "Cualquiera que sea la cosa que puedas hacer o sueñes que puedes hacer, comiénzala. La audacia tiene genio, poder y magia en sí misma.",
            author = "Johann Wolfgang von Goethe",
            category = "Libros y Literatura",
            source = "Atribuido a la tradición de Fausto",
            usageTip = "Potente llamado a la acción para el cierre de un discurso motivacional."
        ),
        OratoryQuote(
            id = "lib14",
            text = "Como descubrieron Juan y Laura en su camino, el orador no nace con un don místico: se forja en cada ensayo, en cada respiración consciente y en cada verdad compartida.",
            author = "Los secretos del orador exitoso",
            category = "Libros y Literatura",
            source = "Libro: Los secretos del orador exitoso",
            usageTip = "Recuerda a tu audiencia que la elocuencia es una habilidad entrenable por todos."
        ),

        // ==========================================
        // 9. CINE Y PELÍCULAS ICÓNICAS (Nueva Categoría)
        // ==========================================
        OratoryQuote(
            id = "cin1",
            text = "¡Porque tengo voz! ¡Tengo derecho a ser escuchado! ¡Tengo una voz!",
            author = "Rey Jorge VI (Colin Firth) & Lionel Logue",
            category = "Cine y Películas",
            source = "Película: El Discurso del Rey (The King's Speech, 2010)",
            usageTip = "Símbolo máximo de superación del bloqueo vocal y el miedo escénico."
        ),
        OratoryQuote(
            id = "cin2",
            text = "Carpe Diem. Aprovechen el día, muchachos. Hagan que sus vidas sean extraordinarias. Sean cuales sean los que digan, las palabras y las ideas sí pueden cambiar el mundo.",
            author = "Profesor John Keating (Robin Williams)",
            category = "Cine y Películas",
            source = "Película: La Sociedad de los Poetas Muertos (1989)",
            usageTip = "Extraordinaria para graduaciones, charlas a jóvenes o cierres inspiradores."
        ),
        OratoryQuote(
            id = "cin3",
            text = "Lo que hacemos en la vida tiene su eco en la eternidad.",
            author = "Máximo Décimo Meridio (Russell Crowe)",
            category = "Cine y Películas",
            source = "Película: Gladiador (2000)",
            usageTip = "Frase épica y breve para hablar de propósito, trascendencia y legado."
        ),
        OratoryQuote(
            id = "cin4",
            text = "Nunca dejes que nadie te diga que no puedes hacer algo. Ni siquiera yo. Si tienes un sueño, tienes que protegerlo.",
            author = "Chris Gardner (Will Smith)",
            category = "Cine y Películas",
            source = "Película: En busca de la felicidad (2006)",
            usageTip = "Conecta profundamente en charlas sobre perseverancia, familia y superación."
        ),
        OratoryQuote(
            id = "cin5",
            text = "No importa qué tan fuerte puedas golpear; lo que importa es qué tan fuerte pueden golpearte y seguir avanzando sin rendirte. ¡Así es como se gana!",
            author = "Rocky Balboa (Sylvester Stallone)",
            category = "Cine y Películas",
            source = "Película: Rocky Balboa (2006)",
            usageTip = "Ideal para arengas deportivas, equipos de ventas o superación de crisis."
        ),
        OratoryQuote(
            id = "cin6",
            text = "Pueden quitarnos la vida, pero jamás nos quitarán ¡la libertad!",
            author = "William Wallace (Mel Gibson)",
            category = "Cine y Películas",
            source = "Película: Corazón Valiente (Braveheart, 1995)",
            usageTip = "Ejemplo clásico de antítesis retórica ('pueden quitarnos X, pero jamás Y')."
        ),
        OratoryQuote(
            id = "cin7",
            text = "Siempre he sabido cuál era el camino correcto, sin excepción. Pero nunca lo tomé. ¿Saben por qué? Porque era demasiado duro.",
            author = "Coronel Frank Slade (Al Pacino)",
            category = "Cine y Películas",
            source = "Película: Perfume de Mujer (Scent of a Woman, 1992)",
            usageTip = "Uno de los mejores monólogos judiciales/éticos del cine sobre integridad y carácter."
        ),
        OratoryQuote(
            id = "cin8",
            text = "Pensamos demasiado y sentimos muy poco. Más que máquinas necesitamos humanidad; más que inteligencia, necesitamos bondad y dulzura.",
            author = "Charles Chaplin",
            category = "Cine y Películas",
            source = "Película: El Gran Dictador (Discurso final, 1940)",
            usageTip = "Obra maestra de la oratoria humanista para hablar de tecnología con propósito."
        ),
        OratoryQuote(
            id = "cin9",
            text = "Llegará un día en que el valor de los hombres decaiga, en que olvidemos a nuestros compañeros... ¡pero hoy no es ese día! ¡Hoy lucharemos!",
            author = "Aragorn (Viggo Mortensen)",
            category = "Cine y Películas",
            source = "Película: El Señor de los Anillos: El Retorno del Rey (2003)",
            usageTip = "Muestra cómo reconocer primero el temor del grupo para luego elevar su moral."
        ),
        OratoryQuote(
            id = "cin10",
            text = "Todo lo que tenemos que decidir es qué hacer con el tiempo que se nos ha dado.",
            author = "Gandalf (Ian McKellen)",
            category = "Cine y Películas",
            source = "Película: El Señor de los Anillos: La Comunidad del Anillo (2001)",
            usageTip = "Sobria, sabia y profunda para reflexionar sobre decisiones estratégicas."
        ),
        OratoryQuote(
            id = "cin11",
            text = "Hazlo o no lo hagas, pero no lo intentes.",
            author = "Maestro Yoda",
            category = "Cine y Películas",
            source = "Película: Star Wars: El Imperio Contraataca (1980)",
            usageTip = "Directa y memorable para hablar de compromiso total frente a la duda."
        ),
        OratoryQuote(
            id = "cin12",
            text = "Solíamos mirar hacia arriba, al cielo, y maravillarnos con nuestro lugar en las estrellas; ahora solo miramos hacia abajo y nos preocupamos por nuestro lugar en el polvo.",
            author = "Cooper (Matthew McConaughey)",
            category = "Cine y Películas",
            source = "Película: Interestelar (Interstellar, 2014)",
            usageTip = "Ideal para conferencias de ciencia, visión a largo plazo y ambición innovadora."
        ),

        // ==========================================
        // 10. FILOSOFÍA Y ESTOICISMO (Nueva Categoría)
        // ==========================================
        OratoryQuote(
            id = "fil1",
            text = "Tenemos dos oídos y una sola boca precisamente para escuchar el doble de lo que hablamos.",
            author = "Zenón de Citio / Epicteto",
            category = "Filosofía y Estoicismo",
            source = "Diógenes Laercio / Tradición Estoica",
            usageTip = "Fundamental para recordar la importancia de escuchar a la audiencia."
        ),
        OratoryQuote(
            id = "fil2",
            text = "No nos afecta lo que nos sucede, sino lo que nos decimos a nosotros mismos acerca de lo que nos sucede.",
            author = "Epicteto",
            category = "Filosofía y Estoicismo",
            source = "Enquiridión (Manual de vida)",
            usageTip = "Base del control mental del orador ante un error técnico o una pregunta hostil."
        ),
        OratoryQuote(
            id = "fil3",
            text = "Tienes poder sobre tu mente, no sobre los acontecimientos externos. Date cuenta de esto y encontrarás la fuerza.",
            author = "Marco Aurelio",
            category = "Filosofía y Estoicismo",
            source = "Meditaciones",
            usageTip = "Enfócate en tu preparación y entrega; suelta la ansiedad por el juicio ajeno."
        ),
        OratoryQuote(
            id = "fil4",
            text = "No hay viento favorable para el marinero que no sabe a qué puerto se dirige.",
            author = "Séneca",
            category = "Filosofía y Estoicismo",
            source = "Cartas morales a Lucilio (Carta LXXI)",
            usageTip = "Imprescindible para abrir presentaciones de planificación estratégica u objetivos."
        ),
        OratoryQuote(
            id = "fil5",
            text = "Sufrimos mucho más en nuestra imaginación que en la realidad.",
            author = "Séneca",
            category = "Filosofía y Estoicismo",
            source = "Cartas morales a Lucilio (Carta XIII)",
            usageTip = "El antídoto estoico definitivo contra el miedo escénico anticipatorio."
        ),
        OratoryQuote(
            id = "fil6",
            text = "Los hombres sabios hablan porque tienen algo que decir; los necios, porque tienen que decir algo.",
            author = "Platón (Atribuido)",
            category = "Filosofía y Estoicismo",
            source = "Tradición filosófica clásica",
            usageTip = "Úsala para enfatizar el valor del contenido sustancial frente a la verborrea."
        ),
        OratoryQuote(
            id = "fil7",
            text = "Habla para que yo pueda verte.",
            author = "Sócrates",
            category = "Filosofía y Estoicismo",
            source = "Apotegmas clásicos",
            usageTip = "Muestra cómo nuestra forma de expresarnos revela nuestra mente y carácter."
        ),
        OratoryQuote(
            id = "fil8",
            text = "Quien tiene un 'porqué' para vivir puede soportar casi cualquier 'cómo'.",
            author = "Friedrich Nietzsche",
            category = "Filosofía y Estoicismo",
            source = "El crepúsculo de los ídolos",
            usageTip = "Cita célebre (también destacada por Viktor Frankl) para hablar de propósito."
        ),
        OratoryQuote(
            id = "fil9",
            text = "Un viaje de mil millas comienza con el primer paso.",
            author = "Lao-Tsé",
            category = "Filosofía y Estoicismo",
            source = "Tao Te Ching (Cap. 64)",
            usageTip = "Ideal para motivar a dar el primer paso tras una conferencia o curso."
        ),
        OratoryQuote(
            id = "fil10",
            text = "Dime y lo olvido, enséñame y lo recuerdo, involúcrame y lo aprendo.",
            author = "Confucio / Xunzi",
            category = "Filosofía y Estoicismo",
            source = "Tradición confuciana",
            usageTip = "Justifica por qué hacer participar al público multiplica el impacto de tu charla."
        ),
        OratoryQuote(
            id = "fil11",
            text = "La palabra pertenece mitad a quien la pronuncia y mitad a quien la escucha.",
            author = "Michel de Montaigne",
            category = "Filosofía y Estoicismo",
            source = "Ensayos (Libro III, Cap. 13)",
            usageTip = "Recuerda que la oratoria nunca es un monólogo, sino un diálogo vivo."
        ),

        // ==========================================
        // 11. FRASES TÍPICAS, REFRANES Y PROVERBIOS (Nueva Categoría)
        // ==========================================
        OratoryQuote(
            id = "ft1",
            text = "Lo bueno, si breve, dos veces bueno; y aun lo malo, si poco, no tan malo.",
            author = "Baltasar Gracián (Refrán Clásico)",
            category = "Frases Típicas y Refranes",
            source = "Oráculo manual y arte de prudencia (1647)",
            usageTip = "El refrán más célebre del idioma español para celebrar la concisión al hablar."
        ),
        OratoryQuote(
            id = "ft2",
            text = "Uno es dueño de lo que calla y esclavo de lo que dice.",
            author = "Sabiduría Popular / Proverbio Clásico",
            category = "Frases Típicas y Refranes",
            source = "Tradición hispana y oriental",
            usageTip = "Úsala para hablar de prudencia comunicativa, vocería y manejo de crisis."
        ),
        OratoryQuote(
            id = "ft3",
            text = "Verba volant, scripta manent (Las palabras vuelan, lo escrito permanece).",
            author = "Cayo Tito (Proverbio Latino)",
            category = "Frases Típicas y Refranes",
            source = "Discurso ante el Senado Romano",
            usageTip = "Excelente máxima latina para discursos jurídicos, acuerdos y compromisos formales."
        ),
        OratoryQuote(
            id = "ft4",
            text = "Rem tene, verba sequentur (Domina el tema, y las palabras seguirán solas).",
            author = "Catón el Viejo (Máxima Latina)",
            category = "Frases Típicas y Refranes",
            source = "Preceptos de retórica romana",
            usageTip = "Recuerda que la mejor improvisación nace del dominio profundo de la materia."
        ),
        OratoryQuote(
            id = "ft5",
            text = "A buen entendedor, pocas palabras bastan.",
            author = "Refrán Tradicional Español",
            category = "Frases Típicas y Refranes",
            source = "Refranero Clásico Castellano",
            usageTip = "Ideal para cerrar un argumento evidente con complicidad hacia la audiencia."
        ),
        OratoryQuote(
            id = "ft6",
            text = "Antes de hablar, pasa tus palabras por tres filtros: ¿Es verdad? ¿Es bondadoso? ¿Es necesario?",
            author = "Los Tres Filtros (Tradición Socrática)",
            category = "Frases Típicas y Refranes",
            source = "Parábola clásica de los tres tamices",
            usageTip = "Regla de oro para la comunicación asertiva en equipos y organizaciones."
        ),
        OratoryQuote(
            id = "ft7",
            text = "Cuando hables, procura que tus palabras sean mejores que tu silencio.",
            author = "Proverbio Árabe",
            category = "Frases Típicas y Refranes",
            source = "Sabiduría tradicional de Oriente Medio",
            usageTip = "Frase de gran impacto para iniciar una intervención reflexiva y pausada."
        ),
        OratoryQuote(
            id = "ft8",
            text = "Si quieres ir rápido, camina solo; si quieres llegar lejos, caminemos juntos.",
            author = "Proverbio Africano",
            category = "Frases Típicas y Refranes",
            source = "Sabiduría tradicional africana",
            usageTip = "Una de las frases más efectivas para cerrar discursos de trabajo en equipo."
        ),
        OratoryQuote(
            id = "ft9",
            text = "Hablando se entiende la gente.",
            author = "Refrán Popular Hispano",
            category = "Frases Típicas y Refranes",
            source = "Sabiduría popular",
            usageTip = "Excelente para abrir mesas de diálogo, mediación o resolución de conflictos."
        ),
        OratoryQuote(
            id = "ft10",
            text = "Cae siete veces y levántate ocho (Nana korobi ya oki).",
            author = "Proverbio Japonés",
            category = "Frases Típicas y Refranes",
            source = "Sabiduría tradicional japonesa",
            usageTip = "Perfecta para inspirar perseverancia y aprendizaje continuo en la oratoria."
        ),
        OratoryQuote(
            id = "ft11",
            text = "Non multa, sed multum (No muchas cosas, sino mucho en sustancia).",
            author = "Plinio el Joven / Quintiliano",
            category = "Frases Típicas y Refranes",
            source = "Aforismo retórico latino",
            usageTip = "Úsala para explicar por qué es mejor profundizar en 3 ideas que mencionar 20."
        ),

        // ==========================================
        // 12. DISCURSOS HISTÓRICOS (Nueva Categoría)
        // ==========================================
        OratoryQuote(
            id = "dh1",
            text = "El gobierno del pueblo, por el pueblo y para el pueblo no desaparecerá de la faz de la tierra.",
            author = "Abraham Lincoln",
            category = "Discursos Históricos",
            source = "Discurso de Gettysburg (19 de noviembre de 1863)",
            usageTip = "Ejemplo supremo de epífora y tricolon: en solo 272 palabras hizo historia."
        ),
        OratoryQuote(
            id = "dh2",
            text = "Yo tengo un sueño: que mis cuatro hijos pequeños vivirán un día en una nación donde no serán juzgados por el color de su piel, sino por el contenido de su carácter.",
            author = "Martin Luther King Jr.",
            category = "Discursos Históricos",
            source = "Discurso 'I Have a Dream' en Washington (1963)",
            usageTip = "El ejemplo más poderoso de anáfora ('Yo tengo un sueño') en la oratoria moderna."
        ),
        OratoryQuote(
            id = "dh3",
            text = "No tengo nada que ofrecer sino sangre, esfuerzo, lágrimas y sudor.",
            author = "Winston Churchill",
            category = "Discursos Históricos",
            source = "Primer discurso ante la Cámara de los Comunes (13 de mayo de 1940)",
            usageTip = "Demuestra cómo la honestidad brutal genera más confianza que las falsas promesas."
        ),
        OratoryQuote(
            id = "dh4",
            text = "No preguntes qué puede hacer tu país por ti; pregunta qué puedes hacer tú por tu país.",
            author = "John F. Kennedy",
            category = "Discursos Históricos",
            source = "Discurso Inaugural (20 de enero de 1961)",
            usageTip = "El ejemplo más famoso de quiasmo (invertir el orden de las palabras para impactar)."
        ),
        OratoryQuote(
            id = "dh5",
            text = "Un niño, un maestro, un libro y una pluma pueden cambiar el mundo. La educación es la única solución.",
            author = "Malala Yousafzai",
            category = "Discursos Históricos",
            source = "Discurso ante las Naciones Unidas (12 de julio de 2013)",
            usageTip = "Muestra cómo una enumeración ascendente sencilla crea un clímax inolvidable."
        ),
        OratoryQuote(
            id = "dh6",
            text = "He luchado contra la dominación blanca y he luchado contra la dominación negra. He abrigado el ideal de una sociedad libre y democrática... Es un ideal por el que espero vivir, pero si es necesario, es un ideal por el que estoy preparado para morir.",
            author = "Nelson Mandela",
            category = "Discursos Históricos",
            source = "Discurso en el Juicio de Rivonia (20 de abril de 1964)",
            usageTip = "Una peroración histórica donde la pausa antes de la última frase conmovió al mundo."
        ),
        OratoryQuote(
            id = "dh7",
            text = "El sistema de gobierno más perfecto es aquel que produce mayor suma de felicidad posible, mayor suma de seguridad social y mayor suma de estabilidad política.",
            author = "Simón Bolívar",
            category = "Discursos Históricos",
            source = "Discurso de Angostura (15 de febrero de 1819)",
            usageTip = "Joya de la oratoria hispanoamericana que utiliza la repetición rítmica ('mayor suma de...')."
        ),
        OratoryQuote(
            id = "dh8",
            text = "Resolved lo que queráis, pero afrontando la responsabilidad de dar entrada a esa mitad de género humano en política, para que la política sea cosa de dos.",
            author = "Clara Campoamor",
            category = "Discursos Históricos",
            source = "Discurso en las Cortes Constituyentes (1 de octubre de 1931)",
            usageTip = "Hito de la oratoria parlamentaria en español que logró el sufragio femenino."
        ),
        OratoryQuote(
            id = "dh9",
            text = "Porque los sepulcros de los hombres ilustres no son solo la tierra donde yacen, sino la memoria viva que dejan en el ánimo de quienes los suceden.",
            author = "Pericles",
            category = "Discursos Históricos",
            source = "Oración Fúnebre de Pericles (Tucídides, 431 a.C.)",
            usageTip = "La pieza fundacional de la oratoria cívica de la Grecia clásica."
        ),
        OratoryQuote(
            id = "dh10",
            text = "Elegimos ir a la Luna en esta década y hacer las demás cosas, no porque sean fáciles, sino porque son difíciles.",
            author = "John F. Kennedy",
            category = "Discursos Históricos",
            source = "Discurso en la Universidad Rice (12 de septiembre de 1962)",
            usageTip = "Ideal para inspirar equipos a asumir metas ambiciosas y desafiantes."
        )
    )
}
