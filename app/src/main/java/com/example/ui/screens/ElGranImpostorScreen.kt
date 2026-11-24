package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    "La importancia de la inteligencia artificial en el trabajo",
    "El impacto del liderazgo empático en las empresas",
    "El futuro de la exploración espacial humana",
    "El valor de la educación financiera desde jóvenes",
    "Cómo superar el miedo al cambio profesional",
    "El impacto de la lectura diaria en el cerebro",
    "La ética en la tecnología moderna"
)

val impostorTopics = listOf(
    "Preparar arepas sin que se quemen",
    "Bañar a un gato que odia el agua",
    "Comer sopa caliente con tenedor",
    "Dormir una siesta a escondidas en la oficina",
    "Bailar reguetón frente al espejo",
    "Atrapar un mosquito a las 3 de la mañana",
    "Sobrevivir a una fila eterna en el banco"
)

private val rhetoricalChallenges = listOf(
    "Debes empezar tu intervención con una Pregunta Retórica.",
    "Debes incluir una Metáfora en tu argumento.",
    "Debes hacer una Pausa Dramática de 2 segundos a mitad de tu discurso.",
    "Debes cerrar tu intervención con un Llamado a la Acción.",
    "Debes usar una estructura de 3 puntos (Tricolon)."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElGranImpostorScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    var phase by remember { mutableStateOf(GamePhase.SETUP) }
    var numPlayers by remember { mutableIntStateOf(4) }
    var speechDuration by remember { mutableIntStateOf(45) }

    var seriousTopic by remember { mutableStateOf("") }
    var impostorTopic by remember { mutableStateOf("") }
    var roundConstraint by remember { mutableStateOf("") }
    var impostorIndex by remember { mutableIntStateOf(0) }

    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    var votedPlayerIndex by remember { mutableStateOf<Int?>(null) }

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
                title = {
                    Column {
                        Text("El Gran Impostor Retórico", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            "Subtexto, Metáforas y Persuasión en Grupo",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
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
            AnimatedContent(
                targetState = phase,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "game_phase"
            ) { currentPhase ->
                when (currentPhase) {
                    GamePhase.SETUP -> SetupPhase(
                        numPlayers = numPlayers,
                        onNumPlayersChange = { numPlayers = it },
                        speechDuration = speechDuration,
                        onDurationChange = { speechDuration = it },
                        onStart = {
                            seriousTopic = seriousTopics.random()
                            impostorTopic = impostorTopics.random()
                            roundConstraint = rhetoricalChallenges.random()
                            impostorIndex = Random.nextInt(numPlayers)
                            currentPlayerIndex = 0
                            votedPlayerIndex = null
                            timeLeft = speechDuration
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
                        constraint = roundConstraint,
                        onNext = {
                            if (currentPlayerIndex < numPlayers - 1) {
                                currentPlayerIndex++
                                phase = GamePhase.PASS_PHONE
                            } else {
                                currentPlayerIndex = 0
                                timeLeft = speechDuration
                                isTimerRunning = false
                                phase = GamePhase.SPEAKING
                            }
                        }
                    )
                    GamePhase.SPEAKING -> SpeakingPhase(
                        playerNumber = currentPlayerIndex + 1,
                        totalPlayers = numPlayers,
                        constraint = roundConstraint,
                        timeLeft = timeLeft,
                        isTimerRunning = isTimerRunning,
                        onToggleTimer = { isTimerRunning = !isTimerRunning },
                        onNextPlayer = {
                            if (currentPlayerIndex < numPlayers - 1) {
                                currentPlayerIndex++
                                timeLeft = speechDuration
                                isTimerRunning = false
                            } else {
                                isTimerRunning = false
                                phase = GamePhase.VOTING
                            }
                        }
                    )
                    GamePhase.VOTING -> VotingPhase(
                        numPlayers = numPlayers,
                        votedPlayerIndex = votedPlayerIndex,
                        onSelectSuspect = { votedPlayerIndex = it },
                        onReveal = { phase = GamePhase.REVEAL_IMPOSTOR }
                    )
                    GamePhase.REVEAL_IMPOSTOR -> RevealImpostorPhase(
                        impostorNumber = impostorIndex + 1,
                        votedNumber = votedPlayerIndex?.plus(1),
                        seriousTopic = seriousTopic,
                        impostorTopic = impostorTopic,
                        onRestart = { phase = GamePhase.SETUP }
                    )
                }
            }
        }
    }
}

@Composable
fun SetupPhase(
    numPlayers: Int,
    onNumPlayersChange: (Int) -> Unit,
    speechDuration: Int,
    onDurationChange: (Int) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFEF4444).copy(alpha = 0.2f),
            modifier = Modifier.size(88.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(0xFFF87171),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Text(
            text = "El Gran Impostor",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Todos los oradores reciben un tema serio excepto el Impostor, que recibe un tema absurdo. Cada jugador debe defender su tema usando metáforas sin ser descubierto.",
            fontSize = 14.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NÚMERO DE ORADORES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (numPlayers > 3) onNumPlayersChange(numPlayers - 1) },
                        modifier = Modifier.background(Color(0xFF0F172A), CircleShape)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Menos", tint = Color.White)
                    }
                    Text(
                        text = "$numPlayers",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                    IconButton(
                        onClick = { if (numPlayers < 10) onNumPlayersChange(numPlayers + 1) },
                        modifier = Modifier.background(Color(0xFF0F172A), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Más", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "TIEMPO POR INTERVENCIÓN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(30, 45, 60).forEach { sec ->
                        val isSel = speechDuration == sec
                        OutlinedButton(
                            onClick = { onDurationChange(sec) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSel) Color(0xFFEF4444).copy(alpha = 0.2f) else Color.Transparent,
                                contentColor = if (isSel) Color(0xFFF87171) else Color(0xFF94A3B8)
                            ),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFFF87171) else Color(0xFF334155)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("${sec}s", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Repartir Roles Secretos", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
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
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Entrega el teléfono en privado al",
            fontSize = 18.sp,
            color = Color(0xFFCBD5E1)
        )
        Text(
            text = "Orador $playerNumber",
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onReveal,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Soy el Orador $playerNumber • Ver mi Tema", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}

@Composable
fun RevealRolePhase(
    playerNumber: Int,
    isImpostor: Boolean,
    topic: String,
    constraint: String,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = if (isImpostor) Color(0xFF7F1D1D) else Color(0xFF1E3A8A),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isImpostor) "🕵️ ROL SECRETO: IMPOSTOR RETÓRICO" else "🎙️ ROL: ORADOR OFICIAL",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(2.dp, if (isImpostor) Color(0xFFEF4444) else Color(0xFF38BDF8)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TU TEMA ASIGNADO ES:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "«$topic»",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🎯 Regla de la Ronda: $constraint",
                        color = Color(0xFFFBBF24),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))
        Button(
            onClick = onNext,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Ocultar mi Rol y Continuar", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}

@Composable
fun SpeakingPhase(
    playerNumber: Int,
    totalPlayers: Int,
    constraint: String,
    timeLeft: Int,
    isTimerRunning: Boolean,
    onToggleTimer: () -> Unit,
    onNextPlayer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TURNO EN EL ESCENARIO ($playerNumber DE $totalPlayers)",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF38BDF8)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Orador $playerNumber",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.5f))
            ) {
                Text(
                    text = "🎯 Recuerda: $constraint",
                    color = Color(0xFFFBBF24),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(
                    if (timeLeft <= 10) Color(0xFFEF4444).copy(alpha = 0.2f)
                    else Color(0xFF38BDF8).copy(alpha = 0.15f)
                )
                .clickable { onToggleTimer() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${timeLeft}s",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Black,
                    color = if (timeLeft <= 10) Color(0xFFF87171) else Color.White
                )
                Text(
                    text = if (isTimerRunning) "Toca para pausar" else "Toca para iniciar",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onToggleTimer,
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8))
            ) {
                Text(if (isTimerRunning) "Pausar" else "Iniciar Reloj", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNextPlayer,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    if (playerNumber < totalPlayers) "Siguiente Orador" else "Ir a Votación",
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
fun VotingPhase(
    numPlayers: Int,
    votedPlayerIndex: Int?,
    onSelectSuspect: (Int) -> Unit,
    onReveal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = Icons.Default.HowToVote,
            contentDescription = null,
            tint = Color(0xFFFBBF24),
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = "¡Deliberación del Jurado!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Selecciona al orador que el grupo considera que estaba defendiendo el tema absurdo:",
            fontSize = 14.sp,
            color = Color(0xFFCBD5E1),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (idx in 0 until numPlayers) {
                val isSelected = votedPlayerIndex == idx
                Surface(
                    onClick = { onSelectSuspect(idx) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFFEF4444).copy(alpha = 0.25f) else Color(0xFF1E293B),
                    border = BorderStroke(
                        1.5.dp,
                        if (isSelected) Color(0xFFF87171) else Color(0xFF334155)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Orador ${idx + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        if (isSelected) {
                            Text("SOSPECHOSO ELEGIDO", color = Color(0xFFF87171), fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onReveal,
            enabled = votedPlayerIndex != null,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Revelar Verdad", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun RevealImpostorPhase(
    impostorNumber: Int,
    votedNumber: Int?,
    seriousTopic: String,
    impostorTopic: String,
    onRestart: () -> Unit
) {
    val groupWon = votedNumber == impostorNumber
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (groupWon) "🕵️‍♂️" else "🎭", fontSize = 54.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (groupWon) "¡El Público Descubrió al Impostor!" else "¡El Impostor Persuadió a Todos!",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (groupWon) Color(0xFF4ADE80) else Color(0xFFF87171),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "El Impostor era el Orador $impostorNumber",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.dp, Color(0xFF334155)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("TEMA OFICIAL DE LA SALA:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(seriousTopic, color = Color(0xFF38BDF8), fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(18.dp))

                Text("TEMA SECRETO DEL IMPOSTOR:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(impostorTopic, color = Color(0xFFF87171), fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onRestart,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Jugar Nueva Partida", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}
