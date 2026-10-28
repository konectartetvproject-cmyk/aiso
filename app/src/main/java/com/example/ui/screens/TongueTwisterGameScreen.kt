package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
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

data class TongueTwister(val title: String, val text: String)

val tongueTwistersList = listOf(
    TongueTwister("Turbulenta Travesía en el Tranvía Tropical", "Tomás, el temerario taxidermista, transporta tigres tropicales hacia Toledo en un tren turístico. Tres trineos tricoloridos tiran tranquilamente, tropezando tercamente sobre terrenos traicioneros. Testigos temblorosos transmiten tragedias travesía tras travesía. Tardamos tres tediosas tardes tomando té tumultuoso en tumultuosos trayectos."),
    TongueTwister("Desafío de Don Quijote", "Don Quijote, el caballero que caza quimeras, cayó cual cometa que choca contra cipreses. Con coraje, cabalga con cojera, corriendo contra cascadas con cascabeles crujientes. \"¡Quisiera quejarme!\", quiso decir, quitándose quijadas quintas, aunque quedó quieto, quieto quedó, quedamente quebrado."),
    TongueTwister("Sinfonía Sibilante de Siete Serpientes", "Siete serpientes sisean sinfonías susurrantes. Sus escamas suenan, susurrando secretos, sin cesar, sin sentido, sin sollozos. Sigilosas, se deslizan sobre suelos sombríos, sintiendo susurros, sintiendo susurros."),
    TongueTwister("El Extraño Enigma de la Excéntrica Esfinge", "En el exuberante Egipto, existe una esfinge excéntrica. Exigiendo explicaciones exquisitas, examina a expedicionarios exhaustos. Exasperada, exhaló exabruptos, exponiendo enigmas extraordinarios, exhortando enérgicamente esforzados enigmas. Exuberante, extravió expedicionarios, exclamando: \"¡Explícame el enigma, excelso explorador!\""),
    TongueTwister("El Ermitaño Espectador en el Estanque Esmeralda", "En el estanque esmeralda, el ermitaño elige escribir epopeyas. Escuchando el eco, escribe extensos escritos sobre espectros espectaculares. El espejismo esmeralda es el escenario, donde emerge el espectro, entonando ecos etéreos, embelesando a los espectadores estupefactos."),
    TongueTwister("Rufino, el Rafio Rojo", "Rufino, el rafio rojo, roza risueñamente ramas resplandecientes. Raudo, recoge racimos relucientes, resonando risas revoltosas. Rítmicamente, Rufino, el rafio rojo, recrea relojes rocambolescos, revelando reseñas rutilantes. Regocijado, Rufino relata relatos rocambolescos, repletos de resonancias rítmicas."),
    TongueTwister("Silvana, la Selvática Serpiente", "Silvana, la selvática serpiente, serpentea sigilosamente sobre suelo selvoso. Sintiendo su ser tan singular, suspira sonidos sibilantes, sorprendiendo sapos sosegados. Selecciona sabiamente su sendero, sorteando sutilmente suculentos saltamontes. Silvana, la selvática serpiente, susurra secretos solemnes, seduciendo a su séquito simpatizante."),
    TongueTwister("Aurora, la Astrónoma Avanzada", "Aurora, la astrónoma avanzada, ajusta ávidamente antiguos artilugios astronómicos. Asombrada, analiza arcanos astros arremolinados, anticipando anomalías astrales. Ágilmente, Aurora atisba áureos anillos astronómicos, acentuando asombrosas anomalías atmosféricas. Aventurera, Aurora alcanza alturas astronómicas, absorbiendo asombrosas auroras astrales."),
    TongueTwister("Quintín, el Químico Quirúrgico", "Quintín, el químico quirúrgico, quiebra quietamente cuantiosos químicos quirúrgicos. Quemando querubines quirúrgicos, Quintín cuestiona cúmulos químicos. Quedamente, Quintín quiebra quintales, quitando cuantiosas cucharas quirúrgicas. Quedo, Quintín queda quieto, cuestionando cúmulos químicos.")
)

val readingInstructionsList = listOf(
    "Léelo alegre 😀",
    "Léelo triste 😢",
    "Léelo molesto 😡",
    "Léelo con asco 🤢",
    "Léelo con seducción 😏",
    "Léelo como si estuvieras cantando 🎶",
    "Escenifica la voz de un personaje 🎭",
    "Utiliza el acento de algún país 🌍",
    "Léelo más rápido ⏩",
    "Léelo más lento ⏪",
    "Aumenta el volumen 📢",
    "Disminuye el volumen 🤫"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TongueTwisterGameScreen(
    onBack: () -> Unit,
    viewModel: com.example.viewmodel.TongueTwisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentTwister by remember { mutableStateOf(tongueTwistersList.random()) }
    var currentInstruction by remember { mutableStateOf(readingInstructionsList.random()) }
    val isRecording by viewModel.isRecording.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val error by viewModel.error.collectAsState()

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.startRecording(context)
            } else {
                android.widget.Toast.makeText(context, "Se requiere permiso de micrófono para grabar.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trabalenguas", fontWeight = FontWeight.Bold) },
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
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Práctica de Dicción",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lee el siguiente trabalenguas en voz alta, intentando no equivocarte y aumentando la velocidad poco a poco.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Instrucción: $currentInstruction",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentTwister.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentTwister.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text("Analizando tu audio...", color = MaterialTheme.colorScheme.primary)
                } else if (feedback != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Retroalimentación (Gemini AI)", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            Spacer(Modifier.height(8.dp))
                            Text(feedback!!, color = Color(0xFF1B5E20))
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            if (isRecording) {
                                viewModel.stopRecordingAndAnalyze(currentTwister.text, currentInstruction)
                            } else {
                                if (androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.RECORD_AUDIO
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                ) {
                                    viewModel.startRecording(context)
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Detener" else "Grabar",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (isRecording) "Grabando... Toca para detener" else "Toca para grabar tu voz",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        var nextTwister = tongueTwistersList.random()
                        while (nextTwister == currentTwister) {
                            nextTwister = tongueTwistersList.random()
                        }
                        currentTwister = nextTwister
                        currentInstruction = readingInstructionsList.random()
                        viewModel.clearFeedback()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Siguiente Trabalenguas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
