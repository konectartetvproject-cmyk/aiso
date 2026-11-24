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

enum class DefenseType(
    val cost: Int,
    val hp: Int,
    val title: String,
    val icon: String,
    val description: String,
    val requiredWave: Int
) {
    RESPIRACION(50, 120, "Respiración", "🧘", "+25 Confianza cada 4s", 1),
    VOZ_CLARA(100, 120, "Voz Clara", "🗣️", "Proyecta argumentos", 1),
    PAUSA(50, 350, "Pausa Poder", "⏸️", "Muro de silencio firme", 1),
    SONRISA(100, 160, "Empatía", "😊", "+50 Confianza cada 5s", 1),
    ANECDOTA(150, 140, "Historia", "📖", "Argumento de doble impacto", 2),
    CONTACTO_VISUAL(200, 150, "Mirada 360", "👀", "Cubre los 3 sectores", 2)
}

enum class DistractionType(
    val speed: Float,
    val maxHp: Int,
    val damage: Int,
    val title: String,
    val icon: String
) {
    EH(0.025f, 90, 12, "«Eh...»", "🗯️"),
    NERVIOS(0.045f, 65, 18, "Nervios", "⚡"),
    MENTE_BLANCO(0.018f, 190, 28, "Bloqueo", "🌫️"),
    SNIPER(0.032f, 130, 22, "Tóxico", "🎯")
}

data class GridCell(val col: Int, val row: Int)
data class Defense(val id: Int, val type: DefenseType, val cell: GridCell, var hp: Int, var lastActionTime: Long)
data class Distraction(val id: Int, val type: DistractionType, var x: Float, val row: Int, var hp: Int, var lastAttackTime: Long)
data class Argument(val id: Int, var x: Float, val row: Int, val damage: Int)
data class ConfidenceDrop(val id: Int, val x: Float, val y: Float, val amount: Int, val spawnTime: Long)
data class Lawnmower(val id: Int, val row: Int, val active: Boolean = true, val triggered: Boolean = false, val x: Float = -0.6f)

data class DefenseGameState(
    val confidence: Int = 200,
    val lawnmowers: List<Lawnmower> = listOf(Lawnmower(0, 0), Lawnmower(1, 1), Lawnmower(2, 2)),
    val defenses: List<Defense> = emptyList(),
    val distractions: List<Distraction> = emptyList(),
    val arguments: List<Argument> = emptyList(),
    val confidenceDrops: List<ConfidenceDrop> = emptyList(),
    val isGameOver: Boolean = false,
    val score: Int = 0,
    val levelScore: Int = 0,
    val targetLevelScore: Int = 100,
    val isLevelComplete: Boolean = false,
    val wave: Int = 1,
    val isPlaying: Boolean = false,
    val selectedDefense: DefenseType? = DefenseType.VOZ_CLARA,
    val isRemoveMode: Boolean = false,
    val statusHint: String = "Toca una casilla del escenario para colocar la técnica seleccionada."
)

class OratoryDefenseViewModel : ViewModel() {
    private val _state = MutableStateFlow(DefenseGameState())
    val state: StateFlow<DefenseGameState> = _state.asStateFlow()
    private var gameLoopJob: Job? = null
    private var nextId = 0
    private var ticks = 0
    val cols = 6
    val rows = 3

    fun startGame() {
        _state.value = DefenseGameState(
            isPlaying = true,
            confidence = 200,
            selectedDefense = DefenseType.VOZ_CLARA,
            targetLevelScore = 100
        )
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
        val nextWave = currentState.wave + 1
        _state.value = DefenseGameState(
            isPlaying = true,
            score = currentState.score,
            wave = nextWave,
            confidence = 200 + (nextWave * 25),
            targetLevelScore = 100 + (nextWave - 1) * 40,
            selectedDefense = DefenseType.VOZ_CLARA,
            statusHint = "¡Oleada $nextWave iniciada! Coloca defensas de respiración y voz."
        )
        ticks = 0
    }

    fun selectDefense(type: DefenseType?) {
        _state.update {
            it.copy(
                selectedDefense = type,
                isRemoveMode = false,
                statusHint = if (type != null) {
                    "${type.icon} ${type.title}: ${type.description} — Toca una casilla libre."
                } else {
                    "Selecciona una técnica superior para colocarla en el escenario."
                }
            )
        }
    }

    fun toggleRemoveMode() {
        _state.update {
            val nextRemove = !it.isRemoveMode
            it.copy(
                isRemoveMode = nextRemove,
                selectedDefense = if (nextRemove) null else DefenseType.VOZ_CLARA,
                statusHint = if (nextRemove) {
                    "🔄 Modo Reemplazo activo: Toca una técnica del tablero para retirarla (+25 💡)."
                } else {
                    "Selecciona una casilla para colocar tu técnica."
                }
            )
        }
    }

    fun placeDefense(col: Int, row: Int) {
        val currentState = _state.value
        val cell = GridCell(col, row)

        if (currentState.isRemoveMode) {
            val existing = currentState.defenses.find { it.cell == cell }
            if (existing != null) {
                _state.update {
                    it.copy(
                        confidence = it.confidence + 25,
                        defenses = it.defenses.filterNot { d -> d.id == existing.id },
                        isRemoveMode = false,
                        selectedDefense = DefenseType.VOZ_CLARA,
                        statusHint = "♻️ Retiraste ${existing.type.title} (+25 💡 Confianza)."
                    )
                }
            }
            return
        }

        val selected = currentState.selectedDefense ?: return
        if (currentState.defenses.any { it.cell == cell }) {
            _state.update { it.copy(statusHint = "⚠️ Esa casilla ya está ocupada. Usa el botón 🔄 si deseas liberarla.") }
            return
        }

        if (currentState.confidence >= selected.cost) {
            _state.update {
                it.copy(
                    confidence = it.confidence - selected.cost,
                    defenses = it.defenses + Defense(nextId++, selected, cell, selected.hp, System.currentTimeMillis()),
                    statusHint = "✅ ${selected.icon} ${selected.title} desplegada en fila ${row + 1}."
                )
            }
        } else {
            _state.update {
                it.copy(statusHint = "💡 Necesitas ${selected.cost} de Confianza para usar ${selected.title}.")
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

    fun collectAllConfidence() {
        _state.update { state ->
            val totalBonus = state.confidenceDrops.sumOf { it.amount }
            if (totalBonus > 0) {
                state.copy(
                    confidence = state.confidence + totalBonus,
                    confidenceDrops = emptyList(),
                    statusHint = "💡 ¡Recolectaste +$totalBonus de Confianza Escénica!"
                )
            } else state
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
            var hint = state.statusHint

            if (newLevelScore >= state.targetLevelScore) {
                isLevelComplete = true
            }

            val newWave = state.wave

            if (!isLevelComplete) {
                val baseSpawnRate = 130
                val spawnRate = (baseSpawnRate - (newWave * 16)).coerceAtLeast(38)
                if (ticks % spawnRate == 0) {
                    val row = (0 until rows).random()
                    val allowedTypes = when (newWave) {
                        1 -> listOf(DistractionType.EH, DistractionType.NERVIOS)
                        2 -> listOf(DistractionType.EH, DistractionType.NERVIOS, DistractionType.SNIPER)
                        else -> DistractionType.entries
                    }
                    val type = allowedTypes.random()
                    newDistractions.add(Distraction(nextId++, type, cols.toFloat() - 0.1f, row, type.maxHp, 0L))
                }
            }

            val dropRate = if (newWave < 3) 85 else 110
            if (ticks % dropRate == 0) {
                newDrops.add(
                    ConfidenceDrop(
                        id = nextId++,
                        x = (0 until cols).random().toFloat(),
                        y = (0 until rows).random().toFloat(),
                        amount = 25,
                        spawnTime = currentTime
                    )
                )
            }

            newDefenses.forEach { defense ->
                when (defense.type) {
                    DefenseType.RESPIRACION -> {
                        if (currentTime - defense.lastActionTime > 4200) {
                            defense.lastActionTime = currentTime
                            newConfidence += 25
                        }
                    }
                    DefenseType.SONRISA -> {
                        if (currentTime - defense.lastActionTime > 5000) {
                            defense.lastActionTime = currentTime
                            newConfidence += 50
                        }
                    }
                    DefenseType.VOZ_CLARA -> {
                        if (currentTime - defense.lastActionTime > 1400) {
                            if (newDistractions.any { it.row == defense.cell.row && it.x > defense.cell.col - 0.2f }) {
                                defense.lastActionTime = currentTime
                                newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row, 25))
                            }
                        }
                    }
                    DefenseType.ANECDOTA -> {
                        if (currentTime - defense.lastActionTime > 1500) {
                            if (newDistractions.any { it.row == defense.cell.row && it.x > defense.cell.col - 0.2f }) {
                                defense.lastActionTime = currentTime
                                newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row, 48))
                            }
                        }
                    }
                    DefenseType.CONTACTO_VISUAL -> {
                        if (currentTime - defense.lastActionTime > 1500) {
                            if (newDistractions.any { it.row in (defense.cell.row - 1)..(defense.cell.row + 1) && it.x > defense.cell.col - 0.2f }) {
                                defense.lastActionTime = currentTime
                                if (defense.cell.row > 0) newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row - 1, 24))
                                newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row, 24))
                                if (defense.cell.row < rows - 1) newArguments.add(Argument(nextId++, defense.cell.col.toFloat() + 0.5f, defense.cell.row + 1, 24))
                            }
                        }
                    }
                    DefenseType.PAUSA -> Unit
                }
            }

            // Auto-recolectar gotas de confianza tras 3.5 segundos para comodidad del jugador
            val remainingDrops = mutableListOf<ConfidenceDrop>()
            for (drop in newDrops) {
                if (currentTime - drop.spawnTime > 3500) {
                    newConfidence += drop.amount
                } else {
                    remainingDrops.add(drop)
                }
            }
            newDrops = remainingDrops

            val argumentsToRemove = mutableListOf<Argument>()
            val distractionsToRemove = mutableListOf<Distraction>()
            newArguments = newArguments.map { arg ->
                var hit = false
                val updatedArg = arg.copy(x = arg.x + 0.26f)

                val target = newDistractions.firstOrNull {
                    it.row == updatedArg.row && it.x <= updatedArg.x + 0.2f && it.x + 0.65f >= updatedArg.x
                }
                if (target != null) {
                    target.hp -= updatedArg.damage
                    hit = true
                    if (target.hp <= 0 && !distractionsToRemove.contains(target)) {
                        distractionsToRemove.add(target)
                        newScore += 15
                        newLevelScore += 15
                    }
                }

                if (updatedArg.x > cols + 0.5f || hit) {
                    argumentsToRemove.add(updatedArg)
                }
                updatedArg
            }.toMutableList()

            newArguments.removeAll(argumentsToRemove)

            newDistractions = newDistractions.mapNotNull { dist ->
                val blockingDefense = newDefenses.firstOrNull {
                    it.cell.row == dist.row &&
                        it.cell.col.toFloat() >= dist.x - 0.65f &&
                        it.cell.col.toFloat() <= dist.x + 0.2f
                }

                if (blockingDefense != null) {
                    if (currentTime - dist.lastAttackTime > 900) {
                        blockingDefense.hp -= dist.type.damage
                        dist.lastAttackTime = currentTime
                    }
                    dist
                } else {
                    val updatedDist = dist.copy(x = dist.x - dist.type.speed)
                    if (updatedDist.x < -0.35f) {
                        val lmIndex = newLawnmowers.indexOfFirst { it.row == dist.row }
                        val lm = newLawnmowers.getOrNull(lmIndex)
                        if (lm != null && lm.active && !lm.triggered) {
                            newLawnmowers[lmIndex] = lm.copy(triggered = true, x = -0.3f)
                            hint = "🛡️ ¡Reserva de Confianza limpió la fila ${dist.row + 1}!"
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
                    val updatedX = lm.x + 0.28f
                    val hitDistractions = newDistractions.filter { it.row == lm.row && it.x <= updatedX + 0.5f }
                    distractionsToRemove.addAll(hitDistractions)
                    hitDistractions.forEach {
                        newScore += 10
                        newLevelScore += 10
                    }

                    if (updatedX > cols + 0.5f) {
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
                wave = newWave,
                statusHint = hint
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}
