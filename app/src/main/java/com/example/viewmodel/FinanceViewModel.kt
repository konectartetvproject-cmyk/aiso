package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FinanceDatabase
import com.example.data.FinanceTransaction
import com.example.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository
    val uiState: StateFlow<List<FinanceTransaction>>

    init {
        val database = FinanceDatabase.getDatabase(application)
        repository = FinanceRepository(database.financeDao())
        uiState = repository.allTransactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun addTransaction(description: String, amount: Double, isIncome: Boolean) {
        viewModelScope.launch {
            repository.insert(
                FinanceTransaction(
                    description = description,
                    amount = amount,
                    isIncome = isIncome
                )
            )
        }
    }

    fun deleteTransaction(transaction: FinanceTransaction) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }
}
