package com.example.ui.screens

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

data class Anecdote(
    val title: String,
    val text: String
)

val anecdotesList = listOf(
    Anecdote(
        title = "Bolívar en Casacoima",
        text = "En 1817, acorralado y enfermo en la laguna de Casacoima, rodeado de enemigos, Simón Bolívar se encontraba en su peor momento. En lugar de rendirse, dio un discurso apasionado y visionario a sus oficiales. Describió cómo tomarían Angostura, liberarían la Nueva Granada, luego Venezuela y fundarían una gran nación. Sus oficiales creyeron que había enloquecido, pero sus palabras crearon la realidad. Esa es la fuerza de la oratoria: inspirar y crear visiones donde solo hay desesperanza."
    ),
    Anecdote(
        title = "La Historia de Juan",
        text = "Juan era un profesional brillante, pero el miedo a hablar en público lo paralizaba; su voz temblaba y olvidaba el mensaje. Al aplicar las técnicas de 'Invierte en ti', descubrió que su error era enfocarse en sí mismo y en el temor a ser juzgado. Cuando cambió su enfoque hacia el mensaje y en cómo ayudar a la audiencia, el miedo se transformó en energía. Hoy, Juan lidera con una comunicación segura."
    ),
    Anecdote(
        title = "La Historia de Laura",
        text = "Laura tenía un emprendimiento fantástico, pero no lograba cerrar ventas porque sus presentaciones eran técnicas y monótonas. Al entrar a la Academia AISO, aprendió el poder del Storytelling. Dejó de vender características y empezó a contar la historia de su producto y cómo transformaba la vida de sus clientes. Al conectar emocionalmente, sus ventas se multiplicaron, demostrando que la emoción vende más que la lógica."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnecdotesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var currentlyPlaying by remember { mutableStateOf<String?>(null) }

    DisposableEffect(context) {
        var textToSpeech: TextToSpeech? = null
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                @Suppress("DEPRECATION")
                textToSpeech?.language = Locale("es", "ES")
                
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    
                    override fun onDone(utteranceId: String?) {
                        Handler(Looper.getMainLooper()).post {
                            if (currentlyPlaying == utteranceId) {
                                currentlyPlaying = null
                            }
                        }
                    }
                    
                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        Handler(Looper.getMainLooper()).post {
                            if (currentlyPlaying == utteranceId) {
                                currentlyPlaying = null
                            }
                        }
                    }
                })
            }
        }
        tts = textToSpeech
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca de Audios", fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            items(anecdotesList) { anecdote ->
                val isPlaying = currentlyPlaying == anecdote.title
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = anecdote.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )
                            
                            IconButton(onClick = {
                                if (isPlaying) {
                                    tts?.stop()
                                    currentlyPlaying = null
                                } else {
                                    tts?.stop()
                                    val params = Bundle()
                                    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, anecdote.title)
                                    tts?.speak(anecdote.text, TextToSpeech.QUEUE_FLUSH, params, anecdote.title)
                                    currentlyPlaying = anecdote.title
                                }
                            }) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Detener" else "Reproducir",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = anecdote.text, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
