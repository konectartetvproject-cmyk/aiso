package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.viewmodel.LiveSpeechPracticeViewModel
import com.example.viewmodel.LiveSpeechState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LiveSpeechPracticeScreen(
    onBack: () -> Unit,
    viewModel: LiveSpeechPracticeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    )

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    val state by viewModel.state.collectAsState()

    BackHandler {
        if (state.isRecording) {
            viewModel.toggleRecording()
        }
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Práctica de Video en Vivo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            text = if (state.isRecording) "Grabando sesión activa" else "Cámara y Coaching Local",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (state.isRecording) viewModel.toggleRecording()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleTts) {
                        Icon(
                            imageVector = if (state.isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Voz del Mentor",
                            tint = if (state.isTtsEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!state.isFrontCamera) {
                        IconButton(onClick = viewModel::toggleTorch) {
                            Icon(
                                imageVector = if (state.isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Linterna",
                                tint = if (state.isTorchOn) Color(0xFFFFD600) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = viewModel::toggleGuideOverlay) {
                        Icon(
                            imageVector = if (state.showGuideOverlay) Icons.Default.GridOn else Icons.Default.GridOff,
                            contentDescription = "Guías de Postura",
                            tint = if (state.showGuideOverlay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
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
        if (permissionsState.allPermissionsGranted) {
            LiveSpeechContent(
                paddingValues = paddingValues,
                state = state,
                onToggleRecording = viewModel::toggleRecording,
                onPartialSpeech = viewModel::onPartialSpeech,
                onFinalSpeech = viewModel::onFinalSpeech,
                onRmsChanged = viewModel::onRmsChanged,
                onFrameCaptured = viewModel::analyzeFrame,
                onDismissSummary = viewModel::dismissSummaryDialog
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Videocam,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Permisos Requeridos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Para el análisis en vivo de tu postura, contacto visual y ritmo vocal, necesitamos acceso a tu cámara y micrófono.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { permissionsState.launchMultiplePermissionRequest() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Conceder Permisos")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveSpeechContent(
    paddingValues: PaddingValues,
    state: LiveSpeechState,
    onToggleRecording: () -> Unit,
    onPartialSpeech: (String) -> Unit,
    onFinalSpeech: (String) -> Unit,
    onRmsChanged: (Float) -> Unit,
    onFrameCaptured: (Bitmap) -> Unit,
    onDismissSummary: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = LocalClipboardManager.current
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    DisposableEffect(Unit) {
        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {
                onRmsChanged(rmsdB)
            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                if (state.isRecording) {
                    try {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                        }
                        speechRecognizer.startListening(intent)
                    } catch (_: Exception) {}
                }
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onFinalSpeech(matches[0])
                }
                if (state.isRecording) {
                    try {
                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                        }
                        speechRecognizer.startListening(intent)
                    } catch (_: Exception) {}
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onPartialSpeech(matches[0])
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
        speechRecognizer.setRecognitionListener(listener)
        
        onDispose {
            try {
                speechRecognizer.destroy()
            } catch (_: Exception) {}
        }
    }

    LaunchedEffect(state.isRecording) {
        if (state.isRecording) {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                }
                speechRecognizer.startListening(intent)
            } catch (_: Exception) {}
        } else {
            try {
                speechRecognizer.stopListening()
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .border(2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            key(state.isFrontCamera) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                        }
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            try {
                                val cameraProvider = cameraProviderFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                                
                                val imageAnalysis = ImageAnalysis.Builder()
                                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                    .build()
                                
                                var lastAnalysisTime = 0L
                                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                                    val currentTime = System.currentTimeMillis()
                                    if (state.isRecording && currentTime - lastAnalysisTime >= 2200) {
                                        lastAnalysisTime = currentTime
                                        try {
                                            val bitmap = imageProxy.toBitmap()
                                            val rotated = bitmap.rotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                                            onFrameCaptured(rotated)
                                        } catch (_: Exception) {}
                                    }
                                    imageProxy.close()
                                }
                                val cameraSelector = if (state.isFrontCamera) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                                cameraProvider.unbindAll()
                                val camera = cameraProvider.bindToLifecycle(
                                    lifecycleOwner, cameraSelector, preview, imageAnalysis
                                )
                                if (!state.isFrontCamera && camera.cameraInfo.hasFlashUnit()) {
                                    camera.cameraControl.enableTorch(state.isTorchOn)
                                }
                            } catch (exc: Exception) {
                                Log.e("Camera", "Use case binding failed", exc)
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (state.showGuideOverlay) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                    drawLine(
                        color = Color.White.copy(alpha = 0.35f),
                        start = Offset(w / 2f, h * 0.08f),
                        end = Offset(w / 2f, h * 0.92f),
                        strokeWidth = 2f,
                        pathEffect = dashEffect
                    )

                    val boxLeft = w * 0.16f
                    val boxTop = h * 0.38f
                    val boxWidth = w * 0.68f
                    val boxHeight = h * 0.42f
                    drawRoundRect(
                        color = Color(0xFF4CAF50).copy(alpha = 0.5f),
                        topLeft = Offset(boxLeft, boxTop),
                        size = Size(boxWidth, boxHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
                        style = Stroke(width = 2.5f, pathEffect = dashEffect)
                    )

                    val cornerLen = 24f
                    val strokeW = 4f
                    val cornerColor = Color.White.copy(alpha = 0.7f)

                    drawLine(cornerColor, Offset(16f, 16f), Offset(16f + cornerLen, 16f), strokeW)
                    drawLine(cornerColor, Offset(16f, 16f), Offset(16f, 16f + cornerLen), strokeW)

                    drawLine(cornerColor, Offset(w - 16f, 16f), Offset(w - 16f - cornerLen, 16f), strokeW)
                    drawLine(cornerColor, Offset(w - 16f, 16f), Offset(w - 16f, 16f + cornerLen), strokeW)

                    drawLine(cornerColor, Offset(16f, h - 16f), Offset(16f + cornerLen, h - 16f), strokeW)
                    drawLine(cornerColor, Offset(16f, h - 16f), Offset(16f, h - 16f - cornerLen), strokeW)

                    drawLine(cornerColor, Offset(w - 16f, h - 16f), Offset(w - 16f - cornerLen, h - 16f), strokeW)
                    drawLine(cornerColor, Offset(w - 16f, h - 16f), Offset(w - 16f, h - 16f + cornerLen), strokeW)
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
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.isRecording) {
                            val infiniteTransition = rememberInfiniteTransition(label = "rec_blink")
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = 0.2f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "alpha_rec"
                            )
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color.Red.copy(alpha = alpha))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val min = state.elapsedSeconds / 60
                            val sec = state.elapsedSeconds % 60
                            Text(
                                text = String.format("%02d:%02d", min, sec),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("LISTO", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                if (state.isRecording) {
                    val animatedProgress by animateFloatAsState(
                        targetValue = (state.audioRmsDb / 8f).coerceIn(0.1f, 1f),
                        label = "vu_meter"
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        contentColor = Color.White
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (state.audioRmsDb > 1f) Icons.Default.GraphicEq else Icons.Default.MicNone,
                                contentDescription = null,
                                tint = if (state.audioRmsDb > 2f) Color(0xFF4CAF50) else Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .width(42.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = if (animatedProgress > 0.8f) Color(0xFFFFD54F) else Color(0xFF4CAF50),
                                trackColor = Color.White.copy(alpha = 0.25f)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
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

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xDD1E1E2E))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (state.tipCategory) {
                            "POSTURA" -> MaterialTheme.colorScheme.primary
                            "GESTOS" -> Color(0xFF2E7D32)
                            "MIRADA" -> Color(0xFFF57C00)
                            "MULETILLAS" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    ) {
                        Text(
                            text = state.tipCategory,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = state.currentTip,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ESTRUCTURA DE ORATORIA",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = state.activeStepName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryCard(
                title = "TIEMPO",
                value = String.format("%02d:%02d", state.elapsedSeconds / 60, state.elapsedSeconds % 60),
                subtitle = "En vivo",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            
            val paceLabel = when {
                state.paceWpm == 0 -> "Esperando"
                state.paceWpm in 115..155 -> "Óptimo"
                state.paceWpm > 155 -> "Rápido"
                else -> "Pausado"
            }
            val paceColor = if (state.paceWpm in 115..155) Color(0xFF2E7D32) else Color(0xFFF57C00)
            TelemetryCard(
                title = "RITMO",
                value = "${state.paceWpm} PPM",
                subtitle = paceLabel,
                color = paceColor,
                modifier = Modifier.weight(1f)
            )
            
            val fillerColor = if (state.fillerCount == 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
            TelemetryCard(
                title = "MULETILLAS",
                value = "${state.fillerCount}",
                subtitle = if (state.fillerCount == 0) "Impecable" else "Filtradas",
                color = fillerColor,
                modifier = Modifier.weight(1f)
            )
            
            TelemetryCard(
                title = "PALABRAS",
                value = "${state.wordCount}",
                subtitle = "Totales",
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(18.dp))
        
        Box(contentAlignment = Alignment.Center) {
            if (state.isRecording) {
                val infinitePulse = rememberInfiniteTransition(label = "pulse_ring")
                val pulseSize by infinitePulse.animateFloat(
                    initialValue = 72f,
                    targetValue = 96f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "size"
                )
                Box(
                    modifier = Modifier
                        .size(pulseSize.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.25f))
                )
            }
            FilledIconButton(
                onClick = onToggleRecording,
                modifier = Modifier.size(76.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (state.isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (state.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (state.isRecording) "Detener Discurso" else "Iniciar Discurso",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = if (state.isRecording) "Pulsa para finalizar y generar diagnóstico" else "Pulsa el micrófono para iniciar tu discurso",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (state.fullTranscript.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Transcripción en Tiempo Real",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { clipboardManager.setText(AnnotatedString(state.fullTranscript)) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = state.fullTranscript,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (state.showSummaryDialog && state.globalRecommendation != null) {
        AlertDialog(
            onDismissRequest = onDismissSummary,
            confirmButton = {
                Button(onClick = onDismissSummary) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val textToCopy = "Calificación: ${state.speechScore}/100\n\n${state.globalRecommendation}"
                        clipboardManager.setText(AnnotatedString(textToCopy))
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copiar Informe")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${state.speechScore}",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Diagnóstico Final", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Puntuación de Oratoria: ${state.speechScore}/100",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "⏱ ${state.elapsedSeconds}s",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🗣 ${state.paceWpm} PPM",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🚫 ${state.fillerCount} muletillas",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = state.globalRecommendation ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
