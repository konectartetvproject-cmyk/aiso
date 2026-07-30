package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.MergeType
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SpeakerNotes
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Signpost
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
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
import com.example.model.Technique
import com.example.model.techniquesList
import com.example.viewmodel.OratoriaViewModel
import com.example.model.Lesson

import androidx.compose.material.icons.filled.Whatshot
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.DailyChallengeViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechniquesListScreen(
    viewModel: OratoriaViewModel,
    dailyChallengeViewModel: DailyChallengeViewModel = viewModel(),
    onTechniqueClick: (Technique) -> Unit,
    onToxicParticipantsClick: () -> Unit,
    onAnecdotesClick: () -> Unit,
    onQuotesClick: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
    fun getTechniqueColorAndIcon(id: String): Pair<Color, ImageVector> {
        return when (id) {
            "inicio_1" -> Pair(Color(0xFFE53935), Icons.Default.QuestionMark)
            "inicio_2" -> Pair(Color(0xFFD81B60), Icons.Default.FormatQuote)
            "inicio_3" -> Pair(Color(0xFF8E24AA), Icons.Default.AutoStories)
            "inicio_4" -> Pair(Color(0xFF5E35B1), Icons.Default.DirectionsRun)
            "inicio_5" -> Pair(Color(0xFF3949AB), Icons.Default.BarChart)
            "inicio_6" -> Pair(Color(0xFF1E88E5), Icons.Default.CardGiftcard)
            "inicio_7" -> Pair(Color(0xFF039BE5), Icons.Default.Extension)
            "inicio_8" -> Pair(Color(0xFF00ACC1), Icons.Default.Warning)
            "inicio_9" -> Pair(Color(0xFF00897B), Icons.Default.Newspaper)
            "inicio_10" -> Pair(Color(0xFF43A047), Icons.Default.MergeType)
            "conclusion_1" -> Pair(Color(0xFF7CB342), Icons.Default.Group)
            "conclusion_2" -> Pair(Color(0xFFC0CA33), Icons.Default.SpeakerNotes)
            "conclusion_3" -> Pair(Color(0xFFFDD835), Icons.Default.Visibility)
            "conclusion_4" -> Pair(Color(0xFFFFB300), Icons.Default.FormatListNumbered)
            "conclusion_5" -> Pair(Color(0xFFFB8C00), Icons.Default.Pets)
            "conclusion_6" -> Pair(Color(0xFFF4511E), Icons.Default.Person)
            "conclusion_7" -> Pair(Color(0xFF6D4C41), Icons.Default.Signpost)
            else -> Pair(Color(0xFF757575), Icons.Default.AutoFixHigh)
        }
    }
    val completedTechniques by viewModel.completedTechniques.collectAsState()
    val lessons by viewModel.lessons.collectAsState()
    val completedLessons by viewModel.completedLessons.collectAsState()
    
    val groupedTechniques = techniquesList.groupBy { it.type }
    val groupedLessons = lessons.groupBy { it.areaName }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taller de Oratoria", fontWeight = FontWeight.Bold) },
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
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (dailyChallengeViewModel.isCompletedToday.collectAsState().value) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val streak = dailyChallengeViewModel.streak.collectAsState().value
                        val currentChallenge = dailyChallengeViewModel.currentChallenge.collectAsState().value
                        val isCompletedToday = dailyChallengeViewModel.isCompletedToday.collectAsState().value
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Racha",
                                tint = if (streak > 0) Color(0xFFFF9800) else Color.Gray,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Racha: $streak ${if (streak == 1) "día" else "días"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (streak > 0) Color(0xFFFF9800) else Color.Gray
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Reto Diario",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentChallenge.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentChallenge.description,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isCompletedToday) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("¡Reto de hoy completado!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                            }
                        } else {
                            Button(
                                onClick = {
                                    dailyChallengeViewModel.markAsCompleted()
                                    onNavigateToRoute(currentChallenge.route)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Completar Reto")
                            }
                        }
                    }
                }
            }
            
            groupedTechniques.forEach { (type, techniques) ->
                item {
                    Text(
                        text = "Técnicas de $type",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                    )
                }
                
                val chunkedTechniques = techniques.chunked(3)
                items(chunkedTechniques.size) { rowIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val rowTechniques = chunkedTechniques[rowIndex]
                        for (technique in rowTechniques) {
                            val isCompleted = completedTechniques.contains(technique.id)
                            val isLocked = false
                            
                            val (color, icon) = getTechniqueColorAndIcon(technique.id)
                            
                            TechniqueNode(
                                technique = technique,
                                isLocked = isLocked,
                                isCompleted = isCompleted,
                                customColor = color,
                                customIcon = icon,
                                onClick = { onTechniqueClick(technique) },
                                modifier = Modifier.width(110.dp)
                            )
                        }
                        val emptySpots = 3 - rowTechniques.size
                        for (i in 0 until emptySpots) {
                            Spacer(modifier = Modifier.width(110.dp))
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Manejo de Público",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                )
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onToxicParticipantsClick).width(110.dp)
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
                            text = "Participantes Tóxicos",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onAnecdotesClick).width(110.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9C27B0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HistoryEdu,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Anécdotas Inspiradoras",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Ruta de Aprendizaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                )
            }
            
            groupedLessons.forEach { (areaName, areaLessons) ->
                item {
                    Text(
                        text = areaName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp, start = 16.dp)
                    )
                }
                val chunkedLessons = areaLessons.chunked(3)
                items(chunkedLessons.size) { rowIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val rowLessons = chunkedLessons[rowIndex]
                        for (lesson in rowLessons) {
                            LessonNode(
                                lesson = lesson,
                                isLocked = false,
                                onClick = {
                                    viewModel.startLesson(lesson)
                                    onNavigateToQuiz()
                                },
                                modifier = Modifier.width(110.dp)
                            )
                        }
                        val emptySpots = 3 - rowLessons.size
                        for (i in 0 until emptySpots) {
                            Spacer(modifier = Modifier.width(110.dp))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun TechniqueNode(
    technique: Technique,
    isLocked: Boolean,
    isCompleted: Boolean,
    customColor: Color,
    customIcon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(enabled = !isLocked, onClick = onClick)
    ) {
        val backgroundColor = when {
            isLocked -> Color.LightGray
            isCompleted -> customColor.copy(alpha = 0.6f)
            else -> customColor
        }
        val icon = when {
            isLocked -> Icons.Default.Lock
            else -> customIcon
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp).align(Alignment.BottomEnd)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = technique.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(110.dp)
        )
    }
}
