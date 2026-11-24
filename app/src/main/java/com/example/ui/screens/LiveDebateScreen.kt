package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.rememberInAppSpeechController
import com.example.viewmodel.ChatMessage
import com.example.viewmodel.OratoriaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDebateScreen(
    viewModel: OratoriaViewModel,
    onBack: () -> Unit
) {
    val topic by viewModel.debateTopic.collectAsState()
    val messages by viewModel.debateMessages.collectAsState()
    val isLoading by viewModel.isDebateLoading.collectAsState()
    val isFinished by viewModel.debateFinished.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var baseTextBeforeMic by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val speechController = rememberInAppSpeechController(
        language = "es-ES",
        continuous = true,
        onTranscriptChanged = { liveSpoken ->
            if (liveSpoken.isNotBlank()) {
                inputText = if (baseTextBeforeMic.isBlank()) liveSpoken else "$baseTextBeforeMic $liveSpoken"
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.startLiveDebate()
    }

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            speechController.cancel()
            viewModel.stopLiveDebate()
        }
    }

    androidx.activity.compose.BackHandler {
        speechController.cancel()
        viewModel.stopLiveDebate()
        onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Debate y Discurso en Vivo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        speechController.cancel()
                        viewModel.stopLiveDebate()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        speechController.cancel()
                        viewModel.startLiveDebate()
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Cambiar Tema")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        bottomBar = {
            if (!isFinished) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .imePadding()
                    ) {
                        if (speechController.isListening) {
                            Surface(
                                color = Color(0xFFD1FAE5),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "🎙️ Escuchando tu argumento en vivo...",
                                        color = Color(0xFF065F46),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(
                                        onClick = { speechController.stopAndFinish() },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("Detener", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    if (speechController.isListening) {
                                        speechController.stopAndFinish()
                                    } else {
                                        baseTextBeforeMic = inputText.trim()
                                        speechController.start(resetText = true)
                                    }
                                },
                                enabled = !isLoading
                            ) {
                                Icon(
                                    imageVector = if (speechController.isListening) Icons.Default.Stop else Icons.Default.Mic,
                                    contentDescription = "Dictar por voz",
                                    tint = if (speechController.isListening) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                                )
                            }
                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = { Text("Habla con el micrófono o escribe tu argumento...") },
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = {
                                    if (speechController.isListening) {
                                        speechController.stopAndFinish()
                                    }
                                    if (inputText.isNotBlank()) {
                                        viewModel.sendDebateMessage(inputText)
                                        inputText = ""
                                        baseTextBeforeMic = ""
                                    }
                                },
                                enabled = !isLoading && inputText.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Enviar",
                                    tint = if (isLoading || inputText.isBlank()) Color.Gray else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.startLiveDebate() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Nuevo Tema")
                        }
                        Button(
                            onClick = {
                                speechController.cancel()
                                viewModel.stopLiveDebate()
                                onBack()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Finalizar")
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
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Tu Tema Polémico:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        TextButton(
                            onClick = { viewModel.startLiveDebate() },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Otro tema")
                        }
                    }
                    Text(
                        topic,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Defiende o refuta este tema. La IA actuará como un participante desafiante para entrenar tu persuasión y temple.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Card(
                                modifier = Modifier.padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    val backgroundColor = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            modifier = Modifier.widthIn(max = 310.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}
