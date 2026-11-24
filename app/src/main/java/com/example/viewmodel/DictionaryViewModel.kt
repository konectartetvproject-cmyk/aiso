package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

data class DictionaryEntry(
    val word: String = "",
    val category: String = "Léxico Oratorio",
    val phonetic: String = "",
    val definition: String = "",
    val oratoryUsage: String = "",
    val exampleSentence: String = "",
    val upgradeTip: String = "",
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val language: String = "Español"
)

data class DictionaryState(
    val isLoading: Boolean = false,
    val entry: DictionaryEntry? = null,
    val error: String? = null,
    val searchHistory: List<String> = emptyList(),
    val favorites: List<String> = emptyList(),
    val language: String = "Español",
    val selectedCategoryFilter: String = "Todos"
)

class DictionaryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DictionaryState())
    val state: StateFlow<DictionaryState> = _state.asStateFlow()

    val curatedLexicon: List<DictionaryEntry> = listOf(
        // CONCEPTOS DEL LIBRO AISO
        DictionaryEntry(
            word = "Trinidad de la Influencia",
            category = "Método AISO",
            phonetic = "[tri-ni-DAD de la in-FLUEN-cia]",
            definition = "Pilar filosófico del libro «Los secretos del orador exitoso» que integra tres inteligencias esenciales: Inteligencia Intrapersonal (autoconocimiento), Inteligencia Interpersonal (empatía social) e Inteligencia Emocional (gestión del miedo y conexión).",
            oratoryUsage = "Úsalo como marco mental antes de salir al escenario: domina tu diálogo interno, lee las emociones del público y canaliza la adrenalina en entusiasmo.",
            exampleSentence = "«Un orador verdaderamente inolvidable no solo domina sus palabras; equilibra su mente, su emoción y su empatía mediante la Trinidad de la Influencia.»",
            upgradeTip = "En lugar de hablar solo de «tener seguridad», explica cómo alinear tu mundo interior con la necesidad del auditorio.",
            synonyms = listOf("Dominio integral", "Liderazgo consciente", "Maestría escénica", "Equilibrio emocional"),
            antonyms = listOf("Desconexión emocional", "Improvisación vacía", "Egocentrismo"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Exordio",
            category = "Estructura 10-80-10",
            phonetic = "[e-XOR-dio]",
            definition = "Primera parte del discurso clásico (el primer 10% en la regla 10-80-10) cuyo objetivo es capturar la atención inmediata, despertar el interés y ganarse la simpatía y credibilidad del auditorio.",
            oratoryUsage = "Aplica en tu exordio una de las 10 técnicas de inicio del libro: pregunta retórica, dato impactante, historia personal u objeto en mano.",
            exampleSentence = "«Desde el primer segundo del exordio, el silencio de la sala anunció que aquella idea cambiaría nuestra forma de trabajar.»",
            upgradeTip = "Reemplaza «el principio de la charla» por «el exordio» o «la apertura de impacto».",
            synonyms = listOf("Apertura", "Introducción", "Preámbulo", "Inicio de impacto"),
            antonyms = listOf("Peroración", "Epílogo", "Cierre", "Conclusión"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Peroración",
            category = "Estructura 10-80-10",
            phonetic = "[pe-ro-ra-CIÓN]",
            definition = "Parte final o clímax conclusivo de un discurso (el último 10% en la regla 10-80-10), diseñada para resumir la tesis central, elevar la emoción y movilizar a la audiencia hacia un Llamado a la Acción.",
            oratoryUsage = "Reserva tu mayor fuerza vocal y tu frase más memorable para la peroración; nunca cierres diciendo «bueno, eso era todo».",
            exampleSentence = "«En su peroración, miró a cada sector del auditorio y nos desafió a actuar hoy mismo sin excusas.»",
            upgradeTip = "Sustituye un final débil por una peroración con Círculo Completo o Resumen en Tricolon.",
            synonyms = listOf("Conclusión", "Cierre magistral", "Epílogo", "Clímax final"),
            antonyms = listOf("Exordio", "Apertura", "Prefacio"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Elocuencia",
            category = "Léxico de Poder",
            phonetic = "[e-lo-CUEN-cia]",
            definition = "Facultad de hablar con fluidez, propiedad, elegancia y fuerza persuasiva para deleitar, conmover y convencer al oyente mediante la palabra hablada y el lenguaje no verbal.",
            oratoryUsage = "La verdadera elocuencia no consiste en usar palabras rebuscadas, sino en expresar ideas profundas con claridad cristalina y emoción genuina.",
            exampleSentence = "«La elocuencia nace cuando la claridad de tu mente se une con la honestidad de tu corazón.»",
            upgradeTip = "En vez de decir «habla muy bonito», di «posee una notable elocuencia».",
            synonyms = listOf("Facundia", "Persuasión", "Expresividad", "Maestría verbal"),
            antonyms = listOf("Inarticulación", "Monotonía", "Torpeza verbal"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Asertividad",
            category = "Método AISO",
            phonetic = "[a-ser-ti-vi-DAD]",
            definition = "Habilidad comunicativa que permite expresar ideas, posturas y límites de manera firme, clara y honesta, respetando simultáneamente los derechos y la dignidad de los demás sin caer en la pasividad ni en la agresividad.",
            oratoryUsage = "Fundamental al aplicar el método E.R.R.G. frente a participantes difíciles como Susana la Sniper o Nicanor el Negativo.",
            exampleSentence = "«Responder con asertividad ante una crítica injusta duplica tu autoridad frente a todo el auditorio.»",
            upgradeTip = "En lugar de «defenderse sin pelear», usa «responder con firmeza asertiva».",
            synonyms = listOf("Firmeza empática", "Claridad respetuosa", "Templanza", "Aplomo"),
            antonyms = listOf("Agresividad", "Sumisión", "Reactividad", "Pasividad"),
            language = "Español"
        ),

        // FIGURAS RETÓRICAS
        DictionaryEntry(
            word = "Anáfora",
            category = "Figuras Retóricas",
            phonetic = "[a-NÁ-fo-ra]",
            definition = "Figura retórica de construcción que consiste en repetir intencionadamente una o varias palabras al inicio de frases o cláusulas sucesivas para crear cadencia musical y énfasis emocional.",
            oratoryUsage = "Úsala en el clímax de tu discurso acompañando cada repetición con un gesto progresivo de tus manos.",
            exampleSentence = "«Hoy elegimos innovar, hoy elegimos liderar, hoy elegimos transformar nuestra historia.»",
            upgradeTip = "Convierte una lista plana en una anáfora repitiendo el verbo inicial en tres frases seguidas.",
            synonyms = listOf("Repetición rítmica", "Iteración inicial", "Paralelismo"),
            antonyms = listOf("Elipsis", "Dispersión sintáctica"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Tricolon",
            category = "Figuras Retóricas",
            phonetic = "[tri-CO-lon]",
            definition = "Estructura retórica clásica compuesta por tres elementos, palabras o cláusulas paralelas de extensión similar que dotan al mensaje de ritmo, equilibrio y máxima recordación.",
            oratoryUsage = "El cerebro humano retiene patrones de tres con facilidad. Agrupa tus argumentos y tus cierres siempre en tríadas.",
            exampleSentence = "«Un discurso memorable exige preparación rigurosa, pasión auténtica y propósito claro.»",
            upgradeTip = "En vez de dar cuatro o cinco puntos sueltos, sintetízalos en un tricolon contundente.",
            synonyms = listOf("Regla de tres", "Tríada retórica", "Paralelismo triple"),
            antonyms = listOf("Enumeración caótica", "Inconclusión"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Quiasmo",
            category = "Figuras Retóricas",
            phonetic = "[QUIAS-mo]",
            definition = "Figura retórica que organiza dos frases en estructura cruzada o simétrica (A-B / B-A), invirtiendo el orden de los términos para provocar una reflexión profunda e inolvidable.",
            oratoryUsage = "Ideal para crear tu frase insignia (soundbite) que el público anotará y compartirá.",
            exampleSentence = "«No preparamos el discurso para llenar el tiempo; usamos el tiempo para dar vida al discurso.»",
            upgradeTip = "Toma una causa y su efecto e inviértelos en la segunda mitad de la oración.",
            synonyms = listOf("Retruécano", "Simetría cruzada", "Antítesis especular"),
            antonyms = listOf("Linealidad simple", "Redundancia"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Antítesis",
            category = "Figuras Retóricas",
            phonetic = "[an-TÍ-te-sis]",
            definition = "Contraposición de dos ideas, palabras o conceptos de significado opuesto dentro de una misma oración para resaltar el contraste y clarificar la elección que propones al público.",
            oratoryUsage = "Acompaña la primera idea mirando e indicando hacia tu izquierda, y el contraste positivo hacia tu derecha.",
            exampleSentence = "«El miedo te invita a callar en la sombra; el propósito te impulsa a hablar en la luz.»",
            upgradeTip = "En lugar de explicar solo los beneficios, contrástalos con el costo de no actuar.",
            synonyms = listOf("Contraste", "Contraposición", "Dualidad retórica"),
            antonyms = listOf("Sinonimia", "Uniformidad"),
            language = "Español"
        ),

        // VOZ Y ESCENARIO
        DictionaryEntry(
            word = "Prosodia",
            category = "Voz y Escenario",
            phonetic = "[pro-SO-dia]",
            definition = "Conjunto de fenómenos acústicos del habla —entonación, acento, ritmo, velocidad y pausas— que otorgan musicalidad, intención emocional y sentido a las palabras pronunciadas.",
            oratoryUsage = "Varía tu prosodia: habla más pausado y grave en las ideas solemnes, y más ágil y brillante al narrar acción o entusiasmo.",
            exampleSentence = "«Una prosodia rica y matizada mantiene hipnotizada a la audiencia incluso en temas técnicos.»",
            upgradeTip = "Reemplaza «el tono de voz» por «la riqueza prosódica» o «la modulación vocal».",
            synonyms = listOf("Melodía vocal", "Entonación", "Cadencia", "Inflexión"),
            antonyms = listOf("Monotonía", "Voz plana", "Roboticidad"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Dicción",
            category = "Voz y Escenario",
            phonetic = "[dic-CIÓN]",
            definition = "Precisión y limpieza articulatoria con la que se pronuncian las vocales, consonantes y sílabas de cada palabra, permitiendo que el mensaje se comprenda sin esfuerzo hasta en la última fila.",
            oratoryUsage = "Entrénala diariamente con el ejercicio del lápiz entre los dientes y los trabalenguas AISO antes de subir al escenario.",
            exampleSentence = "«Su dicción impecable permitió que cada cifra y cada promesa resonaran con total nitidez.»",
            upgradeTip = "Cuida especialmente las consonantes finales ('s', 'd', 'r') para no comerte las palabras.",
            synonyms = listOf("Articulación", "Vocalización", "Pronunciación nítida"),
            antonyms = listOf("Balbuceo", "Mascullado", "Atropello verbal"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Proscenio",
            category = "Voz y Escenario",
            phonetic = "[pros-CE-nio]",
            definition = "Zona frontal del escenario más próxima al público. En la técnica de desplazamiento de 9 cuadrantes, representa el área de máxima cercanía íntima, vulnerabilidad y conexión emocional.",
            oratoryUsage = "Avanza al proscenio cuando cuentes una historia personal o al realizar el Llamado a la Acción final.",
            exampleSentence = "«Al dar dos pasos firmes hacia el proscenio, el orador derribó la distancia y habló de corazón a corazón.»",
            upgradeTip = "Usa el término «proscenio» para referirte al borde delantero del escenario.",
            synonyms = listOf("Frente escénico", "Vanguardia del escenario", "Zona íntima"),
            antonyms = listOf("Foro", "Fondo escénico", "Bastidores"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Kinésica",
            category = "Voz y Escenario",
            phonetic = "[ki-NÉ-si-ca]",
            definition = "Disciplina que estudia el significado expresivo y comunicativo de los movimientos corporales, la postura, los gestos de las manos y las microexpresiones faciales durante la interacción.",
            oratoryUsage = "Mantén tus ademanes dentro de la «caja de poder» (entre la cintura y los hombros) con palmas visibles para proyectar honestidad.",
            exampleSentence = "«Su kinésica abierta y serena transmitió confianza antes siquiera de que pronunciara la primera palabra.»",
            upgradeTip = "En lugar de «mover las manos», habla de «lenguaje kinésico congruente».",
            synonyms = listOf("Lenguaje corporal", "Expresión gestual", "Comunicación no verbal"),
            antonyms = listOf("Rigidez escénica", "Inexpresividad"),
            language = "Español"
        ),

        // LÉXICO DE PODER (VERBOS Y ADJETIVOS PARA DISCURSOS)
        DictionaryEntry(
            word = "Trascendental",
            category = "Léxico de Poder",
            phonetic = "[tras-cen-den-TAL]",
            definition = "Que reviste suma gravedad, importancia decisiva y consecuencias duraderas que van más allá del momento presente.",
            oratoryUsage = "Úsalo para calificar decisiones estratégicas, momentos históricos o el propósito central de tu propuesta.",
            exampleSentence = "«Estamos ante una decisión trascendental que definirá el rumbo de nuestra organización en la próxima década.»",
            upgradeTip = "Sustituye «muy importante» por «trascendental», «crucial» o «determinante».",
            synonyms = listOf("Crucial", "Decisivo", "Fundamental", "Histórico"),
            antonyms = listOf("Trivial", "Intrascendente", "Superfluo", "Pasajero"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Catalizador",
            category = "Léxico de Poder",
            phonetic = "[ca-ta-li-za-DOR]",
            definition = "Elemento, líder o idea que acelera, impulsa y desencadena una transformación profunda o una reacción positiva en un grupo humano.",
            oratoryUsage = "Excelente metáfora ejecutiva para describir el impacto de un proyecto, una tecnología o un liderazgo inspirador.",
            exampleSentence = "«La educación práctica es el verdadero catalizador del progreso social y la libertad económica.»",
            upgradeTip = "En vez de decir «algo que ayuda mucho a cambiar», di «un auténtico catalizador del cambio».",
            synonyms = listOf("Impulsor", "Detonante positivo", "Motor", "Acelerador"),
            antonyms = listOf("Freno", "Obstáculo", "Lastre", "Inercia"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Ineludible",
            category = "Léxico de Poder",
            phonetic = "[i-ne-lu-DI-ble]",
            definition = "Obligación, realidad o desafío que no puede ser evitado, ignorado ni postergado bajo ningún pretexto.",
            oratoryUsage = "Aporta un sentido de urgencia ética y responsabilidad compartida en tus llamados a la acción.",
            exampleSentence = "«Proteger la confianza de nuestros clientes es un compromiso ético ineludible.»",
            upgradeTip = "Reemplaza «algo que tenemos que hacer sí o sí» por «una responsabilidad ineludible».",
            synonyms = listOf("Imperativo", "Impostergable", "Forzoso", "Inevitable"),
            antonyms = listOf("Opcional", "Prescindible", "Secundario"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Sinergia",
            category = "Léxico de Poder",
            phonetic = "[si-NER-gia]",
            definition = "Convergencia activa de varios talentos, equipos o fuerzas cuyo resultado conjunto supera ampliamente la simple suma de las partes individuales.",
            oratoryUsage = "Ideal para discursos de trabajo en equipo, alianzas comerciales y liderazgo colaborativo.",
            exampleSentence = "«Cuando la experiencia de los veteranos se une al ímpetu de los jóvenes, nace una sinergia imparable.»",
            upgradeTip = "En lugar de «trabajar juntos bien», utiliza «crear sinergia estratégica».",
            synonyms = listOf("Cooperación multiplicadora", "Convergencia", "Armonía operativa"),
            antonyms = listOf("Fragmentación", "Aislamiento", "Rivalidad interna"),
            language = "Español"
        ),
        DictionaryEntry(
            word = "Resiliencia",
            category = "Léxico de Poder",
            phonetic = "[re-si-LIEN-cia]",
            definition = "Capacidad humana y organizacional de atravesar la adversidad, adaptarse al cambio crítico y emerger fortalecido tras una crisis.",
            oratoryUsage = "Conecta profundamente en discursos motivacionales y de gestión de crisis al reconocer el esfuerzo del equipo.",
            exampleSentence = "«Nuestra resiliencia nos permitió convertir el año más difícil del sector en nuestro mayor salto de innovación.»",
            upgradeTip = "Sustituye «aguantar los problemas» por «demostrar resiliencia estratégica».",
            synonyms = listOf("Fortaleza", "Entereza", "Capacidad de superación", "Temple"),
            antonyms = listOf("Fragilidad", "Abatimiento", "Rendición"),
            language = "Español"
        ),

        // INGLÉS ORATORIO (ENGLISH PUBLIC SPEAKING)
        DictionaryEntry(
            word = "Call to Action",
            category = "Inglés Oratorio",
            phonetic = "[kól tu ÁK-shon]",
            definition = "Exhortación clara, directa y medible al cierre de una presentación (CTA) que indica a la audiencia exactamente qué paso dar a continuación.",
            oratoryUsage = "En presentaciones internacionales o de negocios, cierra siempre con un único Call to Action concreto.",
            exampleSentence = "«Let me leave you with one clear Call to Action: schedule your first prototype review before Friday.»",
            upgradeTip = "Evita terminar solo con «Thank you»; cierra con un Call to Action y luego agradece.",
            synonyms = listOf("Llamado a la acción", "Closing exhortation", "Next step"),
            antonyms = listOf("Abrupt ending", "Passive conclusion"),
            language = "Inglés"
        ),
        DictionaryEntry(
            word = "Elevator Pitch",
            category = "Inglés Oratorio",
            phonetic = "[É-le-vei-tor pích]",
            definition = "Discurso breve y persuasivo de 30 a 60 segundos (lo que dura un viaje en ascensor) que sintetiza el problema que resuelves, tu propuesta de valor única y el beneficio clave.",
            oratoryUsage = "Estructura tu pitch en 3 bloques: 1) Hook/Problem, 2) Solution & Value, 3) Ask/Call to Action.",
            exampleSentence = "«In a 45-second elevator pitch, clarity and passion beat technical jargon every single time.»",
            upgradeTip = "No intentes contar toda tu biografía en un pitch; despierta el interés para conseguir la segunda reunión.",
            synonyms = listOf("Discurso de ascensor", "Executive summary", "Value pitch"),
            antonyms = listOf("Rambling monologue", "Overloaded lecture"),
            language = "Inglés"
        ),
        DictionaryEntry(
            word = "Storytelling",
            category = "Inglés Oratorio",
            phonetic = "[STÓ-ri-té-ling]",
            definition = "El arte de comunicar ideas, datos y valores a través de relatos estructurados con personaje, conflicto y resolución que activan la empatía del oyente.",
            oratoryUsage = "En el Método AISO (Concepto + Historia + Técnicas), el storytelling es el puente emocional que hace inolvidable el concepto.",
            exampleSentence = "«Data informs the mind, but strategic storytelling moves the heart to take action.»",
            upgradeTip = "Nunca presentes una estadística fría sin acompañarla de la historia humana detrás del número.",
            synonyms = listOf("Narrativa persuasiva", "Relato estratégico", "Narrative arc"),
            antonyms = listOf("Dry data dump", "Abstract exposition"),
            language = "Inglés"
        ),
        DictionaryEntry(
            word = "Signposting",
            category = "Inglés Oratorio",
            phonetic = "[SÁIN-pous-ting]",
            definition = "Técnica de oratoria en inglés que consiste en usar frases guía («First, we will explore...», «Turning now to...», «To sum up...») para orientar al público sobre en qué punto exacto del discurso te encuentras.",
            oratoryUsage = "Esencial cuando hablas en inglés ante audiencias multiculturales para que nadie pierda el hilo conductor.",
            exampleSentence = "«Clear signposting acts like a GPS for your audience throughout your keynote.»",
            upgradeTip = "Usa conectores de transición claros al pasar de un argumento al siguiente.",
            synonyms = listOf("Señalización verbal", "Verbal transitions", "Roadmapping"),
            antonyms = listOf("Abrupt topic jumping", "Disorganized flow"),
            language = "Inglés"
        ),

        // LATÍN RETÓRICO Y JURÍDICO
        DictionaryEntry(
            word = "Captatio benevolentiae",
            category = "Locución Latina",
            phonetic = "[cap-TÁ-tio be-ne-vo-LÉN-ti-e]",
            definition = "Recurso retórico clásico formulado por Cicerón y Quintiliano que consiste en ganarse la buena disposición, simpatía y apertura mental de los oyentes al inicio del discurso.",
            oratoryUsage = "Aplícalo reconociendo un mérito real de la audiencia o compartiendo una breve muestra de humildad genuina al comenzar.",
            exampleSentence = "«Su elogio sincero a la trayectoria de la ciudad funcionó como una magistral captatio benevolentiae.»",
            upgradeTip = "Antes de pedir un cambio difícil al público, construye puentes de respeto común.",
            synonyms = listOf("Conquista del favor del público", "Empatía inicial", "Exordio conciliador"),
            antonyms = listOf("Confrontación hostil", "Arrogancia inicial"),
            language = "Latín"
        ),
        DictionaryEntry(
            word = "In medias res",
            category = "Locución Latina",
            phonetic = "[in MÉ-dias res]",
            definition = "Locución latina que significa literalmente «en mitad de la acción». Técnica narrativa que consiste en iniciar una historia en su momento de mayor tensión en lugar de empezar cronológicamente desde el principio.",
            oratoryUsage = "Úsala al contar anécdotas en el escenario: entra directo al segundo crucial del conflicto para atrapar la atención en 5 segundos.",
            exampleSentence = "«Comenzó su discurso in medias res: 'Faltaban tres minutos para cerrar el contrato y las luces se apagaron'.»",
            upgradeTip = "Elimina los preámbulos largos de tus historias y arranca en el instante decisivo.",
            synonyms = listOf("En pleno clímax", "Inicio directo a la acción"),
            antonyms = listOf("Ab ovo", "Preludio extenso"),
            language = "Latín"
        ),
        DictionaryEntry(
            word = "Sine qua non",
            category = "Locución Latina",
            phonetic = "[sí-ne kua non]",
            definition = "Expresión latina que significa «sin la cual no». Se emplea para señalar una condición, requisito o principio absolutamente indispensable para que algo suceda.",
            oratoryUsage = "Aporta elegancia intelectual y contundencia al definir los pilares no negociables de un acuerdo o proyecto.",
            exampleSentence = "«La transparencia ética es condición sine qua non para construir liderazgo duradero.»",
            upgradeTip = "Úsalo con moderación en discursos ejecutivos, académicos o jurídicos para subrayar un requisito esencial.",
            synonyms = listOf("Requisito indispensable", "Condición esencial", "Imperativo"),
            antonyms = listOf("Detalle accesorio", "Elemento opcional"),
            language = "Latín"
        ),
        DictionaryEntry(
            word = "Ad hominem",
            category = "Locución Latina",
            phonetic = "[ad Ó-mi-nem]",
            definition = "Falacia argumentativa que consiste en atacar a la persona que expone una idea en lugar de refutar con pruebas o razones la validez de su argumento.",
            oratoryUsage = "Si un participante tóxico usa un ataque ad hominem contra ti, no respondas al ataque personal: reconduce la conversación hacia los datos objetivos.",
            exampleSentence = "«Un gran debatiente jamás recurre a descalificaciones ad hominem; desarma con evidencia y serenidad.»",
            upgradeTip = "Identificar una falacia ad hominem con calma te da ventaja inmediata en cualquier debate.",
            synonyms = listOf("Ataque personal", "Falacia de desacreditación"),
            antonyms = listOf("Argumentación objetiva", "Refutación racional"),
            language = "Latín"
        )
    )

    val wordOfTheDay: DictionaryEntry
        get() {
            val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
            return curatedLexicon[dayOfYear % curatedLexicon.size]
        }

    fun setLanguage(lang: String) {
        _state.update { it.copy(language = lang) }
    }

    fun setCategoryFilter(category: String) {
        _state.update { it.copy(selectedCategoryFilter = category) }
    }

    fun clearCurrentEntry() {
        _state.update { it.copy(entry = null, error = null) }
    }

    fun toggleFavorite(word: String) {
        _state.update { current ->
            val list = current.favorites.toMutableList()
            val existingIndex = list.indexOfFirst { it.equals(word, ignoreCase = true) }
            if (existingIndex >= 0) {
                list.removeAt(existingIndex)
            } else {
                list.add(0, word)
            }
            current.copy(favorites = list)
        }
    }

    fun clearHistory() {
        _state.update { it.copy(searchHistory = emptyList()) }
    }

    fun getAutocompleteSuggestions(query: String): List<DictionaryEntry> {
        val q = query.trim().lowercase(Locale.getDefault())
        if (q.isEmpty()) return emptyList()
        val normQ = normalizeAccents(q)
        return curatedLexicon.filter { item ->
            val normWord = normalizeAccents(item.word.lowercase(Locale.getDefault()))
            normWord.contains(normQ) || item.synonyms.any { normalizeAccents(it.lowercase(Locale.getDefault())).contains(normQ) }
        }.take(6)
    }

    fun searchWord(word: String) {
        val cleanWord = word.trim()
        if (cleanWord.isBlank()) return

        val currentLang = _state.value.language
        val normQuery = normalizeAccents(cleanWord.lowercase(Locale.getDefault()))

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            // 1. Buscar primero en el Léxico Curado AISO (coincidencia exacta)
            val curatedMatch = curatedLexicon.firstOrNull {
                normalizeAccents(it.word.lowercase(Locale.getDefault())) == normQuery
            }

            if (curatedMatch != null && (currentLang == curatedMatch.language || currentLang == "Español")) {
                addToHistoryAndPublish(curatedMatch)
                return@launch
            }

            // 2. Consultar a Gemini AI para cualquier palabra, término o locución en el idioma elegido
            if (GeminiClient.isApiKeyConfigured()) {
                try {
                    val prompt = """
                        Analiza la palabra, concepto o expresión "$cleanWord" en el contexto de "$currentLang" aplicado a la Oratoria, Retórica y Comunicación Persuasiva (filosofía AISO: Los secretos del orador exitoso).
                        Responde ESTRICTAMENTE con este formato exacto de 8 líneas (sin asteriscos ni markdown extra):
                        CATEGORIA: [Ej: Léxico de Poder / Figura Retórica / Voz y Escenario / Locución Latina / Inglés Oratorio]
                        FONETICA: [Guía breve de pronunciación y sílaba tónica entre corchetes]
                        DEFINICION: [Definición precisa, clara y elegante en 2 a 3 oraciones]
                        USO_ORATORIA: [Cómo y en qué momento del discurso usar esta palabra para proyectar autoridad y persuadir]
                        EJEMPLO_DISCURSO: [Una frase impactante de discurso real usando la palabra entre comillas]
                        MEJORA_LEXICA: [Qué expresión común o débil reemplaza esta palabra para sonar más profesional]
                        SINONIMOS: [4 sinónimos separados por coma]
                        ANTONIMOS: [3 antónimos separados por coma]
                    """.trimIndent()

                    val aiResponse = GeminiClient.chat(
                        contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                        systemPrompt = "Eres el Diccionario Retórico y Léxico de Poder de AISO IA. Responde siempre con rigor lingüístico y enfoque práctico para oradores."
                    )

                    if (!aiResponse.startsWith("Error:") && aiResponse.contains("DEFINICION:")) {
                        val category = extractField(aiResponse, "CATEGORIA:", "FONETICA:").ifBlank { "Léxico Oratorio ($currentLang)" }
                        val phonetic = extractField(aiResponse, "FONETICA:", "DEFINICION:").ifBlank { "[${cleanWord.uppercase()}]" }
                        val definition = extractField(aiResponse, "DEFINICION:", "USO_ORATORIA:")
                        val usage = extractField(aiResponse, "USO_ORATORIA:", "EJEMPLO_DISCURSO:")
                        val example = extractField(aiResponse, "EJEMPLO_DISCURSO:", "MEJORA_LEXICA:")
                        val upgrade = extractField(aiResponse, "MEJORA_LEXICA:", "SINONIMOS:")
                        val syns = extractField(aiResponse, "SINONIMOS:", "ANTONIMOS:")
                            .split(",")
                            .map { it.trim().removeSuffix(".") }
                            .filter { it.isNotBlank() }
                        val ants = aiResponse.substringAfter("ANTONIMOS:", "")
                            .lines()
                            .firstOrNull()
                            .orEmpty()
                            .split(",")
                            .map { it.trim().removeSuffix(".") }
                            .filter { it.isNotBlank() }

                        if (definition.isNotBlank()) {
                            val entry = DictionaryEntry(
                                word = cleanWord.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                category = category,
                                phonetic = phonetic,
                                definition = definition,
                                oratoryUsage = usage.ifBlank { "Emplea este término en el desarrollo de tu discurso acompañándolo de una pausa estratégica para destacar tu argumento." },
                                exampleSentence = example.ifBlank { "«El verdadero liderazgo se demuestra cuando transformamos el concepto de $cleanWord en acciones concretas.»" },
                                upgradeTip = upgrade.ifBlank { "Incorpora «$cleanWord» en tu vocabulario activo para elevar la precisión y autoridad de tus intervenciones." },
                                synonyms = syns.ifEmpty { listOf("Elocución", "Expresión culta", "Precisión verbal") },
                                antonyms = ants.ifEmpty { listOf("Ambigüedad", "Imprecisión") },
                                language = currentLang
                            )
                            addToHistoryAndPublish(entry)
                            return@launch
                        }
                    }
                } catch (_: Exception) {
                }
            }

            // 3. Respaldo inteligente local si no hay conexión
            val fallback = curatedMatch ?: buildSmartOfflineEntry(cleanWord, currentLang)
            addToHistoryAndPublish(fallback)
        }
    }

    private fun addToHistoryAndPublish(entry: DictionaryEntry) {
        val history = _state.value.searchHistory.toMutableList()
        history.removeAll { it.equals(entry.word, ignoreCase = true) }
        history.add(0, entry.word)

        _state.update {
            it.copy(
                isLoading = false,
                entry = entry,
                error = null,
                searchHistory = history.take(12)
            )
        }
    }

    private fun extractField(source: String, startTag: String, endTag: String): String {
        if (!source.contains(startTag)) return ""
        val after = source.substringAfter(startTag)
        val raw = if (after.contains(endTag)) after.substringBefore(endTag) else after.lines().firstOrNull().orEmpty()
        return raw.trim().removePrefix("*").removeSuffix("*").trim()
    }

    private fun buildSmartOfflineEntry(word: String, lang: String): DictionaryEntry {
        val capitalized = word.trim().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        return DictionaryEntry(
            word = capitalized,
            category = "Vocabulario Oratorio ($lang)",
            phonetic = "[${capitalized.uppercase(Locale.getDefault())}]",
            definition = "Concepto empleado en la expresión oral y argumentativa para dotar de matiz, precisión y fuerza persuasiva al discurso frente a una audiencia.",
            oratoryUsage = "Úsalo dentro de la regla 10-80-10 marcando ligeramente la sílaba tónica y sosteniendo la mirada en el sector central del público.",
            exampleSentence = "«Comprender el valor de '$capitalized' nos permite comunicar nuestra visión con claridad y firmeza.»",
            upgradeTip = "Integra '$capitalized' reemplazando muletillas o palabras genéricas como «cosa» o «algo».",
            synonyms = listOf("Concepto clave", "Expresión precisa", "Recurso verbal", "Matiz retórico"),
            antonyms = listOf("Vaguedad", "Imprecisión", "Muletilla"),
            language = lang
        )
    }

    private fun normalizeAccents(input: String): String {
        return input
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ü", "u")
    }
}
