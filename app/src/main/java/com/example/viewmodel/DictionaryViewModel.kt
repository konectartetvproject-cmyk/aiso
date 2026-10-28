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

data class DictionaryEntry(
    val word: String = "",
    val definition: String = "",
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)

data class DictionaryState(
    val isLoading: Boolean = false,
    val entry: DictionaryEntry? = null,
    val error: String? = null,
    val searchHistory: List<String> = emptyList(),
    val language: String = "Español"
)

class DictionaryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DictionaryState())
    val state: StateFlow<DictionaryState> = _state.asStateFlow()

    fun setLanguage(lang: String) {
        _state.value = _state.value.copy(language = lang)
    }

    fun searchWord(word: String) {
        val cleanWord = word.trim()
        if (cleanWord.isBlank()) return

        val currentLang = _state.value.language

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val offlineEntry = AisoOfflineEngine.lookupWord(cleanWord, currentLang)
                var finalEntry = DictionaryEntry(
                    word = offlineEntry.word,
                    definition = offlineEntry.definition,
                    synonyms = offlineEntry.synonyms,
                    antonyms = offlineEntry.antonyms
                )

                val isGenericFallback = offlineEntry.definition.startsWith("Término relevante en la expresión oral")
                if (isGenericFallback && GeminiClient.isApiKeyConfigured()) {
                    val prompt = """
                        Define la palabra o concepto "$cleanWord" enfocado en el idioma/contexto "$currentLang" y su utilidad en la oratoria y expresión oral.
                        Responde ESTRICTAMENTE con este formato exacto de 3 líneas:
                        DEFINICION: [Definición clara, precisa y su aplicación en el discurso]
                        SINONIMOS: [4 sinónimos separados por coma]
                        ANTONIMOS: [3 antónimos separados por coma]
                    """.trimIndent()

                    val aiResponse = GeminiClient.chat(
                        contents = listOf(Content(role = "user", parts = listOf(Part(text = prompt)))),
                        systemPrompt = "Eres el Diccionario Retórico de AISO IA."
                    )

                    if (!aiResponse.startsWith("Error:") && aiResponse.contains("DEFINICION:")) {
                        val def = aiResponse.substringAfter("DEFINICION:")
                            .substringBefore("SINONIMOS:")
                            .trim()
                            .ifBlank { offlineEntry.definition }
                        val syns = aiResponse.substringAfter("SINONIMOS:", "")
                            .substringBefore("ANTONIMOS:")
                            .split(",")
                            .map { it.trim().removeSuffix(".") }
                            .filter { it.isNotBlank() }
                            .ifEmpty { offlineEntry.synonyms }
                        val ants = aiResponse.substringAfter("ANTONIMOS:", "")
                            .split(",")
                            .map { it.trim().removeSuffix(".") }
                            .filter { it.isNotBlank() }
                            .ifEmpty { offlineEntry.antonyms }

                        finalEntry = DictionaryEntry(
                            word = cleanWord.replaceFirstChar { it.uppercase() },
                            definition = def,
                            synonyms = syns,
                            antonyms = ants
                        )
                    }
                }

                val history = _state.value.searchHistory.toMutableList()
                history.removeAll { it.equals(finalEntry.word, ignoreCase = true) }
                history.add(0, finalEntry.word)

                _state.value = _state.value.copy(
                    isLoading = false,
                    entry = finalEntry,
                    searchHistory = history.take(10)
                )
            } catch (e: Exception) {
                val offlineEntry = AisoOfflineEngine.lookupWord(cleanWord, currentLang)
                _state.value = _state.value.copy(
                    isLoading = false,
                    entry = DictionaryEntry(
                        word = offlineEntry.word,
                        definition = offlineEntry.definition,
                        synonyms = offlineEntry.synonyms,
                        antonyms = offlineEntry.antonyms
                    )
                )
            }
        }
    }
}
