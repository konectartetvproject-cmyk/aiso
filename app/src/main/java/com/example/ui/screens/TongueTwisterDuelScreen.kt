package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.TongueTwisterDuelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TongueTwisterDuelScreen(
    onBack: () -> Unit,
    viewModel: TongueTwisterDuelViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var currentTwister by remember { mutableStateOf(tongueTwistersList.random()) }

    val isRecordingP1 by viewModel.isRecordingP1.collectAsState()
    val isRecordingP2 by viewModel.isRecordingP2.collectAsState()
    val hasAudioP1 by viewModel.hasAudioP1.collectAsState()
    val hasAudioP2 by viewModel.hasAudioP2.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val feedback by viewModel.feedback.collectAsState()
    val error by viewModel.error.collectAsState()

    var activePlayerForPermission by remember { mutableStateOf(1) }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.startRecording(context, activePlayerForPermission)
            } else {
                android.widget.Toast.makeText(context, "Se requiere permiso de micrófono para grabar.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    )

    fun handleRecordClick(player: Int, isRecording: Boolean) {
        if (isRecording) {
            viewModel.stopRecording(player)
        } else {
            activePlayerForPermission = player
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.RECORD_AUDIO
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.startRecording(context, player)
            } else {
                permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duelo de Trabalenguas", fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "¡Duelo de Dicción!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ambos jugadores deben grabar el mismo trabalenguas. La IA de Gemini decidirá al ganador.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = currentTwister.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = currentTwister.text,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Jugadores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Jugador 1
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jugador 1", fontWeight = FontWeight.Bold, color = Color(0xFF1E88E5))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { handleRecordClick(1, isRecordingP1) },
                            enabled = !isRecordingP2 && !isAnalyzing && feedback == null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecordingP1) Color.Red else Color(0xFF1E88E5)
                            )
                        ) {
                            Icon(
                                imageVector = if (isRecordingP1) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        if (hasAudioP1 && !isRecordingP1) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Listo", tint = Color(0xFF4CAF50))
                        } else {
                            Text(if (isRecordingP1) "Grabando..." else "Grabar", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }

                    // Jugador 2
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Jugador 2", fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { handleRecordClick(2, isRecordingP2) },
                            enabled = !isRecordingP1 && !isAnalyzing && feedback == null,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecordingP2) Color.Red else Color(0xFFE53935)
                            )
                        ) {
                            Icon(
                                imageVector = if (isRecordingP2) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        if (hasAudioP2 && !isRecordingP2) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Listo", tint = Color(0xFF4CAF50))
                        } else {
                            Text(if (isRecordingP2) "Grabando..." else "Grabar", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error Message
                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Analyzing state or Evaluate Button
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text("Los jueces de Gemini están evaluando...", color = MaterialTheme.colorScheme.primary)
                } else if (feedback != null) {
                    // Feedback display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Veredicto del Duelo 🏆", fontWeight = FontWeight.Bold, color = Color(0xFFF57F17))
                            Spacer(Modifier.height(8.dp))
                            Text(feedback!!, color = Color(0xFF3E2723))
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.analyzeDuel(currentTwister.text) },
                        enabled = hasAudioP1 && hasAudioP2,
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Evaluar Duelo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        var nextTwister = tongueTwistersList.random()
                        while (nextTwister == currentTwister) {
                            nextTwister = tongueTwistersList.random()
                        }
                        currentTwister = nextTwister
                        viewModel.clearFeedback()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Otro Trabalenguas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
