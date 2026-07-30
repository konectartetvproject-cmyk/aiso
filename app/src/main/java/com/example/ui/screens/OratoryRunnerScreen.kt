package com.example.ui.screens

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
                    text = "Esquiva las muletillas (🧟) y el pánico escénico (👻).\nRecolecta palabras clave (💡).",
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
                // Score Header
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
                
                // Game Board
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
                    
                    // Lanes background
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
                    
                    // Items
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
                    
                    // Player
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
                        Text("🗣️", fontSize = 50.sp)
                    }
                }
            }
        }
    }
}
