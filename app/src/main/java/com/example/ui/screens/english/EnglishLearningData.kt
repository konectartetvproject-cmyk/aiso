package com.example.ui.screens.english

data class EnglishPhraseItem(
    val id: String,
    val category: String,
    val english: String,
    val phonetics: String,
    val spanish: String,
    val exampleEn: String,
    val exampleEs: String,
    val tip: String
)

data class EnglishSoundLesson(
    val id: String,
    val title: String,
    val symbol: String,
    val spanishGuide: String,
    val targetWords: List<Pair<String, String>>, // Word to Phonetics
    val practiceSentenceEn: String,
    val practiceSentencePhonetics: String,
    val practiceSentenceEs: String
)

data class EnglishPitchTemplate(
    val id: String,
    val title: String,
    val occasion: String,
    val hookEn: String,
    val hookEs: String,
    val bodyEn: String,
    val bodyEs: String,
    val closeEn: String,
    val closeEs: String,
    val keyVocab: List<String>
)

data class EnglishImpromptuTopic(
    val id: String,
    val topicEn: String,
    val topicEs: String,
    val starterPhrases: List<Pair<String, String>>,
    val suggestedVocab: List<Pair<String, String>>
)

data class EnglishQaScenario(
    val id: String,
    val personaName: String,
    val personaRole: String,
    val questionEn: String,
    val questionEs: String,
    val bridgePhraseEn: String,
    val bridgePhrasePhonetics: String,
    val modelAnswerEn: String,
    val modelAnswerEs: String,
    val coachTip: String
)

data class EnglishQuizItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

object EnglishLearningCatalog {

    val phraseCategories = listOf(
        "Todas",
        "1. Inicio (Openings)",
        "2. Conectores (Transitions)",
        "3. Negocios (Pitching)",
        "4. Preguntas (Q&A)",
        "5. Cierres (Closings)",
        "6. Falsos Amigos"
    )

    val phrases = listOf(
        // 1. OPENINGS
        EnglishPhraseItem(
            id = "op_1",
            category = "1. Inicio (Openings)",
            english = "Have you ever wondered why some ideas change the world?",
            phonetics = "/jav iu é-ver uán-derd uái som ai-días cheinch de uórld/",
            spanish = "¿Alguna vez te has preguntado por qué algunas ideas cambian el mundo?",
            exampleEn = "Have you ever wondered why some ideas change the world while others are forgotten?",
            exampleEs = "¿Alguna vez te has preguntado por qué algunas ideas cambian el mundo mientras otras se olvidan?",
            tip = "Técnica AISO de Interrogación Retórica: evita empezar con 'Today I will talk about'. Abre con una pregunta que obligue al público a pensar."
        ),
        EnglishPhraseItem(
            id = "op_2",
            category = "1. Inicio (Openings)",
            english = "Let me start by sharing a short story with you.",
            phonetics = "/let mi start bai shé-ring a short stó-ri uiz iu/",
            spanish = "Permítanme comenzar compartiendo una breve historia con ustedes.",
            exampleEn = "Let me start by sharing a short story with you about my first day on stage.",
            exampleEs = "Permítanme comenzar compartiendo una breve historia sobre mi primer día en el escenario.",
            tip = "Ideal para aplicar el segundo pilar del Método AISO (Historia personal) desde el primer minuto."
        ),
        EnglishPhraseItem(
            id = "op_3",
            category = "1. Inicio (Openings)",
            english = "Imagine a world where language is no longer a barrier.",
            phonetics = "/i-má-llin a uórld uer lán-güich is nou lón-guer a bá-rier/",
            spanish = "Imaginen un mundo donde el idioma ya no sea una barrera.",
            exampleEn = "Imagine a world where language is no longer a barrier to your professional dreams.",
            exampleEs = "Imaginen un mundo donde el idioma ya no sea una barrera para tus sueños profesionales.",
            tip = "La palabra 'Imagine' activa la visualización inmediata en cualquier audiencia internacional."
        ),
        EnglishPhraseItem(
            id = "op_4",
            category = "1. Inicio (Openings)",
            english = "By the end of this talk, you will have three practical tools.",
            phonetics = "/bai di end ov dis tok, iu uil jav zri prák-ti-kal tuls/",
            spanish = "Al finalizar esta charla, tendrán tres herramientas prácticas.",
            exampleEn = "By the end of this talk, you will have three practical tools to double your productivity.",
            exampleEs = "Al finalizar esta charla, tendrán tres herramientas prácticas para duplicar su productividad.",
            tip = "Promesa de valor clara: la audiencia anglosajona valora saber desde el inicio qué beneficio concreto obtendrá."
        ),

        // 2. TRANSITIONS
        EnglishPhraseItem(
            id = "tr_1",
            category = "2. Conectores (Transitions)",
            english = "This brings me to my next point.",
            phonetics = "/dis brings mi tu mai nekst point/",
            spanish = "Esto me lleva a mi siguiente punto.",
            exampleEn = "Now that we understand the problem, this brings me to my next point: the solution.",
            exampleEs = "Ahora que entendemos el problema, esto me lleva a mi siguiente punto: la solución.",
            tip = "Úsalo como puente limpio entre dos bloques de tu discurso para evitar muletillas como 'umm' o 'so'."
        ),
        EnglishPhraseItem(
            id = "tr_2",
            category = "2. Conectores (Transitions)",
            english = "Let's take a closer look at the data.",
            phonetics = "/lets teik a klóu-ser luk at de déi-ta/",
            spanish = "Miremos más de cerca los datos.",
            exampleEn = "Let's take a closer look at the data from the last quarter.",
            exampleEs = "Miremos más de cerca los datos del último trimestre.",
            tip = "Excelente para introducir estadísticas o gráficos con autoridad profesional."
        ),
        EnglishPhraseItem(
            id = "tr_3",
            category = "2. Conectores (Transitions)",
            english = "On the other hand, we must consider the human factor.",
            phonetics = "/on di ó-der jand, ui mast kon-sí-der de jiú-man fák-tor/",
            spanish = "Por otro lado, debemos considerar el factor humano.",
            exampleEn = "Technology is fast; on the other hand, we must consider the human factor.",
            exampleEs = "La tecnología es rápida; por otro lado, debemos considerar el factor humano.",
            tip = "Conector de contraste esencial para debates y exposiciones equilibradas."
        ),
        EnglishPhraseItem(
            id = "tr_4",
            category = "2. Conectores (Transitions)",
            english = "In other words, simplicity wins.",
            phonetics = "/in ó-der uórds, sim-plí-si-ti uins/",
            spanish = "En otras palabras, la simplicidad gana.",
            exampleEn = "Our users want fewer clicks. In other words, simplicity wins.",
            exampleEs = "Nuestros usuarios quieren menos clics. En otras palabras, la simplicidad gana.",
            tip = "Úsalo después de una explicación técnica para resumir la idea en una frase memorable."
        ),

        // 3. BUSINESS & PITCHING
        EnglishPhraseItem(
            id = "bz_1",
            category = "3. Negocios (Pitching)",
            english = "Our main goal is to solve a real problem for our customers.",
            phonetics = "/áuer mein goul is tu solv a ríal pró-blem for áuer kás-to-mers/",
            spanish = "Nuestra meta principal es resolver un problema real para nuestros clientes.",
            exampleEn = "Our main goal is to solve a real problem for our customers in record time.",
            exampleEs = "Nuestra meta principal es resolver un problema real para nuestros clientes en tiempo récord.",
            tip = "Frase central para presentaciones de ventas, startups o juntas directivas."
        ),
        EnglishPhraseItem(
            id = "bz_2",
            category = "3. Negocios (Pitching)",
            english = "The bottom line is that this investment pays for itself.",
            phonetics = "/de bó-tom lain is dat dis in-vést-ment peis for it-sélf/",
            spanish = "En conclusión (lo fundamental es que), esta inversión se paga sola.",
            exampleEn = "The bottom line is that this investment pays for itself within six months.",
            exampleEs = "Lo fundamental es que esta inversión se paga sola en seis meses.",
            tip = "'The bottom line' significa la conclusión definitiva o el resultado final financiero."
        ),
        EnglishPhraseItem(
            id = "bz_3",
            category = "3. Negocios (Pitching)",
            english = "We have seen a thirty percent increase in engagement.",
            phonetics = "/ui jav sin a zér-ti per-sént in-krís in en-guéich-ment/",
            spanish = "Hemos visto un incremento del treinta por ciento en participación.",
            exampleEn = "Since launching the program, we have seen a thirty percent increase in engagement.",
            exampleEs = "Desde el lanzamiento del programa, hemos visto un aumento del 30% en participación.",
            tip = "Recuerda que como sustantivo se pronuncia 'ÍN-kris' y como verbo 'in-KRÍS'."
        ),

        // 4. Q&A
        EnglishPhraseItem(
            id = "qa_1",
            category = "4. Preguntas (Q&A)",
            english = "That is a great question. Let me clarify that point.",
            phonetics = "/dat is a greit kués-chon. let mi klá-ri-fai dat point/",
            spanish = "Esa es una excelente pregunta. Permítame aclarar ese punto.",
            exampleEn = "That is a great question. Let me clarify that point with a real example.",
            exampleEs = "Esa es una excelente pregunta. Permítame aclarar ese punto con un ejemplo real.",
            tip = "Valida siempre a quien pregunta; te da 3 segundos para respirar y ordenar tus ideas en inglés."
        ),
        EnglishPhraseItem(
            id = "qa_2",
            category = "4. Preguntas (Q&A)",
            english = "From my perspective, the key priority is quality.",
            phonetics = "/from mai pers-pék-tiv, de ki prai-ó-ri-ti is kuó-li-ti/",
            spanish = "Desde mi perspectiva, la prioridad clave es la calidad.",
            exampleEn = "From my perspective, the key priority is quality over speed.",
            exampleEs = "Desde mi perspectiva, la prioridad clave es la calidad sobre la velocidad.",
            tip = "Mucho más elegante y persuasivo que decir solamente 'I think'."
        ),
        EnglishPhraseItem(
            id = "qa_3",
            category = "4. Preguntas (Q&A)",
            english = "I don't have the exact figure right now, but I will follow up with you.",
            phonetics = "/ai dont jav di eg-zákt fí-guier rait nau, bat ai uil fó-lou ap uiz iu/",
            spanish = "No tengo la cifra exacta en este momento, pero le daré seguimiento.",
            exampleEn = "I don't have the exact figure right now, but I will follow up with you after the session.",
            exampleEs = "No tengo el dato exacto ahora, pero le daré seguimiento al terminar la sesión.",
            tip = "Regla de oro del Cap. 13: nunca inventes un dato en inglés si no lo sabes. Usa 'follow up'."
        ),

        // 5. CLOSINGS
        EnglishPhraseItem(
            id = "cl_1",
            category = "5. Cierres (Closings)",
            english = "To sum up, remember these three key takeaways.",
            phonetics = "/tu sam ap, ri-mém-ber dis zri ki téi-ka-ueis/",
            spanish = "Para resumir, recuerden estos tres aprendizajes clave.",
            exampleEn = "To sum up, remember these three key takeaways: prepare, practice, and connect.",
            exampleEs = "Para resumir, recuerden estos tres aprendizajes clave: preparar, practicar y conectar.",
            tip = "'Key takeaways' es el término estándar en conferencias en inglés para las ideas que el público debe llevarse."
        ),
        EnglishPhraseItem(
            id = "cl_2",
            category = "5. Cierres (Closings)",
            english = "The future belongs to those who take action today. Thank you.",
            phonetics = "/de fiú-chor bi-lóngs tu dous ju teik ák-shon tu-déi. zank iu/",
            spanish = "El futuro pertenece a quienes toman acción hoy. Muchas gracias.",
            exampleEn = "Don't wait for perfection. The future belongs to those who take action today. Thank you.",
            exampleEs = "No esperen la perfección. El futuro pertenece a quienes toman acción hoy. Gracias.",
            tip = "Llamado a la acción (Call to Action) directo y enérgico para despertar el aplauso."
        ),

        // 6. FALSE FRIENDS ON STAGE
        EnglishPhraseItem(
            id = "ff_1",
            category = "6. Falsos Amigos",
            english = "Actually, our results exceeded all expectations.",
            phonetics = "/ák-chua-li, áuer ri-zálts ek-sí-ded ol eks-pek-téi-shons/",
            spanish = "De hecho (en realidad), nuestros resultados superaron todas las expectativas.",
            exampleEn = "Actually, our results exceeded all expectations this year.",
            exampleEs = "En realidad, nuestros resultados superaron todas las expectativas este año.",
            tip = "¡Cuidado! 'Actually' significa 'En realidad / De hecho', NO significa 'Actualmente' (para decir actualmente usa 'Currently' o 'Nowadays')."
        ),
        EnglishPhraseItem(
            id = "ff_2",
            category = "6. Falsos Amigos",
            english = "Let me introduce our guest speaker for today.",
            phonetics = "/let mi in-tro-diús áuer guest spí-ker for tu-déi/",
            spanish = "Permítanme presentar a nuestro orador invitado de hoy.",
            exampleEn = "Let me introduce our guest speaker, Dr. Sarah Jenkins.",
            exampleEs = "Permítanme presentar a nuestra oradora invitada, la Dra. Sarah Jenkins.",
            tip = "En inglés, 'presentar a una persona' se dice 'introduce'. Usa 'present' para presentar un tema o informe."
        ),
        EnglishPhraseItem(
            id = "ff_3",
            category = "6. Falsos Amigos",
            english = "We would like to discuss this opportunity with your team.",
            phonetics = "/ui uud laik tu dis-kás dis o-por-tiú-ni-ti uiz ior tim/",
            spanish = "Nos gustaría conversar (tratar) esta oportunidad con su equipo.",
            exampleEn = "We would like to discuss this opportunity with your team tomorrow.",
            exampleEs = "Nos gustaría tratar esta oportunidad con su equipo mañana.",
            tip = "En inglés 'discuss' significa conversar o analizar un tema en calma (NO pelear; pelear verbalmente es 'argue')."
        )
    )

    val soundLessons = listOf(
        EnglishSoundLesson(
            id = "snd_th",
            title = "1. El sonido 'TH' (/θ/ y /ð/)",
            symbol = "TH",
            spanishGuide = "Coloca la punta de la lengua ligeramente entre los dientes superiores e inferiores y sopla suavemente. Evita cambiarlo por 'T', 'D' o 'S'.",
            targetWords = listOf(
                "Think (Pensar)" to "/zink/",
                "Thank you (Gracias)" to "/zank iu/",
                "Three (Tres)" to "/zri/",
                "Together (Juntos)" to "/tu-gué-der/",
                "This / That (Esto / Eso)" to "/dis / dat/"
            ),
            practiceSentenceEn = "I think that these three things will help us grow together.",
            practiceSentencePhonetics = "/ai zink dat dis zri zings uil jelp as grou tu-gué-der/",
            practiceSentenceEs = "Creo que estas tres cosas nos ayudarán a crecer juntos."
        ),
        EnglishSoundLesson(
            id = "snd_v_b",
            title = "2. Diferencia entre 'V' y 'B'",
            symbol = "V / B",
            spanishGuide = "En español suenan igual, pero en inglés son opuestas: la 'B' junta ambos labios (Best), mientras que la 'V' apoya los dientes superiores sobre el labio inferior vibrando (Very, Value, Voice).",
            targetWords = listOf(
                "Voice (Voz)" to "/vóis/",
                "Value (Valor)" to "/vá-liu/",
                "Vision (Visión)" to "/ví-shon/",
                "Business (Negocio)" to "/bís-nes/",
                "Believe (Creer)" to "/bi-lív/"
            ),
            practiceSentenceEn = "We believe that a brave vision brings very big value to business.",
            practiceSentencePhonetics = "/ui bi-lív dat a breiv ví-shon brings vé-ri big vá-liu tu bís-nes/",
            practiceSentenceEs = "Creemos que una visión valiente aporta un valor muy grande a los negocios."
        ),
        EnglishSoundLesson(
            id = "snd_s_cluster",
            title = "3. Palabras que inician con 'S + Consonante'",
            symbol = "SP / ST / SK",
            spanishGuide = "Error común hispanohablante: agregar una 'E' imaginaria al inicio ('E-speak', 'E-start', 'E-school'). Inicia directamente con el siseo de la serpiente 'Sssss-peak'.",
            targetWords = listOf(
                "Speak (Hablar)" to "/ssspik/ (NO es-pik)",
                "Stage (Escenario)" to "/sssteich/ (NO es-teich)",
                "Story (Historia)" to "/ssstó-ri/ (NO es-tori)",
                "Special (Especial)" to "/ssspé-shal/",
                "Strategy (Estrategia)" to "/ssstrá-te-lli/"
            ),
            practiceSentenceEn = "Steve starts speaking on stage with a special story and strong strategy.",
            practiceSentencePhonetics = "/ssstiv ssstarts ssspí-king on sssteich uiz a ssspé-shal ssstó-ri/",
            practiceSentenceEs = "Steve empieza a hablar en el escenario con una historia especial y una estrategia sólida."
        ),
        EnglishSoundLesson(
            id = "snd_ed",
            title = "4. Pasado '-ED' sin pronunciar la 'E'",
            symbol = "-ED",
            spanishGuide = "En verbos como 'Worked', 'Helped', 'Learned' o 'Changed', NUNCA pronuncies '-ed' como sílaba separada. Pasa directo a una 'T' o 'D' suave al final (excepto si termina en T o D como 'Started' o 'Needed').",
            targetWords = listOf(
                "Worked (Trabajó)" to "/uórkt/ (NO uór-ked)",
                "Helped (Ayudó)" to "/jelpt/ (NO jel-ped)",
                "Learned (Aprendió)" to "/lernd/ (NO ler-ned)",
                "Changed (Cambió)" to "/cheincht/ (NO chein-ched)",
                "Started (Empezó)" to "/stár-ted/ (Aquí sí suena)"
            ),
            practiceSentenceEn = "We worked hard, learned fast, and changed the way we helped our clients.",
            practiceSentencePhonetics = "/ui uórkt jard, lernd fast, and cheincht de uei ui jelpt áuer kláients/",
            practiceSentenceEs = "Trabajamos duro, aprendimos rápido y cambiamos la forma en que ayudamos a nuestros clientes."
        ),
        EnglishSoundLesson(
            id = "snd_r_sh",
            title = "5. La 'R' suave y 'SH vs CH'",
            symbol = "R / SH",
            spanishGuide = "Para la 'R' inglesa (Right, Ready), curva la lengua hacia atrás sin tocar el paladar (nunca vibres como en 'perro'). Para 'SH' (Share), haz el sonido de pedir silencio 'Shhh'.",
            targetWords = listOf(
                "Share (Compartir)" to "/shhh-er/",
                "Chair (Silla)" to "/ch-er/",
                "Right (Correcto)" to "/rait/",
                "Results (Resultados)" to "/ri-zálts/",
                "Future (Futuro)" to "/fiú-chor/"
            ),
            practiceSentenceEn = "She is ready to share the right research and real results for the future.",
            practiceSentencePhonetics = "/shi is ré-di tu sher de rait ri-sérch and ríal ri-zálts for de fiú-chor/",
            practiceSentenceEs = "Ella está lista para compartir la investigación correcta y resultados reales para el futuro."
        )
    )

    val pitchTemplates = listOf(
        EnglishPitchTemplate(
            id = "pitch_elevator",
            title = "1. Elevator Pitch Profesional (60 seg)",
            occasion = "Networking, entrevistas y presentación personal",
            hookEn = "Hi, I'm a communication specialist passionate about helping teams turn complex ideas into clear action.",
            hookEs = "Hola, soy especialista en comunicación apasionado por ayudar a los equipos a convertir ideas complejas en acciones claras.",
            bodyEn = "Over the past few years, I've noticed that great projects often fail not because of bad technology, but because of poor communication. Using the AISO methodology, I train professionals to structure persuasive messages and speak with confidence.",
            bodyEs = "En los últimos años he notado que grandes proyectos suelen fallar no por mala tecnología, sino por mala comunicación. Usando la metodología AISO, entreno a profesionales para estructurar mensajes persuasivos y hablar con seguridad.",
            closeEn = "I'd love to schedule a brief fifteen-minute call this week to explore how we can empower your team. Thank you!",
            closeEs = "Me encantaría agendar una breve llamada de 15 minutos esta semana para explorar cómo potenciar a su equipo. ¡Gracias!",
            keyVocab = listOf("Passionate about (Apasionado por)", "Turn into (Convertir en)", "Empower (Potenciar / Empoderar)", "Schedule a call (Agendar una llamada)")
        ),
        EnglishPitchTemplate(
            id = "pitch_startup",
            title = "2. Presentación de Proyecto o Startup",
            occasion = "Inversionistas, clientes o comité directivo",
            hookEn = "Did you know that professionals lose nearly five hours every week due to inefficient meetings?",
            hookEs = "¿Sabían que los profesionales pierden casi cinco horas cada semana debido a reuniones ineficientes?",
            bodyEn = "Last year, our own team struggled with this exact problem. That is why we developed a streamlined workflow that cuts meeting time in half while doubling clarity and accountability.",
            bodyEs = "El año pasado, nuestro propio equipo sufrió este mismo problema. Por eso desarrollamos un flujo de trabajo ágil que reduce a la mitad el tiempo de reunión mientras duplica la claridad y el compromiso.",
            closeEn = "We are ready to launch next month. Join us today and let's build a more productive future together.",
            closeEs = "Estamos listos para lanzar el próximo mes. Únanse hoy y construyamos juntos un futuro más productivo.",
            keyVocab = listOf("Did you know that... (¿Sabían que...?)", "Struggled with (Luchó con / Sufrió)", "Cuts in half (Reduce a la mitad)", "Accountability (Responsabilidad / Compromiso)")
        ),
        EnglishPitchTemplate(
            id = "pitch_keynote",
            title = "3. Discurso Inspiracional (Estilo Método AISO)",
            occasion = "Conferencias, graduaciones y liderazgo",
            hookEn = "Fear doesn't disappear when you stand on stage; it transforms into energy when you speak from the heart.",
            hookEs = "El miedo no desaparece cuando subes al escenario; se transforma en energía cuando hablas desde el corazón.",
            bodyEn = "When I gave my first speech in English, my hands were shaking and my voice was trembling. Instead of hiding my nerves, I took a deep breath, looked at the audience, and shared my true story. That day I learned that audiences don't want perfection—they want connection.",
            bodyEs = "Cuando di mi primer discurso en inglés, mis manos temblaban y mi voz titubeaba. En lugar de esconder mis nervios, respiré profundo, miré al público y compartí mi verdadera historia. Ese día aprendí que el público no busca perfección, busca conexión.",
            closeEn = "So the next time you step up to a microphone, don't just read words. Speak from the heart and let your voice be heard. Thank you!",
            closeEs = "Así que la próxima vez que te pares frente a un micrófono, no solo leas palabras. Habla desde el corazón y haz escuchar tu voz. ¡Gracias!",
            keyVocab = listOf("Speak from the heart (Hablar desde el corazón)", "Took a deep breath (Respiré profundo)", "Step up to (Dar un paso al frente hacia)", "Let your voice be heard (Haz escuchar tu voz)")
        )
    )

    val impromptuTopics = listOf(
        EnglishImpromptuTopic(
            id = "imp_1",
            topicEn = "Why is public speaking the most valuable skill in the age of AI?",
            topicEs = "¿Por qué hablar en público es la habilidad más valiosa en la era de la IA?",
            starterPhrases = listOf(
                "In my experience, technology can automate tasks, but..." to "En mi experiencia, la tecnología automatiza tareas, pero...",
                "The human voice creates trust and empathy because..." to "La voz humana crea confianza y empatía porque...",
                "In conclusion, AI is a tool, but connection is human." to "En conclusión, la IA es una herramienta, pero la conexión es humana."
            ),
            suggestedVocab = listOf(
                "Trust" to "Confianza",
                "Empathy" to "Empatía",
                "Irreplaceable" to "Irremplazable",
                "Leadership" to "Liderazgo"
            )
        ),
        EnglishImpromptuTopic(
            id = "imp_2",
            topicEn = "Share a mistake or challenge that taught you an important lesson.",
            topicEs = "Comparte un error o reto que te haya enseñado una lección importante.",
            starterPhrases = listOf(
                "A few years ago, I faced a difficult challenge when..." to "Hace unos años enfrenté un reto difícil cuando...",
                "At first, I felt frustrated, however I realized that..." to "Al principio me sentí frustrado; sin embargo, comprendí que...",
                "The biggest lesson I learned from that day was..." to "La mayor lección que aprendí de aquel día fue..."
            ),
            suggestedVocab = listOf(
                "Overcome" to "Superar",
                "Resilience" to "Resiliencia",
                "Turning point" to "Punto de inflexión",
                "Growth" to "Crecimiento"
            )
        ),
        EnglishImpromptuTopic(
            id = "imp_3",
            topicEn = "What makes a great leader in times of crisis?",
            topicEs = "¿Qué hace a un gran líder en tiempos de crisis?",
            starterPhrases = listOf(
                "True leadership is tested when things get difficult." to "El verdadero liderazgo se pone a prueba cuando las cosas se complican.",
                "First of all, a great leader listens before acting." to "En primer lugar, un gran líder escucha antes de actuar.",
                "Ultimately, people follow leaders who stay calm and honest." to "En última instancia, la gente sigue a líderes que mantienen la calma y la honestidad."
            ),
            suggestedVocab = listOf(
                "Emotional intelligence" to "Inteligencia emocional",
                "Clarity" to "Claridad",
                "Example" to "Ejemplo",
                "Courage" to "Valentía"
            )
        )
    )

    val qaScenarios = listOf(
        EnglishQaScenario(
            id = "qa_ernesto",
            personaName = "Ernest the Expert (Ernesto el Erudito)",
            personaRole = "Escéptico de datos en conferencia internacional",
            questionEn = "Your proposal sounds interesting, but do you have any real data proving this works in international markets?",
            questionEs = "Su propuesta suena interesante, pero ¿tiene datos reales que prueben que esto funciona en mercados internacionales?",
            bridgePhraseEn = "Thank you for raising that point. Let's look at the concrete results we have achieved so far.",
            bridgePhrasePhonetics = "/zank iu for réi-zing dat point. lets luk at de kon-krít ri-zálts ui jav a-chívd sou far/",
            modelAnswerEn = "Thank you for raising that point. In our pilot program across three countries, client retention increased by twenty-eight percent in the first quarter. I would be happy to share the full report with you after the session.",
            modelAnswerEs = "Gracias por plantear ese punto. En nuestro programa piloto en tres países, la retención de clientes aumentó un 28% en el primer trimestre. Con gusto le compartiré el informe completo al terminar la sesión.",
            coachTip = "Aplica la técnica AISO contra Ernesto el Erudito: valida su pregunta sin confrontar, aporta un dato concreto y ofrece ampliar en privado para no perder el control del tiempo."
        ),
        EnglishQaScenario(
            id = "qa_susana",
            personaName = "Susan the Sniper (Susana la Francotiradora)",
            personaRole = "Pregunta directa sobre costos y presupuesto",
            questionEn = "Why should we choose your team when your competitors offer a cheaper solution?",
            questionEs = "¿Por qué deberíamos elegir a su equipo cuando sus competidores ofrecen una solución más barata?",
            bridgePhraseEn = "I appreciate your directness. The key difference lies in long-term value and reliability.",
            bridgePhrasePhonetics = "/ai a-prí-shi-eit ior dai-rékt-nes. de ki dí-frens lais in long term vá-liu and ri-lai-a-bí-li-ti/",
            modelAnswerEn = "I appreciate your directness. While initial costs may look lower elsewhere, our solution includes full training and support, which saves your company time and prevents costly mistakes down the road.",
            modelAnswerEs = "Agradezco su franqueza. Aunque el costo inicial parezca menor en otro lugar, nuestra solución incluye capacitación y soporte total, lo que ahorra tiempo y evita errores costosos a futuro.",
            coachTip = "Ante una pregunta agresiva en inglés, usa 'I appreciate your directness' con voz serena y redirige la conversación de 'precio' (price) a 'valor' (value)."
        ),
        EnglishQaScenario(
            id = "qa_nicanor",
            personaName = "Nick the Negative (Nicanor el Negativo)",
            personaRole = "Participante pesimista ante el cambio",
            questionEn = "We tried something similar two years ago and it didn't work. Why would this time be any different?",
            questionEs = "Intentamos algo parecido hace dos años y no funcionó. ¿Por qué esta vez sería diferente?",
            bridgePhraseEn = "I understand your concern, and learning from past experience is essential.",
            bridgePhrasePhonetics = "/ai an-der-stánd ior kon-sérn, and lér-ning from past eks-pí-riens is e-sén-shal/",
            modelAnswerEn = "I understand your concern, and learning from past experience is essential. What makes this approach different is that we designed it step by step with direct feedback from the team, ensuring practical adoption from day one.",
            modelAnswerEs = "Comprendo su inquietud, y aprender de la experiencia pasada es esencial. Lo que hace diferente a este enfoque es que lo diseñamos paso a paso con retroalimentación directa del equipo, asegurando su adopción desde el día uno.",
            coachTip = "Con Nicanor el Negativo (Cap. 15), usa 'I understand your concern' para desactivar su resistencia emocional y muestra el cambio concreto de método."
        )
    )

    val quizItems = listOf(
        EnglishQuizItem(
            question = "1. ¿Qué significa realmente la palabra 'ACTUALLY' cuando la usas en un discurso en inglés?",
            options = listOf(
                "Actualmente / Hoy en día",
                "En realidad / De hecho",
                "Actuar en el escenario",
                "Con mucha energía"
            ),
            correctIndex = 1,
            explanation = "'Actually' es el falso amigo más frecuente: significa 'En realidad / De hecho'. Para decir 'Actualmente' debes usar 'Currently' o 'Nowadays'."
        ),
        EnglishQuizItem(
            question = "2. Según el Método AISO adaptado al inglés, ¿cuál es la mejor forma de abrir una conferencia internacional?",
            options = listOf(
                "Good morning, my English is not very good...",
                "Today I am going to read my slides about...",
                "Have you ever wondered why...? / Imagine a world where...",
                "Sorry for being nervous, let's start."
            ),
            correctIndex = 2,
            explanation = "Nunca pidas disculpas por tu nivel de inglés al iniciar (Cap. 12). Abre con una pregunta retórica ('Have you ever wondered...?') o visualización ('Imagine...')."
        ),
        EnglishQuizItem(
            question = "3. ¿Cómo se pronuncia correctamente el pasado del verbo 'WORKED' en inglés sin caer en el error hispanohablante?",
            options = listOf(
                "/uór-ked/ (dos sílabas marcando la E)",
                "/uórkt/ (una sola sílaba cerrando con T suave)",
                "/uór-kid/",
                "/uórk-ed/"
            ),
            correctIndex = 1,
            explanation = "En verbos terminados en sonidos sordos como K o P (worked, helped), la '-ed' suena como una '/t/' final unida a la palabra: '/uórkt/', nunca '/uór-ked/'."
        ),
        EnglishQuizItem(
            question = "4. Si un asistente te hace una pregunta difícil de la cual no tienes el dato exacto en ese instante, ¿qué frase ejecutiva debes usar?",
            options = listOf(
                "I have no idea about that.",
                "Maybe it is around fifty percent, I guess.",
                "I don't have the exact figure right now, but I will follow up with you after the session.",
                "Next question, please."
            ),
            correctIndex = 2,
            explanation = "'I will follow up with you' (Le daré seguimiento) demuestra máxima solvencia ejecutiva y honestidad sin inventar cifras."
        ),
        EnglishQuizItem(
            question = "5. ¿Qué expresión se usa en el cierre de una presentación en inglés para referirse a las 'ideas o aprendizajes clave que el público se lleva'?",
            options = listOf(
                "Key takeaways",
                "Fast food ideas",
                "Homeworks",
                "Final words only"
            ),
            correctIndex = 0,
            explanation = "'Key takeaways' es el término estándar en conferencias internacionales para resumir los puntos esenciales que la audiencia debe recordar."
        )
    )
}
