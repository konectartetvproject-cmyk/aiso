import os

screens = {}

screens["app/src/main/java/com/example/ui/screens/OpeningClosingGameScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class OpeningClosingQuestion(
    val title: String,
    val example: String,
    val icon: ImageVector,
    val type: String,
    val specificTechnique: String,
    val options: List<String>,
    val explanation: String
)

val openingClosingQuestions = listOf(
    OpeningClosingQuestion(
        title = "El Dato Sorprendente",
        example = "¿Sabías que pasamos más de 5 años de nuestras vidas en redes sociales?",
        icon = Icons.Default.DataExploration,
        type = "Inicio",
        specificTechnique = "Dato Estadístico",
        options = listOf("Dato Estadístico", "Llamado a la acción", "Resumen", "Anécdota"),
        explanation = "Un dato estadístico sorprendente capta la atención del público inmediatamente desde el principio."
    ),
    OpeningClosingQuestion(
        title = "El Llamado a la Acción",
        example = "No esperen a mañana. Empiecen hoy a cambiar su comunidad.",
        icon = Icons.Default.Campaign,
        type = "Conclusión",
        specificTechnique = "Llamado a la Acción",
        options = listOf("Pregunta Retórica", "Llamado a la Acción", "Cita Célebre", "Anécdota"),
        explanation = "El llamado a la acción es ideal para cerrar, motivando a la audiencia a actuar tras escuchar tu mensaje."
    ),
    OpeningClosingQuestion(
        title = "La Historia Personal",
        example = "Hace diez años, estaba sentado exactamente donde están ustedes ahora, lleno de dudas...",
        icon = Icons.Default.MenuBook,
        type = "Inicio",
        specificTechnique = "Anécdota",
        options = listOf("Círculo Completo", "Anécdota", "Resumen", "Llamado a la Acción"),
        explanation = "Comenzar con una anécdota personal genera empatía y conexión con la audiencia desde el primer segundo."
    ),
    OpeningClosingQuestion(
        title = "El Círculo Completo",
        example = "Y así, volviendo al niño del que les hablé al principio, hoy por fin encontró su camino.",
        icon = Icons.Default.AllInclusive,
        type = "Conclusión",
        specificTechnique = "Círculo Completo",
        options = listOf("Anécdota", "Dato Estadístico", "Círculo Completo", "Pregunta Retórica"),
        explanation = "Terminar haciendo referencia a la historia de inicio da una sensación de cierre perfecto y satisfacción."
    ),
    OpeningClosingQuestion(
        title = "La Inspiración Final",
        example = "Como dijo Gandhi: 'Sé el cambio que quieres ver en el mundo'. Gracias.",
        icon = Icons.Default.FormatQuote,
        type = "Conclusión",
        specificTechnique = "Cita Célebre",
        options = listOf("Llamado a la Acción", "Resumen", "Dato Estadístico", "Cita Célebre"),
        explanation = "Cerrar con una cita célebre deja a la audiencia con una frase memorable y poderosa resonando en su mente."
    ),
    OpeningClosingQuestion(
        title = "La Pregunta Reflexiva",
        example = "Imaginen por un momento: ¿Cómo sería su vida si no tuvieran miedo a fracasar?",
        icon = Icons.Default.PsychologyAlt,
        type = "Inicio",
        specificTechnique = "Pregunta Retórica",
        options = listOf("Resumen", "Cita Célebre", "Pregunta Retórica", "Círculo Completo"),
        explanation = "Hacer una pregunta a la audiencia al inicio los obliga a pensar e involucrarse mentalmente con tu tema."
    ),
    OpeningClosingQuestion(
        title = "El Resumen Poderoso",
        example = "En conclusión, hemos visto que la disciplina, la pasión y la resiliencia son la clave.",
        icon = Icons.Default.Summarize,
        type = "Conclusión",
        specificTechnique = "Resumen",
        options = listOf("Resumen", "Dato Estadístico", "Anécdota", "Pregunta Retórica"),
        explanation = "El resumen refuerza los puntos clave para que la audiencia no los olvide al irse."
    )
).shuffled()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpeningClosingGameScreen(onBack: () -> Unit) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var gameState by remember { mutableStateOf("intro") }
    var currentPhase by remember { mutableIntStateOf(1) }
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackColor by remember { mutableStateOf(Color.Green) }

    val currentQuestion = openingClosingQuestions.getOrNull(currentIndex)

    if (gameState == "intro") {
        AlertDialog(
            onDismissRequest = { gameState = "phase1" },
            title = { Text("El Arte del Inicio y el Cierre", fontWeight = FontWeight.Bold) },
            text = { 
                Text("Los primeros y últimos minutos de un discurso son vitales.\\n\\n" +
                     "Fase 1: Clasifica si el ejemplo es ideal para un 'Inicio' o una 'Conclusión'.\\n" +
                     "Fase 2: Identifica la técnica específica.") 
            },
            confirmButton = {
                Button(onClick = { gameState = "phase1" }) {
                    Text("Comenzar")
                }
            }
        )
    }

    if (gameState == "won") {
        AlertDialog(
            onDismissRequest = onBack,
            title = { Text("¡Juego Completado!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)) },
            text = { Text("Has dominado las técnicas de inicio y conclusión. Puntuación final: $score") },
            confirmButton = {
                Button(onClick = onBack) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicios y Cierres", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = "Puntos: $score",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (gameState == "phase1" || gameState == "phase2") {
                Text(
                    text = "Fase $currentPhase - Pregunta ${currentIndex + 1} de ${openingClosingQuestions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (currentQuestion != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = currentQuestion.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = currentQuestion.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "\\"${currentQuestion.example}\\"",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    if (showFeedback) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = feedbackColor.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = feedbackMessage,
                                color = feedbackColor,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp).fillMaxWidth()
                            )
                        }
                    } else {
                        if (currentPhase == 1) {
                            Text(
                                text = "¿Para qué momento es ideal esta frase?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = { 
                                        if (currentQuestion.type == "Inicio") {
                                            score += 10
                                            feedbackMessage = "¡Correcto! Es un Inicio excelente."
                                            feedbackColor = Color(0xFF4CAF50)
                                        } else {
                                            feedbackMessage = "Incorrecto. Debería ser un Cierre."
                                            feedbackColor = Color.Red
                                        }
                                        showFeedback = true
                                    },
                                    modifier = Modifier.weight(1f).height(60.dp)
                                ) {
                                    Text("Inicio", fontSize = 18.sp)
                                }
                                Button(
                                    onClick = { 
                                        if (currentQuestion.type == "Conclusión") {
                                            score += 10
                                            feedbackMessage = "¡Correcto! Es un Cierre poderoso."
                                            feedbackColor = Color(0xFF4CAF50)
                                        } else {
                                            feedbackMessage = "Incorrecto. Debería ser un Inicio."
                                            feedbackColor = Color.Red
                                        }
                                        showFeedback = true
                                    },
                                    modifier = Modifier.weight(1f).height(60.dp)
                                ) {
                                    Text("Conclusión", fontSize = 18.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "¿Qué técnica específica se está usando?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                currentQuestion.options.forEach { option ->
                                    Button(
                                        onClick = {
                                            if (option == currentQuestion.specificTechnique) {
                                                score += 15
                                                feedbackMessage = "¡Correcto! ${currentQuestion.explanation}"
                                                feedbackColor = Color(0xFF4CAF50)
                                            } else {
                                                feedbackMessage = "Incorrecto. Era ${currentQuestion.specificTechnique}. ${currentQuestion.explanation}"
                                                feedbackColor = Color.Red
                                            }
                                            showFeedback = true
                                        },
                                        modifier = Modifier.fillMaxWidth().height(56.dp)
                                    ) {
                                        Text(option, fontSize = 16.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }
                    
                    LaunchedEffect(showFeedback) {
                        if (showFeedback) {
                            delay(3500)
                            showFeedback = false
                            if (currentPhase == 1) {
                                currentPhase = 2
                            } else {
                                currentPhase = 1
                                if (currentIndex < openingClosingQuestions.size - 1) {
                                    currentIndex++
                                } else {
                                    gameState = "won"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/OratoryDefenseScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DefenseType
import com.example.viewmodel.OratoryDefenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OratoryDefenseScreen(
    onBack: () -> Unit,
    viewModel: OratoryDefenseViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()
    
    var draggedDefense by remember { mutableStateOf<DefenseType?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var boardBounds by remember { mutableStateOf(Rect.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Orador vs Distracciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        if (!state.isPlaying && !state.isGameOver && !state.isLevelComplete) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Defiende tu Discurso",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Coloca técnicas de oratoria para defenderte de las muletillas y nervios. Recolecta 'Confianza' para usar más técnicas.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startGame() }, modifier = Modifier.height(56.dp).fillMaxWidth(0.8f)) {
                    Text("Comenzar", fontSize = 18.sp)
                }
            }
        } else if (state.isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Discurso Interrumpido!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Puntuación: ${state.score}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startGame() }) {
                    Text("Reintentar")
                }
            }
        } else if (state.isLevelComplete) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Nivel ${state.wave} Superado!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Puntuación total: ${state.score}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.nextLevel() }) {
                    Text("Siguiente Nivel")
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFE8F5E9))
                ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Confianza: ${state.confidence} 💡",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF57F17)
                    )
                    Text(
                        text = "Nivel: ${state.wave} | Puntos: ${state.score}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val unlockedDefenses = DefenseType.values().filter { it.requiredWave <= state.wave }
                    unlockedDefenses.forEach { type ->
                        val isSelected = state.selectedDefense == type
                        val canAfford = state.confidence >= type.cost
                        var cardPosition by remember { mutableStateOf(Offset.Zero) }

                        Card(
                            modifier = Modifier
                                .size(70.dp, 90.dp)
                                .onGloballyPositioned {
                                    cardPosition = it.localToRoot(Offset.Zero)
                                }
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .pointerInput(canAfford) {
                                    if (canAfford) {
                                        detectDragGestures(
                                            onDragStart = { _ ->
                                                draggedDefense = type
                                                dragPosition = cardPosition
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragPosition += dragAmount
                                            },
                                            onDragEnd = {
                                                if (boardBounds.contains(dragPosition)) {
                                                    val cols = 8
                                                    val rows = 3
                                                    val colWidth = boardBounds.width / cols
                                                    val rowHeight = boardBounds.height / rows
                                                    
                                                    val c = ((dragPosition.x - boardBounds.left) / colWidth).toInt()
                                                    val r = ((dragPosition.y - boardBounds.top) / rowHeight).toInt()
                                                    
                                                    if (c in 0 until cols && r in 0 until rows) {
                                                        viewModel.selectDefense(draggedDefense)
                                                        viewModel.placeDefense(c, r)
                                                    }
                                                }
                                                draggedDefense = null
                                            },
                                            onDragCancel = {
                                                draggedDefense = null
                                            }
                                        )
                                    }
                                }
                                .clickable(enabled = canAfford) {
                                    if (isSelected) viewModel.selectDefense(null) else viewModel.selectDefense(type)
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (canAfford) MaterialTheme.colorScheme.surface else Color.LightGray
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(type.icon, fontSize = 24.sp)
                                Text(type.title, fontSize = 10.sp, maxLines = 1)
                                Text("${type.cost} 💡", fontSize = 10.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                val collectedDrops = remember { mutableStateListOf<Int>() }
                val currentDrops by rememberUpdatedState(state.confidenceDrops)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp)
                        .onGloballyPositioned {
                            boardBounds = it.boundsInRoot()
                        }
                        .pointerInput(Unit) {
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                                    val positions = event.changes.map { it.position }
                                    val rows = 3
                                    val cols = 8
                                    val rowHeightPx = size.height / rows.toFloat()
                                    val colWidthPx = size.width / cols.toFloat()
                                    
                                    positions.forEach { pos ->
                                        currentDrops.forEach { drop ->
                                            if (drop.id !in collectedDrops) {
                                                val dropX = colWidthPx * drop.x + 20.dp.toPx()
                                                val dropY = rowHeightPx * drop.y.coerceAtLeast(0f) + 20.dp.toPx()
                                                val dist = kotlin.math.hypot((pos.x - dropX).toDouble(), (pos.y - dropY).toDouble())
                                                if (dist < 40.dp.toPx()) {
                                                    collectedDrops.add(drop.id)
                                                    viewModel.collectConfidence(drop.id)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                ) {
                    val rows = 3
                    val cols = 8
                    
                    Column(modifier = Modifier.fillMaxSize()) {
                        for (r in 0 until rows) {
                            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                for (c in 0 until cols) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .border(0.5.dp, Color.Black.copy(alpha = 0.1f))
                                            .clickable { viewModel.placeDefense(c, r) }
                                    ) {
                                        val defense = state.defenses.find { it.cell.col == c && it.cell.row == r }
                                        if (defense != null) {
                                            Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(defense.type.icon, fontSize = 32.sp)
                                                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(4.dp).background(Color.Red)) {
                                                    val hpPercent = defense.hp.toFloat() / defense.type.hp.toFloat()
                                                    Box(modifier = Modifier.fillMaxWidth(hpPercent).fillMaxHeight().background(Color.Green))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val rowHeight = maxHeight / rows
                        val colWidth = maxWidth / cols

                        state.lawnmowers.forEach { lm ->
                            if (lm.active || lm.triggered) {
                                val xPos = if (lm.triggered) lm.x else -0.8f
                                Box(
                                    modifier = Modifier
                                        .offset(x = colWidth * xPos, y = rowHeight * lm.row)
                                        .size(colWidth, rowHeight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🚜", fontSize = 32.sp)
                                }
                            }
                        }

                        state.distractions.forEach { dist ->
                            Box(
                                modifier = Modifier
                                    .offset(x = colWidth * dist.x, y = rowHeight * dist.row)
                                    .size(colWidth, rowHeight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(dist.type.icon, fontSize = 32.sp)
                                Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.8f).height(4.dp).background(Color.Red)) {
                                    val hpPercent = dist.hp.toFloat() / dist.type.maxHp.toFloat()
                                    Box(modifier = Modifier.fillMaxWidth(hpPercent).fillMaxHeight().background(Color.Green))
                                }
                            }
                        }

                        state.arguments.forEach { arg ->
                            Box(
                                modifier = Modifier
                                    .offset(x = colWidth * arg.x, y = rowHeight * arg.row + rowHeight / 3)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(Color.Blue)
                            )
                        }

                        state.confidenceDrops.forEach { drop ->
                            Box(
                                modifier = Modifier
                                    .offset(x = colWidth * drop.x, y = rowHeight * drop.y.coerceAtLeast(0f))
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF59D).copy(alpha = 0.8f))
                                    .clickable { viewModel.collectConfidence(drop.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💡", fontSize = 24.sp)
                            }
                        }
                    }
                }
                
                if (draggedDefense != null) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(dragPosition.x.toInt(), dragPosition.y.toInt()) }
                            .size(70.dp, 90.dp)
                            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(draggedDefense!!.icon, fontSize = 32.sp)
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/OratoryMatchScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OratoryMatchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OratoryMatchScreen(
    onBack: () -> Unit,
    viewModel: OratoryMatchViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Oratory Match", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        if (!state.isPlaying && !state.isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Oratory Match",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Conecta 3 o más elementos de oratoria iguales (voz, micrófono, ideas) para ganar puntos.\\n\\nAlcanza la meta de ${state.targetScore} puntos en ${state.moves} movimientos.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startGame() }, modifier = Modifier.height(56.dp).fillMaxWidth(0.8f)) {
                    Text("Jugar", fontSize = 18.sp)
                }
            }
        } else if (state.isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (state.score >= state.targetScore) "¡Nivel Completado!" else "¡Sin Movimientos!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (state.score >= state.targetScore) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Puntuación Final: ${state.score}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startGame() }) {
                    Text("Jugar de nuevo")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Puntos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text("${state.score} / ${state.targetScore}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Movimientos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        Text("${state.moves}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE0E0E0))
                            .padding(8.dp)
                    ) {
                        state.grid.forEachIndexed { r, row ->
                            Row {
                                row.forEachIndexed { c, item ->
                                    val isSelected = state.selectedItem?.first == r && state.selectedItem?.second == c
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) Color.White.copy(alpha = 0.5f) else Color(item.type.color).copy(alpha = 0.3f))
                                            .border(
                                                width = if (isSelected) 2.dp else 0.dp,
                                                color = if (isSelected) Color.Black else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { viewModel.selectItem(r, c) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.type.name != "EMPTY") {
                                            Text(item.type.icon, fontSize = 24.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/OratoryRunnerScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OratoryRunnerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OratoryRunnerScreen(
    onBack: () -> Unit,
    viewModel: OratoryRunnerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrera de Oratoria", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        if (!state.isPlaying && !state.isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Carrera de Oratoria",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Esquiva las muletillas (🛑) y el pánico escénico (👻).\\nRecolecta palabras clave (💡).",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Desliza a la izquierda o derecha para moverte.",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startGame() }, modifier = Modifier.height(56.dp).fillMaxWidth(0.8f)) {
                    Text("Comenzar", fontSize = 18.sp)
                }
            }
        } else if (state.isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Discurso Interrumpido!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Puntuación final: ${state.score}",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.startGame() }) {
                    Text("Reintentar")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFF263238))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Puntuación: ${state.score}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            var dragSwiped = false
                            detectDragGestures(
                                onDragStart = { dragSwiped = false },
                                onDragEnd = { dragSwiped = false },
                                onDragCancel = { dragSwiped = false }
                            ) { change, dragAmount ->
                                change.consume()
                                if (!dragSwiped) {
                                    if (dragAmount.x > 30f) {
                                        viewModel.moveRight()
                                        dragSwiped = true
                                    } else if (dragAmount.x < -30f) {
                                        viewModel.moveLeft()
                                        dragSwiped = true
                                    }
                                }
                            }
                        }
                ) {
                    val laneWidth = maxWidth / 3
                    
                    Row(modifier = Modifier.fillMaxSize()) {
                        for (i in 0..2) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(0.5.dp, Color.White.copy(alpha = 0.2f))
                            )
                        }
                    }
                    
                    state.items.forEach { item ->
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = laneWidth * item.lane,
                                    y = maxHeight * item.y
                                )
                                .width(laneWidth)
                                .height(50.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(item.type.icon, fontSize = 40.sp)
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .offset(
                                x = laneWidth * state.playerLane,
                                y = maxHeight * 0.85f
                            )
                            .width(laneWidth)
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏃", fontSize = 50.sp)
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/PracticeListScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Exercise
import com.example.viewmodel.OratoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeListScreen(
    viewModel: OratoriaViewModel,
    onNavigateToExercise: () -> Unit,
    onBack: () -> Unit
) {
    val exercises by viewModel.exercises.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Práctica Libre", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            items(exercises) { exercise ->
                ExerciseCardItem(exercise = exercise, onClick = {
                    viewModel.selectExercise(exercise)
                    onNavigateToExercise()
                })
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ExerciseCardItem(exercise: Exercise, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon: ImageVector = when (exercise.icon) {
                "lightbulb" -> Icons.Default.Lightbulb
                "person" -> Icons.Default.Person
                "star" -> Icons.Default.Star
                "warning" -> Icons.Default.Warning
                else -> Icons.Default.Mic
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exercise.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/QuizScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OratoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: OratoriaViewModel,
    onBack: () -> Unit
) {
    val currentLesson by viewModel.currentLesson.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsState()
    val isAnswerChecked by viewModel.isAnswerChecked.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    val score by viewModel.score.collectAsState()

    if (currentLesson == null) {
        onBack()
        return
    }

    val totalQuestions = currentLesson!!.questions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LinearProgressIndicator(
                        progress = {
                            if (isQuizFinished) 1f else currentQuestionIndex.toFloat() / totalQuestions.toFloat()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (!isQuizFinished) {
                Surface(
                    color = if (isAnswerChecked) {
                        val isCorrect = selectedOptionIndex == currentLesson!!.questions[currentQuestionIndex].correctAnswerIndex
                        if (isCorrect) Color(0xFFD7FFD9) else Color(0xFFFFD7D7)
                    } else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (isAnswerChecked) {
                            val isCorrect = selectedOptionIndex == currentLesson!!.questions[currentQuestionIndex].correctAnswerIndex
                            Text(
                                text = if (isCorrect) "¡Correcto!" else "Incorrecto",
                                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = currentLesson!!.questions[currentQuestionIndex].explanation,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                        Button(
                            onClick = {
                                if (isAnswerChecked) {
                                    viewModel.nextQuestion()
                                } else {
                                    viewModel.checkAnswer()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = selectedOptionIndex != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAnswerChecked) {
                                    val isCorrect = selectedOptionIndex == currentLesson!!.questions[currentQuestionIndex].correctAnswerIndex
                                    if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                                } else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (isAnswerChecked) "Continuar" else "Comprobar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (isQuizFinished) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "¡Lección Completada!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Puntuación: $score / $totalQuestions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Volver al Inicio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                val question = currentLesson!!.questions[currentQuestionIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    question.options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrectAnswer = index == question.correctAnswerIndex
                        
                        val backgroundColor = when {
                            !isAnswerChecked && isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            isAnswerChecked && isCorrectAnswer -> Color(0xFFE8F5E9)
                            isAnswerChecked && isSelected && !isCorrectAnswer -> Color(0xFFFFEBEE)
                            else -> MaterialTheme.colorScheme.surface
                        }
                        
                        val borderColor = when {
                            !isAnswerChecked && isSelected -> MaterialTheme.colorScheme.primary
                            isAnswerChecked && isCorrectAnswer -> Color(0xFF4CAF50)
                            isAnswerChecked && isSelected && !isCorrectAnswer -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(backgroundColor)
                                .border(
                                    width = if (isSelected || (isAnswerChecked && isCorrectAnswer)) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = !isAnswerChecked) {
                                    viewModel.selectOption(index)
                                }
                                .padding(20.dp)
                        ) {
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/RecommendationsScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Recommendation(
    val title: String,
    val text: String,
    val category: String,
    val colors: List<Color>
)

val recommendationsList = listOf(
    Recommendation("Conexión Inicial", "Empieza con una historia breve, una pregunta retórica o un dato impactante para captar la atención desde el primer segundo.", "Inicio", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("El Silencio Atrapa", "Antes de pronunciar tu primera palabra, haz contacto visual y sonríe ligeramente durante 3 segundos. Ese silencio crea expectativa.", "Inicio", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    Recommendation("Promesa de Valor", "Dile a tu audiencia exactamente qué van a ganar o aprender al escucharte en los próximos minutos.", "Inicio", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    Recommendation("Declaración Sorprendente", "Inicia con una afirmación contraintuitiva o sorprendente que desafíe las creencias comunes y despierte la curiosidad.", "Inicio", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Recommendation("Interacción Inmediata", "Haz que la audiencia levante la mano o responda a una pregunta sencilla para romper el hielo e involucrarlos activamente.", "Inicio", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    
    Recommendation("Cierre Circular", "Termina tu presentación haciendo referencia a la misma historia o concepto con el que empezaste.", "Cierre", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Recommendation("Llamado a la Acción (Call to Action)", "No dejes a tu audiencia a la deriva. Diles exactamente qué acción quieres que tomen con la información que les diste.", "Cierre", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    Recommendation("La Última Frase", "La última oración que digas debe ser breve, poderosa y fácil de recordar. Es lo que se llevarán a casa.", "Cierre", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    Recommendation("Cita Inspiradora", "Termina con una frase célebre bien elegida que encapsule perfectamente tu mensaje principal.", "Cierre", listOf(Color(0xFFff9966), Color(0xFFff5e62))),
    Recommendation("Visión del Futuro", "Pinta una imagen mental clara y positiva de cómo será el mundo o la vida de la audiencia si aplican tu mensaje.", "Cierre", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    
    Recommendation("Matices y Volumen", "Evita hablar siempre al mismo volumen. Usa un tono más bajo para crear intimidad y sube el volumen para enfatizar puntos clave.", "Voz", listOf(Color(0xFFf12711), Color(0xFFf5af19))),
    Recommendation("Pausas Estratégicas", "Las pausas son la puntuación de tu voz. Úsalas antes de revelar algo importante y después para dejar que se asimile.", "Voz", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Recommendation("Articulación Clara", "Mueve bien los músculos de tu boca al hablar. Si no se te entiende, el mensaje se pierde.", "Voz", listOf(Color(0xFFff9966), Color(0xFFff5e62))),
    Recommendation("Velocidad Controlada", "Habla más rápido para inyectar energía y entusiasmo, y más lento para enfatizar puntos graves o complejos.", "Voz", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("Inflexión de Autoridad", "Evita terminar tus oraciones afirmativas con un tono ascendente (como si fuera pregunta). Un tono final descendente denota seguridad.", "Voz", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    
    Recommendation("Contacto Visual Constante", "No mires al suelo ni al techo. Reparte tu mirada en forma de 'W' o 'Z' para incluir a toda la audiencia.", "Lenguaje Corporal", listOf(Color(0xFF4CB8C4), Color(0xFF3CD3AD))),
    Recommendation("Manos Abiertas", "Mostrar las palmas de las manos transmite honestidad y confianza. Evita cruzarte de brazos.", "Lenguaje Corporal", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    Recommendation("Uso del Espacio", "No te quedes estático como una estatua, pero tampoco camines sin sentido (efecto león enjaulado). Muévete con un propósito.", "Lenguaje Corporal", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    Recommendation("Sonrisa Genuina", "Una sonrisa en momentos apropiados relaja a la audiencia y te hace parecer más accesible, carismático y seguro.", "Lenguaje Corporal", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Recommendation("Postura de Poder", "Mantén la espalda recta, los hombros relajados y los pies firmes para proyectar confianza y facilitar una mejor respiración.", "Lenguaje Corporal", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    
    Recommendation("El Poder del Silencio", "La mejor cura para las muletillas ('eh', 'mmm', 'este') es reemplazarlas por silencio. Acostúmbrate a las pausas.", "Muletillas", listOf(Color(0xFF141E30), Color(0xFF243B55))),
    Recommendation("Grabación Personal", "Grábate hablando 2 minutos sobre cualquier tema. Escúchate para identificar cuáles son tus muletillas más frecuentes.", "Muletillas", listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
    Recommendation("Respiración Profunda", "Las muletillas a veces aparecen cuando nos falta aire o estamos nerviosos. Respira profundamente desde el diafragma.", "Muletillas", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("Mapeo de Transiciones", "Las muletillas ocurren frecuentemente al pasar de una idea a otra. Ensaya especialmente tus frases de transición.", "Muletillas", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
    Recommendation("Contacto Visual como Freno", "Cuando sientas que vas a usar una muletilla, mira fijamente a una persona. Esto fomenta una pausa limpia en lugar de ruido.", "Muletillas", listOf(Color(0xFF4CB8C4), Color(0xFF3CD3AD))),
    
    Recommendation("Convierte los Nervios en Entusiasmo", "Físicamente, los nervios y la emoción son casi idénticos (palpitaciones, sudoración). Dile a tu cerebro 'estoy emocionado'.", "Inteligencia Emocional", listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
    Recommendation("Empatía con la Audiencia", "Piensa en las necesidades y preocupaciones de quienes te escuchan. Háblales a ellos, no a ti mismo.", "Inteligencia Emocional", listOf(Color(0xFFDA22FF), Color(0xFF9733EE))),
    Recommendation("Manejo del Error", "Si te equivocas, no te disculpes en exceso ni entres en pánico. Corrige rápidamente o haz una pequeña broma y continúa con confianza.", "Inteligencia Emocional", listOf(Color(0xFFf12711), Color(0xFFf5af19))),
    Recommendation("Lectura de Sala (Read the Room)", "Observa las expresiones de tu audiencia. Si parecen aburridos, cambia el ritmo; si parecen confundidos, simplifica tu mensaje.", "Inteligencia Emocional", listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
    Recommendation("Autocompasión", "No seas tu peor crítico. Si olvidas una palabra o tropiezas, sé amable contigo mismo. La audiencia quiere que tengas éxito.", "Inteligencia Emocional", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    
    Recommendation("La Regla de Tres", "Las personas recuerdan mejor la información presentada en grupos de tres. Estructura tus puntos clave en tres partes.", "Persuasión", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    Recommendation("Apelación Emocional (Pathos)", "La lógica (Logos) hace pensar, pero la emoción hace actuar. Conecta tu mensaje con emociones universales.", "Persuasión", listOf(Color(0xFF834D9B), Color(0xFFD04ED6))),
    Recommendation("Contraste", "Presenta el problema ('Lo que es') y contrástalo con la solución ('Lo que podría ser'). Ese salto genera interés y persuasión.", "Persuasión", listOf(Color(0xFFFDC830), Color(0xFFF37335))),
    Recommendation("Prueba Social", "Menciona ejemplos de otras personas, testimonios o casos de éxito para validar tu propuesta y generar confianza.", "Persuasión", listOf(Color(0xFF1E3C72), Color(0xFF2A5298))),
    Recommendation("El Poder de 'Porque'", "Al pedir algo a la audiencia, da una razón usando la palabra 'porque'. Esto aumenta significativamente la probabilidad de aceptación.", "Persuasión", listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)))
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendationsScreen(onBack: () -> Unit, showBackButton: Boolean = true) {
    val categories = listOf("Inicio", "Cierre", "Voz", "Lenguaje Corporal", "Muletillas", "Inteligencia Emocional", "Persuasión")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    
    val filteredRecommendations = remember(selectedCategory) {
        recommendationsList.filter { it.category == selectedCategory }
    }
    val pagerState = rememberPagerState(pageCount = { filteredRecommendations.size })

    LaunchedEffect(selectedCategory) {
        pagerState.scrollToPage(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val recommendation = filteredRecommendations[page]
            RecommendationCard(recommendation = recommendation)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            if (showBackButton) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }
            }
            
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = if (showBackButton) 0.dp else 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories.size) { index ->
                    val category = categories[index]
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = category },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        color = if (selectedCategory == category) Color.White.copy(alpha = 0.2f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedCategory == category) Color.White else Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = category,
                            color = if (selectedCategory == category) Color.White else Color.White.copy(alpha = 0.7f),
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationCard(recommendation: Recommendation) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(recommendation.colors))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(32.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.4f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💡",
                fontSize = 60.sp,
                modifier = Modifier.offset(y = 10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = recommendation.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.graphicsLayer {
                    shadowElevation = 8.dp.toPx()
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = recommendation.text,
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                lineHeight = 26.sp
            )
        }
        
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Desliza para más",
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/RhetoricalFiguresGameScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class RhetoricalQuestion(
    val sentence: String,
    val icon: ImageVector? = null,
    val correctFigure: String,
    val options: List<String>,
    val explanation: String
)

val rhetoricalQuestionsLevel1 = listOf(
    RhetoricalQuestion(
        sentence = "Tus ojos son dos luceros que iluminan mi camino.",
        correctFigure = "Metáfora",
        options = listOf("Metáfora", "Símil", "Hipérbole", "Anáfora"),
        explanation = "Identifica un término real (ojos) con uno imaginario (luceros) sin usar nexos comparativos."
    ),
    RhetoricalQuestion(
        sentence = "Lloré un río de lágrimas al despedirse.",
        correctFigure = "Hipérbole",
        options = listOf("Metáfora", "Anáfora", "Hipérbole", "Símil"),
        explanation = "Es una exageración evidente de la realidad (llorar un río) para dar mayor impacto al mensaje."
    ),
    RhetoricalQuestion(
        sentence = "Eres fría como el hielo.",
        correctFigure = "Símil",
        options = listOf("Símil", "Metáfora", "Pregunta Retórica", "Hipérbole"),
        explanation = "Compara dos términos de forma explícita usando el nexo 'como'."
    ),
    RhetoricalQuestion(
        sentence = "Temprano levantó la muerte el vuelo, temprano madrugó la madrugada.",
        correctFigure = "Anáfora",
        options = listOf("Símil", "Anáfora", "Metáfora", "Hipérbole"),
        explanation = "Consiste en la repetición intencionada de palabras al comienzo de frases o versos (Temprano...)."
    ),
    RhetoricalQuestion(
        sentence = "¿Cuántas veces tengo que decirte que prestes atención?",
        correctFigure = "Pregunta Retórica",
        options = listOf("Anáfora", "Hipérbole", "Metáfora", "Pregunta Retórica"),
        explanation = "Es una pregunta que no espera respuesta, sino que invita a la reflexión o expresa indignación."
    )
).shuffled()

val rhetoricalQuestionsLevel2 = listOf(
    RhetoricalQuestion(
        sentence = "Imagen: Tiempo + Dinero",
        icon = Icons.Default.AttachMoney,
        correctFigure = "Metáfora",
        options = listOf("Hipérbole", "Símil", "Metáfora", "Anáfora"),
        explanation = "Representa 'El tiempo es oro', identificando el tiempo con el oro para destacar su valor."
    ),
    RhetoricalQuestion(
        sentence = "Imagen: Lágrimas / Agua infinita",
        icon = Icons.Default.WaterDrop,
        correctFigure = "Hipérbole",
        options = listOf("Pregunta Retórica", "Hipérbole", "Símil", "Anáfora"),
        explanation = "Representa 'Llorar un río', una exageración clara para enfatizar tristeza."
    ),
    RhetoricalQuestion(
        sentence = "Imagen: Copo de Nieve / Hielo",
        icon = Icons.Default.AcUnit,
        correctFigure = "Símil",
        options = listOf("Símil", "Metáfora", "Hipérbole", "Anáfora"),
        explanation = "Representa 'Frío como el hielo', comparando el carácter con la temperatura del hielo."
    ),
    RhetoricalQuestion(
        sentence = "Imagen: Repetición constante",
        icon = Icons.Default.Repeat,
        correctFigure = "Anáfora",
        options = listOf("Símil", "Hipérbole", "Metáfora", "Anáfora"),
        explanation = "El símbolo de repetición ilustra la anáfora, que consiste en repetir palabras al inicio de las frases."
    ),
    RhetoricalQuestion(
        sentence = "Imagen: Interrogación sin respuesta",
        icon = Icons.Default.QuestionMark,
        correctFigure = "Pregunta Retórica",
        options = listOf("Anáfora", "Pregunta Retórica", "Símil", "Metáfora"),
        explanation = "Representa una pregunta que no busca ser respondida, sino enfatizar una idea."
    )
).shuffled()

val gameLevels = listOf(
    rhetoricalQuestionsLevel1,
    rhetoricalQuestionsLevel2
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhetoricalFiguresGameScreen(onBack: () -> Unit) {
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    
    var showIntro by remember { mutableStateOf(true) }
    var showLevelUp by remember { mutableStateOf(false) }
    var gameState by remember { mutableStateOf("playing") }
    
    var showExplanation by remember { mutableStateOf(false) }
    var lastAnswerCorrect by remember { mutableStateOf(false) }

    val currentLevelQuestions = gameLevels.getOrNull(currentLevelIndex)
    val currentQuestion = currentLevelQuestions?.getOrNull(currentQuestionIndex)

    if (showIntro) {
        AlertDialog(
            onDismissRequest = { showIntro = false },
            title = { Text("Figuras Retóricas", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Las figuras retóricas embellecen tu discurso y lo hacen memorable.\\n\\n" +
                    "Nivel 1: Identifica figuras en texto.\\n" +
                    "Nivel 2: Identifica figuras a partir de imágenes simbólicas.\\n\\n" +
                    "¡Gana puntos y no pierdas tus vidas!"
                )
            },
            confirmButton = {
                Button(onClick = { showIntro = false }) {
                    Text("¡A jugar!")
                }
            }
        )
    }

    if (showLevelUp) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Nivel ${currentLevelIndex + 1} Completado!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = { Text("Has completado las preguntas. ¡Prepárate para las imágenes!") },
            confirmButton = {
                Button(onClick = { 
                    showLevelUp = false
                    currentLevelIndex++
                    currentQuestionIndex = 0
                    if (lives < 3) lives++
                }) {
                    Text("Siguiente Nivel")
                }
            }
        )
    }

    if (gameState == "won") {
        AlertDialog(
            onDismissRequest = { onBack() },
            title = { Text("¡Excelente!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)) },
            text = { Text("Has completado todas las frases. Puntuación final: $score") },
            confirmButton = {
                Button(onClick = { onBack() }) {
                    Text("Volver")
                }
            }
        )
    }

    if (gameState == "lost") {
        AlertDialog(
            onDismissRequest = { onBack() },
            title = { Text("Fin del Juego", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = { Text("Te has quedado sin vidas. ¡Repasa las figuras y vuelve a intentarlo!") },
            confirmButton = {
                Button(onClick = { 
                    currentLevelIndex = 0
                    currentQuestionIndex = 0
                    lives = 3
                    score = 0
                    gameState = "playing"
                }) {
                    Text("Reintentar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onBack() }) {
                    Text("Salir")
                }
            }
        )
    }

    if (showExplanation && currentQuestion != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { 
                Text(
                    if (lastAnswerCorrect) "¡Correcto!" else "Incorrecto", 
                    fontWeight = FontWeight.Bold, 
                    color = if (lastAnswerCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                ) 
            },
            text = { 
                Column {
                    Text("La respuesta era: ${currentQuestion.correctFigure}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentQuestion.explanation)
                }
            },
            confirmButton = {
                Button(onClick = { 
                    showExplanation = false
                    if (currentLevelQuestions != null) {
                        if (currentQuestionIndex < currentLevelQuestions.size - 1) {
                            currentQuestionIndex++
                        } else if (currentLevelIndex < gameLevels.size - 1) {
                            showLevelUp = true
                        } else {
                            gameState = "won"
                        }
                    }
                }) {
                    Text("Continuar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Figuras Retóricas", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Puntos: $score",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Vida",
                            tint = Color.Red,
                            modifier = Modifier.size(28.dp).padding(horizontal = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Nivel ${currentLevelIndex + 1} - Pregunta ${currentQuestionIndex + 1} de ${currentLevelQuestions?.size ?: 0}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (currentQuestion != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (currentQuestion.icon != null) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = currentQuestion.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(80.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        Text(
                            text = if (currentQuestion.icon != null) currentQuestion.sentence else "\\"${currentQuestion.sentence}\\"",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "¿Qué figura retórica es?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentQuestion.options.forEach { option ->
                        Button(
                            onClick = {
                                if (option == currentQuestion.correctFigure) {
                                    score += 20
                                    lastAnswerCorrect = true
                                } else {
                                    lives--
                                    lastAnswerCorrect = false
                                    if (lives <= 0) {
                                        gameState = "lost"
                                    }
                                }
                                if (gameState != "lost") {
                                    showExplanation = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = option,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/SalesPracticeScreen.kt"] = '''package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.viewmodel.SalesPracticeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SalesPracticeScreen(
    onBack: () -> Unit,
    viewModel: SalesPracticeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    )

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    val state by viewModel.state.collectAsState()

    androidx.activity.compose.BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Práctica de Ventas (Video)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        if (permissionsState.allPermissionsGranted) {
            if (state.isConfiguring) {
                SalesConfigView(paddingValues, state.contextInfo, viewModel::updateContextInfo, viewModel::startPractice)
            } else if (state.isFinished) {
                SalesFeedbackView(paddingValues, state.feedback, viewModel::startPractice)
            } else {
                SalesPracticeView(paddingValues, state, viewModel)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Se requieren permisos de cámara y micrófono.")
            }
        }
    }
}

@Composable
fun SalesConfigView(
    paddingValues: PaddingValues,
    contextInfo: String,
    onContextChanged: (String) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .navigationBarsPadding()
            .imePadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Configura tu pitch",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Describe el producto o servicio que intentas vender para que el cliente (IA) pueda interactuar de manera realista.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = contextInfo,
            onValueChange = onContextChanged,
            label = { Text("Ej: Vendo software SaaS para gestión de RRHH", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = contextInfo.isNotBlank()
        ) {
            Text("Comenzar Roleplay", fontSize = 18.sp)
        }
    }
}

@Composable
fun SalesFeedbackView(
    paddingValues: PaddingValues,
    feedback: String?,
    onRestart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Análisis de Ventas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (feedback == null) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Evaluando tu desempeño...")
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = feedback,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onRestart, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text("Practicar de nuevo")
            }
        }
    }
}

@Composable
fun SalesPracticeView(
    paddingValues: PaddingValues,
    state: com.example.viewmodel.SalesState,
    viewModel: SalesPracticeViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    var currentFrame by remember { mutableStateOf<Bitmap?>(null) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
            }
        }
        tts.language = Locale("es", "ES")
        viewModel.setTts(tts)
        
        onDispose {
            speechRecognizer.destroy()
        }
    }

    LaunchedEffect(state.chatHistory) {
        val lastMessage = state.chatHistory.lastOrNull()
        if (lastMessage?.role == "model") {
            viewModel.speak(lastMessage.text)
            coroutineScope.launch {
                listState.animateScrollToItem(state.chatHistory.size - 1)
            }
        } else if (lastMessage?.role == "user") {
            coroutineScope.launch {
                listState.animateScrollToItem(state.chatHistory.size - 1)
            }
        }
    }

    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                if (state.isRecording) {
                    viewModel.toggleRecording()
                }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    viewModel.onUserSpeechResult(matches[0], currentFrame)
                } else if (state.isRecording) {
                     viewModel.toggleRecording()
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        onDispose {}
    }

    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            }
            speechRecognizer.startListening(intent)
        } else {
            speechRecognizer.stopListening()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        
                        var lastSampleTime = 0L
                        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastSampleTime >= 1500) {
                                lastSampleTime = currentTime
                                try {
                                    currentFrame = imageProxy.toBitmap()
                                } catch (_: Exception) {}
                            }
                            imageProxy.close()
                        }
                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, cameraSelector, preview, imageAnalysis
                            )
                        } catch (exc: Exception) {
                            Log.e("Camera", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            
            if (state.isWaitingForAI) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cliente pensando...", color = Color.White, fontSize = 12.sp)
                    }
                }
            } else if (state.isRecording) { 
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Red.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text("Escuchando...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(state.chatHistory) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .padding(vertical = 4.dp)
                                .background(
                                    color = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isUser) "Tú (Vendedor)" else "Cliente (IA)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg.text,
                                    color = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { viewModel.finishPractice() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Finalizar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Terminar Venta")
                }
                
                FloatingActionButton(
                    onClick = { viewModel.toggleRecording() },
                    containerColor = if (state.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = if (state.isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (state.isRecording) "Detener" else "Hablar"
                    )
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/StageMovementGameScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class StagePoint(val id: Int, val name: String)

val stagePoints = listOf(
    StagePoint(0, "Atrás Derecha"),
    StagePoint(1, "Atrás Centro"),
    StagePoint(2, "Atrás Izquierda"),
    StagePoint(3, "Centro Derecha"),
    StagePoint(4, "Centro"),
    StagePoint(5, "Centro Izquierda"),
    StagePoint(6, "Frente Derecha"),
    StagePoint(7, "Frente Centro"),
    StagePoint(8, "Frente Izquierda")
)

data class StrategicIntent(val description: String, val validPoints: List<Int>)

val strategicIntents = listOf(
    StrategicIntent("Tener una perspectiva más amplia del público", listOf(0, 1, 2)),
    StrategicIntent("Lograr mayor conexión con tu mensaje", listOf(3, 4, 5)),
    StrategicIntent("Interactuar de cerca con el público", listOf(6, 7, 8)),
    StrategicIntent("Conectar con el sector izquierdo del público", listOf(0, 3, 6)),
    StrategicIntent("Conectar con el sector derecho del público", listOf(2, 5, 8))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageMovementGameScreen(
    onBack: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var currentSpeakerPosition by remember { mutableIntStateOf(4) }
    
    var currentLevel by remember { mutableIntStateOf(1) }
    var targetPoint by remember { mutableStateOf(stagePoints.random()) }
    var targetIntent by remember { mutableStateOf(strategicIntents.random()) }
    
    var showReward by remember { mutableStateOf(false) }
    var showIntro by remember { mutableStateOf(true) }
    var showLevel2Intro by remember { mutableStateOf(false) }

    LaunchedEffect(targetPoint, currentLevel) {
        if (currentLevel == 1 && targetPoint.id == currentSpeakerPosition) {
            var newTarget = stagePoints.random()
            while (newTarget.id == currentSpeakerPosition) {
                newTarget = stagePoints.random()
            }
            targetPoint = newTarget
        }
    }
    
    LaunchedEffect(targetIntent, currentLevel) {
        if (currentLevel == 2 && targetIntent.validPoints.contains(currentSpeakerPosition)) {
            var newTarget = strategicIntents.random()
            while (newTarget.validPoints.contains(currentSpeakerPosition)) {
                newTarget = strategicIntents.random()
            }
            targetIntent = newTarget
        }
    }

    if (showIntro) {
        AlertDialog(
            onDismissRequest = { showIntro = false },
            title = { Text("¡Importante!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cuando hablamos en público, nuestra derecha es la izquierda del público, y viceversa.\\n\\nPor lo tanto, en este ejercicio las indicaciones (Izquierda/Derecha) están desde la perspectiva del orador.",
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showIntro = false }) {
                    Text("Entendido, ¡A Jugar!")
                }
            }
        )
    }

    if (showLevel2Intro) {
        AlertDialog(
            onDismissRequest = { showLevel2Intro = false },
            title = { Text("Nivel 2: Estrategia", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Coloca al orador según lo que busques comunicar:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Atrás (1, 2, 3): Para tener una perspectiva más amplia del público.")
                    Text("• Centro (4, 5, 6): Para mayor conexión con tu mensaje.")
                    Text("• Adelante (7, 8, 9): Para interactuar con el público.")
                    Text("• Tu Derecha (1, 4, 7): Para conectar con el sector izquierdo del público.")
                    Text("• Tu Izquierda (3, 6, 9): Para conectar con el sector derecho del público.")
                }
            },
            confirmButton = {
                Button(onClick = { showLevel2Intro = false }) {
                    Text("Comenzar Nivel 2")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Desplazamiento", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                SegmentedButton(
                    selected = currentLevel == 1,
                    onClick = { currentLevel = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Nivel 1: Puntos")
                }
                SegmentedButton(
                    selected = currentLevel == 2,
                    onClick = { 
                        if (currentLevel != 2) {
                            currentLevel = 2
                            showLevel2Intro = true
                        }
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Nivel 2: Estrategia")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Puntos: $score",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Racha",
                        tint = if (streak > 0) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "x$streak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (streak > 0) Color(0xFFFFC107) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (currentLevel == 1) "Mueve al orador hacia:" else "Buscas comunicar:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = if (currentLevel == 1) targetPoint.name else targetIntent.description,
                    style = if (currentLevel == 1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(4.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (col in 0..2) {
                                val pointId = row * 3 + col
                                val isCurrentPosition = pointId == currentSpeakerPosition
                                val scale by animateFloatAsState(if (isCurrentPosition) 1.2f else 1.0f)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrentPosition) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable {
                                            if (pointId != currentSpeakerPosition) {
                                                currentSpeakerPosition = pointId
                                                
                                                val isCorrect = if (currentLevel == 1) {
                                                    pointId == targetPoint.id
                                                } else {
                                                    targetIntent.validPoints.contains(pointId)
                                                }
                                                if (isCorrect) {
                                                    score += 10 + (streak * 5)
                                                    streak++
                                                    showReward = true
                                                    
                                                    if (currentLevel == 1) {
                                                        var newTarget = stagePoints.random()
                                                        while (newTarget.id == currentSpeakerPosition) {
                                                            newTarget = stagePoints.random()
                                                        }
                                                        targetPoint = newTarget
                                                    } else {
                                                        var newTarget = strategicIntents.random()
                                                        while (newTarget.validPoints.contains(currentSpeakerPosition)) {
                                                            newTarget = strategicIntents.random()
                                                        }
                                                        targetIntent = newTarget
                                                    }
                                                } else {
                                                    streak = 0
                                                }
                                            }
                                        }
                                        .border(2.dp, if (isCurrentPosition) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCurrentPosition) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                                            contentDescription = "Orador",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .scale(scale)
                                        )
                                    } else {
                                        Text(
                                            text = "${pointId + 1}",
                                            color = Color.Gray.copy(alpha = 0.5f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Público",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.outline
            )
            if (showReward) {
                LaunchedEffect(Unit) {
                    delay(1000)
                    showReward = false
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Excelente desplazamiento!",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
'''

for path, content in screens.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

print("Part 3 written successfully")
