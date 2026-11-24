package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RunnerItemType(
    val icon: String,
    val label: String,
    val isObstacle: Boolean,
    val points: Int = 0,
    val grantsShield: Boolean = false
) {
    MULETILLA("🗯️", "«Eh... Este...»", true),
    PANICO("😰", "Pánico Escénico", true),
    MENTE_BLANCO("🌫️", "Mente en Blanco", true),
    TOXICO("🎯", "Pregunta Trampa", true),
    PALABRA_CLAVE("💡", "Idea Central", false, points = 15),
    PAUSA_PODER("⏸️", "Pausa de Poder", false, points = 25),
    CONTACTO_VISUAL("👀", "Mirada 3-5s", false, points = 20),
    RESPIRACION_ESCUDO("🛡️", "Respiración Caja", false, points = 15, grantsShield = true)
}

data class RunnerItem(
    val id: Int,
    val type: RunnerItemType,
    val lane: Int,
    val y: Float
)

data class RunnerGameState(
    val playerLane: Int = 1,
    val items: List<RunnerItem> = emptyList(),
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val highScore: Int = 0,
    val lives: Int = 3,
    val maxLives: Int = 3,
    val hasShield: Boolean = false,
    val combo: Int = 0,
    val level: Int = 1,
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val speed: Float = 0.022f,
    val lastFeedback: String = "¡Desliza o toca un carril para esquivar muletillas!"
)

class OratoryRunnerViewModel : ViewModel() {
    private val _state = MutableStateFlow(RunnerGameState())
    val state: StateFlow<RunnerGameState> = _state.asStateFlow()
    private var gameLoopJob: Job? = null
    private var nextId = 0
    private var ticks = 0

    fun startGame() {
        val best = _state.value.highScore
        _state.value = RunnerGameState(
            isPlaying = true,
            highScore = best,
            lives = 3,
            hasShield = false,
            combo = 0,
            level = 1
        )
        ticks = 0
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(40)
                val current = _state.value
                if (current.isPlaying && !current.isGameOver && !current.isPaused) {
                    updateGame()
                }
            }
        }
    }

    fun togglePause() {
        _state.update { it.copy(isPaused = !it.isPaused) }
    }

    fun moveLeft() {
        _state.update {
            if (it.isPlaying && !it.isPaused && it.playerLane > 0) {
                it.copy(playerLane = it.playerLane - 1)
            } else it
        }
    }

    fun moveRight() {
        _state.update {
            if (it.isPlaying && !it.isPaused && it.playerLane < 2) {
                it.copy(playerLane = it.playerLane + 1)
            } else it
        }
    }

    fun setLane(lane: Int) {
        if (lane !in 0..2) return
        _state.update {
            if (it.isPlaying && !it.isPaused) {
                it.copy(playerLane = lane)
            } else it
        }
    }

    private fun updateGame() {
        ticks++
        _state.update { state ->
            var newScore = state.score
            var newLives = state.lives
            var newShield = state.hasShield
            var newCombo = state.combo
            var feedback = state.lastFeedback
            var isGameOver = false
            var newItems = state.items.toMutableList()
            val newLevel = (newScore / 150) + 1
            val speed = (0.020f + (newLevel - 1) * 0.0035f).coerceAtMost(0.045f)

            val spawnRate = (24 - (newLevel * 2)).coerceAtLeast(10)
            if (ticks % spawnRate == 0) {
                val lane = (0..2).random()
                val roll = Math.random()
                val type = when {
                    roll < 0.12 && !newShield -> RunnerItemType.RESPIRACION_ESCUDO
                    roll < 0.48 -> listOf(
                        RunnerItemType.PALABRA_CLAVE,
                        RunnerItemType.PAUSA_PODER,
                        RunnerItemType.CONTACTO_VISUAL
                    ).random()
                    else -> listOf(
                        RunnerItemType.MULETILLA,
                        RunnerItemType.PANICO,
                        RunnerItemType.MENTE_BLANCO,
                        RunnerItemType.TOXICO
                    ).random()
                }
                newItems.add(RunnerItem(nextId++, type, lane, -0.12f))
            }

            val itemsToRemove = mutableListOf<RunnerItem>()
            newItems = newItems.map { item ->
                val updatedItem = item.copy(y = item.y + speed)
                val isColliding = updatedItem.y in 0.76f..0.93f && updatedItem.lane == state.playerLane
                if (isColliding) {
                    itemsToRemove.add(updatedItem)
                    if (updatedItem.type.isObstacle) {
                        if (newShield) {
                            newShield = false
                            feedback = "🛡️ ¡Escudo de Respiración absorbió ${updatedItem.type.label}!"
                        } else {
                            newLives -= 1
                            newCombo = 0
                            feedback = "⚠️ Chocaste con ${updatedItem.type.label} (-1 Confianza)"
                            if (newLives <= 0) {
                                isGameOver = true
                            }
                        }
                    } else {
                        newCombo += 1
                        val multiplier = (1 + (newCombo / 4)).coerceAtMost(4)
                        val gained = updatedItem.type.points * multiplier
                        newScore += gained
                        if (updatedItem.type.grantsShield) {
                            newShield = true
                            feedback = "🛡️ ¡Escudo de Respiración de Caja activado!"
                        } else {
                            feedback = "✨ +$gained pts • ${updatedItem.type.label}" +
                                if (multiplier > 1) " (Combo x$multiplier)" else ""
                        }
                    }
                } else if (updatedItem.y > 1.15f) {
                    itemsToRemove.add(updatedItem)
                }
                updatedItem
            }.toMutableList()

            newItems.removeAll(itemsToRemove)
            val bestScore = maxOf(state.highScore, newScore)

            state.copy(
                items = newItems,
                score = newScore,
                highScore = bestScore,
                lives = newLives,
                hasShield = newShield,
                combo = newCombo,
                level = newLevel,
                isGameOver = isGameOver,
                speed = speed,
                lastFeedback = feedback
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
