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

enum class DefenseType(val cost: Int, val hp: Int, val title: String, val icon: String, val description: String, val requiredWave: Int) {
    RESPIRACION(50, 100, "Respiración", "🧘", "Genera +25 Confianza.", 1),
    VOZ_CLARA(100, 100, "Voz Clara", "🗣️", "Dispara argumentos.", 1),
    PAUSA(50, 300, "Pausa", "⏸️", "Muro resistente.", 1),
    SONRISA(100, 150, "Sonrisa", "😊", "Genera +50 Confianza.", 2),
    ANECDOTA(150, 100, "Anécdota", "📖", "Dispara fuerte.", 3),
    CONTACTO_VISUAL(200, 100, "Mirada", "👀", "Disparo triple.", 4)
}

enum class DistractionType(val speed: Float, val maxHp: Int, val damage: Int, val title: String, val icon: String) {
    EH(0.03f, 100, 10, "Eh...", "🗯️"),
    NERVIOS(0.06f, 60, 20, "Nervios", "⚡"),
    MENTE_BLANCO(0.02f, 200, 30, "Mente en Blanco", "🌫️")
}

data class GridCell(val col: Int, val row: Int)
data class Defense(val id: Int, val type: DefenseType, val cell: GridCell, var hp: Int, var lastActionTime: Long)
data class Distraction(val id: Int, val type: DistractionType, var x: Float, val row: Int, var hp: Int, var lastAttackTime: Long)
data class Argument(val id: Int, var x: Float, val row: Int, val damage: Int)
data class ConfidenceDrop(val id: Int, val x: Float, val y: Float, val amount: Int, val spawnTime: Long)
data class Lawnmower(val id: Int, val row: Int, val active: Boolean = true, val triggered: Boolean = false, val x: Float = -1f)

data class DefenseGameState(
    val confidence: Int = 150,
    val lawnmowers: List<Lawnmower> = listOf(Lawnmower(0, 0), Lawnmower(1, 1), Lawnmower(2, 2)),
    val defenses: List<Defense> = emptyList(),
    val distractions: List<Distraction> = emptyList(),
    val arguments: List<Argument> = emptyList(),
    val confidenceDrops: List<ConfidenceDrop> = emptyList(),
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val levelScore: Int = 0,
    val isLevelComplete: Boolean = false,
    val wave: Int = 1,
    val isPlaying: Boolean = false,
    val selectedDefense: DefenseType? = null
)

class OratoryDefenseViewModel : ViewModel() {
    private val _state = MutableStateFlow(DefenseGameState())
    val state: StateFlow<DefenseGameState> = _state.asStateFlow()
    private var gameLoopJob: Job? = null
    private var nextId = 0
    private var ticks = 0

    fun startGame() {
        _state.value = DefenseGameState(isPlaying = true)
        ticks = 0
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (true) {
                delay(50)
                if (_state.value.isPlaying && !_state.value.isGameOver && !_state.value.isLevelComplete) {
                    updateGame()
                }
            }
        }
    }

    fun nextLevel() {
        val currentState = _state.value
        _state.value = DefenseGameState(
            isPlaying = true,
            score = currentState.score,
            wave = currentState.wave + 1,
            confidence = 150
        )
        ticks = 0
    }

    fun selectDefense(type: DefenseType?) {
        _state.update { it.copy(selectedDefense = type) }
    }

    fun placeDefense(col: Int, row: Int) {
        val currentState = _state.value
        val selected = currentState.selectedDefense ?: return
        
        if (currentState.confidence >= selected.cost) {
            val cell = GridCell(col, row)
            if (currentState.defenses.none { it.cell == cell }) {
                _state.update {
                    it.copy(
                        confidence = it.confidence - selected.cost,
                        defenses = it.defenses + Defense(nextId++, selected, cell, selected.hp, System.currentTimeMillis()),
                        selectedDefense = null
                    )
                }
            }
        }
    }
    
    fun collectConfidence(dropId: Int) {
        _state.update { state ->
            val drop = state.confidenceDrops.find { it.id == dropId }
            if (drop != null) {
                state.copy(
                    confidence = state.confidence + drop.amount,
                    confidenceDrops = state.confidenceDrops.filter { it.id != dropId }
                )
            } else {
                state
            }
        }
    }

    private fun updateGame() {
        ticks++
        val currentTime = System.currentTimeMillis()
        
        _state.update { state ->
            var newConfidence = state.confidence
            val newDefenses = state.defenses.toMutableList()
            var newDistractions = state.distractions.toMutableList()
            var newArguments = state.arguments.toMutableList()
            var newDrops = state.confidenceDrops.toMutableList()
            var newLawnmowers = state.lawnmowers.toMutableList()
            var isGameOver = false
            var isLevelComplete = false
            var newScore = state.score
            var newLevelScore = state.levelScore
            
            if (newLevelScore >= 100) {
                isLevelComplete = true
            }

            val newWave = state.wave
            
            if (!isLevelComplete) {
                val baseSpawnRate = 200
                val spawnRate = (baseSpawnRate - (newWave * 20)).coerceAtLeast(40)
                if (ticks % spawnRate == 0) {
                    val row = (0..2).random()
                    val allowedTypes = when (newWave) {
                        1 -> listOf(DistractionType.EH)
                        2 -> listOf(DistractionType.EH, DistractionType.NERVIOS)
                        else -> DistractionType.values().toList()
                    }
                    val type = allowedTypes.random()
                    newDistractions.add(Distraction(nextId++, type, 8f, row, type.maxHp, 0L))
                }
            }
            
            val dropRate = if (newWave < 3) 100 else 150
            if (ticks % dropRate == 0) { 
                newDrops.add(ConfidenceDrop(nextId++, (0..7).random().toFloat(), -1f, 25, currentTime))
            }

            newDefenses.forEach { defense ->
                if (defense.type == DefenseType.RESPIRACION) {
                    if (currentTime - defense.lastActionTime > 5000) {
                        defense.lastActionTime = currentTime
                        newDrops.add(ConfidenceDrop(nextId++, defense.cell.col.toFloat(), defense.cell.row.toFloat(), 25, currentTime))
                    }
                } else if (defense.type == DefenseType.SONRISA) {
                    if (currentTime - defense.lastActionTime > 5000) {
                        defense.lastActionTime = currentTime
                        newDrops.add(ConfidenceDrop(nextId++, defense.cell.col.toFloat(), defense.cell.row.toFloat(), 50, currentTime))
                    }
                } else if (defense.type == DefenseType.VOZ_CLARA) {
                    if (currentTime - defense.lastActionTime > 1500) {
                        if (newDistractions.any { it.row == defense.cell.row && it.x > defense.cell.col }) {
                            defense.lastActionTime = currentTime
                            newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row, 20))
                        }
                    }
                } else if (defense.type == DefenseType.ANECDOTA) {
                    if (currentTime - defense.lastActionTime > 1500) {
                        if (newDistractions.any { it.row == defense.cell.row && it.x > defense.cell.col }) {
                            defense.lastActionTime = currentTime
                            newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row, 40))
                        }
                    }
                } else if (defense.type == DefenseType.CONTACTO_VISUAL) {
                    if (currentTime - defense.lastActionTime > 1500) {
                        if (newDistractions.any { it.row in (defense.cell.row - 1)..(defense.cell.row + 1) && it.x > defense.cell.col }) {
                            defense.lastActionTime = currentTime
                            if (defense.cell.row > 0) newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row - 1, 20))
                            newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row, 20))
                            if (defense.cell.row < 2) newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row + 1, 20))
                        }
                    }
                }
            }
            
            newDrops = newDrops.mapNotNull { drop ->
                if (drop.y < 2.5f && currentTime - drop.spawnTime > 100) {  
                    drop.copy(y = drop.y + 0.05f)
                } else if (currentTime - drop.spawnTime > 10000) {
                    null
                } else {
                    drop
                }
            }.toMutableList()

            val argumentsToRemove = mutableListOf<Argument>()
            val distractionsToRemove = mutableListOf<Distraction>()
            newArguments = newArguments.map { arg ->
                var hit = false
                val updatedArg = arg.copy(x = arg.x + 0.3f)
                
                val target = newDistractions.firstOrNull { it.row == updatedArg.row && it.x <= updatedArg.x && it.x + 0.5f >= updatedArg.x }
                if (target != null) {
                    target.hp -= updatedArg.damage
                    hit = true
                    if (target.hp <= 0 && !distractionsToRemove.contains(target)) {
                        distractionsToRemove.add(target)
                        newScore += 10
                        newLevelScore += 10
                    }
                }
                
                if (updatedArg.x > 9f || hit) {
                    argumentsToRemove.add(updatedArg)
                }
                updatedArg
            }.toMutableList()
            
            newArguments.removeAll(argumentsToRemove)

            newDistractions = newDistractions.mapNotNull { dist ->
                val blockingDefense = newDefenses.firstOrNull { it.cell.row == dist.row && it.cell.col.toFloat() >= dist.x - 0.8f && it.cell.col.toFloat() <= dist.x }
                
                if (blockingDefense != null) {
                    if (currentTime - dist.lastAttackTime > 1000) {
                        blockingDefense.hp -= dist.type.damage
                        dist.lastAttackTime = currentTime
                    }
                    dist
                } else {
                    val updatedDist = dist.copy(x = dist.x - dist.type.speed)
                    if (updatedDist.x < -0.5f) {
                        val lmIndex = newLawnmowers.indexOfFirst { it.row == dist.row }
                        val lm = newLawnmowers.getOrNull(lmIndex)
                        if (lm != null && lm.active && !lm.triggered) {
                            newLawnmowers[lmIndex] = lm.copy(triggered = true, x = -0.5f)
                            null
                        } else if (lm != null && !lm.active && !lm.triggered) {
                            isGameOver = true
                            updatedDist
                        } else {
                            if (lm != null && lm.triggered && lm.x > updatedDist.x) {
                                isGameOver = true
                            }
                            updatedDist
                        }
                    } else {
                        updatedDist
                    }
                }
            }.toMutableList()
            
            newLawnmowers = newLawnmowers.map { lm ->
                if (lm.triggered) {
                    val updatedX = lm.x + 0.3f
                    val hitDistractions = newDistractions.filter { it.row == lm.row && it.x <= updatedX + 0.5f }
                    distractionsToRemove.addAll(hitDistractions)
                    hitDistractions.forEach { 
                        newScore += 10 
                        newLevelScore += 10
                    }
                    
                    if (updatedX > 9f) {
                        lm.copy(x = updatedX, triggered = false, active = false)
                    } else {
                        lm.copy(x = updatedX)
                    }
                } else {
                    lm
                }
            }.toMutableList()

            newDistractions.removeAll(distractionsToRemove)
            newDefenses.removeAll { it.hp <= 0 }
            
            state.copy(
                confidence = newConfidence,
                defenses = newDefenses,
                distractions = newDistractions,
                arguments = newArguments,
                confidenceDrops = newDrops,
                lawnmowers = newLawnmowers,
                isGameOver = isGameOver,
                isLevelComplete = isLevelComplete,
                score = newScore,
                levelScore = newLevelScore,
                wave = newWave
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
