package com.example.ui.screens

import android.speech.tts.TextToSpeech
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

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

val fillerWordsList = listOf(
    "este", "eh", "bueno", "digamos", "tipo", "osea", "pues", "nada", "básicamente", "basicamente", "entonces", "mmm"
)

data class GameLevel(val levelNumber: Int, val title: String, val sentences: List<GameSentence>)

val level1Sentences = listOf(
    createSentence(1, "Bueno, yo creo que este, la oratoria persuasiva transforma vidas.", fillerWordsList),
    createSentence(2, "Básicamente el problema principal es que osea no planificamos el tiempo.", fillerWordsList),
    createSentence(3, "Hoy vamos a hablar de tipo la inteligencia emocional en el escenario.", fillerWordsList),
    createSentence(4, "Pues nada, digamos que esa es la conclusión más importante.", fillerWordsList),
    createSentence(5, "Eh, me parece que este proyecto tiene un gran potencial.", fillerWordsList)
)

val level2Sentences = listOf(
    createSentence(6, "Nuestra propuesta, digamos, ofrece este un retorno de inversión muy sólido eh.", fillerWordsList),
    createSentence(7, "Osea, básicamente tipo tenemos que escuchar primero al cliente pues.", fillerWordsList),
    createSentence(8, "Eh, bueno, comenzaremos analizando este los tres pilares del liderazgo.", fillerWordsList),
    createSentence(9, "Digamos que osea la claridad del mensaje es tipo fundamental básicamente.", fillerWordsList),
    createSentence(10, "Pues, este, la pausa consciente proyecta nada más seguridad eh.", fillerWordsList)
)

val level3Sentences = listOf(
    createSentence(11, "Bueno, osea, yo estaba tipo pensando que básicamente podemos eh liderar este cambio hoy.", fillerWordsList),
    createSentence(12, "Pues nada, digamos que este, cuando miras al público tipo generas eh confianza inmediata.", fillerWordsList),
    createSentence(13, "Básicamente osea el secreto es que bueno, nunca debes este leer tus diapositivas digamos.", fillerWordsList),
    createSentence(14, "Eh, pues nada, tipo yo afirmo que este una historia personal conecta osea profundamente.", fillerWordsList),
    createSentence(15, "Digamos que bueno, este, tipo básicamente todo gran discurso cierra con un llamado a la acción.", fillerWordsList)
)

val fillerGameLevels = listOf(
    GameLevel(1, "Nivel 1: Limpieza Básica", level1Sentences),
    GameLevel(2, "Nivel 2: Discurso Ejecutivo", level2Sentences),
    GameLevel(3, "Nivel 3: Maestría Editorial", level3Sentences)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FillerWordsGameScreen(
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(context) {
        val engine = TextToSpeech(context) {}
        engine.language = Locale("es", "ES")
        tts = engine
        onDispose {
            engine.stop()
            engine.shutdown()
        }
    }

    var currentLevelIndex by remember { mutableIntStateOf(0) }
    var currentSentenceIndex by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var eliminatedWords by remember { mutableStateOf(setOf<Int>()) }
    var errorWords by remember { mutableStateOf(setOf<Int>()) }
    var hintedWordId by remember { mutableStateOf<Int?>(null) }
    var gameState by remember { mutableStateOf("playing") }

    val currentLevel = fillerGameLevels.getOrNull(currentLevelIndex)
    val currentSentence = currentLevel?.sentences?.getOrNull(currentSentenceIndex)

    val totalFillers = currentSentence?.words?.count { it.isFiller } ?: 0
    val eliminatedFillers = currentSentence?.words?.count { it.isFiller && eliminatedWords.contains(it.id) } ?: 0
    val isSentenceComplete = totalFillers > 0 && eliminatedFillers == totalFillers

    val cleanSentencePreview = remember(currentSentence, eliminatedWords) {
        currentSentence?.words
            ?.filterNot { eliminatedWords.contains(it.id) }
            ?.joinToString(" ") { it.text }
            ?.replace(Regex("\\s+([.,:;!?])"), "$1")
            ?.replace(Regex("^[,\\s]+"), "")
            ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            ?: ""
    }

    fun advanceToNextSentence() {
        val level = currentLevel ?: return
        hintedWordId = null
        if (currentSentenceIndex < level.sentences.size - 1) {
            currentSentenceIndex++
            eliminatedWords = emptySet()
            errorWords = emptySet()
        } else if (currentLevelIndex < fillerGameLevels.size - 1) {
            currentLevelIndex++
            currentSentenceIndex = 0
            eliminatedWords = emptySet()
            errorWords = emptySet()
            if (lives < 3) lives++
        } else {
            gameState = "won"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Caza Muletillas", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text(
                            currentLevel?.title ?: "",
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
                actions = {
                    IconButton(
                        onClick = {
                            val missingFiller = currentSentence?.words?.firstOrNull {
                                it.isFiller && !eliminatedWords.contains(it.id)
                            }
                            hintedWordId = missingFiller?.id
                        },
                        enabled = !isSentenceComplete
                    ) {
                        Icon(Icons.Default.Lightbulb, contentDescription = "Pista", tint = Color(0xFFFBBF24))
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // HUD Superior
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Puntos: $score", fontWeight = FontWeight.ExtraBold, color = Color.White)
                            if (streak > 1) {
                                Text("🔥 x$streak", fontWeight = FontWeight.Black, color = Color(0xFFFBBF24))
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        for (i in 1..3) {
                            Icon(
                                imageVector = if (i <= lives) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Vida",
                                tint = if (i <= lives) Color(0xFFEF4444) else Color.Gray,
                                modifier = Modifier.size(26.dp).padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                // Progreso de Oraciones y Muletillas Restantes
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Oración ${currentSentenceIndex + 1} de ${currentLevel?.sentences?.size ?: 0}",
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            color = if (isSentenceComplete) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFE11D48).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isSentenceComplete) "¡Oración Limpia!" else "Muletillas detectadas: $eliminatedFillers / $totalFillers",
                                color = if (isSentenceComplete) Color(0xFF34D399) else Color(0xFFFDA4AF),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Tarjeta Interactiva de Palabras
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(
                        1.5.dp,
                        if (isSentenceComplete) Color(0xFF10B981) else Color(0xFF475569)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "TOCA CADA MULETILLA PARA ELIMINARLA:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        if (currentSentence != null) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                currentSentence.words.forEach { word ->
                                    val isEliminated = eliminatedWords.contains(word.id)
                                    val isError = errorWords.contains(word.id)
                                    val isHinted = hintedWordId == word.id

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                when {
                                                    isEliminated -> Color(0xFF064E3B)
                                                    isError -> Color(0xFF7F1D1D)
                                                    isHinted -> Color(0xFFF59E0B).copy(alpha = 0.3f)
                                                    else -> Color(0xFF0F172A)
                                                }
                                            )
                                            .border(
                                                width = if (isHinted) 2.dp else 1.dp,
                                                color = when {
                                                    isEliminated -> Color(0xFF10B981)
                                                    isError -> Color(0xFFF87171)
                                                    isHinted -> Color(0xFFFBBF24)
                                                    else -> Color(0xFF334155)
                                                },
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable(
                                                enabled = !isEliminated && !isError && !isSentenceComplete && gameState == "playing"
                                            ) {
                                                if (word.isFiller) {
                                                    eliminatedWords = eliminatedWords + word.id
                                                    hintedWordId = null
                                                    streak++
                                                    score += 15 + (streak * 3)
                                                } else {
                                                    errorWords = errorWords + word.id
                                                    streak = 0
                                                    lives--
                                                    if (lives <= 0) {
                                                        gameState = "lost"
                                                    }
                                                }
                                            }
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            text = word.text,
                                            fontSize = 18.sp,
                                            fontWeight = if (isEliminated) FontWeight.Normal else FontWeight.Bold,
                                            color = when {
                                                isEliminated -> Color(0xFF6EE7B7)
                                                isError -> Color(0xFFFCA5A5)
                                                else -> Color.White
                                            },
                                            textDecoration = if (isEliminated) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Vista Previa Editorial de la Oración Limpia
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✨ VERSIÓN LIMPIA Y EJECUTIVA:",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            IconButton(
                                onClick = {
                                    tts?.speak(cleanSentencePreview, TextToSpeech.QUEUE_FLUSH, null, "clean_preview")
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Escuchar versión limpia",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "«$cleanSentencePreview»",
                            color = Color(0xFFE2E8F0),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        )
                    }
                }

                if (isSentenceComplete) {
                    Button(
                        onClick = { advanceToNextSentence() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("¡Excelente! Siguiente Oración", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }

            if (gameState == "won" || gameState == "lost") {
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
                        border = BorderStroke(
                            1.5.dp,
                            if (gameState == "won") Color(0xFF10B981) else Color(0xFFEF4444)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(if (gameState == "won") "🏆" else "💡", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (gameState == "won") "¡Discurso 100% Limpio!" else "¡Sigue Practicando!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Puntuación Final: $score pts",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = {
                                    currentLevelIndex = 0
                                    currentSentenceIndex = 0
                                    lives = 3
                                    score = 0
                                    streak = 0
                                    eliminatedWords = emptySet()
                                    errorWords = emptySet()
                                    gameState = "playing"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().height(52.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Jugar de Nuevo", fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
