package com.example.ui.screens

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
        val cleanWord = word.lowercase().replace(Regex("[^a-záéíóúñ]"), "")
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
    var gameState by remember { mutableStateOf("playing") } // playing, won, lost

    val currentLevel = fillerGameLevels.getOrNull(currentLevelIndex)
    val currentSentence = currentLevel?.sentences?.getOrNull(currentSentenceIndex)

    LaunchedEffect(eliminatedWords, currentSentenceIndex, currentLevelIndex) {
        if (currentSentence != null && currentLevel != null) {
            val totalFillers = currentSentence.words.count { it.isFiller }
            val eliminatedFillers = currentSentence.words.filter { it.isFiller && eliminatedWords.contains(it.id) }.size
            
            if (totalFillers > 0 && eliminatedFillers == totalFillers) {
                delay(1000) // Wait a second before next sentence
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
                // Failsafe if sentence has no fillers
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
            text = { Text("¡Excelente! Has limpiado todas las oraciones del nivel ${currentLevelIndex + 1}. Prepárate para el siguiente nivel, ¡será más difícil!") },
            confirmButton = {
                Button(onClick = { 
                    showLevelUp = false
                    currentLevelIndex++
                    currentSentenceIndex = 0
                    eliminatedWords = emptySet()
                    errorWords = emptySet()
                    // Recompensar con una vida al pasar de nivel si tiene menos de 3
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
                    "Las muletillas (eh, este, bueno, digamos, osea...) ensucian tu discurso y restan profesionalismo.\n\n" +
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
            // Header stats
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
                            
                            // Don't make punctuation marks clickable by themselves if they are separated
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
