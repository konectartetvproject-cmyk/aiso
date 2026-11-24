package com.example.ui.screens

import android.speech.tts.TextToSpeech
import android.view.SoundEffectConstants
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.rememberInAppSpeechController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class Bubble(
    val id: Int,
    var x: Float,
    var y: Float,
    val radius: Float,
    val text: String,
    val speed: Float,
    val color: Color,
    val isPowerWord: Boolean = false
)

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    val color: Color
)

private val voiceChallengeTopics = listOf(
    "Explica en 45 segundos por qué saber hablar en público transforma tu carrera profesional.",
    "Defiende una idea para mejorar la educación en tu ciudad sin usar ninguna muletilla.",
    "Cuenta una lección importante que hayas aprendido superando un obstáculo difícil.",
    "Presenta tu proyecto o profesión como si estuvieras frente a un inversionista clave.",
    "Habla sobre el impacto de la disciplina diaria frente a la motivación pasajera."
)

private val detectedFillersSet = setOf(
    "eh", "este", "bueno", "osea", "pues", "digamos", "básicamente", "basicamente", "tipo", "nada", "entonces", "mmm"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsquivaMuletillasGameScreen(onBack: () -> Unit) {
    BackHandler { onBack() }
    var selectedMode by remember { mutableIntStateOf(0) } // 0 = Arcade Pop, 1 = Reto de Voz en Vivo

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Esquiva Muletillas", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            if (selectedMode == 0) "Modo Arcade • Estalla Muletillas" else "Modo Voz en Vivo • Habla Fluido",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.78f)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
        ) {
            // Selector de Modo
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                SegmentedButton(
                    selected = selectedMode == 0,
                    onClick = { selectedMode = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFFE11D48),
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color(0xFF1E293B),
                        inactiveContentColor = Color(0xFFCBD5E1)
                    )
                ) {
                    Text("🎯 Arcade Pop", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                SegmentedButton(
                    selected = selectedMode == 1,
                    onClick = { selectedMode = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color(0xFF10B981),
                        activeContentColor = Color.White,
                        inactiveContainerColor = Color(0xFF1E293B),
                        inactiveContentColor = Color(0xFFCBD5E1)
                    )
                ) {
                    Text("🎙️ Reto de Voz", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            if (selectedMode == 0) {
                ArcadeBubblePopMode(modifier = Modifier.weight(1f))
            } else {
                LiveVoiceNoFillersMode(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ArcadeBubblePopMode(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val view = LocalView.current

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    DisposableEffect(context) {
        val ttsInstance = TextToSpeech(context) {}
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
    var combo by remember { mutableIntStateOf(0) }
    var slowMotionTicks by remember { mutableIntStateOf(0) }
    var isGameOver by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var statusBanner by remember {
        mutableStateOf("🔴 Estalla las muletillas rojas • 🟢 Deja pasar las técnicas verdes")
    }

    val bubbles = remember { mutableStateListOf<Bubble>() }
    val particles = remember { mutableStateListOf<Particle>() }

    val fillerWords = listOf("Eh...", "Este...", "Bueno...", "O sea", "Digamos", "Básicamente", "Tipo...", "Pues...")
    val powerWords = listOf("✨ Pausa 2s", "✨ Mirada", "✨ Voz Firme", "✨ Historia")
    val fillerColors = listOf(Color(0xFFE11D48), Color(0xFFDC2626), Color(0xFFDB2777), Color(0xFFEA580C))

    var bubbleIdCounter by remember { mutableIntStateOf(0) }
    var screenWidth by remember { mutableFloatStateOf(0f) }
    var screenHeight by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying, isGameOver) {
        if (isPlaying && !isGameOver) {
            var lastSpawnTime = System.currentTimeMillis()
            var spawnInterval = 1650L

            while (isActive) {
                val currentTime = System.currentTimeMillis()
                if (slowMotionTicks > 0) slowMotionTicks--

                if (currentTime - lastSpawnTime > spawnInterval && screenWidth > 0) {
                    val isPower = Random.nextFloat() < 0.22f
                    val radius = Random.nextFloat() * 22f + 82f
                    val x = Random.nextFloat() * (screenWidth - radius * 2).coerceAtLeast(10f) + radius
                    val baseSpeed = Random.nextFloat() * 2.4f + 3.5f + (level * 0.65f)
                    bubbles.add(
                        Bubble(
                            id = bubbleIdCounter++,
                            x = x,
                            y = -radius,
                            radius = radius,
                            text = if (isPower) powerWords.random() else fillerWords.random(),
                            speed = baseSpeed,
                            color = if (isPower) Color(0xFF10B981) else fillerColors.random(),
                            isPowerWord = isPower
                        )
                    )
                    lastSpawnTime = currentTime
                    spawnInterval = (1650L - (level * 120L)).coerceAtLeast(580L)
                }

                val speedFactor = if (slowMotionTicks > 0) 0.45f else 1f
                val bubblesToRemove = mutableListOf<Bubble>()
                for (bubble in bubbles) {
                    bubble.y += bubble.speed * speedFactor
                    if (bubble.y - bubble.radius > screenHeight - 160f) {
                        bubblesToRemove.add(bubble)
                        if (bubble.isPowerWord) {
                            score += 25
                            if (lives < 5) lives++
                            statusBanner = "🌟 ¡Absorbiste ${bubble.text}! (+25 pts)"
                        } else {
                            tts?.speak(bubble.text, TextToSpeech.QUEUE_FLUSH, null, null)
                            lives -= 1
                            combo = 0
                            statusBanner = "⚠️ El orador dijo «${bubble.text}» (-1 Vida)"
                            if (lives <= 0) {
                                isGameOver = true
                            }
                        }
                    }
                }
                bubbles.removeAll(bubblesToRemove)

                val particlesToRemove = mutableListOf<Particle>()
                for (particle in particles) {
                    particle.x += particle.vx
                    particle.y += particle.vy
                    particle.vy += 0.25f
                    particle.life -= 0.04f
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

        if (bubble.isPowerWord) {
            combo = 0
            score = (score - 10).coerceAtLeast(0)
            statusBanner = "❗ No estalles las técnicas verdes (${bubble.text}) — déjalas llegar al orador."
        } else {
            combo++
            val mult = (1 + combo / 4).coerceAtMost(4)
            val pts = 10 * mult
            score += pts
            level = (score / 120) + 1
            statusBanner = "💥 ¡Muletilla eliminada! +$pts pts" + if (mult > 1) " (Combo x$mult)" else ""
        }

        for (i in 0..18) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = Random.nextFloat() * 14f + 4f
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

    Box(modifier = modifier.fillMaxSize()) {
        if (!isPlaying && !isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¡Estalla las Muletillas!",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🔴 Toca las Muletillas Rojas («Eh...», «Este...», «O sea») antes de que lleguen al micrófono.", color = Color(0xFFFCA5A5), fontSize = 14.sp)
                        Text("🟢 Deja pasar las Técnicas Verdes («✨ Pausa 2s», «✨ Mirada») para que el orador las absorba y gane +25 pts y vida.", color = Color(0xFF86EFAC), fontSize = 14.sp)
                        Text("⏸️ Activa la «Pausa Estratégica» para ralentizar el tiempo cuando haya muchas muletillas.", color = Color(0xFF7DD3FC), fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        isPlaying = true
                        isGameOver = false
                        lives = 5
                        score = 0
                        level = 1
                        combo = 0
                        bubbles.clear()
                        particles.clear()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("¡Comenzar Partida!", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
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
                                distance <= bubble.radius + 65f
                            }
                            clickedBubbles.lastOrNull()?.let { popBubble(it) }
                        }
                    }
            ) {
                for (particle in particles) {
                    drawCircle(
                        color = particle.color.copy(alpha = particle.life),
                        radius = 11f * particle.life,
                        center = Offset(particle.x, particle.y)
                    )
                }
                for (bubble in bubbles) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(bubble.color.copy(alpha = 0.6f), bubble.color.copy(alpha = 0.95f)),
                            center = Offset(bubble.x - bubble.radius * 0.25f, bubble.y - bubble.radius * 0.25f),
                            radius = bubble.radius
                        ),
                        radius = bubble.radius,
                        center = Offset(bubble.x, bubble.y)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = bubble.radius * 0.22f,
                        center = Offset(bubble.x - bubble.radius * 0.35f, bubble.y - bubble.radius * 0.35f)
                    )
                    val textLayoutResult = textMeasurer.measure(
                        text = bubble.text,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 16.sp,
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

            // HUD Superior
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF1E293B).copy(alpha = 0.9f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Nv. $level", color = Color(0xFFFBBF24), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            Text("$score pts", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            if (combo > 1) {
                                Text("🔥 x${(1 + combo / 4).coerceAtMost(4)}", color = Color(0xFFF43F5E), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(
                            onClick = { slowMotionTicks = 180 },
                            enabled = slowMotionTicks == 0 && score >= 30,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF0284C7),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(if (slowMotionTicks > 0) "⏳ Pausa Activa" else "⏸️ Pausa 2s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            color = Color(0xFF1E293B).copy(alpha = 0.9f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "❤️".repeat(lives.coerceAtLeast(0)),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color(0xFF0F172A).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusBanner,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Zona Inferior del Orador
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(84.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF0F172A))
                        )
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎤", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Orador en Escena (Protege su fluidez)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            if (isGameOver) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                        border = BorderStroke(1.5.dp, Color(0xFFE11D48)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(28.dp)
                        ) {
                            Text("🎯", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "¡Partida Finalizada!",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Puntuación Final: $score pts",
                                color = Color(0xFFFBBF24),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    isGameOver = false
                                    lives = 5
                                    score = 0
                                    level = 1
                                    combo = 0
                                    bubbles.clear()
                                    particles.clear()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                                modifier = Modifier
                                    .height(52.dp)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Volver a Jugar", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveVoiceNoFillersMode(modifier: Modifier = Modifier) {
    var currentTopic by remember { mutableStateOf(voiceChallengeTopics.random()) }
    var timeLeft by remember { mutableIntStateOf(45) }
    var liveTranscript by remember { mutableStateOf("") }
    var hasFinishedRound by remember { mutableStateOf(false) }

    val speechController = rememberInAppSpeechController(
        language = "es-ES",
        continuous = true,
        onTranscriptChanged = { text ->
            liveTranscript = text
        }
    )

    val words = remember(liveTranscript) {
        liveTranscript.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    }
    val detectedFillers = remember(words) {
        words.filter { raw ->
            val clean = raw.lowercase().replace(Regex("[^a-zñáéíóú]"), "")
            detectedFillersSet.contains(clean)
        }
    }
    val cleanWordCount = (words.size - detectedFillers.size).coerceAtLeast(0)
    val fluencyPercent = if (words.isEmpty()) 100 else ((cleanWordCount.toFloat() / words.size.toFloat()) * 100).toInt().coerceIn(0, 100)

    LaunchedEffect(speechController.isListening, timeLeft) {
        if (speechController.isListening && timeLeft > 0) {
            delay(1000)
            timeLeft--
            if (timeLeft == 0) {
                speechController.stopAndFinish()
                hasFinishedRound = true
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tarjeta del Reto de Improvisación sin Muletillas
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            border = BorderStroke(1.5.dp, Color(0xFF10B981)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF10B981).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "RETO DE FLUIDEZ 45s",
                            color = Color(0xFF34D399),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    TextButton(
                        onClick = {
                            speechController.cancel()
                            var next = voiceChallengeTopics.random()
                            while (next == currentTopic && voiceChallengeTopics.size > 1) {
                                next = voiceChallengeTopics.random()
                            }
                            currentTopic = next
                            liveTranscript = ""
                            timeLeft = 45
                            hasFinishedRound = false
                        }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cambiar Tema", color = Color(0xFF38BDF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentTopic,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 23.sp
                )
            }
        }

        // Indicadores en Vivo: Tiempo, Palabras Limpias, Muletillas Detectadas, % Fluidez
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatBadgeCard(
                label = "Tiempo",
                value = "${timeLeft}s",
                color = if (timeLeft <= 10) Color(0xFFF87171) else Color(0xFF38BDF8),
                modifier = Modifier.weight(1f)
            )
            StatBadgeCard(
                label = "Palabras",
                value = "${words.size}",
                color = Color(0xFFA7F3D0),
                modifier = Modifier.weight(1f)
            )
            StatBadgeCard(
                label = "Muletillas",
                value = "${detectedFillers.size}",
                color = if (detectedFillers.isEmpty()) Color(0xFF4ADE80) else Color(0xFFF87171),
                modifier = Modifier.weight(1f)
            )
            StatBadgeCard(
                label = "Fluidez",
                value = "$fluencyPercent%",
                color = Color(0xFFFBBF24),
                modifier = Modifier.weight(1f)
            )
        }

        // Transcripción en Vivo con Muletillas Resaltadas en Rojo
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            border = BorderStroke(
                1.5.dp,
                if (speechController.isListening) Color(0xFF10B981) else Color(0xFF334155)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (speechController.isListening) {
                        "🎙️ Escuchando en vivo (las muletillas se marcan en rojo):"
                    } else {
                        "Transcripción de tu intervención:"
                    },
                    color = if (speechController.isListening) Color(0xFF34D399) else Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (words.isEmpty()) {
                    Text(
                        text = "Presiona el botón del micrófono y habla con pausas conscientes en lugar de decir «eh», «este», «bueno» u «o sea».",
                        color = Color(0xFF64748B),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                } else {
                    val annotated = buildAnnotatedString {
                        words.forEachIndexed { idx, w ->
                            val clean = w.lowercase().replace(Regex("[^a-zñáéíóú]"), "")
                            val isFiller = detectedFillersSet.contains(clean)
                            if (isFiller) {
                                withStyle(
                                    SpanStyle(
                                        color = Color(0xFFF87171),
                                        fontWeight = FontWeight.ExtraBold,
                                        background = Color(0xFF7F1D1D).copy(alpha = 0.45f)
                                    )
                                ) {
                                    append(" [$w] ")
                                }
                            } else {
                                withStyle(SpanStyle(color = Color.White)) {
                                    append(w)
                                    if (idx < words.size - 1) append(" ")
                                }
                            }
                        }
                    }
                    Text(
                        text = annotated,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Botón de Micrófono en Pantalla
        Button(
            onClick = {
                if (speechController.isListening) {
                    speechController.stopAndFinish()
                    hasFinishedRound = true
                } else {
                    if (timeLeft == 0) timeLeft = 45
                    hasFinishedRound = false
                    liveTranscript = ""
                    speechController.start()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (speechController.isListening) Color(0xFFDC2626) else Color(0xFF10B981)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = if (speechController.isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (speechController.isListening) "Detener y Ver Diagnóstico" else "Iniciar Reto con Micrófono",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        if (hasFinishedRound && words.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFFFBBF24)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📊 Diagnóstico AISO de Fluidez: $fluencyPercent%",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (detectedFillers.isEmpty()) {
                            "¡Impecable! Hablaste con ${words.size} palabras y 0 muletillas. Dominas el arte de la pausa consciente."
                        } else {
                            "Pronunciaste ${words.size} palabras y detectamos ${detectedFillers.size} muletilla(s): ${detectedFillers.distinct().joinToString(", ")}. Recuerda la regla del libro: cuando tu mente busque la siguiente idea, cierra los labios 2 segundos y respira."
                        },
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBadgeCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E293B),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(label, color = Color(0xFF94A3B8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}
