package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.viewmodel.AudienceSimulatorViewModel
import com.example.viewmodel.audienceArchetypeOptions
import java.util.Locale

private val suggestedTopics = listOf(
    "Liderazgo en tiempos de cambio",
    "Inteligencia Artificial aplicada",
    "Presentación de proyecto e inversión",
    "Comunicación persuasiva y ventas"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudienceSimulatorScreen(
    onBack: () -> Unit,
    viewModel: AudienceSimulatorViewModel = viewModel()
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val topic by viewModel.topic.collectAsState()
    val selectedArchetype by viewModel.selectedArchetype.collectAsState()
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val isGeneratingQuestion by viewModel.isGeneratingQuestion.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val error by viewModel.error.collectAsState()

    var voiceEnabled by remember { mutableStateOf(true) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isTtsReady by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
            }
        }
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    LaunchedEffect(isTtsReady) {
        if (isTtsReady) {
            tts?.language = Locale("es", "ES")
        }
    }

    // Reproducir con voz la pregunta apenas se genera
    LaunchedEffect(currentQuestion, isTtsReady) {
        val questionText = currentQuestion
        if (!questionText.isNullOrBlank() && voiceEnabled && isTtsReady) {
            tts?.speak(questionText, TextToSpeech.QUEUE_FLUSH, null, "audience_question_tts")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                tts?.stop()
                viewModel.startRecording(context)
            } else {
                Toast.makeText(
                    context,
                    "Se requiere permiso de micrófono para grabar tu respuesta.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    Scaffold(
        containerColor = LightAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Simulador de Preguntas con Voz",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = DarkTextTitle
                        )
                        Text(
                            "Método E.R.R.G. • Perfiles del Libro",
                            fontSize = 11.sp,
                            color = DarkTextSubtitle
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            tts?.stop()
                            onBack()
                        },
                        modifier = Modifier.testTag("audience_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = DarkTextTitle)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            voiceEnabled = !voiceEnabled
                            if (!voiceEnabled) tts?.stop()
                        },
                        modifier = Modifier.testTag("audience_tts_toggle")
                    ) {
                        Icon(
                            imageVector = if (voiceEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Activar o silenciar voz del público",
                            tint = if (voiceEnabled) Color(0xFF0D9488) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // 1. Selector de Arquetipo del Público (Ernesto, Susana, Nicanor, Laura, General)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "1. Elige quién te preguntará:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkTextTitle
                )
                Text(
                    text = "Desliza →",
                    fontSize = 11.sp,
                    color = DarkTextSubtitle
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                audienceArchetypeOptions.forEach { option ->
                    val isSelected = selectedArchetype.id == option.id
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF0F766E) else Color.White,
                        border = BorderStroke(
                            1.5.dp,
                            if (isSelected) Color(0xFF14B8A6) else Color(0xFFCBD5E1)
                        ),
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier
                            .width(210.dp)
                            .testTag("archetype_card_${option.id}")
                            .clickable { viewModel.selectArchetype(option) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelected) AmberGold else Color(0xFFF1F5F9)
                                ) {
                                    Text(
                                        text = option.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isSelected) MidnightIndigo else DarkTextSubtitle,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AmberGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = option.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) Color.White else DarkTextTitle
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = option.description,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White.copy(alpha = 0.88f) else DarkTextSubtitle,
                                lineHeight = 15.sp,
                                maxLines = 3
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Táctica recomendada del libro para el arquetipo activo
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF0FDFA),
                border = BorderStroke(1.dp, Color(0xFF99F6E4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF0F766E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Táctica contra ${selectedArchetype.name}: ${selectedArchetype.bookTactic}",
                        fontSize = 12.sp,
                        color = Color(0xFF115E59),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Tema del Discurso + Chips Rápidos
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "2. Tema de tu exposición:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkTextTitle
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = topic,
                    onValueChange = { viewModel.setTopic(it) },
                    label = { Text("Tema de tu presentación") },
                    placeholder = { Text("Ej: Transformación Digital en Salud") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("audience_topic_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestedTopics.forEach { sample ->
                        SuggestionChip(
                            onClick = { viewModel.setTopic(sample) },
                            label = { Text(sample, fontSize = 11.sp) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    tts?.stop()
                    viewModel.generateQuestion()
                },
                enabled = topic.isNotBlank() && !isGeneratingQuestion && !isRecording && !isAnalyzing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_audience_question_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
            ) {
                if (isGeneratingQuestion) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.5.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("El público está levantando la mano...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Recibir Pregunta con Voz", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Tarjeta de la Pregunta Hablada + Grabación de Respuesta
            AnimatedVisibility(visible = currentQuestion != null) {
                currentQuestion?.let { questionText ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            shape = RoundedCornerShape(22.dp),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF134E4A), Color(0xFF0F766E))
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = AmberGold,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${selectedArchetype.name} pregunta:",
                                                color = AmberGold,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 13.sp
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                if (isTtsReady) {
                                                    tts?.speak(questionText, TextToSpeech.QUEUE_FLUSH, null, "replay_q")
                                                }
                                            },
                                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White),
                                            modifier = Modifier.testTag("replay_question_voice_button")
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Repetir Voz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "«$questionText»",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        if (isAnalyzing) {
                            CircularProgressIndicator(color = Color(0xFF0F766E))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Evaluando tu respuesta con el Método E.R.R.G....",
                                color = Color(0xFF0F766E),
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            // Botón de micrófono con contador en vivo
                            Button(
                                onClick = {
                                    if (isRecording) {
                                        viewModel.stopRecordingAndAnalyze()
                                    } else {
                                        tts?.stop()
                                        if (ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.RECORD_AUDIO
                                            ) == PackageManager.PERMISSION_GRANTED
                                        ) {
                                            viewModel.startRecording(context)
                                        } else {
                                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape)
                                    .testTag("record_audience_answer_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isRecording) CoralWarning else Color(0xFF0F766E)
                                )
                            ) {
                                Icon(
                                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = if (isRecording) "Detener y Evaluar" else "Responder con Voz",
                                    modifier = Modifier.size(38.dp),
                                    tint = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isRecording) {
                                    "Grabando respuesta (${recordingSeconds}s)... Toca para evaluar"
                                } else {
                                    "Toca el micrófono para defender tu postura (20s - 45s recomendados)"
                                },
                                fontSize = 13.sp,
                                fontWeight = if (isRecording) FontWeight.Bold else FontWeight.Medium,
                                color = if (isRecording) CoralWarning else DarkTextSubtitle,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (feedback != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White,
                                border = BorderStroke(1.5.dp, Color(0xFF10B981)),
                                shadowElevation = 3.dp
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = null,
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Diagnóstico AISO (Método E.R.R.G.)",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF065F46)
                                        )
                                    }
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = feedback!!,
                                        color = DarkTextTitle,
                                        fontSize = 13.sp,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
