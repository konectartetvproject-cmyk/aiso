package com.example.repository

import com.example.data.FinanceDao
import com.example.data.FinanceTransaction
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val financeDao: FinanceDao) {
    val allTransactions: Flow<List<FinanceTransaction>> = financeDao.getAllTransactions()
    suspend fun insert(transaction: FinanceTransaction) {
        financeDao.insertTransaction(transaction)
    }
    suspend fun delete(transaction: FinanceTransaction) {
        financeDao.deleteTransaction(transaction)
    }
}
