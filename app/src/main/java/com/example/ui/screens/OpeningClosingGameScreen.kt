package com.example.ui.screens

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
    val type: String, // "Inicio" or "Conclusión"
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
    var gameState by remember { mutableStateOf("intro") } // intro, phase1, phase2, won
    var currentPhase by remember { mutableIntStateOf(1) } // 1: Inicio vs Cierre, 2: Specific Technique
    var showFeedback by remember { mutableStateOf(false) }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackColor by remember { mutableStateOf(Color.Green) }

    val currentQuestion = openingClosingQuestions.getOrNull(currentIndex)

    if (gameState == "intro") {
        AlertDialog(
            onDismissRequest = { gameState = "phase1" },
            title = { Text("El Arte del Inicio y el Cierre", fontWeight = FontWeight.Bold) },
            text = { 
                Text("Los primeros y últimos minutos de un discurso son vitales.\n\n" +
                     "Fase 1: Clasifica si el ejemplo es ideal para un 'Inicio' o una 'Conclusión'.\n" +
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
                                text = "\"${currentQuestion.example}\"",
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
