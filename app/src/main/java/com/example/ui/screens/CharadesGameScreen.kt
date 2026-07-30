package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val charadesWords = listOf(
    "Alegría", "Sorpresa", "Enojo", "Miedo", "Confusión",
    "Liderazgo", "Confianza", "Nerviosismo", "Duda", "Aburrimiento",
    "Interés", "Concentración", "Impaciencia", "Tristeza", "Empatía",
    "Preocupación", "Seguridad", "Timidez", "Entusiasmo", "Agresividad",
    "Desesperación", "Esperanza", "Culpa", "Orgullo", "Vergüenza",
    "Celos", "Envidia", "Admiración", "Desprecio", "Asco",
    "Amor", "Odio", "Compasión", "Apatía", "Euforia",
    "Nostalgia", "Melancolía", "Ansiedad", "Alivio", "Satisfacción"
)

val charadesConstraints = listOf(
    "Clásico (Libre) ✨",
    "Sin usar las manos 🚫👐",
    "Solo con expresiones faciales 😲",
    "Como si estuvieras en cámara lenta 🐌",
    "Como un robot 🤖",
    "Haciendo mímica exagerada 🎭",
    "Sin mover los pies 👣",
    "Con un brazo detrás de la espalda 🧍",
    "Como si pesaras 1000 kg 🐘",
    "Como si flotaras en el espacio 🚀",
    "Solo moviendo los ojos y cejas 🤨",
    "Como si tuvieras mucho frío 🥶",
    "Como si estuvieras bajo el agua 🌊",
    "A la máxima velocidad posible ⚡",
    "De espaldas (solo se ve tu postura) 👤"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharadesGameScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf("start") } // "start", "playing", "finished"
    var score by remember { mutableStateOf(0) }
    var currentWord by remember { mutableStateOf("") }
    var currentConstraint by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(60) }

    LaunchedEffect(gameState, timeLeft) {
        if (gameState == "playing" && timeLeft > 0) {
            delay(1000)
            timeLeft--
        } else if (timeLeft == 0 && gameState == "playing") {
            gameState = "finished"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Charadas: Lenguaje Corporal", fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when (gameState) {
                "start" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "Juego de Charadas",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Practica tu lenguaje corporal. Tendrás 60 segundos para actuar la mayor cantidad de emociones o palabras posibles sin hablar, siguiendo la instrucción extra en pantalla.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                score = 0
                                timeLeft = 60
                                currentWord = charadesWords.random()
                                currentConstraint = charadesConstraints.random()
                                gameState = "playing"
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("¡Comenzar!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                "playing" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Puntuación: $score",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Tiempo: ${timeLeft}s",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (timeLeft <= 10) Color.Red else MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(64.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = currentWord,
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = currentConstraint,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(64.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    currentWord = charadesWords.random()
                                    currentConstraint = charadesConstraints.random()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Text("Pasar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    score++
                                    currentWord = charadesWords.random()
                                    currentConstraint = charadesConstraints.random()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier.weight(1f).height(56.dp)
                            ) {
                                Text("¡Correcto!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                "finished" -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "¡Tiempo terminado!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Lograste transmitir:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "$score emociones",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                score = 0
                                timeLeft = 60
                                currentWord = charadesWords.random()
                                currentConstraint = charadesConstraints.random()
                                gameState = "playing"
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("Jugar de nuevo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("Volver al menú", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
