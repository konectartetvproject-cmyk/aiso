package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
        sentence = "Tus ojos son dos faros que guían a este equipo en la tormenta.",
        correctFigure = "Metáfora",
        options = listOf("Metáfora", "Símil", "Hipérbole", "Anáfora"),
        explanation = "Metáfora: Identifica un término real (ojos) con uno imaginario (faros) sin usar la palabra «como»."
    ),
    RhetoricalQuestion(
        sentence = "Lloré un océano entero antes de aprender a hablar sin miedo.",
        correctFigure = "Hipérbole",
        options = listOf("Metáfora", "Anáfora", "Hipérbole", "Símil"),
        explanation = "Hipérbole: Exageración intencionada («un océano entero») para multiplicar la fuerza dramática."
    ),
    RhetoricalQuestion(
        sentence = "Un orador preparado es firme como un roble ante las críticas.",
        correctFigure = "Símil",
        options = listOf("Símil", "Metáfora", "Pregunta Retórica", "Hipérbole"),
        explanation = "Símil: Compara dos elementos explícitamente utilizando el nexo comparativo «como»."
    ),
    RhetoricalQuestion(
        sentence = "Hoy luchamos por nuestra voz, hoy luchamos por nuestra gente, hoy luchamos por el futuro.",
        correctFigure = "Anáfora",
        options = listOf("Símil", "Anáfora", "Metáfora", "Hipérbole"),
        explanation = "Anáfora: Repetición rítmica de las mismas palabras al inicio de frases sucesivas («Hoy luchamos...»)."
    ),
    RhetoricalQuestion(
        sentence = "¿Acaso vamos a permitir que el miedo decida nuestro destino?",
        correctFigure = "Pregunta Retórica",
        options = listOf("Anáfora", "Hipérbole", "Metáfora", "Pregunta Retórica"),
        explanation = "Pregunta Retórica: No espera respuesta verbal, sino despertar la conciencia del público."
    ),
    RhetoricalQuestion(
        sentence = "No preguntes qué puede hacer tu país por ti, pregunta qué puedes hacer tú por tu país.",
        correctFigure = "Antítesis / Quiasmo",
        options = listOf("Antítesis / Quiasmo", "Hipérbole", "Símil", "Metáfora"),
        explanation = "Contrapone dos ideas invirtiendo el orden de las palabras para crear una frase inmortal (J.F. Kennedy)."
    ),
    RhetoricalQuestion(
        sentence = "Llegué, vi y vencí: preparación, coraje y acción.",
        correctFigure = "Tricolon",
        options = listOf("Tricolon", "Hipérbole", "Símil", "Pregunta Retórica"),
        explanation = "Tricolon: Serie de tres elementos o cláusulas paralelas que dan ritmo y contundencia."
    )
)

val rhetoricalQuestionsLevel2 = listOf(
    RhetoricalQuestion(
        sentence = "«El tiempo es oro en cada presentación ejecutiva»",
        icon = Icons.Default.AttachMoney,
        correctFigure = "Metáfora",
        options = listOf("Hipérbole", "Símil", "Metáfora", "Anáfora"),
        explanation = "Metáfora visual y verbal: equipara directamente el tiempo con el oro para resaltar su valor."
    ),
    RhetoricalQuestion(
        sentence = "«Lo he repetido un millón de veces en la junta»",
        icon = Icons.Default.Bolt,
        correctFigure = "Hipérbole",
        options = listOf("Pregunta Retórica", "Hipérbole", "Símil", "Anáfora"),
        explanation = "Hipérbole: Exagera la cifra («un millón de veces») para enfatizar urgencia."
    ),
    RhetoricalQuestion(
        sentence = "«Un argumento claro como el agua cristalina»",
        icon = Icons.Default.WaterDrop,
        correctFigure = "Símil",
        options = listOf("Símil", "Metáfora", "Hipérbole", "Anáfora"),
        explanation = "Símil: Usa el conector «como» para comparar la claridad del argumento con el agua."
    ),
    RhetoricalQuestion(
        sentence = "«Tengo un sueño hoy... Tengo un sueño mañana...»",
        icon = Icons.Default.Repeat,
        correctFigure = "Anáfora",
        options = listOf("Símil", "Hipérbole", "Metáfora", "Anáfora"),
        explanation = "Anáfora inmortalizada por Martin Luther King Jr.: repite el inicio para crear cadencia emocional."
    ),
    RhetoricalQuestion(
        sentence = "«¿Quién de ustedes quiere duplicar sus resultados este año?»",
        icon = Icons.Default.QuestionMark,
        correctFigure = "Pregunta Retórica",
        options = listOf("Anáfora", "Pregunta Retórica", "Símil", "Metáfora"),
        explanation = "Pregunta Retórica de apertura: involucra el cerebro del espectador desde el segundo uno."
    )
)

val gameLevels = listOf(
    rhetoricalQuestionsLevel1,
    rhetoricalQuestionsLevel2
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RhetoricalFiguresGameScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var gameState by remember { mutableStateOf("playing") }

    val currentLevelQuestions = gameLevels.getOrNull(currentLevelIndex) ?: emptyList()
    val currentQuestion = currentLevelQuestions.getOrNull(currentQuestionIndex)

    fun nextQuestion() {
        selectedOption = null
        if (currentQuestionIndex < currentLevelQuestions.size - 1) {
            currentQuestionIndex++
        } else if (currentLevelIndex < gameLevels.size - 1) {
            currentLevelIndex++
            currentQuestionIndex = 0
            if (lives < 3) lives++
        } else {
            gameState = "won"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Figuras Retóricas", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            if (currentLevelIndex == 0) "Nivel 1: Maestría Verbal" else "Nivel 2: Simbología y Ritmo",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // HUD Superior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Puntos: $score", fontWeight = FontWeight.ExtraBold, color = Color.White)
                            if (streak > 1) {
                                Text("🔥 x$streak", fontWeight = FontWeight.Black, color = Color(0xFFFBBF24))
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = if (i <= lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Vida",
                                tint = if (i <= lives) Color(0xFFEF4444) else Color.Gray,
                                modifier = Modifier.size(26.dp).padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                // Barra de Progreso
                val progress = if (currentLevelQuestions.isNotEmpty()) {
                    (currentQuestionIndex + 1).toFloat() / currentLevelQuestions.size.toFloat()
                } else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color(0xFF8B5CF6),
                    trackColor = Color(0xFF1E293B)
                )

                if (currentQuestion != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.5.dp, Color(0xFF8B5CF6))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(22.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PREGUNTA ${currentQuestionIndex + 1} DE ${currentLevelQuestions.size}",
                                color = Color(0xFFC4B5FD),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (currentQuestion.icon != null) {
                                Box(
                                    modifier = Modifier
                                        .size(76.dp)
                                        .background(Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(18.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = currentQuestion.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(44.dp),
                                        tint = Color(0xFFC4B5FD)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Text(
                                text = "«${currentQuestion.sentence}»",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp
                            )
                        }
                    }

                    // Opciones con coloración inmediata
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        currentQuestion.options.forEach { option ->
                            val isAnswered = selectedOption != null
                            val isThisSelected = selectedOption == option
                            val isCorrectOption = option == currentQuestion.correctFigure

                            val bgColor = when {
                                !isAnswered -> Color(0xFF1E293B)
                                isCorrectOption -> Color(0xFF064E3B)
                                isThisSelected -> Color(0xFF7F1D1D)
                                else -> Color(0xFF0F172A)
                            }
                            val borderColor = when {
                                !isAnswered -> Color(0xFF334155)
                                isCorrectOption -> Color(0xFF10B981)
                                isThisSelected -> Color(0xFFF87171)
                                else -> Color(0xFF1E293B)
                            }

                            Surface(
                                onClick = {
                                    if (selectedOption == null && gameState == "playing") {
                                        selectedOption = option
                                        if (isCorrectOption) {
                                            streak++
                                            score += 20 + (streak * 5)
                                        } else {
                                            streak = 0
                                            lives--
                                            if (lives <= 0) {
                                                gameState = "lost"
                                            }
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = bgColor,
                                border = BorderStroke(1.5.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = option,
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isAnswered) {
                                        if (isCorrectOption) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                                        } else if (isThisSelected) {
                                            Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFF87171))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Explicación Inline + Botón Continuar Inmediato
                    if (selectedOption != null && gameState == "playing") {
                        val isCorrect = selectedOption == currentQuestion.correctFigure
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCorrect) Color(0xFF064E3B) else Color(0xFF450A0A)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isCorrect) Color(0xFF10B981) else Color(0xFFF87171)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isCorrect) "✅ ¡Exacto! ${currentQuestion.correctFigure}" else "❌ Era: ${currentQuestion.correctFigure}",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { nextQuestion() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text("Siguiente Pregunta", fontWeight = FontWeight.ExtraBold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            }

            if (gameState == "won" || gameState == "lost") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(
                            1.5.dp,
                            if (gameState == "won") Color(0xFF10B981) else Color(0xFFEF4444)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(if (gameState == "won") "🏆" else "📚", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (gameState == "won") "¡Maestro de la Retórica!" else "¡Fin de la Partida!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Puntuación Final: $score pts",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    currentLevelIndex = 0
                                    currentQuestionIndex = 0
                                    lives = 3
                                    score = 0
                                    streak = 0
                                    selectedOption = null
                                    gameState = "playing"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Jugar de Nuevo", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
