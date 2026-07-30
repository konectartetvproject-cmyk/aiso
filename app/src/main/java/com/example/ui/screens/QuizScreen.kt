package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.OratoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: OratoriaViewModel,
    onBack: () -> Unit
) {
    val currentLesson by viewModel.currentLesson.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOptionIndex by viewModel.selectedOptionIndex.collectAsState()
    val isAnswerChecked by viewModel.isAnswerChecked.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    val score by viewModel.score.collectAsState()

    if (currentLesson == null) {
        onBack()
        return
    }

    val totalQuestions = currentLesson!!.questions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    LinearProgressIndicator(
                        progress = { 
                           if (isQuizFinished) 1f else currentQuestionIndex.toFloat() / totalQuestions.toFloat()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Salir")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (!isQuizFinished) {
                Surface(
                    color = if (isAnswerChecked) {
                        val isCorrect = selectedOptionIndex == currentLesson!!.questions[currentQuestionIndex].correctAnswerIndex
                        if (isCorrect) Color(0xFFD7FFD9) else Color(0xFFFFD7D7)
                    } else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (isAnswerChecked) {
                            val isCorrect = selectedOptionIndex == currentLesson!!.questions[currentQuestionIndex].correctAnswerIndex
                            Text(
                                text = if (isCorrect) "¡Correcto!" else "Incorrecto",
                                color = if (isCorrect) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = currentLesson!!.questions[currentQuestionIndex].explanation,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        Button(
                            onClick = {
                                if (isAnswerChecked) {
                                    viewModel.nextQuestion()
                                } else {
                                    viewModel.checkAnswer()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = selectedOptionIndex != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAnswerChecked) {
                                    val isCorrect = selectedOptionIndex == currentLesson!!.questions[currentQuestionIndex].correctAnswerIndex
                                    if (isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                                } else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = if (isAnswerChecked) "Continuar" else "Comprobar",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (isQuizFinished) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(120.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "¡Lección Completada!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Puntuación: $score / $totalQuestions",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Volver al Inicio", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                val question = currentLesson!!.questions[currentQuestionIndex]
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    question.options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOptionIndex == index
                        val isCorrectAnswer = index == question.correctAnswerIndex
                        
                        val backgroundColor = when {
                            !isAnswerChecked && isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            isAnswerChecked && isCorrectAnswer -> Color(0xFFE8F5E9)
                            isAnswerChecked && isSelected && !isCorrectAnswer -> Color(0xFFFFEBEE)
                            else -> MaterialTheme.colorScheme.surface
                        }
                        
                        val borderColor = when {
                            !isAnswerChecked && isSelected -> MaterialTheme.colorScheme.primary
                            isAnswerChecked && isCorrectAnswer -> Color(0xFF4CAF50)
                            isAnswerChecked && isSelected && !isCorrectAnswer -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(backgroundColor)
                                .border(
                                    width = if (isSelected || (isAnswerChecked && isCorrectAnswer)) 2.dp else 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable(enabled = !isAnswerChecked) {
                                    viewModel.selectOption(index)
                                }
                                .padding(20.dp)
                        ) {
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
