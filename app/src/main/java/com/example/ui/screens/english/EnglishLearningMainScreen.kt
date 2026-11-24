package com.example.ui.screens.english

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.AmberGold
import com.example.ui.theme.MidnightIndigo
import com.example.ui.theme.RoyalPurple
import kotlinx.coroutines.delay
import java.util.Locale

enum class EnglishSection {
    HUB,
    VOCAB_PHRASES,
    PRONUNCIATION_LAB,
    BUSINESS_PITCH,
    IMPROMPTU_EN,
    QA_DEFENSE_EN,
    QUIZ_EN
}

data class EnglishModuleItem(
    val section: EnglishSection,
    val title: String,
    val subtitle: String,
    val badge: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnglishLearningMainScreen(
    onNavigateToRoute: (String) -> Unit = {},
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var currentSection by remember { mutableStateOf(EnglishSection.HUB) }
    val learnedPhraseIds = remember { mutableStateListOf<String>() }

    // TextToSpeech configurado en Inglés (US)
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale.US
                instance?.setSpeechRate(0.95f)
            }
        }
        tts = instance
        onDispose {
            instance?.stop()
            instance?.shutdown()
        }
    }

    fun speakEnglish(text: String, rate: Float = 0.95f) {
        tts?.language = Locale.US
        tts?.setSpeechRate(rate)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "en_tts_${System.currentTimeMillis()}")
    }

    // Manejo de botón atrás: si está en un submódulo regresa al HUB de inglés; si está en el HUB regresa al inicio
    BackHandler(enabled = currentSection != EnglishSection.HUB || onBack != null) {
        if (currentSection != EnglishSection.HUB) {
            tts?.stop()
            currentSection = EnglishSection.HUB
        } else {
            onBack?.invoke()
        }
    }

    // Evaluador de pronunciación por voz en inglés (en-US)
    var activePracticeTarget by remember { mutableStateOf<String?>(null) }
    var isListeningEn by remember { mutableStateOf(false) }
    var spokenTextResult by remember { mutableStateOf("") }
    var pronunciationScore by remember { mutableStateOf<Int?>(null) }

    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer?.destroy()
        }
    }

    fun startEnglishListening(targetSentence: String) {
        activePracticeTarget = targetSentence
        spokenTextResult = ""
        pronunciationScore = null

        try {
            speechRecognizer?.destroy()
            val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer = recognizer
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isListeningEn = true
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isListeningEn = false
                }
                override fun onError(error: Int) {
                    isListeningEn = false
                }
                override fun onResults(results: Bundle?) {
                    isListeningEn = false
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val best = matches?.firstOrNull().orEmpty()
                    if (best.isNotBlank()) {
                        spokenTextResult = best
                        pronunciationScore = calculateEnglishAccuracy(targetSentence, best)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {
                    val partial = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                    if (partial.isNotBlank()) {
                        spokenTextResult = partial
                        pronunciationScore = calculateEnglishAccuracy(targetSentence, partial)
                    }
                }
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-US")
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            }
            recognizer.startListening(intent)
        } catch (_: Exception) {
            isListeningEn = false
            Toast.makeText(context, "Reconocimiento de voz interno no disponible", Toast.LENGTH_SHORT).show()
        }
    }

    var pendingTargetForPermission by remember { mutableStateOf<String?>(null) }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingTargetForPermission?.let { startEnglishListening(it) }
        } else {
            Toast.makeText(context, "Activa el permiso de micrófono para practicar tu pronunciación en inglés", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestEnglishPronunciationTest(targetSentence: String) {
        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (hasPerm) {
            startEnglishListening(targetSentence)
        } else {
            pendingTargetForPermission = targetSentence
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = Color(0xFF4F46E5),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = when (currentSection) {
                                    EnglishSection.HUB -> "Academia de Oratoria en Inglés"
                                    EnglishSection.VOCAB_PHRASES -> "Frases & Vocabulario (Audio US)"
                                    EnglishSection.PRONUNCIATION_LAB -> "Laboratorio de Pronunciación"
                                    EnglishSection.BUSINESS_PITCH -> "Business Pitch & Discursos"
                                    EnglishSection.IMPROMPTU_EN -> "Impromptu 60s in English"
                                    EnglishSection.QA_DEFENSE_EN -> "Q&A & Defense in English"
                                    EnglishSection.QUIZ_EN -> "Quiz de Inglés Escénico"
                                },
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Aprende pronunciación, estructuras AISO y fluidez en inglés",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    if (currentSection != EnglishSection.HUB || onBack != null) {
                        IconButton(
                            onClick = {
                                if (currentSection != EnglishSection.HUB) {
                                    tts?.stop()
                                    currentSection = EnglishSection.HUB
                                } else {
                                    onBack?.invoke()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Panel flotante cuando el usuario está probando su pronunciación en inglés con el micrófono
            AnimatedVisibility(visible = activePracticeTarget != null) {
                activePracticeTarget?.let { target ->
                    EnglishPronunciationFeedbackBanner(
                        targetSentence = target,
                        isListening = isListeningEn,
                        spokenText = spokenTextResult,
                        score = pronunciationScore,
                        onListenNative = { speakEnglish(target, 0.92f) },
                        onListenSlow = { speakEnglish(target, 0.70f) },
                        onRetryMic = { requestEnglishPronunciationTest(target) },
                        onClose = {
                            speechRecognizer?.stopListening()
                            isListeningEn = false
                            activePracticeTarget = null
                        }
                    )
                }
            }

            when (currentSection) {
                EnglishSection.HUB -> {
                    EnglishAcademyHubView(
                        learnedCount = learnedPhraseIds.size,
                        totalPhrases = EnglishLearningCatalog.phrases.size,
                        onSelectSection = { section ->
                            activePracticeTarget = null
                            currentSection = section
                        },
                        onSpeakSample = { speakEnglish("Welcome to the AISO English Public Speaking Academy. Speak from the heart!", 0.92f) }
                    )
                }
                EnglishSection.VOCAB_PHRASES -> {
                    EnglishVocabPhrasesView(
                        learnedIds = learnedPhraseIds,
                        onToggleLearned = { id ->
                            if (learnedPhraseIds.contains(id)) learnedPhraseIds.remove(id)
                            else learnedPhraseIds.add(id)
                        },
                        onSpeakNormal = { text -> speakEnglish(text, 0.95f) },
                        onSpeakSlow = { text -> speakEnglish(text, 0.70f) },
                        onPracticeMic = { text -> requestEnglishPronunciationTest(text) }
                    )
                }
                EnglishSection.PRONUNCIATION_LAB -> {
                    EnglishPronunciationLabView(
                        onSpeakNormal = { text -> speakEnglish(text, 0.95f) },
                        onSpeakSlow = { text -> speakEnglish(text, 0.68f) },
                        onPracticeMic = { text -> requestEnglishPronunciationTest(text) }
                    )
                }
                EnglishSection.BUSINESS_PITCH -> {
                    EnglishBusinessPitchView(
                        onSpeakNormal = { text -> speakEnglish(text, 0.94f) },
                        onSpeakSlow = { text -> speakEnglish(text, 0.72f) },
                        onPracticeMic = { text -> requestEnglishPronunciationTest(text) }
                    )
                }
                EnglishSection.IMPROMPTU_EN -> {
                    EnglishImpromptuChallengeView(
                        onSpeakNormal = { text -> speakEnglish(text, 0.95f) },
                        onPracticeMic = { text -> requestEnglishPronunciationTest(text) }
                    )
                }
                EnglishSection.QA_DEFENSE_EN -> {
                    EnglishQaSimulatorView(
                        onSpeakNormal = { text -> speakEnglish(text, 0.94f) },
                        onSpeakSlow = { text -> speakEnglish(text, 0.72f) },
                        onPracticeMic = { text -> requestEnglishPronunciationTest(text) }
                    )
                }
                EnglishSection.QUIZ_EN -> {
                    EnglishOratoryQuizView(
                        onSpeakEnglish = { text -> speakEnglish(text, 0.94f) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EnglishPronunciationFeedbackBanner(
    targetSentence: String,
    isListening: Boolean,
    spokenText: String,
    score: Int?,
    onListenNative: () -> Unit,
    onListenSlow: () -> Unit,
    onRetryMic: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF1E1B4B),
        border = BorderStroke(1.5.dp, if (isListening) Color(0xFF34D399) else AmberGold)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (isListening) Color(0xFF34D399) else AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isListening) "Listening in English (Speak now...)" else "Evaluador de Pronunciación en Inglés",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Frase objetivo: \"$targetSentence\"",
                color = AmberGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (spokenText.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Lo que detectó el micrófono: \"$spokenText\"",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            if (score != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val badgeColor = when {
                    score >= 80 -> Color(0xFF10B981)
                    score >= 55 -> Color(0xFFF59E0B)
                    else -> Color(0xFFEF4444)
                }
                val feedbackMsg = when {
                    score >= 85 -> "¡Excelente pronunciación! Suenas claro, natural y seguro en inglés."
                    score >= 60 -> "¡Muy buen intento! Escucha el audio lento (🐢) y repite las palabras finales."
                    else -> "Sigue practicando: escucha primero el audio lento (🐢) y pronuncia cada sílaba."
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(8.dp), color = badgeColor) {
                        Text(
                            text = "Precisión: $score%",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feedbackMsg,
                        color = Color.White,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onListenNative,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Normal", color = Color.White, fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = onListenSlow,
                    border = BorderStroke(1.dp, AmberGold),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("🐢 Lento", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onRetryMic,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isListening) "Escuchando..." else "Hablar en Inglés", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EnglishAcademyHubView(
    learnedCount: Int,
    totalPhrases: Int,
    onSelectSection: (EnglishSection) -> Unit,
    onSpeakSample: () -> Unit
) {
    val modules = listOf(
        EnglishModuleItem(
            section = EnglishSection.VOCAB_PHRASES,
            title = "1. Frases & Conectores con Audio US",
            subtitle = "Aprende aperturas, conectores, negocios, Q&A y falsos amigos con fonética en español y micrófono",
            badge = "AUDIO + FONÉTICA",
            icon = Icons.Default.Translate,
            color = Color(0xFF2563EB)
        ),
        EnglishModuleItem(
            section = EnglishSection.PRONUNCIATION_LAB,
            title = "2. Laboratorio de Pronunciación (TH, V/B, -ED)",
            subtitle = "Domina los 5 sonidos del inglés que más cuestan a los hispanohablantes con guía bucal paso a paso",
            badge = "FONÉTICA GUIADA",
            icon = Icons.Default.RecordVoiceOver,
            color = Color(0xFFD97706)
        ),
        EnglishModuleItem(
            section = EnglishSection.BUSINESS_PITCH,
            title = "3. Business Pitch & Discursos Bilingües",
            subtitle = "Plantillas completas en inglés y español (Elevator Pitch, Proyectos y Keynote AISO) listas para escuchar y practicar",
            badge = "BILINGÜE EN/ES",
            icon = Icons.Default.Work,
            color = RoyalPurple
        ),
        EnglishModuleItem(
            section = EnglishSection.IMPROMPTU_EN,
            title = "4. Reto de Fluidez 60s (Impromptu in English)",
            subtitle = "Habla 60 segundos en inglés con frases de apoyo en pantalla, vocabulario sugerido y contador de palabras",
            badge = "PRÁCTICA EN VIVO",
            icon = Icons.Default.Mic,
            color = Color(0xFF059669)
        ),
        EnglishModuleItem(
            section = EnglishSection.QA_DEFENSE_EN,
            title = "5. Simulador de Preguntas (Q&A in English)",
            subtitle = "Aprende a responder en inglés a Ernesto el Erudito, Susana la Sniper y Nicanor el Negativo sin quedarte en blanco",
            badge = "SIMULADOR Q&A",
            icon = Icons.Default.Shield,
            color = Color(0xFFDC2626)
        ),
        EnglishModuleItem(
            section = EnglishSection.QUIZ_EN,
            title = "6. Quiz Interactivo de Inglés para Oradores",
            subtitle = "Pon a prueba tu dominio de conectores, pronunciación de verbos en pasado y falsos amigos en el escenario",
            badge = "EVALUACIÓN",
            icon = Icons.Default.Quiz,
            color = Color(0xFF4F46E5)
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ENGLISH FOR SPEAKERS 🌐",
                                color = AmberGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Aprende a Hablar en Público en Inglés Paso a Paso",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                        IconButton(
                            onClick = onSpeakSample,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AmberGold)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Escuchar bienvenida en inglés",
                                tint = MidnightIndigo
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Cada lección incluye pronunciación figurada para hispanohablantes, audio nativo en inglés (velocidad normal y lenta 🐢) y práctica real con micrófono.",
                        color = Color.White.copy(alpha = 0.88f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Frases dominadas: $learnedCount / $totalPhrases",
                            color = Color(0xFF6EE7B7),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { (learnedCount.toFloat() / totalPhrases.coerceAtLeast(1)).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(130.dp)
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        items(modules) { module ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectSection(module.section) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, module.color.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(module.color.copy(alpha = 0.14f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = module.icon,
                            contentDescription = null,
                            tint = module.color,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = module.color.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = module.badge,
                                color = module.color,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = module.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = module.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = module.color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnglishVocabPhrasesView(
    learnedIds: List<String>,
    onToggleLearned: (String) -> Unit,
    onSpeakNormal: (String) -> Unit,
    onSpeakSlow: (String) -> Unit,
    onPracticeMic: (String) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Todas") }
    val filteredPhrases = remember(selectedCategory) {
        if (selectedCategory == "Todas") EnglishLearningCatalog.phrases
        else EnglishLearningCatalog.phrases.filter { it.category == selectedCategory }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(EnglishLearningCatalog.phraseCategories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color(0xFF4338CA) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { selectedCategory = category }
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredPhrases, key = { it.id }) { item ->
                val isLearned = learnedIds.contains(item.id)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isLearned) Color(0xFF10B981) else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFEEF2FF)
                            ) {
                                Text(
                                    text = item.category,
                                    color = Color(0xFF4338CA),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isLearned) Color(0xFFD1FAE5) else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { onToggleLearned(item.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isLearned) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isLearned) Color(0xFF059669) else Color(0xFF64748B),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isLearned) "Aprendida" else "Marcar aprendida",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLearned) Color(0xFF065F46) else Color(0xFF475569)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Frase en inglés
                        Text(
                            text = item.english,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E1B4B),
                            lineHeight = 23.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Fonética figurada para hispanohablantes
                        Text(
                            text = "🗣️ Pronunciación: ${item.phonetics}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFD97706)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Traducción al español
                        Text(
                            text = "🇪🇸 Significado: «${item.spanish}»",
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Ejemplo real en discurso
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "EJEMPLO EN EL ESCENARIO:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF475569)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "\"${item.exampleEn}\"",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = item.exampleEs,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Consejo AISO
                        Text(
                            text = "💡 Tip AISO: ${item.tip}",
                            fontSize = 12.sp,
                            color = Color(0xFF4338CA),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Botones de acción: Escuchar Normal, Escuchar Lento, Practicar con Micrófono
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onSpeakNormal(item.english) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Escuchar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { onSpeakSlow(item.english) },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFD97706)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🐢 Lento", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }

                            Button(
                                onClick = { onPracticeMic(item.english) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                                modifier = Modifier.weight(1.1f)
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Pronunciar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnglishPronunciationLabView(
    onSpeakNormal: (String) -> Unit,
    onSpeakSlow: (String) -> Unit,
    onPracticeMic: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFFFBEB),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🎯 Técnica de Dicción Bilingüe (Cap. 16 AISO)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF92400E)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toca cualquier palabra para escuchar su sonido exacto en inglés americano, o pulsa «Probar mi Voz» para que la app evalúe tu pronunciación.",
                        fontSize = 12.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 17.sp
                    )
                }
            }
        }

        items(EnglishLearningCatalog.soundLessons, key = { it.id }) { lesson ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF4338CA)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lesson.symbol,
                                color = AmberGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = lesson.title,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Color(0xFF1E1B4B)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "👄 Posición de lengua y labios: ${lesson.spanishGuide}",
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Toca cada palabra para escucharla:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Palabras clave interactivas
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        lesson.targetWords.forEach { (wordWithMeaning, phonetic) ->
                            val englishWordOnly = wordWithMeaning.substringBefore("(").trim()
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSpeakNormal(englishWordOnly) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = wordWithMeaning,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = phonetic,
                                            fontSize = 12.sp,
                                            color = Color(0xFFD97706),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        IconButton(
                                            onClick = { onSpeakSlow(englishWordOnly) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Text("🐢", fontSize = 14.sp)
                                        }
                                        IconButton(
                                            onClick = { onSpeakNormal(englishWordOnly) },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = "Escuchar palabra",
                                                tint = Color(0xFF4338CA),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Oración de práctica escénica
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFEEF2FF),
                        border = BorderStroke(1.dp, Color(0xFFC7D2FE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ORACIÓN DE ENTRENAMIENTO:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4338CA)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${lesson.practiceSentenceEn}\"",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF1E1B4B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lesson.practiceSentencePhonetics,
                                fontSize = 12.sp,
                                color = Color(0xFFB45309),
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "🇪🇸 ${lesson.practiceSentenceEs}",
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { onSpeakNormal(lesson.practiceSentenceEn) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Escuchar", fontSize = 12.sp)
                                }
                                Button(
                                    onClick = { onPracticeMic(lesson.practiceSentenceEn) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Probar mi Voz", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnglishBusinessPitchView(
    onSpeakNormal: (String) -> Unit,
    onSpeakSlow: (String) -> Unit,
    onPracticeMic: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(EnglishLearningCatalog.pitchTemplates, key = { it.id }) { pitch ->
            val fullPitchEn = "${pitch.hookEn} ${pitch.bodyEn} ${pitch.closeEn}"
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = pitch.occasion.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RoyalPurple
                    )
                    Text(
                        text = pitch.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1B4B)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 1. APERTURA / HOOK
                    PitchPartBlock(
                        stepTitle = "1. APERTURA DE IMPACTO (HOOK)",
                        english = pitch.hookEn,
                        spanish = pitch.hookEs,
                        accentColor = Color(0xFF2563EB),
                        onListen = { onSpeakNormal(pitch.hookEn) },
                        onPractice = { onPracticeMic(pitch.hookEn) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. DESARROLLO / BODY
                    PitchPartBlock(
                        stepTitle = "2. HISTORIA Y PROPUESTA DE VALOR (BODY)",
                        english = pitch.bodyEn,
                        spanish = pitch.bodyEs,
                        accentColor = Color(0xFF7C3AED),
                        onListen = { onSpeakNormal(pitch.bodyEn) },
                        onPractice = { onPracticeMic(pitch.bodyEn) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. CIERRE / CALL TO ACTION
                    PitchPartBlock(
                        stepTitle = "3. LLAMADO A LA ACCIÓN (CLOSING)",
                        english = pitch.closeEn,
                        spanish = pitch.closeEs,
                        accentColor = Color(0xFF059669),
                        onListen = { onSpeakNormal(pitch.closeEn) },
                        onPractice = { onPracticeMic(pitch.closeEn) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "🔑 Vocabulario Clave del Discurso:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    pitch.keyVocab.forEach { vocab ->
                        Text(
                            text = "• $vocab",
                            fontSize = 12.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onSpeakNormal(fullPitchEn) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Escuchar Completo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = { onSpeakSlow(fullPitchEn) },
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("🐢 Lento", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PitchPartBlock(
    stepTitle: String,
    english: String,
    spanish: String,
    accentColor: Color,
    onListen: () -> Unit,
    onPractice: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.06f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stepTitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )
                Row {
                    IconButton(onClick = onListen, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Escuchar", tint = accentColor, modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = onPractice, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Mic, contentDescription = "Practicar", tint = Color(0xFF059669), modifier = Modifier.size(17.dp))
                    }
                }
            }
            Text(
                text = english,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "🇪🇸 $spanish",
                fontSize = 12.sp,
                color = Color(0xFF475569),
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun EnglishImpromptuChallengeView(
    onSpeakNormal: (String) -> Unit,
    onPracticeMic: (String) -> Unit
) {
    var topicIndex by remember { mutableIntStateOf(0) }
    val currentTopic = EnglishLearningCatalog.impromptuTopics[topicIndex % EnglishLearningCatalog.impromptuTopics.size]

    var timerRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(60) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        if (remainingSeconds == 0) {
            timerRunning = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "60-SECOND ENGLISH SPEAKING CHALLENGE",
                        color = Color(0xFF6EE7B7),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentTopic.topicEn,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🇪🇸 ${currentTopic.topicEs}",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "${remainingSeconds}s",
                        color = AmberGold,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                if (timerRunning) {
                                    timerRunning = false
                                } else {
                                    if (remainingSeconds == 0) remainingSeconds = 60
                                    timerRunning = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = MidnightIndigo)
                        ) {
                            Icon(if (timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (timerRunning) "Pausar" else "Iniciar 60s", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                timerRunning = false
                                remainingSeconds = 60
                                topicIndex++
                            },
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Otro Tema", color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Frases de Apoyo para Empezar sin Bloquearte (Toca para escuchar o practicar):",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    currentTopic.starterPhrases.forEach { (en, es) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = en, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                    Text(text = es, fontSize = 12.sp, color = Color(0xFF64748B))
                                }
                                Row {
                                    IconButton(onClick = { onSpeakNormal(en) }) {
                                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Escuchar", tint = Color(0xFF4338CA))
                                    }
                                    IconButton(onClick = { onPracticeMic(en) }) {
                                        Icon(Icons.Default.Mic, contentDescription = "Pronunciar", tint = Color(0xFF059669))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "2. Vocabulario Recomendado para este Tema:",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    currentTopic.suggestedVocab.forEach { (wordEn, wordEs) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSpeakNormal(wordEn) }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔊 $wordEn", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF4338CA))
                            Text(text = wordEs, fontSize = 13.sp, color = Color(0xFF475569))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnglishQaSimulatorView(
    onSpeakNormal: (String) -> Unit,
    onSpeakSlow: (String) -> Unit,
    onPracticeMic: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(EnglishLearningCatalog.qaScenarios, key = { it.id }) { qa ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = qa.personaRole.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFDC2626)
                    )
                    Text(
                        text = qa.personaName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1B4B)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pregunta del público en inglés
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF2F2),
                        border = BorderStroke(1.dp, Color(0xFFFECACA)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PREGUNTA DE LA AUDIENCIA:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFB91C1C)
                                )
                                IconButton(
                                    onClick = { onSpeakNormal(qa.questionEn) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Escuchar pregunta", tint = Color(0xFFB91C1C))
                                }
                            }
                            Text(
                                text = "\"${qa.questionEn}\"",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF7F1D1D)
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "🇪🇸 ${qa.questionEs}",
                                fontSize = 12.sp,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Frase puente recomendada
                    Text(
                        text = "🛡️ Frase Puente para Ganar Tiempo:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4338CA)
                    )
                    Text(
                        text = "\"${qa.bridgePhraseEn}\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E1B4B)
                    )
                    Text(
                        text = "🗣️ ${qa.bridgePhrasePhonetics}",
                        fontSize = 12.sp,
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Respuesta modelo completa
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFECFDF5),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "RESPUESTA EJECUTIVA MODELO (MÉTODO AISO):",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF047857)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${qa.modelAnswerEn}\"",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064E3B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🇪🇸 ${qa.modelAnswerEs}",
                                fontSize = 12.sp,
                                color = Color(0xFF065F46)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 Estrategia: ${qa.coachTip}",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { onSpeakNormal(qa.modelAnswerEn) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Oír Respuesta", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { onSpeakSlow(qa.bridgePhraseEn) },
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🐢 Puente", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onPracticeMic(qa.bridgePhraseEn) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            modifier = Modifier.weight(1.1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Responder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EnglishOratoryQuizView(
    onSpeakEnglish: (String) -> Unit
) {
    val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(EnglishLearningCatalog.quizItems) { qIndex, item ->
            val userChoice = selectedAnswers[qIndex]
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.question,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E1B4B)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    item.options.forEachIndexed { optIndex, optionText ->
                        val isSelected = userChoice == optIndex
                        val isCorrect = optIndex == item.correctIndex
                        val bgColor = when {
                            userChoice == null -> Color(0xFFF8FAFC)
                            isCorrect -> Color(0xFFD1FAE5)
                            isSelected -> Color(0xFFFEE2E2)
                            else -> Color(0xFFF8FAFC)
                        }
                        val borderColor = when {
                            userChoice == null -> Color(0xFFE2E8F0)
                            isCorrect -> Color(0xFF10B981)
                            isSelected -> Color(0xFFEF4444)
                            else -> Color(0xFFE2E8F0)
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedAnswers[qIndex] = optIndex
                                    onSpeakEnglish(optionText)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = optionText,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected || (userChoice != null && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.weight(1f)
                                )
                                if (userChoice != null && isCorrect) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    if (userChoice != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEEF2FF),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "💡 ${item.explanation}",
                                fontSize = 12.sp,
                                color = Color(0xFF312E81),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculateEnglishAccuracy(target: String, spoken: String): Int {
    val cleanTarget = target.lowercase(Locale.US).replace(Regex("[^a-z0-9\\s]"), "").split(Regex("\\s+")).filter { it.isNotBlank() }
    val cleanSpoken = spoken.lowercase(Locale.US).replace(Regex("[^a-z0-9\\s]"), "").split(Regex("\\s+")).filter { it.isNotBlank() }.toSet()
    if (cleanTarget.isEmpty()) return 0
    val matched = cleanTarget.count { word -> cleanSpoken.contains(word) }
    return ((matched.toFloat() / cleanTarget.size) * 100f).toInt().coerceIn(10, 100)
}
