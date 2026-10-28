package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Locale

enum class BreathingPhase(val title: String, val targetScale: Float, val voiceCue: String) {
    IDLE("Listo para Empezar", 1f, ""),
    INHALE("Inhala por la nariz", 1.85f, "Inhala profundo al abdomen"),
    HOLD_IN("Sostén el aire", 1.85f, "Sostén"),
    EXHALE("Exhala controlado", 1.0f, "Exhala suavemente"),
    HOLD_OUT("Pausa en vacío", 1.0f, "Pausa")
}

enum class BreathingMode(
    val id: String,
    val title: String,
    val shortLabel: String,
    val subtitle: String,
    val inhaleSec: Int,
    val holdInSec: Int,
    val exhaleSec: Int,
    val holdOutSec: Int,
    val bookInsight: String
) {
    DIAPHRAGMATIC(
        id = "diaphragmatic",
        title = "Respiración Diafragmática (4-2-6-2)",
        shortLabel = "Diafragmática",
        subtitle = "Expande la base abdominal para dar resonancia, volumen y sostén sin fatigar la garganta.",
        inhaleSec = 4,
        holdInSec = 2,
        exhaleSec = 6,
        holdOutSec = 2,
        bookInsight = "Principio del Libro: La voz de autoridad nace en el diafragma, no en las cuerdas vocales."
    ),
    BOX_BREATHING(
        id = "box",
        title = "Respiración de Caja (4-4-4-4)",
        shortLabel = "Caja 4x4",
        subtitle = "Técnica táctica para reducir el ritmo cardíaco en 90 segundos y transformar el miedo escénico en enfoque.",
        inhaleSec = 4,
        holdInSec = 4,
        exhaleSec = 4,
        holdOutSec = 4,
        bookInsight = "Principio del Libro: Domina tu Inteligencia Intrapersonal regulando tu fisiología antes de pisar el escenario."
    ),
    VOCAL_WARMUP(
        id = "vocal_warmup",
        title = "Ritual Vocal Pre-Escenario (2 Min)",
        shortLabel = "Calentamiento Vocal",
        subtitle = "Circuito guiado de 4 estaciones: Trinos labiales, Ejercicio del Lápiz (3 posiciones), Sirenas y Postura de Poder.",
        inhaleSec = 4,
        holdInSec = 2,
        exhaleSec = 6,
        holdOutSec = 2,
        bookInsight = "Principio del Libro: Un orador profesional jamás sale al podio con los músculos articulatorios fríos."
    )
}

data class VocalWarmupStep(
    val stepNumber: Int,
    val title: String,
    val durationSeconds: Int,
    val instruction: String,
    val practicePhrase: String,
    val bookTip: String
)

private val preStageWarmupSteps = listOf(
    VocalWarmupStep(
        stepNumber = 1,
        title = "1. Vibración Labial y Relajación Mandibular",
        durationSeconds = 30,
        instruction = "Relaja los hombros, toma aire al diafragma y haz vibrar tus labios produciendo el sonido 'Brrrrr' subiendo y bajando suavemente de tono.",
        practicePhrase = "Brrrrr... (suelta toda tensión en labios y mejillas) ... Mmmmm-ma-me-mi-mo-mu.",
        bookTip = "Libera la rigidez facial producida por la adrenalina previa al discurso."
    ),
    VocalWarmupStep(
        stepNumber = 2,
        title = "2. Ejercicio del Lápiz (3 Posiciones del Libro)",
        durationSeconds = 45,
        instruction = "Sostén un lápiz limpio entre los dientes (15s en incisivos frontales, 15s en caninos, 15s al fondo en molares) y lee en voz alta exagerando lengua y labios:",
        practicePhrase = "«El poder de la palabra hablada transforma mentes cuando cada consonante resuena con nitidez, firmeza y convicción absoluta.»",
        bookTip = "Al retirar el lápiz sentirás una agilidad articulatoria inmediata y cero trabas."
    ),
    VocalWarmupStep(
        stepNumber = 3,
        title = "3. Sirenas Vocales y Resonancia de Pecho",
        durationSeconds = 25,
        instruction = "Coloca una mano en tu pecho y emite una 'M' prolongada pasando a vocales abiertas. Siente la vibración en el esternón.",
        practicePhrase = "Mmmmmmm-Aaaaaah • Mmmmmmm-Ooooooh • Proyectando hacia la última fila de la sala.",
        bookTip = "El 38% del impacto de tu comunicación proviene del tono, timbre y proyección de tu voz."
    ),
    VocalWarmupStep(
        stepNumber = 4,
        title = "4. Anclaje de la Trinidad de la Influencia",
        durationSeconds = 20,
        instruction = "Adopta la 'Columna de Oro': pies firmes al ancho de los hombros, pecho abierto, mirada al frente y repite con voz firme:",
        practicePhrase = "«Domino mi mente (Intrapersonal), conecto con empatía (Interpersonal) y transmito pasión auténtica (Emocional). ¡Estoy listo!»",
        bookTip = "Transforma la adrenalina en entusiasmo contagioso antes de pronunciar tu primera frase gancho."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingExerciseScreen(onBack: () -> Unit) {
    BackHandler { onBack() }

    val context = LocalContext.current
    var selectedMode by remember { mutableStateOf(BreathingMode.DIAPHRAGMATIC) }
    var voiceGuidanceEnabled by remember { mutableStateOf(true) }

    // Estado para modos de respiración (Diafragmática y Caja)
    var isRunning by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathingPhase.IDLE) }
    var secondsLeftInPhase by remember { mutableIntStateOf(0) }
    var cycleCount by remember { mutableIntStateOf(0) }

    // Estado para el modo de Calentamiento Vocal Pre-Escenario
    var currentWarmupIndex by remember { mutableIntStateOf(0) }
    var isWarmupRunning by remember { mutableStateOf(false) }
    var warmupSecondsRemaining by remember {
        mutableIntStateOf(preStageWarmupSteps[0].durationSeconds)
    }

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

    fun speakCue(text: String) {
        if (voiceGuidanceEnabled && isTtsReady && text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "breathing_cue")
        }
    }

    fun phaseDurationSec(mode: BreathingMode, phase: BreathingPhase): Int {
        return when (phase) {
            BreathingPhase.IDLE -> 0
            BreathingPhase.INHALE -> mode.inhaleSec
            BreathingPhase.HOLD_IN -> mode.holdInSec
            BreathingPhase.EXHALE -> mode.exhaleSec
            BreathingPhase.HOLD_OUT -> mode.holdOutSec
        }
    }

    // Animación fluida del círculo respiratorio
    val activePhaseDurationMs = remember(selectedMode, currentPhase, isRunning) {
        if (!isRunning || currentPhase == BreathingPhase.IDLE) 500
        else phaseDurationSec(selectedMode, currentPhase) * 1000
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (isRunning) currentPhase.targetScale else 1f,
        animationSpec = tween(
            durationMillis = activePhaseDurationMs,
            easing = LinearEasing
        ),
        label = "breathing_scale"
    )

    // Loop de respiración con cuenta regresiva por segundo
    LaunchedEffect(isRunning, selectedMode) {
        if (!isRunning || selectedMode == BreathingMode.VOCAL_WARMUP) {
            currentPhase = BreathingPhase.IDLE
            secondsLeftInPhase = 0
            return@LaunchedEffect
        }

        val phasesOrder = listOf(
            BreathingPhase.INHALE,
            BreathingPhase.HOLD_IN,
            BreathingPhase.EXHALE,
            BreathingPhase.HOLD_OUT
        )

        var phaseIdx = 0
        while (isRunning && selectedMode != BreathingMode.VOCAL_WARMUP) {
            val phase = phasesOrder[phaseIdx]
            currentPhase = phase
            val totalSec = phaseDurationSec(selectedMode, phase)
            speakCue(phase.voiceCue)

            for (sec in totalSec downTo 1) {
                secondsLeftInPhase = sec
                delay(1000L)
                if (!isRunning) break
            }

            if (isRunning) {
                if (phase == BreathingPhase.HOLD_OUT) {
                    cycleCount++
                }
                phaseIdx = (phaseIdx + 1) % phasesOrder.size
            }
        }
    }

    // Loop de temporizador del Calentamiento Vocal Pre-Escenario
    LaunchedEffect(isWarmupRunning, currentWarmupIndex, selectedMode) {
        if (!isWarmupRunning || selectedMode != BreathingMode.VOCAL_WARMUP) {
            return@LaunchedEffect
        }
        val step = preStageWarmupSteps[currentWarmupIndex]
        if (warmupSecondsRemaining == step.durationSeconds) {
            speakCue("${step.title}. ${step.instruction}")
        }
        while (isWarmupRunning && warmupSecondsRemaining > 0) {
            delay(1000L)
            warmupSecondsRemaining--
        }
        if (isWarmupRunning && warmupSecondsRemaining == 0) {
            if (currentWarmupIndex < preStageWarmupSteps.lastIndex) {
                currentWarmupIndex++
                warmupSecondsRemaining = preStageWarmupSteps[currentWarmupIndex].durationSeconds
            } else {
                isWarmupRunning = false
                speakCue("¡Calentamiento completado! Tu voz y tu mente están listas para triunfar en el escenario.")
            }
        }
    }

    Scaffold(
        containerColor = LightAppBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Respiración y Calentamiento Vocal",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = DarkTextTitle
                        )
                        Text(
                            "Ritual pre-escenario • Los secretos del orador exitoso",
                            fontSize = 11.sp,
                            color = DarkTextSubtitle
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("breathing_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = DarkTextTitle)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            voiceGuidanceEnabled = !voiceGuidanceEnabled
                            if (!voiceGuidanceEnabled) tts?.stop()
                        },
                        modifier = Modifier.testTag("toggle_breathing_voice")
                    ) {
                        Icon(
                            imageVector = if (voiceGuidanceEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (voiceGuidanceEnabled) "Silenciar guía de voz" else "Activar guía de voz",
                            tint = if (voiceGuidanceEnabled) Color(0xFF0284C7) else Color.Gray
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Selector de los 3 Modos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BreathingMode.entries.forEach { mode ->
                    val isSelected = selectedMode == mode
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) Color(0xFF0284C7) else Color.White,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) Color(0xFF0284C7) else Color(0xFFCBD5E1)
                        ),
                        shadowElevation = if (isSelected) 3.dp else 0.dp,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("mode_tab_${mode.id}")
                            .clickable {
                                isRunning = false
                                isWarmupRunning = false
                                tts?.stop()
                                selectedMode = mode
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (mode) {
                                    BreathingMode.DIAPHRAGMATIC -> Icons.Default.Air
                                    BreathingMode.BOX_BREATHING -> Icons.Default.GridView
                                    BreathingMode.VOCAL_WARMUP -> Icons.Default.RecordVoiceOver
                                },
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mode.shortLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else DarkTextTitle,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta descriptiva del modo actual
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = selectedMode.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextTitle
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = selectedMode.subtitle,
                        fontSize = 13.sp,
                        color = DarkTextSubtitle,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0F9FF),
                        border = BorderStroke(1.dp, Color(0xFFBAE6FD))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedMode.bookInsight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0369A1)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedMode != BreathingMode.VOCAL_WARMUP) {
                // Visualizador de Respiración (Diafragmática o Caja)
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Anillo guía exterior
                    Box(
                        modifier = Modifier
                            .size(235.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0284C7).copy(alpha = 0.10f))
                            .border(2.dp, Color(0xFF0284C7).copy(alpha = 0.25f), CircleShape)
                    )

                    // Esfera dinámica animada
                    Box(
                        modifier = Modifier
                            .size(120.dp * animatedScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0369A1))
                                )
                            )
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentPhase.title,
                            color = if (isRunning) Color.White else DarkTextTitle,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        if (isRunning && secondsLeftInPhase > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${secondsLeftInPhase}s",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 34.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Indicadores de tiempos del ciclo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PhasePill("Inhala", "${selectedMode.inhaleSec}s", currentPhase == BreathingPhase.INHALE)
                    PhasePill("Sostén", "${selectedMode.holdInSec}s", currentPhase == BreathingPhase.HOLD_IN)
                    PhasePill("Exhala", "${selectedMode.exhaleSec}s", currentPhase == BreathingPhase.EXHALE)
                    PhasePill("Pausa", "${selectedMode.holdOutSec}s", currentPhase == BreathingPhase.HOLD_OUT)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ciclos completados en esta sesión: $cycleCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0369A1)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        isRunning = !isRunning
                        if (!isRunning) tts?.stop()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_stop_breathing_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) CoralWarning else Color(0xFF0284C7),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRunning) "Detener Respiración" else "Iniciar ${selectedMode.shortLabel}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // MODO 3: CALENTAMIENTO VOCAL PRE-ESCENARIO (2 MINUTOS)
                val currentStep = preStageWarmupSteps[currentWarmupIndex]

                // Barra de progreso de las 4 estaciones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    preStageWarmupSteps.forEachIndexed { idx, step ->
                        val active = idx == currentWarmupIndex
                        val completed = idx < currentWarmupIndex
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                active -> Color(0xFF0284C7)
                                completed -> EmeraldSuccess
                                else -> Color(0xFFE2E8F0)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clickable {
                                    isWarmupRunning = false
                                    currentWarmupIndex = idx
                                    warmupSecondsRemaining = step.durationSeconds
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Paso ${step.stepNumber}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (active || completed) Color.White else DarkTextSubtitle
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(targetState = currentStep, label = "warmup_step") { step ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.5.dp, Color(0xFF0284C7).copy(alpha = 0.35f)),
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF0F9FF),
                                border = BorderStroke(2.dp, Color(0xFF0284C7)),
                                modifier = Modifier.size(84.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${warmupSecondsRemaining}s",
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0284C7)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = step.title,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkTextTitle,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = step.instruction,
                                fontSize = 14.sp,
                                color = DarkTextSubtitle,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF1E1B4B),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "EJERCICIO EN VOZ ALTA:",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = AmberGold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = step.practicePhrase,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "💡 ${step.bookTip}",
                                fontSize = 12.sp,
                                color = Color(0xFF0369A1),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            isWarmupRunning = false
                            warmupSecondsRemaining = currentStep.durationSeconds
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("reset_warmup_step_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reiniciar", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isWarmupRunning = !isWarmupRunning },
                        modifier = Modifier
                            .weight(1.6f)
                            .height(52.dp)
                            .testTag("start_stop_warmup_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isWarmupRunning) CoralWarning else Color(0xFF0284C7)
                        )
                    ) {
                        Icon(
                            imageVector = if (isWarmupRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isWarmupRunning) "Pausar Paso" else "Iniciar Paso ${currentStep.stepNumber}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PhasePill(label: String, secondsText: String, isActive: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isActive) Color(0xFF0284C7) else Color.White,
        border = BorderStroke(1.dp, if (isActive) Color(0xFF0284C7) else Color(0xFFCBD5E1))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isActive) Color.White else DarkTextSubtitle
            )
            Text(
                text = secondsText,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isActive) Color.White else DarkTextTitle
            )
        }
    }
}
