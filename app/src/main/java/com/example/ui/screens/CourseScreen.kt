package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
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
import com.example.model.Lesson
import com.example.viewmodel.OratoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    viewModel: OratoriaViewModel,
    onNavigateToQuiz: () -> Unit
) {
    val lessons by viewModel.lessons.collectAsState()
    val completedLessons by viewModel.completedLessons.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz", fontWeight = FontWeight.Bold) },
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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ruta de Aprendizaje",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.Start)
            )

            val groupedLessons = lessons.groupBy { it.areaName }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                groupedLessons.forEach { (areaName, areaLessons) ->
                    item {
                        Text(
                            text = areaName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp, bottom = 8.dp)
                        )
                    }
                    items(areaLessons.size) { index ->
                        val lesson = areaLessons[index]
                        LessonNode(
                            lesson = lesson,
                            isLocked = false,
                            onClick = {
                                viewModel.startLesson(lesson)
                                onNavigateToQuiz()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonNode(lesson: Lesson, isLocked: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(enabled = !isLocked, onClick = onClick)
    ) {
        val (icon, backgroundColor) = when (lesson.id) {
            "1_1" -> Pair(Icons.Default.Mood, Color(0xFFE91E63))
            "1_2" -> Pair(Icons.Default.Air, Color(0xFF9C27B0))
            "2_1" -> Pair(Icons.Default.RecordVoiceOver, Color(0xFF673AB7))
            "2_2" -> Pair(Icons.Default.GraphicEq, Color(0xFF3F51B5))
            "3_1" -> Pair(Icons.Default.Architecture, Color(0xFF009688))
            "3_2" -> Pair(Icons.Default.DoneAll, Color(0xFF4CAF50))
            "4_1" -> Pair(Icons.Default.EmojiObjects, Color(0xFFFF9800))
            "4_2" -> Pair(Icons.Default.Flag, Color(0xFFFF5722))
            else -> Pair(Icons.Default.Star, MaterialTheme.colorScheme.primary)
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (isLocked) Color.LightGray else backgroundColor
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isLocked) Icons.Default.Lock else icon,
                contentDescription = lesson.title,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.width(110.dp)
        )
    }
}
