package com.example.viewmodel.english

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Flashcard(
    val english: String,
    val spanish: String,
    val pronunciation: String
)

data class EnglishGame12State(
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isShowingTranslation: Boolean = false
)

class EnglishGame12ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame12State())
    val state: StateFlow<EnglishGame12State> = _state.asStateFlow()

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        val words = listOf(
            Flashcard("Apple", "Manzana", "æpl"),
            Flashcard("House", "Casa", "haʊs"),
            Flashcard("Water", "Agua", "wɔːtər"),
            Flashcard("Dog", "Perro", "dɒɡ"),
            Flashcard("Cat", "Gato", "kæt"),
            Flashcard("Sun", "Sol", "sʌn"),
            Flashcard("Moon", "Luna", "mun"),
            Flashcard("Book", "Libro", "bʊk"),
            Flashcard("Time", "Tiempo", "taɪm"),
            Flashcard("Friend", "Amigo", "frɛnd"),
            Flashcard("Hello", "Hola", "həˈloʊ"),
            Flashcard("Thank you", "Gracias", "θæŋk juː"),
            Flashcard("Please", "Por favor", "pliz"),
            Flashcard("Good morning", "Buenos días", "ɡʊd ˈmɔːrnɪŋ"),
            Flashcard("Goodbye", "Adiós", "ɡʊdˈbaɪ")
        )
        _state.value = EnglishGame12State(flashcards = words)
    }

    fun flipCard() {
        _state.update { it.copy(isShowingTranslation = !it.isShowingTranslation) }
    }

    fun nextCard() {
        _state.update { 
            if (it.currentIndex + 1 < it.flashcards.size) {
                it.copy(currentIndex = it.currentIndex + 1, isShowingTranslation = false)
            } else {
                it
            }
        }
    }

    fun previousCard() {
        _state.update { 
            if (it.currentIndex > 0) {
                it.copy(currentIndex = it.currentIndex - 1, isShowingTranslation = false)
            } else {
                it
            }
        }
    }
}
