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
                    "Las figuras retóricas embellecen tu discurso y lo hacen memorable.\n\n" +
                    "Nivel 1: Identifica figuras en texto.\n" +
                    "Nivel 2: Identifica figuras a partir de imágenes simbólicas.\n\n" +
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
                            text = if (currentQuestion.icon != null) currentQuestion.sentence else "\"${currentQuestion.sentence}\"",
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
