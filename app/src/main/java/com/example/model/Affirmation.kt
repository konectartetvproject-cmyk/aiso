package com.example.model

data class Affirmation(
    val id: String,
    val title: String,
    val category: String,
    val text: String
)

val sampleAffirmations = listOf(
    // 1. Oratoria (Original + Expansión)
    Affirmation(
        id = "aff1",
        title = "Confianza en el escenario",
        category = "Oratoria",
        text = "Mi voz es poderosa y mis ideas merecen ser escuchadas. Hablo con claridad, seguridad y conecto profundamente con mi audiencia."
    ),
    Affirmation(
        id = "aff5",
        title = "Dominio del Miedo Escénico",
        category = "Oratoria",
        text = "El escenario es mi espacio de servicio y entrega. No busco ser perfecto, busco ser auténtico y aportar verdadero valor."
    ),
    Affirmation(
        id = "aff12",
        title = "Trinidad de la Influencia",
        category = "Oratoria",
        text = "Cultivo mi inteligencia intrapersonal con auto-confianza, mi interpersonal con empatía y mi emocional con dominio de mis impulsos."
    ),
    Affirmation(
        id = "aff13",
        title = "Anfitrión del Auditorio",
        category = "Oratoria",
        text = "Piso cada escenario con la serenidad de quien recibe amigos en su propia casa. La sala es un lugar seguro para mi mensaje."
    ),

    // 2. Gestión Emocional (Original + Expansión)
    Affirmation(
        id = "aff2",
        title = "Calma bajo presión",
        category = "Gestión Emocional",
        text = "Respiro profundamente y encuentro la calma en mi interior. Transformo los nervios en energía y entusiasmo positivo."
    ),
    Affirmation(
        id = "aff14",
        title = "Inmunidad ante el Juicio",
        category = "Gestión Emocional",
        text = "Suelto la necesidad de aprobación externa. Mi valor como comunicador reside en la honestidad y generosidad de lo que comparto."
    ),
    Affirmation(
        id = "aff15",
        title = "Serenidad ante el Imprevisto",
        category = "Gestión Emocional",
        text = "Si olvido una palabra o surge un imprevisto técnico, sonrío, hago una pausa consciente y continúo con total aplomo."
    ),

    // 3. Crecimiento Personal (Original + Expansión)
    Affirmation(
        id = "aff3",
        title = "Abundancia y Éxito",
        category = "Crecimiento Personal",
        text = "Atraigo el éxito y la prosperidad en todo lo que emprendo. Cada desafío es una oportunidad para crecer y brillar."
    ),
    Affirmation(
        id = "aff16",
        title = "Evolución Constante",
        category = "Crecimiento Personal",
        text = "Cada discurso, ensayo y conversación es una oportunidad para pulir mi voz y convertirme en mi mejor versión como orador."
    ),

    // 4. Liderazgo (Original + Expansión)
    Affirmation(
        id = "aff4",
        title = "Liderazgo Positivo",
        category = "Liderazgo",
        text = "Inspiro a otros con mi ejemplo. Mi liderazgo está basado en la empatía, la visión y la acción constante."
    ),
    Affirmation(
        id = "aff17",
        title = "Voz que Guía e Inspira",
        category = "Liderazgo",
        text = "Hablo con integridad y propósito claro; mis palabras despiertan lo mejor en las personas que me rodean."
    ),

    // 5. Técnica Vocal (Original + Expansión)
    Affirmation(
        id = "aff6",
        title = "Claridad y Dicción Impecable",
        category = "Técnica Vocal",
        text = "Articulo cada palabra con precisión y ritmo. Mi respiración diafragmática sostiene un tono firme, cálido y resonante."
    ),
    Affirmation(
        id = "aff11",
        title = "El Valor de las Pausas",
        category = "Técnica Vocal",
        text = "No temo al silencio; uso las pausas estratégicas para dar peso a mis ideas y permitir que el público reflexione."
    ),
    Affirmation(
        id = "aff18",
        title = "Matices que Enamoran",
        category = "Técnica Vocal",
        text = "Modifico mi volumen, velocidad y entonación con maestría para mantener despierta la mente y el corazón de quienes me escuchan."
    ),

    // 6. Comunicación (Original + Expansión)
    Affirmation(
        id = "aff7",
        title = "Conexión Visual y Empatía",
        category = "Comunicación",
        text = "Miro a los ojos de mi audiencia con sincero interés. Cada mirada crea un puente de confianza y entendimiento mutuo."
    ),
    Affirmation(
        id = "aff19",
        title = "Escucha Activa y Diálogo",
        category = "Comunicación",
        text = "Comunico con claridad porque primero sé escuchar con atención plena las inquietudes y anhelos de mi audiencia."
    ),

    // 7. Persuasión (Original + Expansión)
    Affirmation(
        id = "aff8",
        title = "Asertividad ante Preguntas Difíciles",
        category = "Persuasión",
        text = "Mantengo la compostura y la serenidad ante cualquier interrupción. Respondo con fundamentos sólidos, respeto y firmeza."
    ),
    Affirmation(
        id = "aff20",
        title = "Argumentos que Convencen",
        category = "Persuasión",
        text = "Combino datos sólidos con historias humanas que tocan el corazón y movilizan a mi audiencia hacia la acción."
    ),

    // 8. Estructura del Discurso (Original + Expansión)
    Affirmation(
        id = "aff9",
        title = "Poder de la Conclusión",
        category = "Estructura del Discurso",
        text = "Cierro mis presentaciones con impacto y convicción, dejando un mensaje memorable que motiva a la acción."
    ),
    Affirmation(
        id = "aff21",
        title = "Orden y Claridad Mental",
        category = "Estructura del Discurso",
        text = "Mis ideas fluyen con orden impecable desde un gancho inicial cautivador hasta un cierre redondo y memorable."
    ),

    // 9. Presencia Escénica (Original + Expansión)
    Affirmation(
        id = "aff10",
        title = "Lenguaje Corporal Seguro",
        category = "Presencia Escénica",
        text = "Mi postura es erguida, abierta y receptiva. Mis gestos acompañan con naturalidad la fuerza de mi mensaje."
    ),
    Affirmation(
        id = "aff22",
        title = "Dominio del Espacio",
        category = "Presencia Escénica",
        text = "Me desplazo por el escenario con propósito y elegancia; cada paso refuerza la transición hacia una nueva gran idea."
    )
)
