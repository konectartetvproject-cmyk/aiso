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

enum class RunnerItemType(val icon: String, val isObstacle: Boolean) {
    MULETILLA("🧟", true),
    PANICO("👻", true),
    PALABRA_CLAVE("💡", false)
}

data class RunnerItem(
    val id: Int,
    val type: RunnerItemType,
    val lane: Int,
    var y: Float
)

data class RunnerGameState(
    val playerLane: Int = 1,
    val items: List<RunnerItem> = emptyList(),
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val isPlaying: Boolean = false,
    val speed: Float = 0.02f
)

class OratoryRunnerViewModel : ViewModel() {
    private val _state = MutableStateFlow(RunnerGameState())
    val state: StateFlow<RunnerGameState> = _state.asStateFlow()

    private var gameLoopJob: Job? = null
    private var nextId = 0
    private var ticks = 0

    fun startGame() {
        _state.value = RunnerGameState(isPlaying = true)
        ticks = 0
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(50) // 20 FPS
                if (_state.value.isPlaying && !_state.value.isGameOver) {
                    updateGame()
                }
            }
        }
    }

    fun moveLeft() {
        _state.update {
            if (it.playerLane > 0) it.copy(playerLane = it.playerLane - 1) else it
        }
    }

    fun moveRight() {
        _state.update {
            if (it.playerLane < 2) it.copy(playerLane = it.playerLane + 1) else it
        }
    }

    private fun updateGame() {
        ticks++
        
        _state.update { state ->
            var newScore = state.score
            var isGameOver = false
            var newItems = state.items.toMutableList()
            var speed = state.speed

            // Aumentar velocidad con el tiempo
            if (ticks % 200 == 0) {
                speed += 0.002f
            }
            
            // Frecuencia de aparición (se hace más rápida con la velocidad)
            val spawnRate = (30 - (speed * 100).toInt()).coerceAtLeast(8)
            if (ticks % spawnRate == 0) {
                val lane = (0..2).random()
                val isObstacle = Math.random() > 0.3
                val type = if (isObstacle) {
                    if (Math.random() > 0.5) RunnerItemType.MULETILLA else RunnerItemType.PANICO
                } else {
                    RunnerItemType.PALABRA_CLAVE
                }
                newItems.add(RunnerItem(nextId++, type, lane, -0.1f))
            }
            
            val itemsToRemove = mutableListOf<RunnerItem>()
            
            // Mover objetos y comprobar colisiones
            newItems = newItems.map { item ->
                val updatedItem = item.copy(y = item.y + speed)
                
                // El jugador está en y = 0.85f
                val isColliding = updatedItem.y > 0.8f && updatedItem.y < 0.95f && updatedItem.lane == state.playerLane
                if (isColliding) {
                    if (updatedItem.type.isObstacle) {
                        isGameOver = true
                    } else {
                        newScore += 10
                        itemsToRemove.add(updatedItem)
                    }
                }
                
                if (updatedItem.y > 1.2f) {
                    itemsToRemove.add(updatedItem)
                }
                
                updatedItem
            }.toMutableList()
            
            newItems.removeAll(itemsToRemove)
            
            state.copy(
                items = newItems,
                score = newScore,
                isGameOver = isGameOver,
                speed = speed
            )
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
