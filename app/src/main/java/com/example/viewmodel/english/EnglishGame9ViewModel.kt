package com.example.viewmodel.english

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EnglishGame9State(
    val playerHealth: Int = 100,
    val enemyHealth: Int = 100,
    val currentEnemy: Int = 1,
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val message: String = "¡Un enemigo salvaje aparece!",
    val isGameOver: Boolean = false,
    val hasWon: Boolean = false
)

class EnglishGame9ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame9State())
    val state: StateFlow<EnglishGame9State> = _state.asStateFlow()

    private val vocabulary = listOf(
        "Sword" to "Espada", "Shield" to "Escudo", "Magic" to "Magia",
        "Knight" to "Caballero", "Dragon" to "Dragón", "Castle" to "Castillo",
        "King" to "Rey", "Queen" to "Reina", "Battle" to "Batalla",
        "Armor" to "Armadura", "Potion" to "Poción", "Arrow" to "Flecha"
    )

    init {
        nextTurn()
    }
    
    fun restart() {
        _state.value = EnglishGame9State()
        nextTurn()
    }

    private fun nextTurn(customMessage: String? = null) {
        val current = _state.value
        if (current.playerHealth <= 0) {
            _state.update { it.copy(isGameOver = true, hasWon = false, message = "Has sido derrotado...") }
            return
        }
        if (current.enemyHealth <= 0) {
            val nextEnemy = current.currentEnemy + 1
            if (nextEnemy > 3) {
                _state.update { it.copy(isGameOver = true, hasWon = true, message = "¡Has derrotado a todos los enemigos!") }
                return
            } else {
                _state.update { it.copy(
                    enemyHealth = 100 + (nextEnemy * 50),
                    currentEnemy = nextEnemy,
                    message = "¡Un enemigo más fuerte aparece!"
                )}
            }
        }

        val pair = vocabulary.random()
        val correct = pair.second
        val wrong = vocabulary.filter { it.second != correct }.map { it.second }.shuffled().take(3)
        val opts = (wrong + listOf(correct)).shuffled()

        _state.update { it.copy(
            question = "¿Qué significa '${pair.first}'?",
            correctAnswer = correct,
            options = opts,
            message = customMessage ?: it.message
        )}
    }

    fun answer(selected: String) {
        val current = _state.value
        if (current.isGameOver) return

        if (selected == current.correctAnswer) {
            val damage = 25 + (Math.random() * 15).toInt()
            _state.update { it.copy(
                enemyHealth = maxOf(0, it.enemyHealth - damage)
            )}
            nextTurn("¡Ataque certero! Causaste $damage de daño.")
        } else {
            val damage = 15 + (Math.random() * 15).toInt() * current.currentEnemy
            _state.update { it.copy(
                playerHealth = maxOf(0, it.playerHealth - damage)
            )}
            nextTurn("¡Fallaste! El enemigo te atacó y causó $damage de daño.")
        }
    }
}
