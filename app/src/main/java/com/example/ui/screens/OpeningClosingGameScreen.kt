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

data class OpeningClosingQuestion(
    val title: String,
    val example: String,
    val icon: ImageVector,
    val type: String, // "Inicio" o "Conclusión"
    val specificTechnique: String,
    val options: List<String>,
    val explanation: String
)

val openingClosingQuestions = listOf(
    OpeningClosingQuestion(
        title = "Impacto Cuantitativo",
        example = "¿Sabías que el 75% de los profesionales brillantes pierden ascensos por miedo a tomar el micrófono?",
        icon = Icons.Default.DataExploration,
        type = "Inicio",
        specificTechnique = "Dato Estadístico",
        options = listOf("Dato Estadístico", "Llamado a la Acción", "Resumen Tricolon", "Anécdota Personal"),
        explanation = "Un dato estadístico impactante rompe la inercia mental del público en los primeros 10 segundos."
    ),
    OpeningClosingQuestion(
        title = "Movilización Inmediata",
        example = "No esperen al próximo lunes. Hoy mismo, en su próxima reunión, pónganse de pie y compartan su idea.",
        icon = Icons.Default.Campaign,
        type = "Conclusión",
        specificTechnique = "Llamado a la Acción",
        options = listOf("Pregunta Retórica", "Llamado a la Acción", "Cita Célebre", "Objeto en Mano"),
        explanation = "El Llamado a la Acción cierra el discurso indicando un paso concreto e inmediato que la audiencia puede ejecutar."
    ),
    OpeningClosingQuestion(
        title = "Vulnerabilidad y Conexión",
        example = "Hace ocho años, mis manos temblaban tanto frente a 20 personas que se me cayó el micrófono al piso...",
        icon = Icons.Default.MenuBook,
        type = "Inicio",
        specificTechnique = "Anécdota Personal",
        options = listOf("Círculo Completo", "Anécdota Personal", "Resumen Tricolon", "Llamado a la Acción"),
        explanation = "Iniciar con una historia vivencial activa la empatía y la escucha emocional del auditorio."
    ),
    OpeningClosingQuestion(
        title = "Simetría Narrativa",
        example = "Y aquel joven al que se le cayó el micrófono hace ocho años hoy les dice: el miedo se vence hablando.",
        icon = Icons.Default.AllInclusive,
        type = "Conclusión",
        specificTechnique = "Círculo Completo",
        options = listOf("Anécdota Personal", "Dato Estadístico", "Círculo Completo", "Pregunta Retórica"),
        explanation = "El Círculo Completo retoma la historia del inicio al final para dar una sensación de perfección estructural."
    ),
    OpeningClosingQuestion(
        title = "Autoridad Inmortal",
        example = "Como escribió Maya Angelou: «La gente olvidará lo que dijiste, pero nunca cómo la hiciste sentir». Muchas gracias.",
        icon = Icons.Default.FormatQuote,
        type = "Conclusión",
        specificTechnique = "Cita Célebre",
        options = listOf("Llamado a la Acción", "Resumen Tricolon", "Dato Estadístico", "Cita Célebre"),
        explanation = "Cerrar con una frase célebre condensa la filosofía de tu mensaje en una sentencia memorable."
    ),
    OpeningClosingQuestion(
        title = "Activación Mental",
        example = "Imaginen por un instante: ¿Qué pasaría con sus ingresos si duplicaran su capacidad de persuadir?",
        icon = Icons.Default.PsychologyAlt,
        type = "Inicio",
        specificTechnique = "Pregunta Retórica",
        options = listOf("Resumen Tricolon", "Cita Célebre", "Pregunta Retórica", "Círculo Completo"),
        explanation = "La Interrogación Retórica e Imaginación de Escenarios obliga al cerebro del oyente a involucrarse."
    ),
    OpeningClosingQuestion(
        title = "Anclaje Visual",
        example = "(Levantando una llave antigua en silencio) Esta pequeña llave abrió la primera oficina de nuestra empresa...",
        icon = Icons.Default.Key,
        type = "Inicio",
        specificTechnique = "Objeto en Mano",
        options = listOf("Objeto en Mano", "Resumen Tricolon", "Llamado a la Acción", "Círculo Completo"),
        explanation = "Mostrar un objeto físico al iniciar concentra todas las miradas y despierta curiosidad instantánea."
    ),
    OpeningClosingQuestion(
        title = "Síntesis en 3 Pilares",
        example = "En conclusión, llévense estas tres llaves hoy: respiren con calma, hablen sin leer y conecten con el corazón.",
        icon = Icons.Default.Summarize,
        type = "Conclusión",
        specificTechnique = "Resumen Tricolon",
        options = listOf("Resumen Tricolon", "Dato Estadístico", "Objeto en Mano", "Pregunta Retórica"),
        explanation = "El Resumen en 3 puntos (Tricolon) fija la estructura 10-80-10 en la memoria de largo plazo."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpeningClosingGameScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    var currentIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var currentPhase by remember { mutableIntStateOf(1) } // 1 = Inicio vs Conclusión, 2 = Técnica específica
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswerCorrect by remember { mutableStateOf(false) }
    var feedbackText by remember { mutableStateOf("") }
    var isGameFinished by remember { mutableStateOf(false) }

    val currentQuestion = openingClosingQuestions.getOrNull(currentIndex)

    fun advanceStep() {
        selectedAnswer = null
        if (currentPhase == 1) {
            currentPhase = 2
        } else {
            currentPhase = 1
            if (currentIndex < openingClosingQuestions.size - 1) {
                currentIndex++
            } else {
                isGameFinished = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Inicios y Cierres de Impacto", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            "Regla 10-80-10 • Paso $currentPhase de 2",
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
            if (!isGameFinished && currentQuestion != null) {
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
                                Text("🏆 Puntos: $score", fontWeight = FontWeight.ExtraBold, color = Color.White)
                                if (streak > 1) {
                                    Text("🔥 x$streak", fontWeight = FontWeight.Black, color = Color(0xFFFBBF24))
                                }
                            }
                        }

                        Text(
                            text = "Caso ${currentIndex + 1} de ${openingClosingQuestions.size}",
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    val progress = (currentIndex * 2 + currentPhase).toFloat() / (openingClosingQuestions.size * 2).toFloat()
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF3B82F6),
                        trackColor = Color(0xFF1E293B)
                    )

                    // Tarjeta del Ejemplo de Discurso
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.5.dp, Color(0xFF3B82F6))
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.18f), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = currentQuestion.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(38.dp),
                                    tint = Color(0xFF60A5FA)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = currentQuestion.title,
                                color = Color(0xFF93C5FD),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "«${currentQuestion.example}»",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )
                        }
                    }

                    if (currentPhase == 1) {
                        Text(
                            text = "PASO 1: ¿En qué momento del discurso usarías esta frase?",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("Inicio", "Conclusión").forEach { choice ->
                                val isAnswered = selectedAnswer != null
                                val isCorrectChoice = choice == currentQuestion.type
                                val isSelectedChoice = selectedAnswer == choice

                                Button(
                                    onClick = {
                                        if (!isAnswered) {
                                            selectedAnswer = choice
                                            if (isCorrectChoice) {
                                                isAnswerCorrect = true
                                                streak++
                                                score += 10 + (streak * 2)
                                                feedbackText = "✅ ¡Correcto! Es un ${currentQuestion.type} de alto impacto. Ahora identifica cuál técnica exacta utiliza."
                                            } else {
                                                isAnswerCorrect = false
                                                streak = 0
                                                feedbackText = "❌ En realidad corresponde a: ${currentQuestion.type}. Pasemos a identificar la técnica."
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = when {
                                            !isAnswered -> Color(0xFF2563EB)
                                            isCorrectChoice -> Color(0xFF059669)
                                            isSelectedChoice -> Color(0xFFDC2626)
                                            else -> Color(0xFF1E293B)
                                        }
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(60.dp)
                                ) {
                                    Text(
                                        text = if (choice == "Inicio") "🚀 Inicio" else "🏁 Conclusión",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "PASO 2: ¿Qué técnica específica del libro AISO está aplicando?",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            currentQuestion.options.forEach { option ->
                                val isAnswered = selectedAnswer != null
                                val isCorrectOption = option == currentQuestion.specificTechnique
                                val isSelectedOption = selectedAnswer == option

                                Surface(
                                    onClick = {
                                        if (!isAnswered) {
                                            selectedAnswer = option
                                            if (isCorrectOption) {
                                                isAnswerCorrect = true
                                                streak++
                                                score += 15 + (streak * 3)
                                                feedbackText = "✅ ¡Exacto! ${currentQuestion.explanation}"
                                            } else {
                                                isAnswerCorrect = false
                                                streak = 0
                                                feedbackText = "❌ Era «${currentQuestion.specificTechnique}». ${currentQuestion.explanation}"
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    color = when {
                                        !isAnswered -> Color(0xFF1E293B)
                                        isCorrectOption -> Color(0xFF064E3B)
                                        isSelectedOption -> Color(0xFF7F1D1D)
                                        else -> Color(0xFF0F172A)
                                    },
                                    border = BorderStroke(
                                        1.5.dp,
                                        when {
                                            !isAnswered -> Color(0xFF334155)
                                            isCorrectOption -> Color(0xFF10B981)
                                            isSelectedOption -> Color(0xFFF87171)
                                            else -> Color(0xFF1E293B)
                                        }
                                    ),
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
                                        Text(option, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        if (isAnswered) {
                                            if (isCorrectOption) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399))
                                            } else if (isSelectedOption) {
                                                Icon(Icons.Default.Cancel, contentDescription = null, tint = Color(0xFFF87171))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Tarjeta de Retroalimentación + Botón Inmediato
                    if (selectedAnswer != null) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAnswerCorrect) Color(0xFF064E3B) else Color(0xFF450A0A)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isAnswerCorrect) Color(0xFF10B981) else Color(0xFFF87171)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = feedbackText,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { advanceStep() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isAnswerCorrect) Color(0xFF10B981) else Color(0xFF3B82F6)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text(
                                        if (currentPhase == 1) "Ir al Paso 2: Identificar Técnica" else "Siguiente Caso",
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                                }
                            }
                        }
                    }
                }
            } else {
                // Pantalla Final
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.5.dp, Color(0xFF10B981)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏁", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "¡Dominio de Aperturas y Cierres!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Puntuación Total: $score pts",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    currentIndex = 0
                                    score = 0
                                    streak = 0
                                    currentPhase = 1
                                    selectedAnswer = null
                                    isGameFinished = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Practicar de Nuevo", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
