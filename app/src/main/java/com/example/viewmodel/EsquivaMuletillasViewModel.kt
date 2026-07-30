package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.Part
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EsquivaMuletillasViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<EsquivaChatMessage>>(emptyList())
    val messages: StateFlow<List<EsquivaChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val systemPrompt = """
        Eres "Esquiva-Muletillas", un árbitro de oratoria dinámico, divertido y estricto en un juego de improvisación rápida. 
        Tu objetivo es desafiar al usuario a hablar sobre un tema sin usar palabras de relleno (muletillas).

        MECÁNICA DEL JUEGO:
        1. Al iniciar, saluda al jugador con entusiasmo, dale 3 "Vidas" (❤️❤️❤️) y genera un "Tema Absurdo" aleatorio.
        2. Pídele al usuario que empiece su discurso (debe escribir un párrafo corto continuando la idea).
        3. En cada turno, el usuario responderá. Tú debes evaluar su respuesta buscando "Muletillas Prohibidas".

        MULETILLAS PROHIBIDAS:
        "eh", "ehh", "este", "bueno", "o sea", "verdad", "básicamente", "como tal", "nada", "¿sí me entiendes?", "entonces" (usado repetidamente), o puntos suspensivos que indiquen duda ("...").

        REGLAS DE EVALUACIÓN:
        - Si el usuario NO usa muletillas: ¡Punto para él! Sube su medidor de aplausos (+10 puntos), celebra su elocuencia y complícale las cosas metiendo un "Obstáculo" en el siguiente turno (ej. "¡Un bebé empezó a llorar en primera fila, incorpóralo al discurso!" o "Se cayó el micrófono, ¡continúa!").
        - Si el usuario USA una muletilla: Detén el juego dramáticamente. Muestra la muletilla que usó, réstale una Vida (❤️) y dile que el público murmuró. 

        CONDICIONES DE FIN DE JUEGO:
        - Si las Vidas llegan a cero: "¡GAME OVER! El público se durmió". Muestra su puntaje final y una frase motivacional divertida. Termina el juego.
        - Si el usuario sobrevive 5 turnos limpios: "¡OVACIÓN DE PIE!". Gana la partida y se convierte en Orador Maestro. Termina el juego.

        FORMATO DE RESPUESTA OBLIGATORIO:
        Mantén tus respuestas visuales, usando emojis, limpias y estructuradas de la siguiente manera en CADA turno, incluso el inicial:
        - [Estado: ❤️❤️❤️ | Puntaje: 0 pts] (Actualiza esto según las reglas)
        - [Reacción del público / Comentario del árbitro]
        - [Siguiente desafío / Obstáculo + Tema actual] (Si el juego no ha terminado)
    """.trimIndent()

    init {
        startGame()
    }

    fun startGame() {
        _messages.value = emptyList()
        sendMessage("¡Iniciemos el juego!")
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank()) return

        val userMessage = EsquivaChatMessage(text = userText, isUser = true)
        if (userText != "¡Iniciemos el juego!") {
            _messages.value = _messages.value + userMessage
        }
        
        _isLoading.value = true

        viewModelScope.launch {
            // Convert history to Content objects
            val history = mutableListOf<Content>()
            
            // Always start the history with the initial user trigger
            history.add(Content(role = "user", parts = listOf(Part(text = "Hola árbitro. Empieza el juego de Esquiva-Muletillas."))))
            
            history.addAll(
                _messages.value.map { msg ->
                    Content(
                        role = if (msg.isUser) "user" else "model",
                        parts = listOf(Part(text = msg.text))
                    )
                }
            )

            // If this is the start trigger, we don't need to add it again since it's already prepended
            // But if it's a normal user message, it's already in _messages.value so it was added in the map

            val responseText = GeminiClient.chat(history, systemPrompt)

            val botMessage = EsquivaChatMessage(text = responseText, isUser = false)
            _messages.value = _messages.value + botMessage
            _isLoading.value = false
        }
    }
}

data class EsquivaChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean
)
