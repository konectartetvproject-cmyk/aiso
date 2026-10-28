package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import com.example.viewmodel.AisoLiveViewModel
import com.example.viewmodel.defaultTeleprompterTemplates
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisoLiveScreen(
    viewModel: AisoLiveViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val isAiProcessing by viewModel.isAiProcessing.collectAsState()
    val latestFeedback by viewModel.latestFeedback.collectAsState()
    val ttsEnabled by viewModel.ttsEnabled.collectAsState()

    val teleprompterVisible by viewModel.teleprompterVisible.collectAsState()
    val teleprompterScript by viewModel.teleprompterScript.collectAsState()
    val teleprompterSpeed by viewModel.teleprompterSpeed.collectAsState()
    val teleprompterFontSize by viewModel.teleprompterFontSize.collectAsState()

    val wpm by viewModel.wpm.collectAsState()
    val fillerWordCount by viewModel.fillerWordCount.collectAsState()
    val detectedFillers by viewModel.detectedFillers.collectAsState()

    var isTeleprompterPlaying by remember { mutableStateOf(false) }
    var showEditScriptDialog by remember { mutableStateOf(false) }
    var isVoiceMetricsActive by remember { mutableStateOf(false) }

    val teleprompterScrollState = rememberScrollState()

    // Auto-scroll del Teleprompter Flotante
    LaunchedEffect(isTeleprompterPlaying, teleprompterSpeed, teleprompterVisible) {
        if (!isTeleprompterPlaying || !teleprompterVisible) return@LaunchedEffect
        while (isActive && isTeleprompterPlaying) {
            if (teleprompterScrollState.value < teleprompterScrollState.maxValue) {
                teleprompterScrollState.scrollTo(
                    (teleprompterScrollState.value + (2 * teleprompterSpeed).toInt().coerceAtLeast(1))
                        .coerceAtMost(teleprompterScrollState.maxValue)
                )
            } else {
                isTeleprompterPlaying = false
            }
            delay(45L)
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasCameraPermission = result[Manifest.permission.CAMERA] ?: hasCameraPermission
        hasAudioPermission = result[Manifest.permission.RECORD_AUDIO] ?: hasAudioPermission
    }

    LaunchedEffect(Unit) {
        val toRequest = mutableListOf<String>()
        if (!hasCameraPermission) toRequest.add(Manifest.permission.CAMERA)
        if (!hasAudioPermission) toRequest.add(Manifest.permission.RECORD_AUDIO)
        if (toRequest.isNotEmpty()) {
            permissionsLauncher.launch(toRequest.toTypedArray())
        }
    }

    // SpeechRecognizer para medir PPM y Muletillas en tiempo real
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    DisposableEffect(context, isVoiceMetricsActive, hasAudioPermission) {
        if (isVoiceMetricsActive && hasAudioPermission && SpeechRecognizer.isRecognitionAvailable(context)) {
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onError(error: Int) {
                    if (isVoiceMetricsActive) {
                        try {
                            recognizer.startListening(intent)
                        } catch (_: Exception) {}
                    }
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val spoken = matches?.firstOrNull().orEmpty()
                    if (spoken.isNotBlank()) {
                        viewModel.processRecognizedSpeechChunk(spoken)
                    }
                    if (isVoiceMetricsActive) {
                        try {
                            recognizer.startListening(intent)
                        } catch (_: Exception) {}
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
            try {
                viewModel.startSpeechMetricsSession()
                recognizer.startListening(intent)
            } catch (_: Exception) {}
            speechRecognizer = recognizer
        }
        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
            speechRecognizer = null
        }
    }

    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    if (showEditScriptDialog) {
        var draftScript by remember { mutableStateOf(teleprompterScript) }
        AlertDialog(
            onDismissRequest = { showEditScriptDialog = false },
            title = {
                Text("Guion del Teleprompter", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Plantillas del libro «Los secretos del orador exitoso»:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkTextSubtitle
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        defaultTeleprompterTemplates.forEach { tpl ->
                            FilterChip(
                                selected = draftScript == tpl.script,
                                onClick = { draftScript = tpl.script },
                                label = { Text(tpl.title, fontSize = 11.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = draftScript,
                        onValueChange = { draftScript = it },
                        label = { Text("Tu discurso o puntos clave") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .testTag("teleprompter_script_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateTeleprompterScript(draftScript)
                        showEditScriptDialog = false
                    }
                ) {
                    Text("Guardar Guion", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditScriptDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(CoralWarning)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Entrenador en Vivo + Teleprompter",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Cámara • Ritmo PPM • Detector de Muletillas",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("live_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                actions = {
                    // Mostrar/Ocultar Teleprompter
                    IconButton(
                        onClick = { viewModel.toggleTeleprompter() },
                        modifier = Modifier.testTag("toggle_teleprompter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Subtitles,
                            contentDescription = "Mostrar u ocultar Teleprompter",
                            tint = if (teleprompterVisible) AmberGold else Color.Gray
                        )
                    }
                    // Activar/Silenciar voz del tutor
                    IconButton(onClick = { viewModel.toggleTts() }) {
                        Icon(
                            imageVector = if (ttsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Activar/Silenciar voz de AISO",
                            tint = if (ttsEnabled) AmberGold else Color.Gray
                        )
                    }
                    // Alternar cámara frontal/trasera
                    IconButton(onClick = {
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        } else {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        }
                    }) {
                        Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Cambiar cámara", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground.copy(alpha = 0.92f)
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (hasCameraPermission) {
                // Vista de la cámara con CameraX
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            previewViewRef = this
                            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = view.surfaceProvider
                            }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                )

                // CAPA SUPERIOR: TELEPROMPTER FLOTANTE BAJO LA LENTE + MÉTRICAS DE VOZ EN VIVO
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Barra de Métricas de Ritmo (PPM) y Muletillas en Vivo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Activar Medidor de Voz (PPM + Muletillas)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isVoiceMetricsActive) CoralWarning else DarkSurface.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .testTag("toggle_voice_metrics_button")
                                .clickable {
                                    if (!hasAudioPermission) {
                                        permissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                                    } else {
                                        isVoiceMetricsActive = !isVoiceMetricsActive
                                        if (isVoiceMetricsActive) {
                                            isTeleprompterPlaying = true
                                        }
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isVoiceMetricsActive) Icons.Default.Mic else Icons.Default.MicNone,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isVoiceMetricsActive) "Escuchando Voz" else "Medir PPM/Muletillas",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Píldoras de PPM y Muletillas
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurface.copy(alpha = 0.88f),
                                border = BorderStroke(
                                    1.dp,
                                    if (wpm in 115..155) EmeraldSuccess else AmberGold
                                )
                            ) {
                                Text(
                                    text = "Ritmo: ${if (wpm > 0) "$wpm PPM" else "130 PPM ideal"}",
                                    color = if (wpm in 115..155) EmeraldSuccess else AmberGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurface.copy(alpha = 0.88f),
                                border = BorderStroke(
                                    1.dp,
                                    if (fillerWordCount == 0) EmeraldSuccess else CoralWarning
                                )
                            ) {
                                Text(
                                    text = "Muletillas: $fillerWordCount",
                                    color = if (fillerWordCount == 0) EmeraldSuccess else CoralWarning,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    if (detectedFillers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Detectadas: ${detectedFillers.joinToString(", ") { "«$it»" }}",
                            color = CoralWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // TELEPROMPTER FLOTANTE TRANSPARENTE (Justo debajo de la cámara frontal)
                    AnimatedVisibility(visible = teleprompterVisible) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = DarkBackground.copy(alpha = 0.76f),
                            border = BorderStroke(1.2.dp, AmberGold.copy(alpha = 0.55f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 185.dp)
                                .testTag("floating_teleprompter_card")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Controles del Teleprompter
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = AmberGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "TELEPROMPTER BAJO LENTE",
                                            color = AmberGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Velocidad - / +
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.14f),
                                            modifier = Modifier.clickable {
                                                val nextSpeed = when {
                                                    teleprompterSpeed < 1.0f -> 1.0f
                                                    teleprompterSpeed < 1.5f -> 1.5f
                                                    teleprompterSpeed < 2.0f -> 2.0f
                                                    else -> 0.5f
                                                }
                                                viewModel.setTeleprompterSpeed(nextSpeed)
                                            }
                                        ) {
                                            Text(
                                                text = "${teleprompterSpeed}x",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }

                                        // Tamaño de letra
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.14f),
                                            modifier = Modifier.clickable { viewModel.cycleTeleprompterFontSize() }
                                        ) {
                                            Text(
                                                text = "Aa",
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }

                                        // Editar guion
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White.copy(alpha = 0.14f),
                                            modifier = Modifier
                                                .testTag("edit_teleprompter_button")
                                                .clickable { showEditScriptDialog = true }
                                        ) {
                                            Text(
                                                text = "Guion",
                                                color = AmberGold,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }

                                        // Play / Pause Auto-scroll
                                        Surface(
                                            shape = CircleShape,
                                            color = AmberGold,
                                            modifier = Modifier
                                                .size(26.dp)
                                                .testTag("play_pause_teleprompter_button")
                                                .clickable { isTeleprompterPlaying = !isTeleprompterPlaying }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (isTeleprompterPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                    contentDescription = "Auto-scroll",
                                                    tint = MidnightIndigo,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Texto deslizable del discurso
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .verticalScroll(teleprompterScrollState)
                                ) {
                                    Text(
                                        text = teleprompterScript,
                                        color = Color.White,
                                        fontSize = teleprompterFontSize.sp,
                                        lineHeight = (teleprompterFontSize + 6).sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                // Panel flotante inferior de retroalimentación inmediata con IA
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, DarkBackground.copy(alpha = 0.95f))
                            )
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(
                        visible = latestFeedback != null,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        latestFeedback?.let { feedback ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DarkSurface.copy(alpha = 0.95f),
                                border = BorderStroke(1.dp, AmberGold.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Feedback AISO IA", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            MetricChip(label = "Postura", score = feedback.postureScore)
                                            MetricChip(label = "Mirada", score = feedback.eyeContactScore)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = feedback.feedbackText,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val bitmap: Bitmap? = previewViewRef?.bitmap
                            if (bitmap != null) {
                                viewModel.analyzeFrame(bitmap)
                            }
                        },
                        enabled = !isAiProcessing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberGold,
                            contentColor = MidnightIndigo
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(52.dp)
                            .testTag("btn_instant_ai_analysis")
                    ) {
                        if (isAiProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MidnightIndigo,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Analizando postura y ritmo...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = MidnightIndigo)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Evaluar Postura, Mirada y Ritmo con IA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Mensaje si no hay permisos de cámara
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideocamOff,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Permiso de Cámara Requerido",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Para que AISO analice tu postura, contacto visual y gestos junto al Teleprompter en vivo, concede acceso a la cámara.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            permissionsLauncher.launch(
                                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = MidnightIndigo)
                    ) {
                        Text("Conceder Permisos", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricChip(label: String, score: Int) {
    Surface(
        color = EmeraldSuccess.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$label: $score%",
                color = EmeraldSuccess,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
