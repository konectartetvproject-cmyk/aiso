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
        name = "El Sabelotodo",
        description = "Cree que sabe más que el orador. Interrumpe para corregir o agregar información constantemente buscando demostrar su superioridad intelectual.",
        action = "Reconocer su conocimiento y usarlo a favor (\"Como bien menciona...\"), pero mantener el control. No discutir. Como enseñamos en 'Invierte en ti', tu autoridad no se pierde por validar a otros, se fortalece. Nunca entres en una lucha de egos.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Estás explicando un concepto y 'El Sabelotodo' te interrumpe diciendo: 'En realidad, según el último estudio de Harvard, eso es inexacto...'. ¿Qué haces?",
                options = listOf(
                    "Le dices que tu fuente es mejor y comienzas a debatir con él frente a todos.",
                    "Le dices: 'Excelente aporte, el estudio de Harvard complementa muy bien lo que estamos viendo. Avanzando con el tema...'",
                    "Te quedas callado, le das el marcador y le dices que si sabe tanto dé la clase él."
                ),
                correctAnswerIndex = 1
            ),
            ToxicQuizQuestion(
                question = "Más tarde, vuelve a levantar la mano para corregir un término técnico sin mucha relevancia para el tema. ¿Cómo lo manejas ahora?",
                options = listOf(
                    "Le pides amablemente que anote sus precisiones para conversarlas al final, ya que hay mucho material por cubrir.",
                    "Te disculpas profusamente por tu error y corriges todas tus diapositivas.",
                    "Lo ignoras por completo y haces de cuenta que no levantó la mano."
                ),
                correctAnswerIndex = 0
            )
        )
    ),
    ToxicParticipant(
        name = "El Charlatán",
        description = "Habla con su vecino o interrumpe sin punto, desviando la atención del grupo. Suele estar distraído por su propia necesidad de hablar.",
        action = "Hacer una pausa de silencio mirándolo fijamente, acercarse físicamente, o pedir que comparta su idea con todo el grupo. La Academia AISO recomienda el 'silencio táctico' como la herramienta más elegante para recuperar la atención.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Mientras das una instrucción importante, él está contando una anécdota personal a su compañero de al lado, distrayendo a los demás. ¿Cuál es la primera acción a tomar?",
                options = listOf(
                    "Le gritas: '¡Silencio por favor, estoy hablando!'",
                    "Haces una pausa total, te quedas en silencio y lo miras fijamente hasta que se dé cuenta (Silencio Táctico).",
                    "Hablas mucho más fuerte para que tu voz tape la suya."
                ),
                correctAnswerIndex = 1
            ),
            ToxicQuizQuestion(
                question = "El silencio táctico no funcionó del todo y sigue susurrando. ¿Cuál es tu siguiente movida?",
                options = listOf(
                    "Caminas lentamente hacia su lugar mientras sigues hablando, usando la proximidad física para que se calle.",
                    "Le pides a seguridad que lo saque de la sala.",
                    "Detienes la clase y le dices: 'O te callas o me voy'."
                ),
                correctAnswerIndex = 0
            )
        )
    ),
    ToxicParticipant(
        name = "El Negativo / Cuestionador",
        description = "Siempre encuentra un \"pero\" o cuestiona la viabilidad de lo que se enseña. Tiende a bajar la moral del grupo con su pesimismo.",
        action = "Evitar ponerse a la defensiva. Pedirle que anote sus dudas para el final, o usar la técnica del rebote (preguntar al resto del grupo qué opina). Recuerda, el libro nos enseña a transformar la negatividad en debate constructivo.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Presentas una nueva técnica de ventas y él dice en voz alta: 'Eso aquí nunca va a funcionar, los clientes son diferentes'. ¿Cuál es la mejor respuesta?",
                options = listOf(
                    "Le dices que tiene una actitud terrible y que así nunca tendrá éxito.",
                    "Le das la razón para no pelear y pasas rápido a otro tema.",
                    "Usas la técnica del rebote: 'Es una perspectiva interesante. ¿Qué opina el resto del grupo sobre este desafío?'"
                ),
                correctAnswerIndex = 2
            ),
            ToxicQuizQuestion(
                question = "Sigue insistiendo en que todo lo que propones es imposible. ¿Qué haces?",
                options = listOf(
                    "Te frustras y le dices que si no le gusta, se puede ir.",
                    "Validas su preocupación y le pides que, como ejercicio, intente pensar en un solo escenario donde sí podría funcionar.",
                    "Lo ignoras completamente el resto de la sesión."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "El Distraído / Silencioso",
        description = "No participa, está en el celular o parece aburrido. Su falta de energía puede contagiar a los demás.",
        action = "Hacer preguntas directas pero fáciles, incluir dinámicas grupales, o moverse por el salón para mantener su atención. Invertir en tu lenguaje corporal te ayudará a reconectarlo sin exhibirlo.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Estás en la mitad de tu charla y ves a alguien en la última fila bostezando y scrolleando en redes sociales. ¿Qué haces?",
                options = listOf(
                    "Detienes la presentación y le dices: 'Oye tú, el del celular, ¿te aburro?'",
                    "Lanzas una pregunta abierta y caminas sutilmente hacia su lado de la sala para que tu cercanía lo reconecte.",
                    "Le haces una pregunta extremadamente difícil para que pase vergüenza por no prestar atención."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "El Monopolizador",
        description = "Quiere responder a todas las preguntas y acapara el tiempo de participación, no dejando espacio para los demás.",
        action = "Agradecer su entusiasmo, pero limitar su tiempo. La Academia AISO enfatiza que el orador es el 'director de orquesta' y debe dar espacio a que todos los instrumentos suenen.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Haces una pregunta abierta al grupo y él levanta la mano por quinta vez consecutiva para contar una historia muy larga. ¿Cómo lo manejas?",
                options = listOf(
                    "Le dices: 'Gracias por tu gran aporte de hoy, pero me encantaría escuchar la opinión de alguien que no haya participado aún'.",
                    "Le das la palabra porque es el único que participa voluntariamente.",
                    "Le dices: 'Ya hablaste mucho, deja hablar a los demás'."
                ),
                correctAnswerIndex = 0
            )
        )
    ),
    ToxicParticipant(
        name = "El Agresivo / Confrontacional",
        description = "Ataca directamente al orador o a sus ideas con tono hostil. Su objetivo es desestabilizarte emocionalmente.",
        action = "Mantener la calma absoluta. No devolver la agresión. Escuchar activamente, validar su frustración y proponer hablarlo en privado durante el receso. Nunca pierdas el control de tus emociones.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "Se levanta enojado y te dice: 'Lo que estás enseñando es una estafa y una pérdida de mi tiempo'. ¿Qué acción tomas?",
                options = listOf(
                    "Le respondes agresivamente para defender tu honor y demostrar autoridad.",
                    "Mantienes un tono calmado: 'Entiendo que sientas frustración. Este es un tema complejo. Te propongo que lo hablemos a detalle tú y yo en el receso'.",
                    "Lo mandas a callar inmediatamente y llamas a seguridad."
                ),
                correctAnswerIndex = 1
            )
        )
    ),
    ToxicParticipant(
        name = "El Bromista",
        description = "Hace chistes de todo, interrumpiendo el flujo y restando seriedad al tema. Busca constantemente la validación y risa del grupo.",
        action = "Sonreír ante la primera broma, pero si continúa, redirigir la atención rápidamente al tema central. Si persiste, usa su nombre con tono firme y amable para pedirle enfoque. El humor es bueno, pero tú marcas el ritmo.",
        scenarios = listOf(
            ToxicQuizQuestion(
                question = "En un momento reflexivo y emotivo de la charla, él hace un chiste de doble sentido buscando hacer reír a todos rompiendo el clima. El grupo se ríe. ¿Cómo procedes?",
                options = listOf(
                    "Te enojas y le dices frente a todos que es un inmaduro.",
                    "Le sigues el juego y empiezas a contar chistes también para caerle bien al grupo.",
                    "Sonríes levemente, pero cambias inmediatamente tu tono a uno serio y dices: 'Volviendo al punto importante...' y continúas."
                ),
                correctAnswerIndex = 2
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
