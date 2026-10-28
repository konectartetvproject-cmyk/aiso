package com.example.viewmodel.english

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Tile(
    val id: Int,
    val text: String,
    val isEnglish: Boolean,
    val pairId: Int,
    val isSelected: Boolean = false
)

data class EnglishGame10State(
    val grid: List<List<Tile?>> = emptyList(),
    val score: Int = 0,
    val moves: Int = 20,
    val isGameOver: Boolean = false
)

class EnglishGame10ViewModel : ViewModel() {
    private val _state = MutableStateFlow(EnglishGame10State())
    val state: StateFlow<EnglishGame10State> = _state.asStateFlow()

    private val cols = 3
    private val rows = 4
    private var tileIdCounter = 0

    private val wordPairs = listOf(
        "Cat" to "Gato", "Dog" to "Perro", "Bird" to "Pájaro",
        "Fish" to "Pez", "Cow" to "Vaca", "Pig" to "Cerdo",
        "Horse" to "Caballo", "Sheep" to "Oveja", "Duck" to "Pato",
        "Red" to "Rojo", "Blue" to "Azul", "Green" to "Verde",
        "Yellow" to "Amar.", "Black" to "Negro", "White" to "Blanco"
    )

    init {
        initGrid()
    }

    fun initGrid() {
        tileIdCounter = 0
        val initialTiles = mutableListOf<Tile>()
        for (i in 0 until (cols * rows) / 2) {
            val pair = getNextPair()
            initialTiles.add(pair.first)
            initialTiles.add(pair.second)
        }
        initialTiles.shuffle()
        
        val grid = List(rows) { r ->
            List(cols) { c ->
                initialTiles[r * cols + c]
            }
        }
        _state.value = EnglishGame10State(grid = grid)
    }

    private fun getNextPair(): Pair<Tile, Tile> {
        val p = wordPairs.random()
        val pId = tileIdCounter++
        val t1 = Tile(tileIdCounter++, p.first, true, pId)
        val t2 = Tile(tileIdCounter++, p.second, false, pId)
        return t1 to t2
    }

    fun selectTile(row: Int, col: Int) {
        val currentState = _state.value
        if (currentState.isGameOver) return
        
        val tile = currentState.grid[row][col] ?: return
        val currentlySelected = currentState.grid.flatMap { r -> r.filterNotNull().filter { it.isSelected } }
        
        if (tile.isSelected) {
            val newGrid = currentState.grid.map { r -> r.map { if (it?.id == tile.id) it.copy(isSelected = false) else it } }
            _state.update { it.copy(grid = newGrid) }
            return
        }
        
        if (currentlySelected.size == 1) {
            val selectedTile = currentlySelected.first()
            if (selectedTile.pairId == tile.pairId && selectedTile.isEnglish != tile.isEnglish) {
                var newGrid = currentState.grid.map { r -> r.map { if (it?.id == tile.id || it?.id == selectedTile.id) null else it }.toMutableList() }.toMutableList()
                
                for (c in 0 until cols) {
                    var emptySpaces = 0
                    for (r in rows - 1 downTo 0) {
                        if (newGrid[r][c] == null) {
                            emptySpaces++
                        } else if (emptySpaces > 0) {
                            newGrid[r + emptySpaces][c] = newGrid[r][c]
                            newGrid[r][c] = null
                        }
                    }
                }
                
                val emptySpots = mutableListOf<Pair<Int, Int>>()
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if (newGrid[r][c] == null) {
                            emptySpots.add(r to c)
                        }
                    }
                }
                
                emptySpots.shuffle()
                for (i in 0 until emptySpots.size step 2) {
                    if (i + 1 < emptySpots.size) {
                        val pair = getNextPair()
                        newGrid[emptySpots[i].first][emptySpots[i].second] = pair.first
                        newGrid[emptySpots[i+1].first][emptySpots[i+1].second] = pair.second
                    }
                }
                
                _state.update { it.copy(
                    grid = newGrid,
                    score = it.score + 20,
                    moves = it.moves - 1,
                    isGameOver = it.moves - 1 <= 0
                ) }
            } else {
                val newGrid = currentState.grid.map { r -> r.map { 
                    if (it?.id == tile.id) it.copy(isSelected = true) 
                    else if (it?.isSelected == true) it.copy(isSelected = false)
                    else it 
                } }
                _state.update { it.copy(grid = newGrid, moves = it.moves - 1, isGameOver = it.moves - 1 <= 0) }
            }
        } else {
            val newGrid = currentState.grid.map { r -> r.map { if (it?.id == tile.id) it.copy(isSelected = true) else it } }
            _state.update { it.copy(grid = newGrid) }
        }
    }
}
