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
        title = "Isaac Newton y el valor de observar (Cap. 2)",
        text = "En 1752, William Stukeley dejó escrito su recuerdo de una tarde de 1726 en la que conversó con Isaac Newton bajo unos manzanos. Newton le contó que en 1666, al ver caer una manzana en su jardín, se preguntó por qué caía siempre perpendicularmente hacia el centro de la Tierra. No existe evidencia histórica de que la fruta le golpeara la cabeza, pero esa observación atenta abrió una pregunta que transformó la ciencia. En el Método AISO, este relato nos enseña a observar con atención y a contar historias con rigor y honestidad."
    ),
    Anecdote(
        title = "El hombre y el capullo de la mariposa (Cap. 4 · I03)",
        text = "Un hombre observaba durante horas cómo una mariposa luchaba por salir a través de una pequeña abertura en su capullo. Creyendo ayudarla, tomó unas tijeras y cortó el resto del capullo. La mariposa salió de inmediato, pero su cuerpo estaba débil y sus alas encogidas; nunca pudo volar. El esfuerzo por atravesar la abertura era precisamente el proceso que fortalecía sus alas. En la oratoria ocurre lo mismo: ningún atajo sustituye la fuerza que ganas en cada ensayo con tus propias palabras."
    ),
    Anecdote(
        title = "El samurái y el maestro zen (Cap. 4 · I03)",
        text = "Un guerrero impetuoso acudió a un maestro zen y le exigió con tono desafiante que le explicara qué eran el cielo y el infierno. Cuando el maestro señaló su falta de serenidad, el samurái montó en cólera y desenvainó su espada. El maestro le dijo con calma: «Ahí se abren las puertas del infierno». Al comprender que su propia ira lo dominaba, el guerrero envainó la espada e hizo una reverencia. Entonces el maestro concluyó: «Y ahí se abren las puertas del cielo». En el escenario, la pausa estratégica marca la diferencia entre reaccionar y liderar."
    ),
    Anecdote(
        title = "La taza de té (Cap. 4 · I03)",
        text = "Un visitante llegó a consultar a un maestro, pero en lugar de escuchar hablaba sin detenerse sobre sus propias opiniones. El maestro comenzó a servir té en la taza del visitante y continuó vertiendo incluso cuando ya se desbordaba. «¡Deténgase, ya está llena!», exclamó el visitante. El maestro respondió con serenidad: «Como esta taza, vienes lleno de tus propias certezas; para aprender y conectar con otros, primero hay que abrir espacio para escuchar»."
    ),
    Anecdote(
        title = "Miguel Ángel y el bloque de mármol (Cap. 4 · I03)",
        text = "Cuenta la tradición que, al contemplar la escultura del David, alguien preguntó a Miguel Ángel cómo había logrado crear tanta belleza a partir de una piedra fría. La enseñanza que transmite este relato es sencilla y profunda: consistió en retirar del bloque todo aquello que sobraba. Al preparar un discurso con el Método AISO, la claridad aparece cuando quitamos los rodeos innecesarios y dejamos visible la idea esencial."
    ),
    Anecdote(
        title = "Thomas Edison y los ensayos de la bombilla (Cap. 4 · I03)",
        text = "Durante el desarrollo de la bombilla incandescente, Thomas Edison y su equipo probaron numerosos materiales antes de lograr un filamento duradero. En lugar de interpretar cada intento fallido como una derrota personal, lo registraban como información valiosa sobre qué camino descartar. En el entrenamiento AISO («Una toma y un ajuste»), cada práctica no sirve para castigarte, sino para descubrir un ajuste concreto que mejora tu siguiente exposición."
    ),
    Anecdote(
        title = "Steve Jobs en la Universidad de Stanford (Cap. 9)",
        text = "El 12 de junio de 2005, en su discurso de graduación en la Universidad de Stanford, Steve Jobs no ofreció una lista fría de consejos abstractos, sino tres relatos personales estructurados en tres movimientos: una situación concreta, una dificultad real y la decisión o aprendizaje que surgió de ella. Su intervención demuestra cómo una historia honesta ilumina una idea y permanece en la memoria de la audiencia."
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
