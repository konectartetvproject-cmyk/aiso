package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.network.Content
import com.example.network.GeminiClient
import com.example.network.Part
import com.example.util.AisoOfflineEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AINotesState {
    object Idle : AINotesState()
    object Loading : AINotesState()
    data class Success(val organizedNotes: String) : AINotesState()
    data class Error(val message: String) : AINotesState()
}

class AINotesViewModel : ViewModel() {
    private val _state = MutableStateFlow<AINotesState>(AINotesState.Idle)
    val state: StateFlow<AINotesState> = _state.asStateFlow()

    fun organizeNotes(rawNotes: String) {
        if (rawNotes.isBlank()) return
        _state.value = AINotesState.Loading
        viewModelScope.launch {
            try {
                if (GeminiClient.isApiKeyConfigured()) {
                    val prompt = """
                        Organiza las siguientes ideas sueltas o notas en un esquema profesional de discurso siguiendo la metodología del libro "Los secretos del orador exitoso":
                        1. Apertura de Impacto (sugiere una Interrogación Retórica, Dato Sorprendente o Anécdota).
                        2. Desarrollo Estructurado (puntos clave claros y transiciones fluidas).
                        3. Conclusión Memorable (Llamado a la acción o Cierre Circular).
                        4. Consejo Escénico (postura, pausas y modulación de voz).
                        
                        Notas del usuario:
                        "$rawNotes"
                    """.trimIndent()

                    val response = GeminiClient.chat(
                        contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                        systemPrompt = "Eres AISO IA, mentor experto en estructuración de discursos."
                    )

                    if (!response.startsWith("Error:") && response.isNotBlank()) {
                        _state.value = AINotesState.Success(response)
                        return@launch
                    }
                }

                val organized = AisoOfflineEngine.organizeNotes(rawNotes)
                _state.value = AINotesState.Success(organized)
            } catch (e: Exception) {
                val organized = AisoOfflineEngine.organizeNotes(rawNotes)
                _state.value = AINotesState.Success(organized)
            }
        }
    }

    fun resetState() {
        _state.value = AINotesState.Idle
    }
}
