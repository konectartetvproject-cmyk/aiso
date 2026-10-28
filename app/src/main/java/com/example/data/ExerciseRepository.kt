package com.example.data

import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val dao: ExerciseAttemptDao) {
    fun getAttemptsForExercise(exerciseId: String): Flow<List<ExerciseAttempt>> = dao.getAttemptsForExercise(exerciseId)
    fun getAllAttempts(): Flow<List<ExerciseAttempt>> = dao.getAllAttempts()

    suspend fun getCachedFeedback(exerciseId: String, userText: String): ExerciseAttempt? {
        return dao.getCachedFeedback(exerciseId, userText)
    }

    suspend fun insertAttempt(attempt: ExerciseAttempt) {
        dao.insertAttempt(attempt)
    }
}
