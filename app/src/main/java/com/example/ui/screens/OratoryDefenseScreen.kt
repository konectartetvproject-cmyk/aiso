package com.example.ui.screens

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
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Confianza: ${state.confidence} ☀️",
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

                // Plant Selection
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
                                Text("${type.cost}☀️", fontSize = 10.sp, color = Color(0xFFF57F17), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Game Board
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
                                    Text("🎤", fontSize = 32.sp)
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
                                Text("☀️", fontSize = 24.sp)
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

}
