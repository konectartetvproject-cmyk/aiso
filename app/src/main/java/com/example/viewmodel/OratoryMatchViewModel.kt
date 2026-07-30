package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class MatchItemType(val icon: String, val color: Long) {
    VOZ("🗣️", 0xFFE57373),
    MICROFONO("🎤", 0xFF64B5F6),
    IDEA("💡", 0xFFFFD54F),
    RELOJ("⏱️", 0xFF81C784),
    NOTAS("📝", 0xFFBA68C8),
    OJO("👁️", 0xFFFF8A65),
    EMPTY("", 0x00000000)
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
    val isPlaying: Boolean = false,
    val isGameOver: Boolean = false,
    val selectedItem: Pair<Int, Int>? = null,
    val moves: Int = 20,
    val targetScore: Int = 1000
)

class OratoryMatchViewModel : ViewModel() {
    private val _state = MutableStateFlow(MatchGameState())
    val state: StateFlow<MatchGameState> = _state.asStateFlow()

    private val rows = 6
    private val cols = 6
    private var itemIdCounter = 0

    fun startGame() {
        _state.value = MatchGameState(
            grid = generateInitialGrid(),
            isPlaying = true,
            score = 0,
            moves = 20
        )
        viewModelScope.launch {
            checkMatchesAndDrop()
        }
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
        return MatchItemType.values().filter { it != MatchItemType.EMPTY }.random()
    }

    private fun generateId(): String {
        return "item_${itemIdCounter++}"
    }

    fun selectItem(row: Int, col: Int) {
        val currentState = _state.value
        if (!currentState.isPlaying || currentState.isGameOver) return

        val selected = currentState.selectedItem
        if (selected == null) {
            _state.update { it.copy(selectedItem = Pair(row, col)) }
        } else {
            val (r, c) = selected
            if (isAdjacent(r, c, row, col)) {
                viewModelScope.launch {
                    swapItems(r, c, row, col)
                    val matchFound = checkMatchesAndDrop()
                    if (!matchFound) {
                        delay(300)
                        swapItems(row, col, r, c)
                    } else {
                        _state.update {
                            val newMoves = it.moves - 1
                            val gameOver = newMoves <= 0 || it.score >= it.targetScore
                            it.copy(moves = newMoves, isGameOver = gameOver)
                        }
                    }
                }
            }
            _state.update { it.copy(selectedItem = null) }
        }
    }

    private fun isAdjacent(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        return (Math.abs(r1 - r2) == 1 && c1 == c2) || (Math.abs(c1 - c2) == 1 && r1 == r2)
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

        while (matchFound) {
            delay(300)
            val currentState = _state.value
            val grid = currentState.grid.map { it.toMutableList() }.toMutableList()
            val toRemove = mutableSetOf<Pair<Int, Int>>()

            for (r in 0 until rows) {
                for (c in 0 until cols - 2) {
                    val t1 = grid[r][c].type
                    if (t1 != MatchItemType.EMPTY && t1 == grid[r][c + 1].type && t1 == grid[r][c + 2].type) {
                        toRemove.add(Pair(r, c))
                        toRemove.add(Pair(r, c + 1))
                        toRemove.add(Pair(r, c + 2))
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
                    }
                }
            }

            matchFound = toRemove.isNotEmpty()
            if (matchFound) {
                overallMatchFound = true
                var scoreAdd = toRemove.size * 10
                if (toRemove.size > 3) scoreAdd += 20

                _state.update { it.copy(score = it.score + scoreAdd) }

                for ((r, c) in toRemove) {
                    grid[r][c] = grid[r][c].copy(type = MatchItemType.EMPTY)
                }
                _state.update { it.copy(grid = grid) }
                delay(300)

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
             if (it.moves <= 0 || it.score >= it.targetScore) it.copy(isGameOver = true) else it
        }
        
        return overallMatchFound
    }
}
