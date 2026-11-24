package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

enum class MatchItemType(
    val icon: String,
    val label: String,
    val color: Long,
    val bookTip: String
) {
    VOZ(
        "🗣️",
        "Voz",
        0xFFE11D48,
        "Modulación Vocal: Alterna volumen, tono y velocidad para mantener despierta a la audiencia."
    ),
    MICROFONO(
        "🎤",
        "Escena",
        0xFF2563EB,
        "Presencia Escénica: Habla sin leer para proyectar autoridad y dominio del tema."
    ),
    IDEA(
        "💡",
        "Método",
        0xFFD97706,
        "Fórmula AISO: 1 Concepto claro + 1 Historia vivencial + Técnicas aplicables."
    ),
    RELOJ(
        "⏱️",
        "Pausa",
        0xFF059669,
        "Silencio Estratégico: Haz una pausa de 2 segundos antes de tu frase más importante."
    ),
    NOTAS(
        "📝",
        "11 Pasos",
        0xFF7C3AED,
        "Estructura 10-80-10: 10% Inicio cautivador, 80% Cuerpo sólido y 10% Cierre memorable."
    ),
    OJO(
        "👀",
        "Mirada",
        0xFF0D9488,
        "Contacto Visual: Conecta con cada sector del público durante 3 a 5 segundos."
    ),
    EMPTY("", "", 0x00000000, "")
}

data class MatchItem(
    val id: String,
    val type: MatchItemType,
    val row: Int,
    val col: Int
)

data class MatchGameState(
    val grid: List<List<MatchItem>> = emptyList(),
    val score: Int = 0,
    val level: Int = 1,
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false,
    val isLevelWon: Boolean = false,
    val selectedItem: Pair<Int, Int>? = null,
    val hintCells: Set<Pair<Int, Int>> = emptySet(),
    val moves: Int = 18,
    val targetScore: Int = 450,
    val shufflesLeft: Int = 3,
    val lastCombo: Int = 0,
    val activeTipTitle: String = "Conecta 3 o más símbolos iguales",
    val activeTipBody: String = "Cada combinación desbloquea un principio clave del libro «Los secretos del orador exitoso»."
)

class OratoryMatchViewModel : ViewModel() {
    private val _state = MutableStateFlow(MatchGameState())
    val state: StateFlow<MatchGameState> = _state.asStateFlow()
    private val rows = 6
    private val cols = 6
    private var itemIdCounter = 0
    private var isProcessing = false

    fun startGame(level: Int = 1) {
        isProcessing = false
        val baseScore = if (level == 1) 0 else _state.value.score
        val target = baseScore + (level * 450)
        val movesForLevel = (16 + level * 2).coerceAtMost(25)
        _state.value = MatchGameState(
            grid = generateInitialGrid(),
            isPlaying = true,
            score = baseScore,
            level = level,
            moves = movesForLevel,
            targetScore = target,
            shufflesLeft = 3
        )
        viewModelScope.launch {
            checkMatchesAndDrop()
        }
    }

    fun nextLevel() {
        val next = _state.value.level + 1
        startGame(level = next)
    }

    private fun generateInitialGrid(): List<List<MatchItem>> {
        val grid = mutableListOf<MutableList<MatchItem>>()
        for (r in 0 until rows) {
            val rowList = mutableListOf<MatchItem>()
            for (c in 0 until cols) {
                var type: MatchItemType
                do {
                    type = getRandomType()
                } while (
                    (c >= 2 && rowList[c - 1].type == type && rowList[c - 2].type == type) ||
                    (r >= 2 && grid[r - 1][c].type == type && grid[r - 2][c].type == type)
                )
                rowList.add(MatchItem(generateId(), type, r, c))
            }
            grid.add(rowList)
        }
        return grid
    }

    private fun getRandomType(): MatchItemType {
        return MatchItemType.entries.filter { it != MatchItemType.EMPTY }.random()
    }

    private fun generateId(): String {
        return "item_${itemIdCounter++}"
    }

    fun shuffleBoard() {
        val current = _state.value
        if (!current.isPlaying || current.isGameOver || current.shufflesLeft <= 0 || isProcessing) return
        _state.update {
            it.copy(
                grid = generateInitialGrid(),
                selectedItem = null,
                hintCells = emptySet(),
                shufflesLeft = it.shufflesLeft - 1,
                activeTipTitle = "🔄 Tablero reorganizado",
                activeTipBody = "Busca nuevas combinaciones estratégicas en el escenario."
            )
        }
    }

    fun showHint() {
        val current = _state.value
        if (!current.isPlaying || current.isGameOver || isProcessing) return
        val grid = current.grid
        if (grid.isEmpty()) return

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val neighbors = listOf(Pair(r, c + 1), Pair(r + 1, c))
                for ((nr, nc) in neighbors) {
                    if (nr in 0 until rows && nc in 0 until cols) {
                        if (wouldMatchAfterSwap(grid, r, c, nr, nc)) {
                            _state.update {
                                it.copy(
                                    hintCells = setOf(Pair(r, c), Pair(nr, nc)),
                                    activeTipTitle = "💡 Pista encontrada",
                                    activeTipBody = "Intercambia las dos fichas resaltadas en dorado."
                                )
                            }
                            return
                        }
                    }
                }
            }
        }
        // Si no hay movimientos posibles, reorganizar gratis
        _state.update {
            it.copy(
                grid = generateInitialGrid(),
                hintCells = emptySet(),
                activeTipTitle = "🔄 Sin jugadas disponibles",
                activeTipBody = "El tablero se mezcló automáticamente para continuar."
            )
        }
    }

    private fun wouldMatchAfterSwap(
        grid: List<List<MatchItem>>,
        r1: Int,
        c1: Int,
        r2: Int,
        c2: Int
    ): Boolean {
        val copy = grid.map { it.toMutableList() }.toMutableList()
        val temp = copy[r1][c1]
        copy[r1][c1] = copy[r2][c2]
        copy[r2][c2] = temp

        for (r in 0 until rows) {
            for (c in 0 until cols - 2) {
                val t = copy[r][c].type
                if (t != MatchItemType.EMPTY && t == copy[r][c + 1].type && t == copy[r][c + 2].type) {
                    return true
                }
            }
        }
        for (c in 0 until cols) {
            for (r in 0 until rows - 2) {
                val t = copy[r][c].type
                if (t != MatchItemType.EMPTY && t == copy[r + 1][c].type && t == copy[r + 2][c].type) {
                    return true
                }
            }
        }
        return false
    }

    fun selectItem(row: Int, col: Int) {
        val currentState = _state.value
        if (!currentState.isPlaying || currentState.isGameOver || isProcessing) return
        val selected = currentState.selectedItem
        if (selected == null) {
            _state.update { it.copy(selectedItem = Pair(row, col), hintCells = emptySet()) }
        } else {
            val (r, c) = selected
            if (r == row && c == col) {
                _state.update { it.copy(selectedItem = null) }
            } else if (isAdjacent(r, c, row, col)) {
                isProcessing = true
                _state.update { it.copy(selectedItem = null, hintCells = emptySet()) }
                viewModelScope.launch {
                    swapItems(r, c, row, col)
                    val matchFound = checkMatchesAndDrop()
                    if (!matchFound) {
                        delay(240)
                        swapItems(row, col, r, c)
                    } else {
                        _state.update {
                            val newMoves = (it.moves - 1).coerceAtLeast(0)
                            val won = it.score >= it.targetScore
                            val over = won || newMoves <= 0
                            it.copy(
                                moves = newMoves,
                                isLevelWon = won,
                                isGameOver = over
                            )
                        }
                    }
                    isProcessing = false
                }
            } else {
                // Selecciona inmediatamente la nueva celda si no es adyacente
                _state.update { it.copy(selectedItem = Pair(row, col), hintCells = emptySet()) }
            }
        }
    }

    private fun isAdjacent(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        return (abs(r1 - r2) == 1 && c1 == c2) || (abs(c1 - c2) == 1 && r1 == r2)
    }

    private fun swapItems(r1: Int, c1: Int, r2: Int, c2: Int) {
        val currentState = _state.value
        val grid = currentState.grid.map { it.toMutableList() }.toMutableList()
        val item1 = grid[r1][c1]
        val item2 = grid[r2][c2]
        grid[r1][c1] = item2.copy(row = r1, col = c1)
        grid[r2][c2] = item1.copy(row = r2, col = c2)
        _state.update { it.copy(grid = grid) }
    }

    private suspend fun checkMatchesAndDrop(): Boolean {
        var overallMatchFound = false
        var matchFound = true
        var cascadeCount = 0

        while (matchFound) {
            delay(220)
            val currentState = _state.value
            val grid = currentState.grid.map { it.toMutableList() }.toMutableList()
            val toRemove = mutableSetOf<Pair<Int, Int>>()
            var matchedType: MatchItemType? = null

            for (r in 0 until rows) {
                for (c in 0 until cols - 2) {
                    val t1 = grid[r][c].type
                    if (t1 != MatchItemType.EMPTY && t1 == grid[r][c + 1].type && t1 == grid[r][c + 2].type) {
                        toRemove.add(Pair(r, c))
                        toRemove.add(Pair(r, c + 1))
                        toRemove.add(Pair(r, c + 2))
                        matchedType = t1
                    }
                }
            }
            for (c in 0 until cols) {
                for (r in 0 until rows - 2) {
                    val t1 = grid[r][c].type
                    if (t1 != MatchItemType.EMPTY && t1 == grid[r + 1][c].type && t1 == grid[r + 2][c].type) {
                        toRemove.add(Pair(r, c))
                        toRemove.add(Pair(r + 1, c))
                        toRemove.add(Pair(r + 2, c))
                        if (matchedType == null) matchedType = t1
                    }
                }
            }

            matchFound = toRemove.isNotEmpty()
            if (matchFound) {
                overallMatchFound = true
                cascadeCount++
                var scoreAdd = toRemove.size * 15 * cascadeCount
                if (toRemove.size >= 4) scoreAdd += 35

                val tipTitle = matchedType?.let { "${it.icon} Dominio de ${it.label}" } ?: "✨ ¡Combinación!"
                val tipBody = matchedType?.bookTip ?: ""

                _state.update {
                    val updatedScore = it.score + scoreAdd
                    val won = updatedScore >= it.targetScore
                    it.copy(
                        score = updatedScore,
                        lastCombo = cascadeCount,
                        activeTipTitle = tipTitle,
                        activeTipBody = tipBody,
                        isLevelWon = won,
                        isGameOver = won || it.isGameOver
                    )
                }

                for ((r, c) in toRemove) {
                    grid[r][c] = grid[r][c].copy(type = MatchItemType.EMPTY)
                }
                _state.update { it.copy(grid = grid) }
                delay(220)

                for (c in 0 until cols) {
                    for (r in rows - 1 downTo 0) {
                        if (grid[r][c].type == MatchItemType.EMPTY) {
                            var k = r - 1
                            while (k >= 0 && grid[k][c].type == MatchItemType.EMPTY) {
                                k--
                            }
                            if (k >= 0) {
                                grid[r][c] = grid[k][c].copy(row = r, col = c)
                                grid[k][c] = grid[k][c].copy(type = MatchItemType.EMPTY)
                            } else {
                                grid[r][c] = MatchItem(generateId(), getRandomType(), r, c)
                            }
                        }
                    }
                }
                _state.update { it.copy(grid = grid) }
            }
        }

        _state.update {
            val won = it.score >= it.targetScore
            if (won || it.moves <= 0) {
                it.copy(isLevelWon = won, isGameOver = true)
            } else it
        }

        return overallMatchFound
    }
}
