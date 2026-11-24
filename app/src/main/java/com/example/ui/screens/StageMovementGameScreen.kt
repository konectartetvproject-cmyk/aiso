package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class StagePoint(
    val id: Int,
    val name: String,
    val shortTag: String,
    val bookMeaning: String
)

val stagePoints = listOf(
    StagePoint(0, "Atrás Derecha", "A-DER", "Perspectiva amplia + conexión con el sector izquierdo del público."),
    StagePoint(1, "Atrás Centro", "A-CEN", "Posición de reflexión, autoridad panorámica y pausa estratégica."),
    StagePoint(2, "Atrás Izquierda", "A-IZQ", "Perspectiva amplia + conexión con el sector derecho del público."),
    StagePoint(3, "Centro Derecha", "C-DER", "Transición dinámica para explicar el primer argumento clave."),
    StagePoint(4, "Centro de Poder", "CENTRO", "El núcleo del escenario: máxima autoridad para tu tesis central."),
    StagePoint(5, "Centro Izquierda", "C-IZQ", "Transición hacia un contraste o segundo bloque argumentativo."),
    StagePoint(6, "Frente Derecha", "F-DER", "Máxima cercanía emocional con el sector izquierdo de la sala."),
    StagePoint(7, "Frente Centro", "PROSCENIO", "Cercanía íntima: ideal para historias personales y llamados a la acción."),
    StagePoint(8, "Frente Izquierda", "F-IZQ", "Máxima cercanía emocional con el sector derecho de la sala.")
)

data class StrategicIntent(
    val description: String,
    val validPoints: List<Int>,
    val coachingTip: String
)

val strategicIntents = listOf(
    StrategicIntent(
        "Tener una perspectiva panorámica y reflexiva de todo el auditorio",
        listOf(0, 1, 2),
        "La zona trasera te da visión completa del público y proyecta serenidad."
    ),
    StrategicIntent(
        "Declarar la idea central de tu discurso con máxima autoridad",
        listOf(4),
        "El Centro de Poder (5) es el ancla donde nacen y concluyen las grandes ideas."
    ),
    StrategicIntent(
        "Contar una anécdota íntima y lograr cercanía emocional",
        listOf(6, 7, 8),
        "Avanzar al frente (Proscenio) rompe la barrera física y genera empatía inmediata."
    ),
    StrategicIntent(
        "Integrar al sector izquierdo del público (tu derecha como orador)",
        listOf(0, 3, 6),
        "Recuerda: tu derecha en el escenario corresponde a la izquierda de los espectadores."
    ),
    StrategicIntent(
        "Integrar al sector derecho del público (tu izquierda como orador)",
        listOf(2, 5, 8),
        "Desplazarte a tu izquierda equilibra la energía con el sector derecho del auditorio."
    ),
    StrategicIntent(
        "Hacer el Llamado a la Acción final mirando de cerca a todos",
        listOf(7, 4),
        "El cierre de alto impacto se ejecuta en el Frente Centro o Centro de Poder."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageMovementGameScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var currentSpeakerPosition by remember { mutableIntStateOf(4) }

    var currentLevel by remember { mutableIntStateOf(1) } // 1 = Zonas, 2 = Estrategia, 3 = Contrarreloj 45s
    var targetPoint by remember { mutableStateOf(stagePoints.first { it.id != 4 }) }
    var targetIntent by remember { mutableStateOf(strategicIntents.first()) }

    var timeLeft by remember { mutableIntStateOf(45) }
    var isTimerRunning by remember { mutableStateOf(false) }

    var lastFeedbackText by remember {
        mutableStateOf("Perspectiva del Orador: Tu derecha es la izquierda del público.")
    }
    var lastMoveWasCorrect by remember { mutableStateOf<Boolean?>(null) }
    var showGuideDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentLevel) {
        if (currentLevel == 3) {
            timeLeft = 45
            score = 0
            streak = 0
            isTimerRunning = true
        } else {
            isTimerRunning = false
        }
    }

    LaunchedEffect(isTimerRunning, timeLeft, currentLevel) {
        if (currentLevel == 3 && isTimerRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                isTimerRunning = false
            }
        }
    }

    fun pickNextChallenge(newPos: Int) {
        if (currentLevel == 1) {
            var next = stagePoints.random()
            while (next.id == newPos) {
                next = stagePoints.random()
            }
            targetPoint = next
        } else {
            var next = strategicIntents.random()
            while (next.validPoints.contains(newPos) && next.validPoints.size < 3) {
                next = strategicIntents.random()
            }
            targetIntent = next
        }
    }

    if (showGuideDialog) {
        AlertDialog(
            onDismissRequest = { showGuideDialog = false },
            title = { Text("Mapa del Escenario AISO", fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "En oratoria profesional, el escenario se divide en 9 cuadrantes vistos desde los ojos del orador:",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("• Atrás (1, 2, 3): Perspectiva panorámica, pausas reflexivas y control visual.")
                    Text("• Centro (4, 5, 6): Equilibrio, desarrollo de argumentos y Centro de Poder.")
                    Text("• Frente / Proscenio (7, 8, 9): Intimidad, historias personales y cierres.")
                    Text("• Tu Derecha (1, 4, 7): Conecta con el sector izquierdo del público.")
                    Text("• Tu Izquierda (3, 6, 9): Conecta con el sector derecho del público.")
                }
            },
            confirmButton = {
                Button(onClick = { showGuideDialog = false }) {
                    Text("¡Entendido!")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Desplazamiento Escénico", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            "Domina las 9 Zonas de Poder en el Escenario",
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
                    IconButton(onClick = { showGuideDialog = true }) {
                        Icon(Icons.Default.Info, contentDescription = "Guía del escenario")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Selector de Nivel
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = currentLevel == 1,
                    onClick = {
                        currentLevel = 1
                        pickNextChallenge(currentSpeakerPosition)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text("1. Zonas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                SegmentedButton(
                    selected = currentLevel == 2,
                    onClick = {
                        currentLevel = 2
                        pickNextChallenge(currentSpeakerPosition)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                ) {
                    Text("2. Estrategia", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                SegmentedButton(
                    selected = currentLevel == 3,
                    onClick = {
                        currentLevel = 3
                        pickNextChallenge(currentSpeakerPosition)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                ) {
                    Text("3. Reto 45s", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // HUD Puntos, Racha y Reloj
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
                        text = "🏆 Puntos: $score",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                if (currentLevel == 3) {
                    Surface(
                        color = if (timeLeft <= 10) Color(0xFF7F1D1D) else Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (timeLeft <= 10) Color(0xFFF87171) else Color(0xFF38BDF8))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${timeLeft}s",
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Racha",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Racha x$streak",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFBBF24)
                        )
                    }
                }
            }

            // Consigna Actual
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.5.dp, Color(0xFF0D9488)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (currentLevel == 1) "DESPLAZA AL ORADOR HACIA:" else "OBJETIVO COMUNICATIVO:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2DD4BF)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (currentLevel == 1) targetPoint.name else targetIntent.description,
                        fontSize = if (currentLevel == 1) 22.sp else 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Escenario Teatral 3x3
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(2.dp, Color(0xFF475569)),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.02f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FONDO DEL ESCENARIO (Perspectiva del Orador)",
                        color = Color(0xFF64748B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (row in 0..2) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0..2) {
                                    val point = stagePoints[row * 3 + col]
                                    val isCurrentPosition = point.id == currentSpeakerPosition
                                    val scale by animateFloatAsState(
                                        if (isCurrentPosition) 1.06f else 1.0f,
                                        label = "stage_node_scale"
                                    )
                                    val bgColor by animateColorAsState(
                                        targetValue = when {
                                            isCurrentPosition -> Color(0xFF0D9488)
                                            point.id == 4 -> Color(0xFF1E293B)
                                            else -> Color(0xFF1E293B).copy(alpha = 0.7f)
                                        },
                                        label = "stage_node_bg"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .scale(scale)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(bgColor)
                                            .border(
                                                width = if (isCurrentPosition) 2.5.dp else 1.dp,
                                                color = if (isCurrentPosition) Color(0xFF5EEAD4) else Color(0xFF334155),
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                            .clickable(enabled = currentLevel != 3 || isTimerRunning) {
                                                currentSpeakerPosition = point.id
                                                val isCorrect = if (currentLevel == 1) {
                                                    point.id == targetPoint.id
                                                } else {
                                                    targetIntent.validPoints.contains(point.id)
                                                }
                                                lastMoveWasCorrect = isCorrect
                                                if (isCorrect) {
                                                    score += 15 + (streak * 5)
                                                    streak++
                                                    lastFeedbackText = "✅ ${point.name}: ${point.bookMeaning}"
                                                    pickNextChallenge(point.id)
                                                } else {
                                                    streak = 0
                                                    lastFeedbackText = "❌ Te moviste a ${point.name}. Intenta otra zona para cumplir el objetivo."
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            if (isCurrentPosition) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                                                    contentDescription = "Orador",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                                Text(
                                                    text = "ORADOR",
                                                    color = Color(0xFFA7F3D0),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            } else {
                                                Text(
                                                    text = "${point.id + 1}",
                                                    color = Color(0xFF94A3B8),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp
                                                )
                                                Text(
                                                    text = point.shortTag,
                                                    color = Color(0xFF64748B),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Borde del Proscenio
                    Surface(
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "▼ BORDE DEL ESCENARIO / PROSCENIO ▼",
                            color = Color(0xFFFBBF24),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // Público dividido por sectores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Sector Izq. Público", "Sector Central", "Sector Der. Público").forEach { sector ->
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Groups, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(sector, color = Color(0xFFCBD5E1), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Retroalimentación Pedagógica Instantánea
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = when (lastMoveWasCorrect) {
                    true -> Color(0xFF064E3B)
                    false -> Color(0xFF7F1D1D)
                    null -> Color(0xFF1E293B)
                },
                border = BorderStroke(
                    1.dp,
                    when (lastMoveWasCorrect) {
                        true -> Color(0xFF10B981)
                        false -> Color(0xFFF87171)
                        null -> Color(0xFF334155)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = lastFeedbackText,
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            if (currentLevel == 3 && !isTimerRunning && timeLeft == 0) {
                Button(
                    onClick = {
                        timeLeft = 45
                        score = 0
                        streak = 0
                        isTimerRunning = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Repetir Reto de 45 Segundos", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
