package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CustomerServiceQuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerServiceQuizScreen(
    onBack: () -> Unit,
    viewModel: CustomerServiceQuizViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reto de Respuestas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        if (state.isFinished) {
            CSQuizResultView(paddingValues, state.score, state.questions.size, viewModel::restartQuiz)
        } else if (state.questions.isNotEmpty()) {
            CSQuizQuestionView(paddingValues, state, viewModel)
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun CSQuizResultView(paddingValues: PaddingValues, score: Int, total: Int, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡Reto Completado!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Tu puntuación:",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "$score / $total",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = if (score == total) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRestart, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Text("Jugar de nuevo", fontSize = 18.sp)
        }
    }
}

@Composable
fun CSQuizQuestionView(
    paddingValues: PaddingValues,
    state: com.example.viewmodel.CSQuizState,
    viewModel: CustomerServiceQuizViewModel
) {
    val question = state.questions[state.currentQuestionIndex]
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Progress bar
        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex.toFloat() / state.questions.size.toFloat()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Situation
        Text(
            text = "Situación:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.situation,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "¿Qué responderías?",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Options
        question.options.forEachIndexed { index, option ->
            val isSelected = state.selectedOptionIndex == index
            val isCorrect = index == question.correctOptionIndex
            
            val backgroundColor = when {
                !state.showExplanation -> if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                state.showExplanation && isCorrect -> Color(0xFFE8F5E9) // Greenish
                state.showExplanation && isSelected && !isCorrect -> Color(0xFFFFEBEE) // Reddish
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            
            val borderColor = when {
                !state.showExplanation -> if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                state.showExplanation && isCorrect -> Color(0xFF4CAF50)
                state.showExplanation && isSelected && !isCorrect -> Color(0xFFF44336)
                else -> Color.Transparent
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable(enabled = !state.showExplanation) { viewModel.selectOption(index) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                border = androidx.compose.foundation.BorderStroke(2.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (backgroundColor == MaterialTheme.colorScheme.surfaceVariant) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface
                    )
                    
                    if (state.showExplanation) {
                        if (isCorrect) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Correcto", tint = Color(0xFF4CAF50))
                        } else if (isSelected) {
                            Icon(Icons.Default.Cancel, contentDescription = "Incorrecto", tint = Color(0xFFF44336))
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))

        // Explanation and Next button
        if (state.showExplanation) {
            val isCorrect = state.selectedOptionIndex == question.correctOptionIndex
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isCorrect) "¡Correcto!" else "Incorrecto",
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.explanation,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (state.showExplanation) {
                    viewModel.nextQuestion()
                } else {
                    viewModel.checkAnswer()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = state.selectedOptionIndex != null || state.showExplanation,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.showExplanation) {
                    if (state.selectedOptionIndex == question.correctOptionIndex) Color(0xFF4CAF50) else Color(0xFFF44336)
                } else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (state.showExplanation) "Continuar" else "Comprobar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
