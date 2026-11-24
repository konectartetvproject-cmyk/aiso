package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign

data class ToxicQuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

data class ToxicParticipant(
    val name: String,
    val description: String,
    val action: String,
    val scenarios: List<ToxicQuizQuestion>
)

val toxicParticipantsList = listOf(
    ToxicParticipant(
        name = "Caso 01 · Quien corrige o añade detalles constantemente",
        description = "Interrumpe para matizar términos técnicos o agregar datos secundarios, desviando el ritmo de la explicación principal.",
        action = "Aplicar el recurso A.V.R. (Agradecer, Valorar y Redirigir) y la secuencia Observa → Comprueba → Responde → Limita y continúa. Reconoce el matiz sin discutir por tener la razón (T51) y ofrece revisar detalles específicos al finalizar.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Estás explicando un concepto y un participante te interrumpe diciendo: 'En realidad, ese indicador tiene tres excepciones técnicas adicionales...'. ¿Cómo aplicas A.V.R.?",
                options = listOf(
                    "Debates con él frente a todos para demostrar que dominas las excepciones.",
                    "Agradeces el matiz, valoras su precisión y rediriges: 'Gracias por señalar ese detalle técnico; para el propósito general de hoy nos enfocaremos en la base común y al cierre revisamos esas excepciones'.",
                    "Le dices que si quiere puede pasar al frente a dar la sesión."
                ),
                correctAnswerIndex = 1
            ),
            ToxicQuizQuestion(
                question = "Minutos después vuelve a levantar la mano para agregar otro tecnicismo menor. ¿Qué haces según el Capítulo 15?",
                options = listOf(
                    "Acuerdas con serenidad reunir las precisiones adicionales al final para cuidar el tiempo de todo el grupo.",
                    "Ignoras su mano el resto de la presentación.",
                    "Detienes el tema principal para discutir cada detalle."
                ),
                correctAnswerIndex = 0
            )
        )
    ),
    ToxicParticipant(
        name = "Caso 02 y 04 · Quien encadena preguntas o prolonga su turno",
        description = "Formula tres o cuatro preguntas seguidas sin dejar responder, o convierte su turno de pregunta en un relato extenso que ocupa el tiempo común.",
        action = "Cuidar los turnos del grupo (Cap. 6 y 15): escuchar con atención, aprovechar la primera pausa respiratoria y pedir con amabilidad que elija la pregunta prioritaria en una sola oración.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Un asistente toma la palabra y lleva dos minutos contando su experiencia personal sin formular ninguna pregunta. ¿Cuál es la respuesta AISO?",
                options = listOf(
                    "Cortarlo con brusquedad: 'Ya hablaste demasiado, siguiente turno'.",
                    "Intervenir cuando tome aire: 'Gracias por compartir ese contexto. Para que podamos responder con claridad y escuchar a más personas, ¿cuál sería tu pregunta principal en una frase?'",
                    "Mirar el reloj con gesto de impaciencia sin decir nada."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "Caso 03 y 08 · Comentarios hirientes o tono elevado",
        description = "Lanza un comentario sarcástico, ataque indirecto o eleva el volumen buscando provocar una reacción defensiva.",
        action = "Activar el Escudo de la Serenidad y el Termostato Emocional (IE05–06): hacer una Pausa Estratégica (IE01), no tomar la crítica como ataque personal (T46), bajar la intensidad (T49), reformular hacia hechos verificables (T59) y establecer límites claros si hay falta de respeto (T55).",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Alguien dice con sarcasmo: 'Lo que propones suena muy bonito en teoría, pero se nota que no conoces el trabajo real'. ¿Cómo respondes?",
                options = listOf(
                    "Confrontas su comentario enumerando todos tus títulos y años de experiencia.",
                    "Haces una pausa de 2 segundos, mantienes el tono sereno y reformulas hacia el hecho: 'Entiendo que te preocupa cómo aplicarlo bajo la carga diaria de trabajo. Veamos un caso concreto de implementación paso a paso'.",
                    "Le ordenas que se calme inmediatamente."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "Caso 05 · Quien repite «aquí eso nunca funciona»",
        description = "Reitera objeciones cerradas basadas en intentos pasados y frena el avance hacia las soluciones prácticas.",
        action = "Validar la preocupación antes de responder (T43), convertir la objeción en información útil (T44) y abrir al grupo preguntando qué condición concreta sí permitiría dar un primer paso realizable.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Propones una mejora en la comunicación del equipo y alguien afirma: 'Eso ya se intentó hace años y nunca funcionó'. ¿Cuál es tu mejor decisión?",
                options = listOf(
                    "Decirle que tiene una mentalidad negativa.",
                    "Validar y redirigir: 'Gracias por mencionarlo; conocer qué dificultó aquel intento es muy útil. ¿Qué ajuste concreto creen ustedes que haría viable probar hoy una versión más sencilla?'",
                    "Cambiar de tema sin responder."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "Caso 06 y 10 · Conversaciones paralelas o interrupciones virtuales",
        description = "Dos o más personas conversan entre sí impidiendo escuchar, o alguien abre el micrófono repetidamente en una sesión virtual sin respetar turnos.",
        action = "En presencial: pausa breve, cercanía serena sin exhibir a nadie o recuperar la atención con una tarea pertinente. En virtual (Caso 10): recordar con cortesía el acuerdo de participación (levantar la mano o escribir en el chat).",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Mientras explicas el concepto central, dos personas conversan en voz baja distrayendo a su fila. ¿Qué recomiendan los Capítulos 13 y 15?",
                options = listOf(
                    "Gritar desde el escenario para imponer silencio.",
                    "Hacer una pausa breve (silencio estratégico T35) o desplazarte con serenidad hacia ese sector mientras continúas explicando para recuperar la atención sin humillar a nadie.",
                    "Señalarlos con el dedo frente a toda la sala."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "Caso 07 y 09 · Ridiculizar a otro o afirmar datos no verificados",
        description = "Alguien se burla de la pregunta de un compañero (Caso 07) o presenta como hecho absoluto un rumor o cifra sin fuente comprobada (Caso 09).",
        action = "En el Caso 07: poner un límite inmediato y sereno para proteger el respeto del grupo (T54, T55). En el Caso 09: aplicar el Árbol de decisión (Ruta B) distinguiendo con cortesía entre un dato verificado con fuente y una suposición pendiente de confirmar.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Un participante hace una pregunta básica y otro se ríe diciendo que eso es obvio (Caso 07). ¿Qué debe hacer el orador AISO?",
                options = listOf(
                    "Dejar pasar la burla para evitar momentos incómodos.",
                    "Intervenir de inmediato con serenidad y firmeza: 'En este espacio todas las preguntas son valiosas y nos tratamos con respeto. Gracias por plantear la duda, vamos a responderla con claridad'.",
                    "Reírse también para caer bien."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "Caso 11 y 12 · Discrepancia firme y respetuosa o silencio al participar",
        description = "El Caso 11 es alguien que expresa un desacuerdo con argumentos y respeto (¡no es conducta tóxica!). El Caso 12 es quien permanece callado o necesita otro modo de participar.",
        action = "En el Caso 11: escuchar con apertura (IT03), responder con razones verificables sin confrontación personal (T45). En el Caso 12: nunca forzar ni exponer a quien guarda silencio; ofrecer reflexión en parejas o participación voluntaria (C01).",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Una persona levanta la mano y presenta con respeto un argumento sólido que difiere de tu propuesta (Caso 11). ¿Cómo actúas?",
                options = listOf(
                    "Lo tratas como un participante hostil y cortas su intervención.",
                    "Escuchas su argumento completo, reconoces el punto válido que aporta y explicas con datos verificables tu postura sin convertir el desacuerdo en disputa personal.",
                    "Cedes por completo aunque tus datos digan lo contrario."
                ),
                correctAnswerIndex = 1
            )
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToxicParticipantsScreen(onBack: () -> Unit) {
    var participantIndex by remember { mutableStateOf(0) }
    var scenarioIndex by remember { mutableStateOf(-1) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manejo de Público", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    if (participantIndex < toxicParticipantsList.size) {
                        val participant = toxicParticipantsList[participantIndex]
                        
                        if (scenarioIndex == -1) {
                            Button(
                                onClick = { scenarioIndex = 0 },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Practicar Escenarios")
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (!showFeedback) {
                                        showFeedback = true
                                    } else {
                                        showFeedback = false
                                        selectedOption = null
                                        if (scenarioIndex < participant.scenarios.size - 1) {
                                            scenarioIndex++
                                        } else {
                                            participantIndex++
                                            scenarioIndex = -1
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = selectedOption != null
                            ) {
                                Text(if (!showFeedback) "Comprobar Respuesta" else "Continuar")
                            }
                        }
                    } else {
                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finalizar y Volver")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            if (participantIndex < toxicParticipantsList.size) {
                val participant = toxicParticipantsList[participantIndex]
                
                Text("Participante ${participantIndex + 1} de ${toxicParticipantsList.size}", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelLarge)
                LinearProgressIndicator(
                    progress = { participantIndex.toFloat() / toxicParticipantsList.size },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                if (scenarioIndex == -1) {
                    Text(
                        text = participant.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Cómo identificarlo:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(participant.description)
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text("Qué hacer (Técnica AISO):", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(participant.action)
                        }
                    }
                } else {
                    val question = participant.scenarios[scenarioIndex]
                    
                    Text(
                        text = "${participant.name} - Escenario ${scenarioIndex + 1}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    question.options.forEachIndexed { index, option ->
                        val isSelected = selectedOption == index
                        val isCorrect = index == question.correctAnswerIndex
                        
                        val backgroundColor = when {
                            !showFeedback && isSelected -> MaterialTheme.colorScheme.primaryContainer
                            showFeedback && isSelected && isCorrect -> androidx.compose.ui.graphics.Color(0xFFE8F5E9)
                            showFeedback && isSelected && !isCorrect -> androidx.compose.ui.graphics.Color(0xFFFFEBEE)
                            showFeedback && !isSelected && isCorrect -> androidx.compose.ui.graphics.Color(0xFFE8F5E9)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        
                        val borderColor = when {
                            showFeedback && isCorrect -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            showFeedback && isSelected && !isCorrect -> androidx.compose.ui.graphics.Color(0xFFF44336)
                            isSelected -> MaterialTheme.colorScheme.primary
                            else -> androidx.compose.ui.graphics.Color.Transparent
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        if (!showFeedback) selectedOption = index
                                    }
                                ),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            border = androidx.compose.foundation.BorderStroke(if (isSelected || showFeedback) 2.dp else 0.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = if (showFeedback && !isCorrect) androidx.compose.ui.graphics.Color(0xFFF44336) else MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(option, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                    
                    if (showFeedback) {
                        Spacer(modifier = Modifier.height(16.dp))
                        if (selectedOption == question.correctAnswerIndex) {
                            Text("¡Correcto! Excelente manejo de la situación.", color = androidx.compose.ui.graphics.Color(0xFF4CAF50), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        } else {
                            Text("Incorrecto. Recuerda las técnicas del libro para no perder el control.", color = androidx.compose.ui.graphics.Color(0xFFF44336), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().padding(vertical = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "¡Excelente!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Has aprendido a manejar a todos los perfiles de participantes difíciles con las técnicas de la Academia AISO.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}
