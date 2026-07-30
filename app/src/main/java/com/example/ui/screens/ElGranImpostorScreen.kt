package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class GamePhase {
    SETUP, PASS_PHONE, REVEAL_ROLE, SPEAKING, VOTING, REVEAL_IMPOSTOR
}

val seriousTopics = listOf(
    "La importancia de la inteligencia artificial",
    "El impacto del cambio climático en la economía",
    "El futuro de la exploración espacial",
    "La evolución de la medicina moderna",
    "El valor de la ética en la sociedad",
    "La importancia de la educación financiera",
    "El impacto de las redes sociales en la salud mental"
)

val impostorTopics = listOf(
    "Hacer arepas",
    "Bañar a un perro que no quiere bañarse",
    "Comer sopa con tenedor",
    "Dormir una siesta después del almuerzo",
    "Bailar reguetón solo en el cuarto",
    "Lidiar con una mosca en la habitación",
    "Hacer fila en el supermercado"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElGranImpostorScreen(onBack: () -> Unit) {
    var phase by remember { mutableStateOf(GamePhase.SETUP) }
    var numPlayers by remember { mutableIntStateOf(4) }
    
    var seriousTopic by remember { mutableStateOf("") }
    var impostorTopic by remember { mutableStateOf("") }
    var impostorIndex by remember { mutableIntStateOf(0) }
    
    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    
    var timeLeft by remember { mutableIntStateOf(45) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning, timeLeft) {
        if (isTimerRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0) {
            isTimerRunning = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("El Gran Impostor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212),
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
                .background(Color(0xFF121212))
        ) {
            AnimatedContent(
                targetState = phase,
                transitionSpec = {
                    fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
                },
                label = "game_phase"
            ) { currentPhase ->
                when (currentPhase) {
                    GamePhase.SETUP -> SetupPhase(
                        numPlayers = numPlayers,
                        onNumPlayersChange = { numPlayers = it },
                        onStart = {
                            seriousTopic = seriousTopics.random()
                            impostorTopic = impostorTopics.random()
                            impostorIndex = Random.nextInt(numPlayers)
                            currentPlayerIndex = 0
                            phase = GamePhase.PASS_PHONE
                        }
                    )
                    GamePhase.PASS_PHONE -> PassPhonePhase(
                        playerNumber = currentPlayerIndex + 1,
                        onReveal = { phase = GamePhase.REVEAL_ROLE }
                    )
                    GamePhase.REVEAL_ROLE -> RevealRolePhase(
                        playerNumber = currentPlayerIndex + 1,
                        isImpostor = currentPlayerIndex == impostorIndex,
                        topic = if (currentPlayerIndex == impostorIndex) impostorTopic else seriousTopic,
                        onNext = {
                            if (currentPlayerIndex < numPlayers - 1) {
                                currentPlayerIndex++
                                phase = GamePhase.PASS_PHONE
                            } else {
                                currentPlayerIndex = 0
                                phase = GamePhase.SPEAKING
                            }
                        }
                    )
                    GamePhase.SPEAKING -> SpeakingPhase(
                        playerNumber = currentPlayerIndex + 1,
                        timeLeft = timeLeft,
                        isTimerRunning = isTimerRunning,
                        onToggleTimer = { isTimerRunning = !isTimerRunning },
                        onNextPlayer = {
                            if (currentPlayerIndex < numPlayers - 1) {
                                currentPlayerIndex++
                                timeLeft = 45
                                isTimerRunning = false
                            } else {
                                phase = GamePhase.VOTING
                            }
                        }
                    )
                    GamePhase.VOTING -> VotingPhase(
                        onReveal = { phase = GamePhase.REVEAL_IMPOSTOR }
                    )
                    GamePhase.REVEAL_IMPOSTOR -> RevealImpostorPhase(
                        impostorNumber = impostorIndex + 1,
                        seriousTopic = seriousTopic,
                        impostorTopic = impostorTopic,
                        onRestart = {
                            phase = GamePhase.SETUP
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SetupPhase(numPlayers: Int, onNumPlayersChange: (Int) -> Unit, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = Color(0xFFFF5252),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "El Gran Impostor",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Un jugador recibirá un tema ridículo. Los demás un tema serio. Da un discurso de 45 segundos usando metáforas y subtexto para que no te descubran.",
            fontSize = 16.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "Número de Jugadores",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { if (numPlayers > 3) onNumPlayersChange(numPlayers - 1) },
                modifier = Modifier.background(Color(0xFF333333), CircleShape)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Menos", tint = Color.White)
            }
            Text(
                text = "$numPlayers",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            IconButton(
                onClick = { if (numPlayers < 10) onNumPlayersChange(numPlayers + 1) },
                modifier = Modifier.background(Color(0xFF333333), CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Más", tint = Color.White)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Comenzar Juego", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun PassPhonePhase(playerNumber: Int, onReveal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.VisibilityOff,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Pasa el dispositivo al",
            fontSize = 20.sp,
            color = Color.LightGray
        )
        Text(
            text = "Jugador $playerNumber",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onReveal,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Soy el Jugador $playerNumber - Ver Rol", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun RevealRolePhase(playerNumber: Int, isImpostor: Boolean, topic: String, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Jugador $playerNumber",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isImpostor) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color(0xFFFF5252),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡ERES EL IMPOSTOR!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFFF5252),
                textAlign = TextAlign.Center
            )
        } else {
            Icon(
                imageVector = Icons.Default.RecordVoiceOver,
                contentDescription = null,
                tint = Color(0xFF2196F3),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ERES UN ORADOR NORMAL",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF2196F3),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Tu tema para el discurso es:",
            fontSize = 18.sp,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .background(Color(0xFF222222), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Text(
                text = topic,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Ocultar Rol y Continuar", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun SpeakingPhase(
    playerNumber: Int, 
    timeLeft: Int, 
    isTimerRunning: Boolean, 
    onToggleTimer: () -> Unit,
    onNextPlayer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Turno de hablar",
            fontSize = 20.sp,
            color = Color.LightGray
        )
        Text(
            text = "Jugador $playerNumber",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        // Timer circle
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    if (timeLeft <= 10) Color(0xFFFF5252).copy(alpha = 0.2f) else Color(0xFF2196F3).copy(alpha = 0.2f),
                    CircleShape
                )
                .clip(CircleShape)
                .clickable { onToggleTimer() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$timeLeft",
                fontSize = 80.sp,
                fontWeight = FontWeight.Black,
                color = if (timeLeft <= 10) Color(0xFFFF5252) else Color.White
            )
            if (!isTimerRunning && timeLeft > 0) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(100.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = onToggleTimer,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(if (isTimerRunning) "Pausar" else "Iniciar", fontSize = 18.sp)
            }
            
            Button(
                onClick = onNextPlayer,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .padding(start = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Siguiente", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun VotingPhase(onReveal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.HowToVote,
            contentDescription = null,
            tint = Color(0xFFFFC107),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "¡Hora de Votar!",
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "A la cuenta de 3, todos deben señalar al jugador que creen que es el impostor.",
            fontSize = 20.sp,
            color = Color.LightGray,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(80.dp))
        Button(
            onClick = onReveal,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Revelar Resultados", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun RevealImpostorPhase(impostorNumber: Int, seriousTopic: String, impostorTopic: String, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "EL IMPOSTOR ERA...",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Jugador $impostorNumber!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFFFF5252),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tema Serio:", color = Color.LightGray, fontSize = 16.sp)
                Text(seriousTopic, color = Color(0xFF2196F3), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text("Tema del Impostor:", color = Color.LightGray, fontSize = 16.sp)
                Text(impostorTopic, color = Color(0xFFFF5252), fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Jugar de Nuevo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
