package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_attempts")
data class ExerciseAttempt(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val exerciseId: String,
    val userText: String,
    val aiFeedback: String,
    val timestamp: Long = System.currentTimeMillis()
)
