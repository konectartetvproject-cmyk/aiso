import os

screens = {}

screens["app/src/main/java/com/example/ui/screens/CustomerServiceQuizScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CustomerServiceQuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerServiceQuizScreen(
    onBack: () -> Unit,
    viewModel: CustomerServiceQuizViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reto de Respuestas", fontWeight = FontWeight.Bold) },
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
        if (state.isFinished) {
            CSQuizResultView(paddingValues, state.score, state.questions.size, viewModel::restartQuiz)
        } else if (state.questions.isNotEmpty()) {
            CSQuizQuestionView(paddingValues, state, viewModel)
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CSQuizResultView(paddingValues: PaddingValues, score: Int, total: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Reto Completado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Tu puntuación:",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$score / $total",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (score == total) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Jugar de nuevo", fontSize = 18.sp)
        }
    }
}

@Composable
fun CSQuizQuestionView(
    paddingValues: PaddingValues,
    state: com.example.viewmodel.CSQuizState,
    viewModel: CustomerServiceQuizViewModel
) {
    val question = state.questions[state.currentQuestionIndex]
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex.toFloat() / state.questions.size.toFloat()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Situación:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.situation,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "¿Qué responderías?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        question.options.forEachIndexed { index, option ->
            val isSelected = state.selectedOptionIndex == index
            val isCorrect = index == question.correctOptionIndex
            
            val backgroundColor = when {
                !state.showExplanation -> if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                state.showExplanation && isCorrect -> Color(0xFFE8F5E9)
                state.showExplanation && isSelected && !isCorrect -> Color(0xFFFFEBEE)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            
            val borderColor = when {
                !state.showExplanation -> if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                state.showExplanation && isCorrect -> Color(0xFF4CAF50)
                state.showExplanation && isSelected && !isCorrect -> Color(0xFFF44336)
                else -> Color.Transparent
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(enabled = !state.showExplanation) { viewModel.selectOption(index) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (state.showExplanation) {
                        if (isCorrect) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Correcto", tint = Color(0xFF4CAF50))
                        } else if (isSelected) {
                            Icon(Icons.Default.Cancel, contentDescription = "Incorrecto", tint = Color(0xFFF44336))
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        if (state.showExplanation) {
            val isCorrect = state.selectedOptionIndex == question.correctOptionIndex
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isCorrect) "¡Correcto!" else "Incorrecto",
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.explanation,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (state.showExplanation) {
                    viewModel.nextQuestion()
                } else {
                    viewModel.checkAnswer()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = state.selectedOptionIndex != null || state.showExplanation,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.showExplanation) {
                    if (state.selectedOptionIndex == question.correctOptionIndex) Color(0xFF4CAF50) else Color(0xFFF44336)
                } else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (state.showExplanation) "Continuar" else "Comprobar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/CharadesGameScreen.kt"] = '''package com.example.ui.screens

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
    "Clásico (Libre)",
    "Sin usar las manos",
    "Solo con expresiones faciales",
    "Como si estuvieras en cámara lenta",
    "Como un robot",
    "Haciendo mímica exagerada",
    "Sin mover los pies",
    "Con un brazo detrás de la espalda",
    "Como si pesaras 1000 kg",
    "Como si flotaras en el espacio",
    "Solo moviendo los ojos y cejas",
    "Como si tuvieras mucho frío",
    "Como si estuvieras bajo el agua",
    "A la máxima velocidad posible",
    "De espaldas (solo se ve tu postura)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharadesGameScreen(onBack: () -> Unit) {
    var gameState by remember { mutableStateOf("start") }
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
'''

screens["app/src/main/java/com/example/ui/screens/BodyLanguageScreen.kt"] = '''package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.BodyDimension
import com.example.viewmodel.BodyLanguageViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyLanguageScreen(
    onBack: () -> Unit,
    viewModel: BodyLanguageViewModel = viewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = LocalClipboardManager.current
    val state by viewModel.state.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    BackHandler {
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Espejo de Oratoria", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Análisis de Lenguaje Corporal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleGuides) {
                        Icon(
                            imageVector = if (state.showGuides) Icons.Default.GridOn else Icons.Default.GridOff,
                            contentDescription = "Guías de Postura",
                            tint = if (state.showGuides) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = viewModel::toggleCamera) {
                        Icon(
                            imageVector = Icons.Default.FlipCameraAndroid,
                            contentDescription = "Cambiar Cámara"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        if (hasCameraPermission) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(BodyDimension.values()) { dimension ->
                        FilterChip(
                            selected = state.activeDimension == dimension,
                            onClick = { viewModel.setDimension(dimension) },
                            label = { Text("${dimension.iconLabel} ${dimension.title}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                ) {
                    key(state.isFrontCamera) {
                        BodyCameraPreview(
                            context = context,
                            lifecycleOwner = lifecycleOwner,
                            isFrontCamera = state.isFrontCamera,
                            onAnalyzeRequest = { bitmap ->
                                viewModel.analyzeFrame(bitmap)
                            }
                        )
                    }

                    if (state.showGuides) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)

                            drawLine(
                                color = Color.White.copy(alpha = 0.4f),
                                start = Offset(w / 2f, h * 0.05f),
                                end = Offset(w / 2f, h * 0.95f),
                                strokeWidth = 2.5f,
                                pathEffect = dashEffect
                            )

                            drawOval(
                                color = Color(0xFFFFB300).copy(alpha = 0.5f),
                                topLeft = Offset(w * 0.32f, h * 0.06f),
                                size = Size(w * 0.36f, h * 0.28f),
                                style = Stroke(width = 2.5f, pathEffect = dashEffect)
                            )

                            val boxLeft = w * 0.15f
                            val boxTop = h * 0.38f
                            val boxWidth = w * 0.70f
                            val boxHeight = h * 0.45f
                            drawRoundRect(
                                color = Color(0xFF4CAF50).copy(alpha = 0.55f),
                                topLeft = Offset(boxLeft, boxTop),
                                size = Size(boxWidth, boxHeight),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                                style = Stroke(width = 2.5f, pathEffect = dashEffect)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            contentColor = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CAF50))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (state.isAnalyzing) "Analizando..." else "En Vivo",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = if (state.isFrontCamera) "Cámara Frontal" else "Cámara Trasera",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            "Métricas de Lenguaje Corporal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MetricProgressBar(
                            label = "Columna de Oro (Espalda y Barbilla)",
                            score = state.postureScore,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MetricProgressBar(
                            label = "Contacto Visual (Abanico 180°)",
                            score = state.eyeContactScore,
                            color = Color(0xFF1976D2)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        MetricProgressBar(
                            label = "Zona de Influencia (Plexo Solar)",
                            score = state.handsScore,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Consejo del Mentor en Escena",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 14.sp
                                )
                            }
                            IconButton(
                                onClick = {
                                    state.feedback?.let {
                                        clipboardManager.setText(AnnotatedString(it))
                                    }
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copiar",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.feedback ?: "Posiciónate frente a la cámara dentro del marco verde para recibir retroalimentación instantánea.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.VideocamOff,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Acceso a Cámara Necesario",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "El Espejo de Oratoria necesita tu cámara frontal para medir tu postura, contacto visual y gestos en el escenario.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("Activar Cámara")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricProgressBar(
    label: String,
    score: Int,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            Text("$score%", fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun BodyCameraPreview(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    isFrontCamera: Boolean,
    onAnalyzeRequest: (Bitmap) -> Unit
) {
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    
    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraExecutor.shutdown()
            } catch (_: Exception) {}
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                var lastAnalyzedTime = 0L
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastAnalyzedTime >= 2200) {
                                lastAnalyzedTime = currentTime
                                try {
                                    val bitmap = imageProxy.toBitmap()
                                    val rotatedBitmap = bitmap.rotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                                    onAnalyzeRequest(rotatedBitmap)
                                } catch (_: Exception) {}
                            }
                            imageProxy.close()
                        }
                    }

                val cameraSelector = if (isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    if (degrees == 0f) return this
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
'''

screens["app/src/main/java/com/example/ui/screens/DashboardScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.OratoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: OratoriaViewModel) {
    val completedLessons by viewModel.completedLessons.collectAsState()
    val allLessons by viewModel.lessons.collectAsState()
    
    val generalProgress = if (allLessons.isNotEmpty()) {
        completedLessons.size.toFloat() / allLessons.size.toFloat()
    } else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progreso", fontWeight = FontWeight.Bold) },
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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Tu Avance General",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Completado", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("${(generalProgress * 100).toInt()}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { generalProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Áreas Clave",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val areasGrouped = allLessons.groupBy { it.areaName }
            val areas = areasGrouped.map { (areaName, areaLessons) ->
                val completedInArea = areaLessons.count { completedLessons.contains(it.id) }
                val progress = if (areaLessons.isNotEmpty()) completedInArea.toFloat() / areaLessons.size.toFloat() else 0f
                Pair(areaName, progress)
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(areas) { area ->
                    AreaProgressCard(name = area.first, progress = area.second)
                }
            }
        }
    }
}

@Composable
fun AreaProgressCard(name: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/CourseScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
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
import com.example.model.Lesson
import com.example.viewmodel.OratoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: OratoriaViewModel,
    onNavigateToQuiz: () -> Unit
) {
    val lessons by viewModel.lessons.collectAsState()
    val completedLessons by viewModel.completedLessons.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz", fontWeight = FontWeight.Bold) },
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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ruta de Aprendizaje",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.Start)
            )

            val groupedLessons = lessons.groupBy { it.areaName }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                groupedLessons.forEach { (areaName, areaLessons) ->
                    item {
                        Text(
                            text = areaName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 8.dp)
                        )
                    }
                    items(areaLessons.size) { index ->
                        val lesson = areaLessons[index]
                        LessonNode(
                            lesson = lesson,
                            isLocked = false,
                            onClick = {
                                viewModel.startLesson(lesson)
                                onNavigateToQuiz()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonNode(lesson: Lesson, isLocked: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(enabled = !isLocked, onClick = onClick)
    ) {
        val (icon, backgroundColor) = when (lesson.id) {
            "1_1" -> Pair(Icons.Default.Mood, Color(0xFFE91E63))
            "1_2" -> Pair(Icons.Default.Air, Color(0xFF9C27B0))
            "2_1" -> Pair(Icons.Default.RecordVoiceOver, Color(0xFF673AB7))
            "2_2" -> Pair(Icons.Default.GraphicEq, Color(0xFF3F51B5))
            "3_1" -> Pair(Icons.Default.Architecture, Color(0xFF009688))
            "3_2" -> Pair(Icons.Default.DoneAll, Color(0xFF4CAF50))
            "4_1" -> Pair(Icons.Default.EmojiObjects, Color(0xFFFF9800))
            "4_2" -> Pair(Icons.Default.Flag, Color(0xFFFF5722))
            else -> Pair(Icons.Default.Star, MaterialTheme.colorScheme.primary)
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isLocked) Color.LightGray else backgroundColor
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLocked) Icons.Default.Lock else icon,
                contentDescription = lesson.title,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(110.dp)
        )
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/DictionPracticeScreen.kt"] = '''package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.viewmodel.DictionPracticeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionPracticeScreen(
    onBack: () -> Unit,
    viewModel: DictionPracticeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val transcription by viewModel.transcription.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val error by viewModel.error.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val feedback by viewModel.feedback.collectAsState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            viewModel.startListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Práctica de Dicción", fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Graba tu voz y observa cómo se transcribe en tiempo real para evaluar tu pronunciación y dicción.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (transcription.isEmpty()) {
                        Text(
                            text = if (isRecording) "Escuchando..." else "Presiona el micrófono para comenzar a hablar...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Text(
                            text = transcription,
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, lineHeight = 28.sp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (isAnalyzing) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Analizando tu dicción...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else if (feedback != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Retroalimentación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = feedback!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) Color.Red else MaterialTheme.colorScheme.primary)
                    .clickable {
                        if (isRecording) {
                            viewModel.stopListening()
                        } else if (!isAnalyzing) {
                            if (hasPermission) {
                                viewModel.startListening()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Detener" else "Grabar",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isRecording) "Detener" else "Grabar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/ImpromptuSpeechScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.OratoriaViewModel
import com.example.viewmodel.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpromptuSpeechScreen(
    viewModel: OratoriaViewModel,
    onBack: () -> Unit
) {
    val topic by viewModel.impromptuTopic.collectAsState()
    val messages by viewModel.impromptuMessages.collectAsState()
    val isLoading by viewModel.isImpromptuLoading.collectAsState()
    val isFinished by viewModel.impromptuFinished.collectAsState()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.startImpromptuSpeech()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discurso Improvisado", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.stopImpromptuSpeech()
                        onBack()
                    }) {
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
            if (!isFinished) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Desarrolla tu idea...") },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendImpromptuMessage(inputText)
                                    inputText = ""
                                }
                            },
                            enabled = !isLoading && inputText.isNotBlank()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                tint = if (isLoading || inputText.isBlank()) Color.Gray else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Button(
                        onClick = {
                            viewModel.stopImpromptuSpeech()
                            onBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Finalizar Práctica")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tema a Desarrollar:",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        topic,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Desarrolla una idea sobre este tema. La IA actuará como tu mentor, pidiéndote que profundices o guiándote hasta que tengas un discurso completo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Card(
                                modifier = Modifier.padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/FillerWordsGameScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class GameSentence(val id: Int, val words: List<GameWord>)
data class GameWord(val id: Int, val text: String, val isFiller: Boolean)

fun createSentence(id: Int, text: String, fillerWords: List<String>): GameSentence {
    val rawWords = text.split(" ")
    val gameWords = rawWords.mapIndexed { index, word ->
        val cleanWord = word.lowercase().replace(Regex("[^a-zñáéíóú]"), "")
        val isFiller = fillerWords.contains(cleanWord)
        GameWord(index, word, isFiller)
    }
    return GameSentence(id, gameWords)
}

val fillerWordsList = listOf("este", "eh", "bueno", "digamos", "tipo", "osea", "pues", "nada", "básicamente")

data class GameLevel(val levelNumber: Int, val sentences: List<GameSentence>)

val level1Sentences = listOf(
    createSentence(1, "Bueno , yo creo que este , la oratoria es importante .", fillerWordsList),
    createSentence(2, "Básicamente el problema es que osea no hay tiempo .", fillerWordsList),
    createSentence(3, "Vamos a hablar de tipo el cambio climático .", fillerWordsList),
    createSentence(4, "Pues nada , digamos que esa es la conclusión .", fillerWordsList),
    createSentence(5, "Eh , me parece que este tema es muy relevante .", fillerWordsList)
).shuffled()

val level2Sentences = listOf(
    createSentence(6, "Yo opino que digamos , el proyecto este , no va a funcionar eh .", fillerWordsList),
    createSentence(7, "Osea , básicamente tipo tenemos que hacerlo de nuevo pues nada .", fillerWordsList),
    createSentence(8, "Eh , bueno , vamos a empezar con este el primer punto .", fillerWordsList),
    createSentence(9, "Digamos que osea es tipo muy complicado de explicar básicamente .", fillerWordsList),
    createSentence(10, "Pues , este , yo creo que nada , es mejor así eh .", fillerWordsList)
).shuffled()

val level3Sentences = listOf(
    createSentence(11, "Bueno , osea , yo estaba tipo pensando que básicamente podíamos eh hacer este proyecto .", fillerWordsList),
    createSentence(12, "Pues nada , digamos que este , la situación es tipo muy eh difícil .", fillerWordsList),
    createSentence(13, "Básicamente osea el tema es que bueno , no tenemos este mucho eh presupuesto digamos .", fillerWordsList),
    createSentence(14, "Eh , pues nada , tipo yo diría que este vamos a dejarlo así osea por ahora .", fillerWordsList),
    createSentence(15, "Digamos que bueno , este , tipo básicamente es un problema que osea hay que eh resolver .", fillerWordsList)
).shuffled()

val fillerGameLevels = listOf(
    GameLevel(1, level1Sentences),
    GameLevel(2, level2Sentences),
    GameLevel(3, level3Sentences)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FillerWordsGameScreen(
    onBack: () -> Unit
) {
    var currentLevelIndex by remember { mutableIntStateOf(0) }
    var currentSentenceIndex by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var eliminatedWords by remember { mutableStateOf(setOf<Int>()) }
    var errorWords by remember { mutableStateOf(setOf<Int>()) }
    
    var showIntro by remember { mutableStateOf(true) }
    var showLevelUp by remember { mutableStateOf(false) }
    var gameState by remember { mutableStateOf("playing") }

    val currentLevel = fillerGameLevels.getOrNull(currentLevelIndex)
    val currentSentence = currentLevel?.sentences?.getOrNull(currentSentenceIndex)

    LaunchedEffect(eliminatedWords, currentSentenceIndex, currentLevelIndex) {
        if (currentSentence != null && currentLevel != null) {
            val totalFillers = currentSentence.words.count { it.isFiller }
            val eliminatedFillers = currentSentence.words.filter { it.isFiller && eliminatedWords.contains(it.id) }.size
            
            if (totalFillers > 0 && eliminatedFillers == totalFillers) {
                delay(1000)
                if (currentSentenceIndex < currentLevel.sentences.size - 1) {
                    currentSentenceIndex++
                    eliminatedWords = emptySet()
                    errorWords = emptySet()
                } else if (currentLevelIndex < fillerGameLevels.size - 1) {
                    showLevelUp = true
                } else {
                    gameState = "won"
                }
            } else if (totalFillers == 0) {
                delay(1000)
                if (currentSentenceIndex < currentLevel.sentences.size - 1) {
                    currentSentenceIndex++
                    eliminatedWords = emptySet()
                    errorWords = emptySet()
                } else if (currentLevelIndex < fillerGameLevels.size - 1) {
                    showLevelUp = true
                } else {
                    gameState = "won"
                }
            }
        }
    }

    if (showLevelUp) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Nivel ${currentLevelIndex + 1} Completado!", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = { Text("¡Excelente! Has limpiado todas las oraciones del nivel ${currentLevelIndex + 1}. Prepárate para el siguiente nivel, ¡es más difícil!") },
            confirmButton = {
                Button(onClick = { 
                    showLevelUp = false
                    currentLevelIndex++
                    currentSentenceIndex = 0
                    eliminatedWords = emptySet()
                    errorWords = emptySet()
                    if (lives < 3) lives++
                }) {
                    Text("Siguiente Nivel")
                }
            }
        )
    }

    if (showIntro) {
        AlertDialog(
            onDismissRequest = { showIntro = false },
            title = { Text("Caza Muletillas", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Las muletillas (eh, este, bueno, digamos, osea...) ensucian tu discurso y restan profesionalismo.\\n\\n" +
                    "Objetivo: Toca todas las muletillas en la oración para eliminarlas antes de avanzar. ¡Cuidado con tocar palabras válidas porque perderás vidas!"
                )
            },
            confirmButton = {
                Button(onClick = { showIntro = false }) {
                    Text("¡A jugar!")
                }
            }
        )
    }

    if (gameState == "won") {
        AlertDialog(
            onDismissRequest = { onBack() },
            title = { Text("¡Excelente!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50)) },
            text = { Text("Has limpiado todas las oraciones. Puntuación final: $score") },
            confirmButton = {
                Button(onClick = { onBack() }) {
                    Text("Volver")
                }
            }
        )
    }

    if (gameState == "lost") {
        AlertDialog(
            onDismissRequest = { onBack() },
            title = { Text("Fin del Juego", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error) },
            text = { Text("Te has quedado sin vidas. ¡Sigue practicando para limpiar tu discurso!") },
            confirmButton = {
                Button(onClick = { 
                    currentLevelIndex = 0
                    currentSentenceIndex = 0
                    lives = 3
                    score = 0
                    eliminatedWords = emptySet()
                    errorWords = emptySet()
                    gameState = "playing"
                }) {
                    Text("Reintentar")
                }
            },
            dismissButton = {
                TextButton(onClick = { onBack() }) {
                    Text("Salir")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Caza Muletillas", fontWeight = FontWeight.Bold) },
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
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Vida",
                            tint = Color.Red,
                            modifier = Modifier.size(28.dp).padding(horizontal = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Nivel ${currentLevelIndex + 1} - Oración ${currentSentenceIndex + 1} de ${currentLevel?.sentences?.size ?: 0}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                if (currentSentence != null) {
                    FlowRow(
                        modifier = Modifier.padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        currentSentence.words.forEach { word ->
                            val isEliminated = eliminatedWords.contains(word.id)
                            val isError = errorWords.contains(word.id)
                            val isPunctuation = word.text.matches(Regex("[.,:;!?]"))
                            
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when {
                                            isEliminated -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                            isError -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                            isPunctuation -> Color.Transparent
                                            else -> MaterialTheme.colorScheme.surface
                                        }
                                    )
                                    .clickable(enabled = !isEliminated && !isError && !isPunctuation && gameState == "playing") {
                                        if (word.isFiller) {
                                            eliminatedWords = eliminatedWords + word.id
                                            score += 15
                                        } else {
                                            errorWords = errorWords + word.id
                                            lives--
                                            if (lives <= 0) {
                                                gameState = "lost"
                                            }
                                        }
                                    }
                                    .padding(horizontal = if (isPunctuation) 0.dp else 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = word.text,
                                    fontSize = 22.sp,
                                    fontWeight = if (isEliminated) FontWeight.Normal else FontWeight.Bold,
                                    color = when {
                                        isEliminated -> Color(0xFF4CAF50)
                                        isError -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    textDecoration = if (isEliminated) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Toca las muletillas para eliminarlas",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/GamesListScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.BubbleChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesListScreen(
    onStageMovementClick: () -> Unit,
    onFillerWordsClick: () -> Unit,
    onRhetoricalFiguresClick: () -> Unit,
    onOpeningClosingClick: () -> Unit,
    onEsquivaMuletillasClick: () -> Unit,
    onElGranImpostorClick: () -> Unit,
    onCharadesClick: () -> Unit,
    onTongueTwistersClick: () -> Unit,
    onTongueTwisterDuelClick: () -> Unit,
    onBodyLanguageClick: () -> Unit,
    onTongueTwisterOfflineClick: () -> Unit,
    onAudienceSimulatorClick: () -> Unit,
    onDictionPracticeClick: () -> Unit,
    onOratoryDefenseClick: () -> Unit,
    onOratoryRunnerClick: () -> Unit,
    onOratoryMatchClick: () -> Unit,
    onCustomerServiceQuizClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Juegos Prácticos", fontWeight = FontWeight.Bold) },
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
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onOratoryRunnerClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Runner",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onOratoryDefenseClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
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
                            text = "Defensa",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onStageMovementClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Desplazamiento",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onFillerWordsClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE91E63)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Caza Muletillas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onRhetoricalFiguresClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3F51B5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Figuras Retóricas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onOpeningClosingClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Inicios y Cierres",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onEsquivaMuletillasClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9C27B0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BubbleChart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pop Muletillas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onElGranImpostorClick)
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
                            text = "Gran Impostor",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onCharadesClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00BCD4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Charadas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onTongueTwistersClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8BC34A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Trabalenguas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onTongueTwisterDuelClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE91E63)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Duelo Trabalenguas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onBodyLanguageClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF9800)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lenguaje Corporal",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onTongueTwisterOfflineClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF673AB7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Práctica Offline",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onAudienceSimulatorClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF009688)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Simulador Audiencia",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onDictionPracticeClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3F51B5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Práctica Dicción",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onOratoryDefenseClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
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
                            text = "Defensa",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onOratoryMatchClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE91E63)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BubbleChart,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Oratory Match",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onCustomerServiceQuizClick)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4CAF50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reto Respuestas",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
'''

screens["app/src/main/java/com/example/ui/screens/EsquivaMuletillasGameScreen.kt"] = '''package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import android.speech.tts.TextToSpeech
import android.view.SoundEffectConstants
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import java.util.Locale

data class Bubble(
    val id: Int,
    var x: Float,
    var y: Float,
    val radius: Float,
    val text: String,
    val speed: Float,
    val color: Color
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsquivaMuletillasGameScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    
    DisposableEffect(context) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
            }
        }
        ttsInstance.language = Locale("es", "ES")
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    val textMeasurer = rememberTextMeasurer()
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(5) }
    var level by remember { mutableIntStateOf(1) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    val bubbles = remember { mutableStateListOf<Bubble>() }
    val particles = remember { mutableStateListOf<Particle>() }
    
    val fillerWords = listOf("Eh...", "Este...", "Bueno", "O sea", "Nada", "Básicamente", "Como tal", "Entonces", "Pues")
    val bubbleColors = listOf(
        Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF3F51B5), 
        Color(0xFF00BCD4), Color(0xFF9C27B0), Color(0xFF4CAF50), Color(0xFFF44336)
    )
    var bubbleIdCounter by remember { mutableIntStateOf(0) }
    
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying, isGameOver) {
        if (isPlaying && !isGameOver) {
            var lastSpawnTime = System.currentTimeMillis()
            var spawnInterval = 2000L
            
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                
                if (currentTime - lastSpawnTime > spawnInterval && screenWidth > 0) {
                    val radius = Random.nextFloat() * 30f + 90f
                    val x = Random.nextFloat() * (screenWidth - radius * 2) + radius
                    val speed = Random.nextFloat() * 3.0f + 4.0f + (level * 0.8f) 
                    bubbles.add(
                        Bubble(
                            id = bubbleIdCounter++,
                            x = x,
                            y = -radius,
                            radius = radius,
                            text = fillerWords.random(),
                            speed = speed,
                            color = bubbleColors.random()
                        )
                    )
                    lastSpawnTime = currentTime
                    spawnInterval = (2000L - (level * 150L)).coerceAtLeast(600L)
                }

                val bubblesToRemove = mutableListOf<Bubble>()
                for (bubble in bubbles) {
                    bubble.y += bubble.speed
                    if (bubble.y - bubble.radius > screenHeight - 200f) {
                        bubblesToRemove.add(bubble)
                        tts?.speak(bubble.text, TextToSpeech.QUEUE_FLUSH, null, null)
                        
                        lives -= 1
                        if (lives <= 0) {
                            isGameOver = true
                        }
                    }
                }
                bubbles.removeAll(bubblesToRemove)
                
                val particlesToRemove = mutableListOf<Particle>()
                for (particle in particles) {
                    particle.x += particle.vx
                    particle.y += particle.vy
                    particle.vy += 0.2f
                    particle.life -= 0.03f
                    if (particle.life <= 0) particlesToRemove.add(particle)
                }
                particles.removeAll(particlesToRemove)

                delay(16)
            }
        }
    }
    
    fun popBubble(bubble: Bubble) {
        view.playSoundEffect(SoundEffectConstants.CLICK)
        
        bubbles.remove(bubble)
        score += 10
        level = (score / 100) + 1

        for (i in 0..20) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = Random.nextFloat() * 15f + 5f
            particles.add(
                Particle(
                    x = bubble.x,
                    y = bubble.y,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    life = 1f,
                    color = bubble.color
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pop de Muletillas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
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
                        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
                    )
                )
        ) {
            if (!isPlaying && !isGameOver) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Estalla las Muletillas!", 
                        color = Color.White, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Toca las burbujas de relleno antes", 
                        color = Color.LightGray, 
                        fontSize = 18.sp
                    )
                    Text(
                        text = "de que caigan y el orador las diga.", 
                        color = Color.LightGray, 
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { 
                            isPlaying = true
                            lives = 5
                            score = 0
                            level = 1
                            bubbles.clear()
                            particles.clear()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94560)),
                        modifier = Modifier
                            .height(56.dp)
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("¡Comenzar a Jugar!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            screenWidth = it.width.toFloat()
                            screenHeight = it.height.toFloat()
                        }
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val clickedBubbles = bubbles.filter { bubble ->
                                    val dx = offset.x - bubble.x
                                    val dy = offset.y - bubble.y
                                    val distance = sqrt(dx * dx + dy * dy)
                                    distance <= bubble.radius + 80f
                                }
                                clickedBubbles.lastOrNull()?.let { popBubble(it) }
                            }
                        }
                ) {
                    for (particle in particles) {
                        drawCircle(
                            color = particle.color.copy(alpha = particle.life),
                            radius = 12f * particle.life,
                            center = Offset(particle.x, particle.y)
                        )
                    }
                    for (bubble in bubbles) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(bubble.color.copy(alpha = 0.5f), bubble.color.copy(alpha = 0.9f)),
                                center = Offset(bubble.x - bubble.radius * 0.3f, bubble.y - bubble.radius * 0.3f),
                                radius = bubble.radius
                            ),
                            radius = bubble.radius,
                            center = Offset(bubble.x, bubble.y)
                        )
                        
                        drawCircle(
                            color = Color.White.copy(alpha = 0.4f),
                            radius = bubble.radius * 0.25f,
                            center = Offset(bubble.x - bubble.radius * 0.4f, bubble.y - bubble.radius * 0.4f)
                        )
                        
                        val textLayoutResult = textMeasurer.measure(
                            text = bubble.text,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                bubble.x - textLayoutResult.size.width / 2f,
                                bubble.y - textLayoutResult.size.height / 2f
                            )
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Nivel: $level", color = Color(0xFFE94560), fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Puntos: $score", color = Color(0xFF0F3460), fontSize = 20.sp, fontWeight = FontWeight.Bold,
                              modifier = Modifier
                                 .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                 .padding(horizontal = 12.dp, vertical = 4.dp))
                    }
                    
                    Text("❤️".repeat(lives), fontSize = 24.sp, 
                          modifier = Modifier
                             .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                             .padding(horizontal = 12.dp, vertical = 4.dp))
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        ),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        text = "🎤 Orador", 
                        color = Color.White, 
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (isGameOver) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .background(Color(0xFF1A1A2E), RoundedCornerShape(24.dp))
                                .padding(40.dp)
                        ) {
                            Text("¡GAME OVER!", color = Color(0xFFE94560), fontSize = 42.sp, fontWeight = FontWeight.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Puntuación Final: $score", color = Color.White, fontSize = 28.sp)
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = {
                                    isGameOver = false
                                    lives = 5
                                    score = 0
                                    level = 1
                                    bubbles.clear()
                                    particles.clear()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F3460)),
                                modifier = Modifier
                                    .height(56.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Volver a jugar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
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

print("Part 1 written successfully")
