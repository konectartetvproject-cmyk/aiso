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

data class SpellCheckerState(
    val isLoading: Boolean = false,
    val correctedText: String? = null,
    val explanation: String? = null,
    val error: String? = null
)

class SpellCheckerViewModel : ViewModel() {
    private val _state = MutableStateFlow(SpellCheckerState())
    val state: StateFlow<SpellCheckerState> = _state.asStateFlow()

    fun analyzeText(text: String) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                correctedText = null,
                explanation = null
            )

            try {
                val (offlineCorrected, offlineExpl) = AisoOfflineEngine.checkSpellAndStyle(text)

                if (GeminiClient.isApiKeyConfigured()) {
                    val prompt = """
                        Corrige la ortografía, puntuación, gramática y estilo retórico del siguiente borrador de discurso u oración.
                        Elimina muletillas y mejora la fuerza persuasiva manteniendo la idea original del autor.
                        
                        Responde ESTRICTAMENTE con este formato exacto de dos secciones:
                        TEXTO_CORREGIDO:
                        [Aquí el texto corregido y pulido listo para leer en un discurso]
                        EXPLICACION:
                        [Aquí una lista breve en viñetas explicando las correcciones ortográficas, gramaticales y mejoras de oratoria realizadas]
                        
                        Texto del usuario:
                        "$text"
                    """.trimIndent()

                    val response = GeminiClient.chat(
                        contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                        systemPrompt = "Eres AISO IA, corrector experto en ortografía española y estilo retórico basado en 'Los secretos del orador exitoso'."
                    )

                    if (!response.startsWith("Error:") && response.contains("TEXTO_CORREGIDO:")) {
                        val parts = response.split("EXPLICACION:")
                        val corr = parts.getOrNull(0)
                            ?.substringAfter("TEXTO_CORREGIDO:")
                            ?.trim()
                            ?.removeSurrounding("\"")
                            ?.ifBlank { offlineCorrected } ?: offlineCorrected
                        val expl = parts.getOrNull(1)?.trim()?.ifBlank { offlineExpl } ?: offlineExpl

                        _state.value = _state.value.copy(
                            isLoading = false,
                            correctedText = corr,
                            explanation = expl
                        )
                        return@launch
                    }
                }

                _state.value = _state.value.copy(
                    isLoading = false,
                    correctedText = offlineCorrected,
                    explanation = offlineExpl
                )
            } catch (e: Exception) {
                val (offlineCorrected, offlineExpl) = AisoOfflineEngine.checkSpellAndStyle(text)
                _state.value = _state.value.copy(
                    isLoading = false,
                    correctedText = offlineCorrected,
                    explanation = offlineExpl
                )
            }
        }
    }

    fun clear() {
        _state.value = SpellCheckerState()
    }
}
