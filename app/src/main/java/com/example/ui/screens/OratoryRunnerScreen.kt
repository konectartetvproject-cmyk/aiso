package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OratoryRunnerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OratoryRunnerScreen(
    onBack: () -> Unit,
    viewModel: OratoryRunnerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    BackHandler { onBack() }
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Oratory Runner", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            "Carrera Escénica • Nivel ${state.level}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (state.isPlaying && !state.isGameOver) {
                        IconButton(onClick = { viewModel.togglePause() }) {
                            Icon(
                                imageVector = if (state.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = if (state.isPaused) "Reanudar" else "Pausar"
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
            if (!state.isPlaying && !state.isGameOver) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFF9800).copy(alpha = 0.18f),
                        modifier = Modifier.size(96.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🏃‍♂️", fontSize = 48.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Carrera de Oratoria AISO",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Avanza por los 3 sectores del escenario recolectando técnicas de poder y esquivando lo que sabotea tu discurso.",
                        textAlign = TextAlign.Center,
                        color = Color(0xFFCBD5E1),
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "GUÍA RÁPIDA DE ELEMENTOS:",
                                color = Color(0xFFFBBF24),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp
                            )
                            Text("✅ Recolecta: 💡 Idea Central (+15) • 👀 Mirada (+20) • ⏸️ Pausa de Poder (+25)", color = Color(0xFF86EFAC), fontSize = 13.sp)
                            Text("🛡️ Power-Up: Respiración de Caja (te protege de 1 impacto)", color = Color(0xFF38BDF8), fontSize = 13.sp)
                            Text("❌ Esquiva: 🗯️ Muletillas • 😰 Pánico • 🌫️ Mente en Blanco • 🎯 Pregunta Trampa", color = Color(0xFFFCA5A5), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                    Button(
                        onClick = { viewModel.startGame() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "¡Salir al Escenario!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // HUD Superior
                    Surface(
                        color = Color(0xFF1E293B),
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    for (i in 1..state.maxLives) {
                                        Icon(
                                            imageVector = if (i <= state.lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Confianza",
                                            tint = if (i <= state.lives) Color(0xFFEF4444) else Color.Gray,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    if (state.hasShield) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            color = Color(0xFF0284C7).copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(8.dp),
                                            border = BorderStroke(1.dp, Color(0xFF38BDF8))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Shield,
                                                    contentDescription = "Escudo",
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Escudo", color = Color(0xFF38BDF8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (state.combo > 1) {
                                        Surface(
                                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "🔥 x${(1 + (state.combo / 4)).coerceAtMost(4)} (${state.combo})",
                                                color = Color(0xFFFBBF24),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${state.score} pts",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.lastFeedback,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Pista de 3 Carriles
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                var dragSwiped = false
                                detectDragGestures(
                                    onDragStart = { dragSwiped = false },
                                    onDragEnd = { dragSwiped = false },
                                    onDragCancel = { dragSwiped = false }
                                ) { change, dragAmount ->
                                    change.consume()
                                    if (!dragSwiped) {
                                        if (dragAmount.x > 25f) {
                                            viewModel.moveRight()
                                            dragSwiped = true
                                        } else if (dragAmount.x < -25f) {
                                            viewModel.moveLeft()
                                            dragSwiped = true
                                        }
                                    }
                                }
                            }
                    ) {
                        val laneWidth = maxWidth / 3
                        val laneNames = listOf("Sector Izq.", "Centro Poder", "Sector Der.")

                        // Fondo de carriles interactivos (tocar para moverse a ese carril)
                        Row(modifier = Modifier.fillMaxSize()) {
                            for (laneIndex in 0..2) {
                                val isCurrentLane = state.playerLane == laneIndex
                                val bgColor by animateColorAsState(
                                    if (isCurrentLane) Color(0xFF1E293B).copy(alpha = 0.65f)
                                    else Color.Transparent,
                                    label = "lane_bg"
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(bgColor)
                                        .clickable { viewModel.setLane(laneIndex) }
                                        .padding(top = 8.dp),
                                    contentAlignment = Alignment.TopCenter
                                ) {
                                    Text(
                                        text = laneNames[laneIndex],
                                        color = if (isCurrentLane) Color(0xFFFBBF24) else Color.White.copy(alpha = 0.25f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Líneas divisorias de carril punteadas
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val third = size.width / 3f
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 20f), 0f)
                            drawLine(
                                color = Color.White.copy(alpha = 0.18f),
                                start = Offset(third, 0f),
                                end = Offset(third, size.height),
                                strokeWidth = 3f,
                                pathEffect = dashEffect
                            )
                            drawLine(
                                color = Color.White.copy(alpha = 0.18f),
                                start = Offset(third * 2, 0f),
                                end = Offset(third * 2, size.height),
                                strokeWidth = 3f,
                                pathEffect = dashEffect
                            )
                        }

                        // Ítems cayendo con tarjeta y etiqueta
                        state.items.forEach { item ->
                            val isObstacle = item.type.isObstacle
                            val cardColor = when {
                                item.type.grantsShield -> Color(0xFF0284C7)
                                isObstacle -> Color(0xFFDC2626)
                                else -> Color(0xFF059669)
                            }
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = laneWidth * item.lane,
                                        y = maxHeight * item.y
                                    )
                                    .width(laneWidth)
                                    .padding(horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = cardColor.copy(alpha = 0.92f),
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (isObstacle) Color(0xFFFCA5A5) else Color(0xFFA7F3D0)
                                    ),
                                    shadowElevation = 4.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(item.type.icon, fontSize = 26.sp)
                                        Text(
                                            text = item.type.label,
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }

                        // Jugador (Orador en Escena)
                        val animatedPlayerX by animateDpAsState(
                            targetValue = laneWidth * state.playerLane,
                            label = "player_x"
                        )
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = animatedPlayerX,
                                    y = maxHeight * 0.80f
                                )
                                .width(laneWidth),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (state.hasShield) Color(0xFF0284C7)
                                            else Color(0xFFF59E0B)
                                        )
                                        .border(
                                            width = 3.dp,
                                            color = if (state.hasShield) Color(0xFF7DD3FC) else Color.White,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎤", fontSize = 30.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Surface(
                                    color = Color(0xFF0F172A).copy(alpha = 0.85f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (state.hasShield) "ORADOR BLINDADO" else "ORADOR",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Controles Inferiores Táctiles (Botones Izquierda / Derecha)
                    Surface(
                        color = Color(0xFF0F172A),
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { viewModel.moveLeft() },
                                enabled = state.playerLane > 0 && !state.isPaused,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF1E293B),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Izquierda")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Izquierda", fontWeight = FontWeight.Bold)
                            }

                            FilledTonalButton(
                                onClick = { viewModel.moveRight() },
                                enabled = state.playerLane < 2 && !state.isPaused,
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF1E293B),
                                    contentColor = Color.White
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Text("Derecha", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Derecha")
                            }
                        }
                    }
                }

                // Modal de Fin de Juego
                if (state.isGameOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.82f))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = BorderStroke(1.5.dp, Color(0xFFF59E0B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🏆", fontSize = 48.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "¡Fin del Recorrido Escénico!",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Puntuación: ${state.score} pts",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFBBF24)
                                )
                                Text(
                                    text = "Récord de la sesión: ${state.highScore} pts • Nivel ${state.level}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "💡 Consejo AISO: Cuando sientas que una muletilla («eh», «este») se acerca, reemplázala por una Pausa de Impacto de 2 segundos y respira con el diafragma.",
                                        color = Color(0xFFCBD5E1),
                                        fontSize = 12.sp,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { viewModel.startGame() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Jugar de Nuevo", fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
