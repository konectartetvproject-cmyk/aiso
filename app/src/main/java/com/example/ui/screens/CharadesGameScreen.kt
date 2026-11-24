package com.example.ui.screens

import android.view.SoundEffectConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val charadesCategories = mapOf(
    "Emociones Escénicas" to listOf(
        "Autoridad Serena", "Entusiasmo Contagioso", "Asombro Genuino", "Empatía Profunda",
        "Indignación Justa", "Urgencia Ejecutiva", "Curiosidad Intrigante", "Gratitud Sincera",
        "Determinación Inquebrantable", "Confianza Absoluta", "Suspenso Dramático", "Inspiración"
    ),
    "Arquetipos del Libro" to listOf(
        "Ernesto el Erudito", "Susana la Sniper", "Nicanor el Negativo", "El Orador que Lee Todo",
        "El Orador con Pánico Escénico", "El Líder Carismático", "El Público Aburrido",
        "El Moderador Imparcial", "El Vendedor Persuasivo", "El Mentor Sabio"
    ),
    "Retos en Escena" to listOf(
        "Se apaga el micrófono en pleno clímax", "Olvidas la siguiente diapositiva",
        "Alguien interrumpe desde la primera fila", "Recibes una ovación de pie",
        "Explicas un gráfico complejo sin hablar", "Pides silencio solo con la mirada",
        "Conectas con las tres zonas del auditorio", "Cerras una venta millonaria"
    )
)

private val charadesConstraints = listOf(
    "Exprésalo usando únicamente gestos de manos y postura abierta",
    "Exprésalo solo con el rostro y la mirada (manos quietas)",
    "Exprésalo en cámara lenta como en cine mudo",
    "Exprésalo desplazándote por los 3 sectores del escenario",
    "Exprésalo con la máxima energía corporal posible (sin emitir sonido)",
    "Exprésalo pasando de la duda total a la seguridad absoluta en 5 segundos"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharadesGameScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    val view = LocalView.current

    var gameState by remember { mutableStateOf("start") }
    var selectedCategory by remember { mutableStateOf("Emociones Escénicas") }
    var roundDuration by remember { mutableIntStateOf(60) }
    var timeLeft by remember { mutableIntStateOf(60) }
    var isPaused by remember { mutableStateOf(false) }

    var score by remember { mutableIntStateOf(0) }
    var currentWord by remember { mutableStateOf("") }
    var currentConstraint by remember { mutableStateOf("") }

    val guessedWords = remember { mutableStateListOf<String>() }
    val skippedWords = remember { mutableStateListOf<String>() }

    fun pickNextCard() {
        val list = charadesCategories[selectedCategory] ?: charadesCategories.values.first()
        var next = list.random()
        if (list.size > 1) {
            while (next == currentWord) {
                next = list.random()
            }
        }
        currentWord = next
        currentConstraint = charadesConstraints.random()
    }

    LaunchedEffect(gameState, timeLeft, isPaused) {
        if (gameState == "playing" && !isPaused && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                gameState = "finished"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Charadas de Oratoria", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            "Expresión Corporal y Lenguaje No Verbal",
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
                actions = {
                    if (gameState == "playing") {
                        IconButton(onClick = { isPaused = !isPaused }) {
                            Icon(
                                imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (isPaused) "Reanudar" else "Pausar"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
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
            when (gameState) {
                "start" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            modifier = Modifier.size(88.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎭", fontSize = 44.sp)
                            }
                        }

                        Text(
                            text = "Laboratorio de Lenguaje No Verbal",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "El 55% del impacto escénico proviene de tu lenguaje corporal. Actúa cada emoción, arquetipo o situación sin pronunciar ni una sola palabra.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        // Selector de Categoría
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    "1. ELIGE LA CATEGORÍA:",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                charadesCategories.keys.forEach { category ->
                                    val isSelected = selectedCategory == category
                                    Surface(
                                        onClick = { selectedCategory = category },
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isSelected) Color(0xFFD97706) else Color(0xFF0F172A),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFFFDE68A) else Color(0xFF334155)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = category,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Selector de Tiempo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            listOf(45, 60, 90).forEach { seconds ->
                                val isSel = roundDuration == seconds
                                OutlinedButton(
                                    onClick = { roundDuration = seconds },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSel) Color(0xFF38BDF8).copy(alpha = 0.2f) else Color.Transparent,
                                        contentColor = if (isSel) Color(0xFF38BDF8) else Color(0xFF94A3B8)
                                    ),
                                    border = BorderStroke(1.dp, if (isSel) Color(0xFF38BDF8) else Color(0xFF334155)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("${seconds}s", fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                score = 0
                                timeLeft = roundDuration
                                isPaused = false
                                guessedWords.clear()
                                skippedWords.clear()
                                pickNextCard()
                                gameState = "playing"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text(
                                "¡Subir al Escenario!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }
                }

                "playing" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
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
                                Text(
                                    text = "Aciertos: $score",
                                    color = Color(0xFF4ADE80),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }

                            Surface(
                                color = if (timeLeft <= 10) Color(0xFF7F1D1D) else Color(0xFF1E293B),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (timeLeft <= 10) Color(0xFFF87171) else Color(0xFF38BDF8))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPaused) "PAUSA" else "${timeLeft}s",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        // Tarjeta Principal de Actuación
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(vertical = 20.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(2.dp, Color(0xFFF59E0B))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Surface(
                                    color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = selectedCategory.uppercase(),
                                        color = Color(0xFFFBBF24),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = currentWord,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 36.sp
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Text(
                                        text = "🎯 Consigna: $currentConstraint",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                            }
                        }

                        // Botones de Pasar / Correcto
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Button(
                                onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    skippedWords.add(currentWord)
                                    pickNextCard()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pasar", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            }

                            Button(
                                onClick = {
                                    view.playSoundEffect(SoundEffectConstants.CLICK)
                                    score++
                                    guessedWords.add(currentWord)
                                    pickNextCard()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(60.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("¡Logrado!", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                "finished" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        Text("🎭", fontSize = 52.sp)
                        Text(
                            text = "¡Ronda Completada!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "$score interpretaciones logradas",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFBBF24)
                        )

                        if (guessedWords.isNotEmpty() || skippedWords.isNotEmpty()) {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (guessedWords.isNotEmpty()) {
                                        Text(
                                            "✅ Logradas (${guessedWords.size}): ${guessedWords.joinToString(", ")}",
                                            color = Color(0xFF86EFAC),
                                            fontSize = 13.sp
                                        )
                                    }
                                    if (skippedWords.isNotEmpty()) {
                                        Text(
                                            "⏭️ Pasadas (${skippedWords.size}): ${skippedWords.joinToString(", ")}",
                                            color = Color(0xFFFCA5A5),
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { gameState = "start" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF0F172A))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nueva Ronda", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                        }
                    }
                }
            }
        }
    }
}
