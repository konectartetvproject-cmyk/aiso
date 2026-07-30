package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Stop
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.Technique
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

enum class PracticeStep {
    LESSON, EXAMPLE, CHALLENGE, FEEDBACK, DASHBOARD
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun TechniquePracticeScreen(
    technique: Technique,
    isEvaluating: Boolean,
    aiFeedback: String?,
    voiceMetrics: com.example.viewmodel.VoiceMetrics?,
    onEvaluate: (String) -> Unit,
    onEvaluateWithAudio: (ByteArray) -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableStateOf(PracticeStep.LESSON) }
    var userInput by remember { mutableStateOf("") }
    
    val recordAudioPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)
    
    val context = LocalContext.current
    val audioRecorder = remember { com.example.util.AudioRecorderHelper(context) }
    var isRecording by remember { mutableStateOf(false) }
    var recordedAudioBytes by remember { mutableStateOf<ByteArray?>(null) }

    // When feedback arrives, move to FEEDBACK step
    LaunchedEffect(aiFeedback) {
        if (aiFeedback != null) {
            currentStep = PracticeStep.FEEDBACK
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(technique.name, fontWeight = FontWeight.Bold) },
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
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    when (currentStep) {
                        PracticeStep.LESSON -> {
                            Button(
                                onClick = { currentStep = PracticeStep.EXAMPLE },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver Ejemplo")
                            }
                        }
                        PracticeStep.EXAMPLE -> {
                            Button(
                                onClick = { currentStep = PracticeStep.CHALLENGE },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Empezar Reto")
                            }
                        }
                        PracticeStep.CHALLENGE -> {
                            Button(
                                onClick = { 
                                    if (recordedAudioBytes != null) {
                                        onEvaluateWithAudio(recordedAudioBytes!!)
                                    } else {
                                        onEvaluate(userInput) 
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = (userInput.isNotBlank() || recordedAudioBytes != null) && !isEvaluating
                            ) {
                                if (isEvaluating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp), 
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Evaluar mi Ejemplo")
                                }
                            }
                        }
                        PracticeStep.FEEDBACK -> {
                            Button(
                                onClick = { currentStep = PracticeStep.DASHBOARD },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ver Análisis de Voz")
                            }
                        }
                        PracticeStep.DASHBOARD -> {
                            Button(
                                onClick = onComplete,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Finalizar")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            // Linear Progress Indicator
            LinearProgressIndicator(
                progress = {
                    when (currentStep) {
                        PracticeStep.LESSON -> 0.2f
                        PracticeStep.EXAMPLE -> 0.4f
                        PracticeStep.CHALLENGE -> 0.6f
                        PracticeStep.FEEDBACK -> 0.8f
                        PracticeStep.DASHBOARD -> 1.0f
                    }
                },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(targetState = currentStep, label = "step_content") { step ->
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    when (step) {
                        PracticeStep.LESSON -> {
                            Text("1. La Lección", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Propósito", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(technique.purpose, modifier = Modifier.padding(bottom = 16.dp, top = 4.dp))
                                    
                                    Text("Fórmula Exacta", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    technique.formula.forEachIndexed { index, item ->
                                        Text("${index + 1}. $item", modifier = Modifier.padding(start = 8.dp, top = 4.dp))
                                    }
                                }
                            }
                        }
                        PracticeStep.EXAMPLE -> {
                            Text("2. El Ejemplo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Modelo del Autor", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(technique.example, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                }
                            }
                        }
                        PracticeStep.CHALLENGE -> {
                            Text("3. El Reto", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Elige un tema de tu interés y redacta tu propio fragmento de discurso aplicando la fórmula exacta que acabas de aprender. Puedes escribirlo o dictarlo por voz.")
                            OutlinedTextField(
                                value = userInput,
                                onValueChange = { userInput = it },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                                placeholder = { Text("Escribe o dicta tu discurso aquí...") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FloatingActionButton(
                                    onClick = {
                                        if (recordAudioPermissionState.status.isGranted) {
                                            if (isRecording) {
                                                recordedAudioBytes = audioRecorder.stopRecording()
                                                isRecording = false
                                            } else {
                                                audioRecorder.startRecording()
                                                isRecording = true
                                            }
                                        } else {
                                            recordAudioPermissionState.launchPermissionRequest()
                                        }
                                    },
                                    containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = if (isRecording) "Detener Grabación" else "Grabar Audio",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            if (recordedAudioBytes != null && !isRecording) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Audio grabado listo para evaluar.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }
                        PracticeStep.FEEDBACK -> {
                            Text("Feedback del Tutor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(aiFeedback ?: "", color = MaterialTheme.colorScheme.onTertiaryContainer)
                                }
                            }
                        }
                        PracticeStep.DASHBOARD -> {
                            Text("Análisis de Voz (Estimado)", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("Basado en el análisis de tu discurso, aquí tienes las métricas estimadas de tu desempeño vocal.")
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    MetricRow(title = "Ritmo (Pace)", value = voiceMetrics?.pace ?: 0f, color = androidx.compose.ui.graphics.Color(0xFF4CAF50))
                                    MetricRow(title = "Claridad", value = voiceMetrics?.clarity ?: 0f, color = androidx.compose.ui.graphics.Color(0xFF2196F3))
                                    MetricRow(title = "Tono (Positividad)", value = voiceMetrics?.sentiment ?: 0f, color = androidx.compose.ui.graphics.Color(0xFF9C27B0))
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MetricRow(title: String, value: Float, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, fontWeight = FontWeight.Bold)
            Text("${(value * 100).toInt()}%")
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
