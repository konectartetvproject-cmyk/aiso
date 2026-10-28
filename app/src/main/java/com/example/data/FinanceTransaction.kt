package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "finance_transactions")
data class FinanceTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val isIncome: Boolean,
    val amount: Double,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
