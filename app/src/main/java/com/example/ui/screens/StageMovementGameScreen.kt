package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

data class StagePoint(val id: Int, val name: String)

val stagePoints = listOf(
    StagePoint(0, "Atrás Derecha"),
    StagePoint(1, "Atrás Centro"),
    StagePoint(2, "Atrás Izquierda"),
    StagePoint(3, "Centro Derecha"),
    StagePoint(4, "Centro"),
    StagePoint(5, "Centro Izquierda"),
    StagePoint(6, "Frente Derecha"),
    StagePoint(7, "Frente Centro"),
    StagePoint(8, "Frente Izquierda")
)

data class StrategicIntent(val description: String, val validPoints: List<Int>)

val strategicIntents = listOf(
    StrategicIntent("Tener una perspectiva más amplia del público", listOf(0, 1, 2)),
    StrategicIntent("Lograr mayor conexión con tu mensaje", listOf(3, 4, 5)),
    StrategicIntent("Interactuar de cerca con el público", listOf(6, 7, 8)),
    StrategicIntent("Conectar con el sector izquierdo del público", listOf(0, 3, 6)),
    StrategicIntent("Conectar con el sector derecho del público", listOf(2, 5, 8))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StageMovementGameScreen(
    onBack: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var currentSpeakerPosition by remember { mutableIntStateOf(4) } // Start at center
    
    var currentLevel by remember { mutableIntStateOf(1) }
    var targetPoint by remember { mutableStateOf(stagePoints.random()) }
    var targetIntent by remember { mutableStateOf(strategicIntents.random()) }
    
    var showReward by remember { mutableStateOf(false) }
    var showIntro by remember { mutableStateOf(true) }
    var showLevel2Intro by remember { mutableStateOf(false) }
    
    // Make sure target is not the same as current position
    LaunchedEffect(targetPoint, currentLevel) {
        if (currentLevel == 1 && targetPoint.id == currentSpeakerPosition) {
            var newTarget = stagePoints.random()
            while (newTarget.id == currentSpeakerPosition) {
                newTarget = stagePoints.random()
            }
            targetPoint = newTarget
        }
    }
    
    LaunchedEffect(targetIntent, currentLevel) {
        if (currentLevel == 2 && targetIntent.validPoints.contains(currentSpeakerPosition)) {
            var newTarget = strategicIntents.random()
            while (newTarget.validPoints.contains(currentSpeakerPosition)) {
                newTarget = strategicIntents.random()
            }
            targetIntent = newTarget
        }
    }

    if (showIntro) {
        AlertDialog(
            onDismissRequest = { showIntro = false },
            title = { Text("¡Importante!", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Cuando hablamos en público, nuestra derecha es la izquierda del público, y viceversa.\n\nPor lo tanto, en este ejercicio las indicaciones (Izquierda/Derecha) están desde la perspectiva del orador.",
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showIntro = false }) {
                    Text("Entendido, ¡A Jugar!")
                }
            }
        )
    }

    if (showLevel2Intro) {
        AlertDialog(
            onDismissRequest = { showLevel2Intro = false },
            title = { Text("Nivel 2: Estrategia", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Coloca al orador según lo que busques comunicar:", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Atrás (1, 2, 3): Para tener una perspectiva más amplia del público.")
                    Text("• Centro (4, 5, 6): Para mayor conexión con tu mensaje.")
                    Text("• Adelante (7, 8, 9): Para interactuar con el público.")
                    Text("• Tu Derecha (1, 4, 7): Para conectar con el sector izquierdo del público.")
                    Text("• Tu Izquierda (3, 6, 9): Para conectar con el sector derecho del público.")
                }
            },
            confirmButton = {
                Button(onClick = { showLevel2Intro = false }) {
                    Text("Comenzar Nivel 2")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Desplazamiento", fontWeight = FontWeight.Bold) },
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
            // Level switch
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                SegmentedButton(
                    selected = currentLevel == 1,
                    onClick = { currentLevel = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Nivel 1: Puntos")
                }
                SegmentedButton(
                    selected = currentLevel == 2,
                    onClick = { 
                        if (currentLevel != 2) {
                            currentLevel = 2
                            showLevel2Intro = true
                        }
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Nivel 2: Estrategia")
                }
            }

            // Header stats
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
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Racha",
                        tint = if (streak > 0) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "x$streak",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (streak > 0) Color(0xFFFFC107) else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Objective
            Text(
                text = if (currentLevel == 1) "Mueve al orador hacia:" else "Buscas comunicar:",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = if (currentLevel == 1) targetPoint.name else targetIntent.description,
                    style = if (currentLevel == 1) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Stage Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(4.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (col in 0..2) {
                                val pointId = row * 3 + col
                                val isCurrentPosition = pointId == currentSpeakerPosition
                                val scale by animateFloatAsState(if (isCurrentPosition) 1.2f else 1.0f)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrentPosition) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                        .clickable {
                                            if (pointId != currentSpeakerPosition) {
                                                currentSpeakerPosition = pointId
                                                
                                                val isCorrect = if (currentLevel == 1) {
                                                    pointId == targetPoint.id
                                                } else {
                                                    targetIntent.validPoints.contains(pointId)
                                                }

                                                if (isCorrect) {
                                                    score += 10 + (streak * 5)
                                                    streak++
                                                    showReward = true
                                                    
                                                    // Set new target
                                                    if (currentLevel == 1) {
                                                        var newTarget = stagePoints.random()
                                                        while (newTarget.id == currentSpeakerPosition) {
                                                            newTarget = stagePoints.random()
                                                        }
                                                        targetPoint = newTarget
                                                    } else {
                                                        var newTarget = strategicIntents.random()
                                                        while (newTarget.validPoints.contains(currentSpeakerPosition)) {
                                                            newTarget = strategicIntents.random()
                                                        }
                                                        targetIntent = newTarget
                                                    }
                                                } else {
                                                    streak = 0
                                                }
                                            }
                                        }
                                        .border(2.dp, if (isCurrentPosition) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCurrentPosition) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                                            contentDescription = "Orador",
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .scale(scale)
                                        )
                                    } else {
                                        Text(
                                            text = "${pointId + 1}",
                                            color = Color.Gray.copy(alpha = 0.5f),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Público",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.outline
            )

            if (showReward) {
                LaunchedEffect(Unit) {
                    delay(1000)
                    showReward = false
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡Excelente desplazamiento!",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
