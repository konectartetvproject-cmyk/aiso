package com.example.viewmodel.english

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FallingWord(
    val id: Int,
    val english: String,
    val spanish: String,
    var yPosition: Float = 0f,
    val speed: Float = 0.05f,
    val xOffset: Float
)

data class EnglishGame8State(
    val words: List<FallingWord> = emptyList(),
    val health: Int = 3,
    val score: Int = 0,
    val isGameOver: Boolean = false
)

class EnglishGame8ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame8State())
    val state: StateFlow<EnglishGame8State> = _state.asStateFlow()

    private val vocabulary = listOf(
        "Apple" to "Manzana", "House" to "Casa", "Car" to "Coche",
        "Dog" to "Perro", "Cat" to "Gato", "Sun" to "Sol",
        "Moon" to "Luna", "Water" to "Agua", "Fire" to "Fuego",
        "Tree" to "Arbol", "Book" to "Libro", "Pen" to "Boligrafo",
        "City" to "Ciudad", "Road" to "Camino", "Sky" to "Cielo"
    )

    private var wordIdCounter = 0
    private var isPlaying = false

    fun startGame() {
        if (isPlaying) return
        isPlaying = true
        _state.value = EnglishGame8State()
        viewModelScope.launch {
            gameLoop()
        }
        viewModelScope.launch {
            spawnLoop()
        }
    }

    fun stopGame() {
        isPlaying = false
    }

    private suspend fun gameLoop() {
        while (isPlaying && _state.value.health > 0) {
            delay(50)
            _state.update { currentState ->
                val newWords = currentState.words.map { it.copy(yPosition = it.yPosition + it.speed) }.toMutableList()
                var newHealth = currentState.health
                
                val iterator = newWords.iterator()
                while (iterator.hasNext()) {
                    val word = iterator.next()
                    if (word.yPosition >= 1f) {
                        iterator.remove()
                        newHealth -= 1
                    }
                }
                
                val gameOver = newHealth <= 0
                if (gameOver) isPlaying = false
                
                currentState.copy(words = newWords, health = newHealth, isGameOver = gameOver)
            }
        }
    }

    private suspend fun spawnLoop() {
        while (isPlaying && _state.value.health > 0) {
            val spawnRate = maxOf(1500L, 3500L - (_state.value.score * 20L))
            delay(spawnRate)
            if (isPlaying) {
                val pair = vocabulary.random()
                val newWord = FallingWord(
                    id = wordIdCounter++,
                    english = pair.first,
                    spanish = pair.second,
                    xOffset = Math.random().toFloat() * 0.8f + 0.1f,
                    speed = 0.002f + (Math.random().toFloat() * 0.002f) + (_state.value.score * 0.00005f)
                )
                _state.update { it.copy(words = it.words + newWord) }
            }
        }
    }

    fun checkWord(input: String): Boolean {
        var matched = false
        _state.update { currentState ->
            val cleanInput = input.trim().lowercase()
            val targetWord = currentState.words.find { 
                it.spanish.lowercase() == cleanInput || 
                it.spanish.lowercase().replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u") == cleanInput 
            }
            if (targetWord != null) {
                matched = true
                currentState.copy(
                    words = currentState.words.filter { it.id != targetWord.id },
                    score = currentState.score + 10
                )
            } else {
                currentState
            }
        }
        return matched
    }
}
