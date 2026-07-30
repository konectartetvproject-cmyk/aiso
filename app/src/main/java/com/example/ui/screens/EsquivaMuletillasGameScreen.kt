package com.example.ui.screens

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
import android.media.AudioManager
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
                // Initialize TTS
            }
        }
        // Set language to Spanish since filler words are in Spanish
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

    // Game Loop
    LaunchedEffect(isPlaying, isGameOver) {
        if (isPlaying && !isGameOver) {
            var lastSpawnTime = System.currentTimeMillis()
            var spawnInterval = 2000L
            
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                
                // Spawn new bubbles
                if (currentTime - lastSpawnTime > spawnInterval && screenWidth > 0) {
                    val radius = Random.nextFloat() * 30f + 90f // Base radius
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

                // Update bubbles
                val bubblesToRemove = mutableListOf<Bubble>()
                for (bubble in bubbles) {
                    bubble.y += bubble.speed
                    if (bubble.y - bubble.radius > screenHeight - 200f) { // Bottom area threshold
                        bubblesToRemove.add(bubble)
                        // Play TTS for the filler word that reached the speaker
                        tts?.speak(bubble.text, TextToSpeech.QUEUE_FLUSH, null, null)
                        
                        lives -= 1
                        if (lives <= 0) {
                            isGameOver = true
                        }
                    }
                }
                bubbles.removeAll(bubblesToRemove)
                
                // Update particles
                val particlesToRemove = mutableListOf<Particle>()
                for (particle in particles) {
                    particle.x += particle.vx
                    particle.y += particle.vy
                    particle.vy += 0.2f // gravity
                    particle.life -= 0.03f
                    if (particle.life <= 0) particlesToRemove.add(particle)
                }
                particles.removeAll(particlesToRemove)

                delay(16) // ~60fps
            }
        }
    }
    
    fun popBubble(bubble: Bubble) {
        // Play click sound
        view.playSoundEffect(SoundEffectConstants.CLICK)
        
        bubbles.remove(bubble)
        score += 10
        level = (score / 100) + 1 // Level up every 10 bubbles
        // Spawn particles
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
                // Intro Screen
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
                // Game Canvas
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
                                    distance <= bubble.radius + 80f // Increased hit margin for easier tapping
                                }
                                clickedBubbles.lastOrNull()?.let { popBubble(it) }
                            }
                        }
                ) {
                    // Draw particles
                    for (particle in particles) {
                        drawCircle(
                            color = particle.color.copy(alpha = particle.life),
                            radius = 12f * particle.life,
                            center = Offset(particle.x, particle.y)
                        )
                    }

                    // Draw bubbles
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
                        
                        // Bubble reflection/highlight
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

                // HUD
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
                
                // Orador / Base
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
                        text = "🗣️ Orador", 
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
