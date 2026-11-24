package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    BackHandler { onBack() }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        if (!state.isPlaying && !state.isGameOver && !state.isLevelComplete) {
            viewModel.startGame()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Defensa del Discurso", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            "Oleada ${state.wave} • Estrategia Antinervios",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.78f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleRemoveMode() }) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Retirar técnica",
                            tint = if (state.isRemoveMode) Color(0xFFF87171) else Color.White
                        )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // HUD de Confianza, Progreso de Oleada y Puntos
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Color(0xFFFBBF24))
                            ) {
                                Text(
                                    text = "💡 Confianza: ${state.confidence}",
                                    color = Color(0xFFFBBF24),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            if (state.confidenceDrops.isNotEmpty()) {
                                FilledTonalButton(
                                    onClick = { viewModel.collectAllConfidence() },
                                    colors = ButtonDefaults.filledTonalButtonColors(
                                        containerColor = Color(0xFF10B981),
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Recolectar +${state.confidenceDrops.sumOf { it.amount }} 💡", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = "${state.score} pts",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        val waveProgress = (state.levelScore.toFloat() / state.targetLevelScore.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Oleada ${state.wave}:",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            LinearProgressIndicator(
                                progress = { waveProgress },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFF10B981),
                                trackColor = Color(0xFF0F172A)
                            )
                            Text(
                                text = "${(waveProgress * 100).toInt()}%",
                                color = Color(0xFF10B981),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                // Selector de Técnicas de Oratoria
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val unlockedDefenses = DefenseType.entries.filter { it.requiredWave <= state.wave }
                    unlockedDefenses.forEach { type ->
                        val isSelected = state.selectedDefense == type && !state.isRemoveMode
                        val canAfford = state.confidence >= type.cost
                        Card(
                            modifier = Modifier
                                .width(92.dp)
                                .clickable { viewModel.selectDefense(type) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isSelected -> Color(0xFF0284C7)
                                    canAfford -> Color(0xFF1E293B)
                                    else -> Color(0xFF0F172A).copy(alpha = 0.6f)
                                }
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = when {
                                    isSelected -> Color(0xFF7DD3FC)
                                    canAfford -> Color(0xFF334155)
                                    else -> Color(0xFF1E293B)
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(type.icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = type.title,
                                    color = if (canAfford) Color.White else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${type.cost} 💡",
                                    color = if (canAfford) Color(0xFFFBBF24) else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Botón de Retirar / Reemplazar
                    Card(
                        modifier = Modifier
                            .width(86.dp)
                            .clickable { viewModel.toggleRemoveMode() },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (state.isRemoveMode) Color(0xFFDC2626) else Color(0xFF1E293B)
                        ),
                        border = BorderStroke(
                            1.5.dp,
                            if (state.isRemoveMode) Color(0xFFFCA5A5) else Color(0xFF334155)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔄", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Retirar",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "+25 💡",
                                color = Color(0xFF86EFAC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Mensaje de estado / instrucción
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.statusHint,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Tablero Táctico 6x3
                val rows = viewModel.rows
                val cols = viewModel.cols

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B)),
                    border = BorderStroke(2.dp, Color(0xFF047857)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        val rowHeight = maxHeight / rows
                        val colWidth = maxWidth / cols

                        // Cuadrícula 6x3
                        Column(modifier = Modifier.fillMaxSize()) {
                            for (r in 0 until rows) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                ) {
                                    for (c in 0 until cols) {
                                        val existingDefense = state.defenses.find { it.cell.col == c && it.cell.row == r }
                                        val isEven = (r + c) % 2 == 0
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isEven) Color(0xFF065F46)
                                                    else Color(0xFF047857).copy(alpha = 0.75f)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = Color(0xFF10B981).copy(alpha = 0.28f),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable { viewModel.placeDefense(c, r) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (existingDefense != null) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center,
                                                    modifier = Modifier.padding(2.dp)
                                                ) {
                                                    Text(existingDefense.type.icon, fontSize = 26.sp)
                                                    Text(
                                                        text = existingDefense.type.title,
                                                        color = Color.White,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1
                                                    )
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    val hpRatio = (existingDefense.hp.toFloat() / existingDefense.type.hp.toFloat()).coerceIn(0f, 1f)
                                                    LinearProgressIndicator(
                                                        progress = { hpRatio },
                                                        modifier = Modifier
                                                            .fillMaxWidth(0.75f)
                                                            .height(4.dp)
                                                            .clip(RoundedCornerShape(2.dp)),
                                                        color = Color(0xFF4ADE80),
                                                        trackColor = Color(0xFFEF4444)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Escudos de última línea (Lawnmowers)
                        state.lawnmowers.forEach { lm ->
                            if (lm.active || lm.triggered) {
                                val xPos = if (lm.triggered) lm.x else 0f
                                Box(
                                    modifier = Modifier
                                        .offset(x = colWidth * xPos, y = rowHeight * lm.row)
                                        .size(26.dp, rowHeight),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF0284C7),
                                        border = BorderStroke(1.dp, Color.White),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Shield,
                                                contentDescription = "Escudo de fila",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Distracciones avanzando
                        state.distractions.forEach { dist ->
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = colWidth * dist.x.coerceIn(0f, cols - 0.8f),
                                        y = rowHeight * dist.row
                                    )
                                    .size(colWidth, rowHeight)
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF991B1B).copy(alpha = 0.92f),
                                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(dist.type.icon, fontSize = 22.sp)
                                        Text(
                                            text = dist.type.title,
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            maxLines = 1
                                        )
                                        val hpPercent = (dist.hp.toFloat() / dist.type.maxHp.toFloat()).coerceIn(0f, 1f)
                                        LinearProgressIndicator(
                                            progress = { hpPercent },
                                            modifier = Modifier
                                                .fillMaxWidth(0.8f)
                                                .height(3.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = Color(0xFFFBBF24),
                                            trackColor = Color(0xFF450A0A)
                                        )
                                    }
                                }
                            }
                        }

                        // Proyectiles de Argumentos
                        state.arguments.forEach { arg ->
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = colWidth * arg.x,
                                        y = rowHeight * arg.row + (rowHeight / 2) - 8.dp
                                    )
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8))
                                    .border(1.5.dp, Color.White, CircleShape)
                            )
                        }

                        // Gotas de Confianza (Tocar o esperar 3.5s para auto-recolección)
                        state.confidenceDrops.forEach { drop ->
                            Box(
                                modifier = Modifier
                                    .offset(
                                        x = colWidth * drop.x.coerceIn(0f, (cols - 1).toFloat()),
                                        y = rowHeight * drop.y.coerceIn(0f, (rows - 1).toFloat())
                                    )
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFBBF24))
                                    .border(2.dp, Color.White, CircleShape)
                                    .clickable { viewModel.collectConfidence(drop.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💡", fontSize = 22.sp)
                            }
                        }
                    }
                }
            }

            if (state.isLevelComplete || state.isGameOver) {
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
                        border = BorderStroke(
                            1.5.dp,
                            if (state.isLevelComplete) Color(0xFF10B981) else Color(0xFFEF4444)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(if (state.isLevelComplete) "🛡️" else "⚠️", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (state.isLevelComplete) "¡Oleada ${state.wave} Superada!" else "¡El Pánico Rompió tu Concentración!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Puntuación: ${state.score} pts",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBBF24)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    if (state.isLevelComplete) viewModel.nextLevel() else viewModel.startGame()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isLevelComplete) Color(0xFF10B981) else Color(0xFF38BDF8)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (state.isLevelComplete) "Siguiente Oleada (${state.wave + 1})" else "Reintentar Defensa",
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
