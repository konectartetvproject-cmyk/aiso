import os

screens = {}

screens["app/src/main/java/com/example/ui/screens/SpeechTimerScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import com.example.viewmodel.SpeechPhase
import com.example.viewmodel.SpeechTimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeechTimerScreen(
    onBack: () -> Unit,
    viewModel: SpeechTimerViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val totalTimeMinutes by viewModel.totalTimeMinutes.collectAsState()
    val remainingTimeSeconds by viewModel.remainingTimeSeconds.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val currentPhase by viewModel.currentPhase.collectAsState()
    val currentHint by viewModel.currentHint.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronómetro de Discurso", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Configura el tiempo de tu discurso y practica tu ritmo.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Duración: ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = totalTimeMinutes.toFloat(),
                    onValueChange = { viewModel.setTotalTime(it.toInt()) },
                    valueRange = 3f..30f,
                    steps = 26,
                    enabled = currentPhase == SpeechPhase.NOT_STARTED,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("$totalTimeMinutes min", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = when (currentPhase) {
                        SpeechPhase.INTRO -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        SpeechPhase.DEVELOPMENT -> MaterialTheme.colorScheme.primaryContainer
                        SpeechPhase.CONCLUSION -> Color(0xFFFF9800).copy(alpha = 0.2f)
                        SpeechPhase.FINISHED -> Color(0xFFF44336).copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val minutes = remainingTimeSeconds / 60
                        val seconds = remainingTimeSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontSize = 72.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val phaseText = when (currentPhase) {
                            SpeechPhase.NOT_STARTED -> "Listo para iniciar"
                            SpeechPhase.INTRO -> "Inicio (1 min)"
                            SpeechPhase.DEVELOPMENT -> "Desarrollo"
                            SpeechPhase.CONCLUSION -> "Conclusión (1 min)"
                            SpeechPhase.FINISHED -> "¡Tiempo Terminado!"
                        }
                        
                        Text(
                            text = phaseText,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentHint != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentHint!!,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp).fillMaxWidth()
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reiniciar",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FloatingActionButton(
                    onClick = { viewModel.toggleTimer() },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pausar" else "Iniciar",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/NormalTimerScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NormalTimerScreen(
    onBack: () -> Unit
) {
    var elapsedSeconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronómetro Normal", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                shape = CircleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    val minutes = elapsedSeconds / 60
                    val seconds = elapsedSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isRunning = false
                        elapsedSeconds = 0
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reiniciar",
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FloatingActionButton(
                    onClick = { isRunning = !isRunning },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pausar" else "Iniciar",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/DebateTimerScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebateTimerScreen(
    onBack: () -> Unit
) {
    var teamASeconds by remember { mutableStateOf(180) }
    var teamBSeconds by remember { mutableStateOf(180) }
    var activeTeam by remember { mutableStateOf(0) }

    LaunchedEffect(activeTeam) {
        while (activeTeam != 0) {
            delay(1000L)
            if (activeTeam == 1 && teamASeconds > 0) {
                teamASeconds--
            } else if (activeTeam == 2 && teamBSeconds > 0) {
                teamBSeconds--
            } else {
                activeTeam = 0
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronómetro de Debate", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        activeTeam = 0
                        teamASeconds = 180
                        teamBSeconds = 180
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reiniciar")
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
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (activeTeam == 2) Color(0xFFFF9800) else Color(0xFFFF9800).copy(alpha = 0.3f))
                    .clickable { activeTeam = if (activeTeam == 2) 0 else 2 },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Equipo B", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    val minutesB = teamBSeconds / 60
                    val secondsB = teamBSeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutesB, secondsB),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activeTeam = 0 }) {
                    Icon(if (activeTeam == 0) Icons.Default.PlayArrow else Icons.Default.Pause, contentDescription = "Pausar", modifier = Modifier.size(32.dp))
                }
            }
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (activeTeam == 1) Color(0xFF2196F3) else Color(0xFF2196F3).copy(alpha = 0.3f))
                    .clickable { activeTeam = if (activeTeam == 1) 0 else 1 },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Equipo A", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    val minutesA = teamASeconds / 60
                    val secondsA = teamASeconds % 60
                    Text(
                        text = String.format("%02d:%02d", minutesA, secondsA),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/TimersListScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimersListScreen(
    onNavigateToSpeechTimer: () -> Unit,
    onNavigateToNormalTimer: () -> Unit,
    onNavigateToDebateTimer: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cronómetros", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Controla tus tiempos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Selecciona el tipo de cronómetro que necesitas para tu práctica.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                TimerOptionCard(
                    title = "Cronómetro de Discurso",
                    description = "Configura la duración y recibe avisos de introducción y conclusión.",
                    icon = Icons.Default.Timer,
                    onClick = onNavigateToSpeechTimer
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                TimerOptionCard(
                    title = "Cronómetro Normal",
                    description = "Un cronómetro básico para medir cualquier tipo de actividad (cuenta progresiva).",
                    icon = Icons.Default.HourglassEmpty,
                    onClick = onNavigateToNormalTimer
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                TimerOptionCard(
                    title = "Cronómetro de Debate",
                    description = "Controla el tiempo de dos equipos con un reloj compartido.",
                    icon = Icons.Default.Forum,
                    onClick = onNavigateToDebateTimer
                )
            }
        }
    }
}

@Composable
fun TimerOptionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/TechniquePracticeScreen.kt"] = '''package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Technique
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

enum class PracticeStep {
    LESSON, EXAMPLE, CHALLENGE, FEEDBACK, DASHBOARD
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TechniquePracticeScreen(
    technique: Technique,
    isEvaluating: Boolean,
    aiFeedback: String?,
    voiceMetrics: com.example.viewmodel.VoiceMetrics?,
    onEvaluate: (String) -> Unit,
    onEvaluateWithAudio: (ByteArray) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableStateOf(PracticeStep.LESSON) }
    var userInput by remember { mutableStateOf("") }
    
    val recordAudioPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    
    val context = LocalContext.current
    val audioRecorder = remember { com.example.util.AudioRecorderHelper(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordedAudioBytes by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(aiFeedback) {
        if (aiFeedback != null) {
            currentStep = PracticeStep.FEEDBACK
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(technique.name, fontWeight = FontWeight.Bold) },
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
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    when (currentStep) {
                        PracticeStep.LESSON -> {
                            Button(
                                onClick = { currentStep = PracticeStep.EXAMPLE },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver Ejemplo")
                            }
                        }
                        PracticeStep.EXAMPLE -> {
                            Button(
                                onClick = { currentStep = PracticeStep.CHALLENGE },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Empezar Reto")
                            }
                        }
                        PracticeStep.CHALLENGE -> {
                            Button(
                                onClick = { 
                                    if (recordedAudioBytes != null) {
                                        onEvaluateWithAudio(recordedAudioBytes!!)
                                    } else {
                                        onEvaluate(userInput) 
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = (userInput.isNotBlank() || recordedAudioBytes != null) && !isEvaluating
                            ) {
                                if (isEvaluating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp), 
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Evaluar mi Ejemplo")
                                }
                            }
                        }
                        PracticeStep.FEEDBACK -> {
                            Button(
                                onClick = { currentStep = PracticeStep.DASHBOARD },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver Análisis de Voz")
                            }
                        }
                        PracticeStep.DASHBOARD -> {
                            Button(
                                onClick = onComplete,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Finalizar")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = {
                    when (currentStep) {
                        PracticeStep.LESSON -> 0.2f
                        PracticeStep.EXAMPLE -> 0.4f
                        PracticeStep.CHALLENGE -> 0.6f
                        PracticeStep.FEEDBACK -> 0.8f
                        PracticeStep.DASHBOARD -> 1.0f
                    }
                },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(targetState = currentStep, label = "step_content") { step ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    when (step) {
                        PracticeStep.LESSON -> {
                            Text("1. La Lección", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Propósito", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(technique.purpose, modifier = Modifier.padding(bottom = 16.dp, top = 4.dp))
                                    
                                    Text("Fórmula Exacta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    technique.formula.forEachIndexed { index, item ->
                                        Text("${index + 1}. $item", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                                    }
                                }
                            }
                        }
                        PracticeStep.EXAMPLE -> {
                            Text("2. El Ejemplo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Modelo del Autor", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(technique.example, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                        PracticeStep.CHALLENGE -> {
                            Text("3. El Reto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Elige un tema de tu interés y redacta tu propio fragmento de discurso aplicando la fórmula exacta que acabas de aprender. Puedes escribirlo o dictarlo por voz.")
                            OutlinedTextField(
                                value = userInput,
                                onValueChange = { userInput = it },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                                placeholder = { Text("Escribe o dicta tu discurso aquí...") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        if (recordAudioPermissionState.status.isGranted) {
                                            if (isRecording) {
                                                recordedAudioBytes = audioRecorder.stopRecording()
                                                isRecording = false
                                            } else {
                                                audioRecorder.startRecording()
                                                isRecording = true
                                            }
                                        } else {
                                            recordAudioPermissionState.launchPermissionRequest()
                                        }
                                    },
                                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = if (isRecording) "Detener Grabación" else "Grabar Audio",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            if (recordedAudioBytes != null && !isRecording) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Audio grabado listo para evaluar.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }
                        PracticeStep.FEEDBACK -> {
                            Text("Feedback del Tutor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(aiFeedback ?: "", color = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                        }
                        PracticeStep.DASHBOARD -> {
                            Text("Análisis de Voz (Estimado)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Basado en el análisis de tu discurso, aquí tienes las métricas estimadas de tu desempeño vocal.")
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    MetricRow(title = "Ritmo (Pace)", value = voiceMetrics?.pace ?: 0f, color = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                                    MetricRow(title = "Claridad", value = voiceMetrics?.clarity ?: 0f, color = androidx.compose.ui.graphics.Color(0xFF2196F3))
                                    MetricRow(title = "Tono (Positividad)", value = voiceMetrics?.sentiment ?: 0f, color = androidx.compose.ui.graphics.Color(0xFF9C27B0))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MetricRow(title: String, value: Float, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text("${(value * 100).toInt()}%")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/TechniquesListScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SpeakerNotes
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.model.Technique
import com.example.model.techniquesList
import com.example.viewmodel.OratoriaViewModel
import com.example.model.Lesson
import androidx.compose.material.icons.filled.Whatshot
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.DailyChallengeViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniquesListScreen(
    viewModel: OratoriaViewModel,
    dailyChallengeViewModel: DailyChallengeViewModel = viewModel(),
    onTechniqueClick: (Technique) -> Unit,
    onToxicParticipantsClick: () -> Unit,
    onAnecdotesClick: () -> Unit,
    onQuotesClick: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    fun getTechniqueColorAndIcon(id: String): Pair<Color, ImageVector> {
        return when (id) {
            "inicio_1" -> Pair(Color(0xFFE53935), Icons.Default.QuestionMark)
            "inicio_2" -> Pair(Color(0xFFD81B60), Icons.Default.FormatQuote)
            "inicio_3" -> Pair(Color(0xFF8E24AA), Icons.Default.AutoStories)
            "inicio_4" -> Pair(Color(0xFF5E35B1), Icons.Default.DirectionsRun)
            "inicio_5" -> Pair(Color(0xFF3949AB), Icons.Default.BarChart)
            "inicio_6" -> Pair(Color(0xFF1E88E5), Icons.Default.CardGiftcard)
            "inicio_7" -> Pair(Color(0xFF039BE5), Icons.Default.Extension)
            "inicio_8" -> Pair(Color(0xFF00ACC1), Icons.Default.Warning)
            "inicio_9" -> Pair(Color(0xFF00897B), Icons.Default.Newspaper)
            "inicio_10" -> Pair(Color(0xFF43A047), Icons.Default.MergeType)
            "conclusion_1" -> Pair(Color(0xFF7CB342), Icons.Default.Group)
            "conclusion_2" -> Pair(Color(0xFFC0CA33), Icons.Default.SpeakerNotes)
            "conclusion_3" -> Pair(Color(0xFFFDD835), Icons.Default.Visibility)
            "conclusion_4" -> Pair(Color(0xFFFFB300), Icons.Default.FormatListNumbered)
            "conclusion_5" -> Pair(Color(0xFFFB8C00), Icons.Default.Pets)
            "conclusion_6" -> Pair(Color(0xFFF4511E), Icons.Default.Person)
            "conclusion_7" -> Pair(Color(0xFF6D4C41), Icons.Default.Signpost)
            else -> Pair(Color(0xFF757575), Icons.Default.AutoFixHigh)
        }
    }

    val completedTechniques by viewModel.completedTechniques.collectAsState()
    val lessons by viewModel.lessons.collectAsState()
    val completedLessons by viewModel.completedLessons.collectAsState()
    
    val groupedTechniques = techniquesList.groupBy { it.type }
    val groupedLessons = lessons.groupBy { it.areaName }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taller de Oratoria", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dailyChallengeViewModel.isCompletedToday.collectAsState().value) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val streak = dailyChallengeViewModel.streak.collectAsState().value
                        val currentChallenge = dailyChallengeViewModel.currentChallenge.collectAsState().value
                        val isCompletedToday = dailyChallengeViewModel.isCompletedToday.collectAsState().value
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Racha",
                                tint = if (streak > 0) Color(0xFFFF9800) else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Racha: $streak ${if (streak == 1) "día" else "días"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (streak > 0) Color(0xFFFF9800) else Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Reto Diario",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentChallenge.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentChallenge.description,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isCompletedToday) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("¡Reto de hoy completado!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                        } else {
                            Button(
                                onClick = {
                                    dailyChallengeViewModel.markAsCompleted()
                                    onNavigateToRoute(currentChallenge.route)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Completar Reto")
                            }
                        }
                    }
                }
            }
            
            groupedTechniques.forEach { (type, techniques) ->
                item {
                    Text(
                        text = "Técnicas de $type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                    )
                }
                
                val chunkedTechniques = techniques.chunked(3)
                items(chunkedTechniques.size) { rowIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val rowTechniques = chunkedTechniques[rowIndex]
                        for (technique in rowTechniques) {
                            val isCompleted = completedTechniques.contains(technique.id)
                            val isLocked = false
                            
                            val (color, icon) = getTechniqueColorAndIcon(technique.id)
                            
                            TechniqueNode(
                                technique = technique,
                                isLocked = isLocked,
                                isCompleted = isCompleted,
                                customColor = color,
                                customIcon = icon,
                                onClick = { onTechniqueClick(technique) },
                                modifier = Modifier.width(110.dp)
                            )
                        }
                        val emptySpots = 3 - rowTechniques.size
                        for (i in 0 until emptySpots) {
                            Spacer(modifier = Modifier.width(110.dp))
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Manejo de Público",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onToxicParticipantsClick).width(110.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF44336)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Participantes Tóxicos",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onAnecdotesClick).width(110.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9C27B0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HistoryEdu,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Anécdotas Inspiradoras",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Ruta de Aprendizaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                )
            }
            
            groupedLessons.forEach { (areaName, areaLessons) ->
                item {
                    Text(
                        text = areaName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                    )
                }
                val chunkedLessons = areaLessons.chunked(3)
                items(chunkedLessons.size) { rowIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val rowLessons = chunkedLessons[rowIndex]
                        for (lesson in rowLessons) {
                            LessonNode(
                                lesson = lesson,
                                isLocked = false,
                                onClick = {
                                    viewModel.startLesson(lesson)
                                    onNavigateToQuiz()
                                },
                                modifier = Modifier.width(110.dp)
                            )
                        }
                        val emptySpots = 3 - rowLessons.size
                        for (i in 0 until emptySpots) {
                            Spacer(modifier = Modifier.width(110.dp))
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun TechniqueNode(
    technique: Technique,
    isLocked: Boolean,
    isCompleted: Boolean,
    customColor: Color,
    customIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(enabled = !isLocked, onClick = onClick)
    ) {
        val backgroundColor = when {
            isLocked -> Color.LightGray
            isCompleted -> customColor.copy(alpha = 0.6f)
            else -> customColor
        }
        val icon = when {
            isLocked -> Icons.Default.Lock
            else -> customIcon
        }
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp).align(Alignment.BottomEnd)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = technique.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(110.dp)
        )
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/TongueTwisterGameScreen.kt"] = '''package com.example.ui.screens

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
    TongueTwister("Desafío de Don Quijote", "Don Quijote, el caballero que caza quimeras, cayó cual cometa que choca contra cipreses. Con coraje, cabalga con cojera, corriendo contra cascadas con cascabeles crujientes. \\"¡Quisiera quejarme!\\", quiso decir, quitándose quijadas quintas, aunque quedó quieto, quieto quedó, quedamente quebrado."),
    TongueTwister("Sinfonía Sibilante de Siete Serpientes", "Siete serpientes sisean sinfonías susurrantes. Sus escamas suenan, susurrando secretos, sin cesar, sin sentido, sin sollozos. Sigilosas, se deslizan sobre suelos sombríos, sintiendo susurros, sintiendo susurros."),
    TongueTwister("El Extraño Enigma de la Excéntrica Esfinge", "En el exuberante Egipto, existe una esfinge excéntrica. Exigiendo explicaciones exquisitas, examina a expedicionarios exhaustos. Exasperada, exhaló exabruptos, exponiendo enigmas extraordinarios, exhortando enérgicamente esforzados enigmas. Exuberante, extravió expedicionarios, exclamando: \\"¡Explícame el enigma, excelso explorador!\\""),
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
'''

for path, content in screens.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)

print("Part 3 written successfully")
