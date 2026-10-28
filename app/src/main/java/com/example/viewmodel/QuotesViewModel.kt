package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.QuotesCatalog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OratoryQuote(
    val id: String,
    val text: String,
    val author: String,
    val category: String,
    val source: String = "",
    val usageTip: String = "",
    val isAffirmation: Boolean = false,
    var isFavorite: Boolean = false
)

class QuotesViewModel : ViewModel() {

    private val _quotes = MutableStateFlow(QuotesCatalog.allQuotes)
    val quotes: StateFlow<List<OratoryQuote>> = _quotes.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedAuthor = MutableStateFlow("Todos")
    val selectedAuthor: StateFlow<String> = _selectedAuthor.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        // Reset author filter if switching category so user doesn't get an empty intersection unexpectedly
        if (_selectedAuthor.value != "Todos") {
            _selectedAuthor.value = "Todos"
        }
    }

    fun selectAuthor(author: String) {
        _selectedAuthor.value = author
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(id: String) {
        _quotes.value = _quotes.value.map {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
        }
    }
}
