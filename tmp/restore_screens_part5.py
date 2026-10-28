import os

screens = {}

screens["app/src/main/java/com/example/ui/screens/TongueTwisterDuelScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.TongueTwisterDuelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TongueTwisterDuelScreen(
    onBack: () -> Unit,
    viewModel: TongueTwisterDuelViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentTwister by remember { mutableStateOf(tongueTwistersList.random()) }
    val isRecordingP1 by viewModel.isRecordingP1.collectAsState()
    val isRecordingP2 by viewModel.isRecordingP2.collectAsState()
    val hasAudioP1 by viewModel.hasAudioP1.collectAsState()
    val hasAudioP2 by viewModel.hasAudioP2.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val error by viewModel.error.collectAsState()

    var activePlayerForPermission by remember { mutableStateOf(1) }
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.startRecording(context, activePlayerForPermission)
            } else {
                android.widget.Toast.makeText(context, "Se requiere permiso de micrófono para grabar.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun handleRecordClick(player: Int, isRecording: Boolean) {
        if (isRecording) {
            viewModel.stopRecording(player)
        } else {
            activePlayerForPermission = player
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.RECORD_AUDIO
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.startRecording(context, player)
            } else {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duelo de Trabalenguas", fontWeight = FontWeight.Bold) },
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "¡Duelo de Dicción!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ambos jugadores deben grabar el mismo trabalenguas. La IA de Gemini decidirá al ganador.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentTwister.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentTwister.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jugador 1", fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { handleRecordClick(1, isRecordingP1) },
                            enabled = !isRecordingP2 && !isAnalyzing && feedback == null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecordingP1) Color.Red else Color(0xFF1E88E5)
                            )
                        ) {
                            Icon(
                                imageVector = if (isRecordingP1) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        if (hasAudioP1 && !isRecordingP1) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Listo", tint = Color(0xFF4CAF50))
                        } else {
                            Text(if (isRecordingP1) "Grabando..." else "Grabar", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jugador 2", fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { handleRecordClick(2, isRecordingP2) },
                            enabled = !isRecordingP1 && !isAnalyzing && feedback == null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecordingP2) Color.Red else Color(0xFFE53935)
                            )
                        ) {
                            Icon(
                                imageVector = if (isRecordingP2) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        if (hasAudioP2 && !isRecordingP2) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Listo", tint = Color(0xFF4CAF50))
                        } else {
                            Text(if (isRecordingP2) "Grabando..." else "Grabar", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text("Los jueces de Gemini están evaluando...", color = MaterialTheme.colorScheme.primary)
                } else if (feedback != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Veredicto del Duelo ⚖️", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                            Spacer(Modifier.height(8.dp))
                            Text(feedback!!, color = Color(0xFF3E2723))
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.analyzeDuel(currentTwister.text) },
                        enabled = hasAudioP1 && hasAudioP2,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Evaluar Duelo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        var nextTwister = tongueTwistersList.random()
                        while (nextTwister == currentTwister) {
                            nextTwister = tongueTwistersList.random()
                        }
                        currentTwister = nextTwister
                        viewModel.clearFeedback()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Otro Trabalenguas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/ToxicParticipantsScreen.kt"] = '''package com.example.ui.screens

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
        action = "Reconocer su conocimiento y usarlo a favor (\\"Como bien menciona...\\"), pero mantener el control. No discutir. Como enseñamos en 'Invierte en ti', tu autoridad no se pierde por validar a otros, se fortalece. Nunca entres en una lucha de egos.",
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
        description = "Siempre encuentra un \\"pero\\" o cuestiona la viabilidad de lo que se enseña. Tiende a bajar la moral del grupo con su pesimismo.",
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
'''

screens["app/src/main/java/com/example/ui/screens/ToxicVideoSimulationScreen.kt"] = '''package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.viewmodel.ToxicInterruptionItem
import com.example.viewmodel.ToxicVideoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToxicVideoSimulationScreen(
    onBack: () -> Unit,
    viewModel: ToxicVideoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val state by viewModel.state.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasCameraPermission = permissions[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission = permissions[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
        
        if (hasCameraPermission && hasAudioPermission && !state.isRecording) {
            viewModel.startPractice()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPractice()
        }
    }

    BackHandler {
        viewModel.stopPractice()
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Simulador de Audiencia Tóxica", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Tauromaquia Verbal y Temple Escénico",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopPractice()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleCamera) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Cambiar Cámara"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (hasCameraPermission) {
                key(state.isFrontCamera) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            val previewView = PreviewView(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                scaleType = PreviewView.ScaleType.FILL_CENTER
                            }
                            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                            cameraProviderFuture.addListener({
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                val cameraSelector = if (state.isFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                try {
                                    cameraProvider.unbindAll()
                                    cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        cameraSelector,
                                        preview
                                    )
                                } catch (exc: Exception) {
                                    Log.e("ToxicCamera", "Binding failed", exc)
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                            previewView
                        }
                    )
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VideocamOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.55f),
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (state.isRecording) Color.Red else Color(0xFF4CAF50),
                                modifier = Modifier.size(10.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (state.isRecording) "EN EL ESCENARIO" else "LISTO PARA HABLAR",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Temple: ", color = Color.Gray, fontSize = 11.sp)
                            Text(
                                "${state.audienceCalmScore}%",
                                color = Color(0xFF64B5F6),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))

                val listState = rememberLazyListState()
                LaunchedEffect(state.interruptions.size) {
                    if (state.interruptions.isNotEmpty()) {
                        listState.animateScrollToItem(state.interruptions.size - 1)
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (state.interruptions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "🎤 Comienza tu discurso sobre cualquier tema.\\nLos participantes tóxicos (Ernesto, Susana, Nicanor) intervendrán periódicamente para probar tu calma escénica.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.interruptions) { item ->
                                InterruptionBubbleCard(item = item)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (state.userTranscription.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xDD1E1E2E)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 90.dp)
                    ) {
                        Text(
                            text = "Tú: ${state.userTranscription}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                FilledTonalButton(
                    onClick = {
                        if (state.isRecording) {
                            viewModel.stopPractice()
                        } else {
                            if (hasCameraPermission && hasAudioPermission) {
                                viewModel.startPractice()
                            } else {
                                permissionsLauncher.launch(
                                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (state.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (state.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isRecording) "Detener Práctica" else "Iniciar Discurso en Escenario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InterruptionBubbleCard(item: ToxicInterruptionItem) {
    var showTactics by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xEE2A1215)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF5350).copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFEF5350),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = item.sender.firstOrNull()?.toString() ?: "T",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = item.sender,
                            color = Color(0xFFFFCDD2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = item.senderRole,
                            color = Color(0xFFE57373),
                            fontSize = 10.sp
                        )
                    }
                }
                TextButton(
                    onClick = { showTactics = !showTactics },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (showTactics) Icons.Default.ExpandLess else Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (showTactics) "Ocultar" else "Capote",
                        color = Color(0xFFFFB74D),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\\"${item.text}\\"",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
            AnimatedVisibility(visible = showTactics) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF332010))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFFFFB74D),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Consejo del Libro 'Los secretos del orador':",
                            color = Color(0xFFFFB74D),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.counterMeasure,
                        color = Color(0xFFFFE0B2),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/TongueTwisterOfflineScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TongueTwisterOfflineScreen(onBack: () -> Unit) {
    var currentTwister by remember { mutableStateOf(tongueTwistersList.random()) }
    var timeElapsed by remember { mutableLongStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(100L)
            timeElapsed += 100L
        }
    }

    fun formatTime(ms: Long): String {
        val seconds = (ms / 1000) % 60
        val millis = (ms % 1000) / 100
        val minutes = (ms / 1000) / 60
        return String.format("%02d:%02d.%01d", minutes, seconds, millis)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Práctica Offline", fontWeight = FontWeight.Bold) },
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Práctica contra el Reloj",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lee el trabalenguas lo más rápido que puedas, con la mejor dicción.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentTwister.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentTwister.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = formatTime(timeElapsed),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { isRunning = !isRunning },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (isRunning) "Detener" else if (timeElapsed > 0) "Continuar" else "Iniciar", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = {
                            isRunning = false
                            timeElapsed = 0L
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Reiniciar", fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        var nextTwister = tongueTwistersList.random()
                        while (nextTwister == currentTwister) {
                            nextTwister = tongueTwistersList.random()
                        }
                        currentTwister = nextTwister
                        isRunning = false
                        timeElapsed = 0L
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Siguiente Trabalenguas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
'''

for path, content in screens.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

print("Part 4 written successfully")
