package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseAttemptDao {
    @Query("SELECT * FROM exercise_attempts WHERE exerciseId = :exerciseId ORDER BY timestamp DESC")
    fun getAttemptsForExercise(exerciseId: String): Flow<List<ExerciseAttempt>>

    @Query("SELECT * FROM exercise_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<ExerciseAttempt>>

    @Query("SELECT * FROM exercise_attempts WHERE exerciseId = :exerciseId AND userText = :userText LIMIT 1")
    suspend fun getCachedFeedback(exerciseId: String, userText: String): ExerciseAttempt?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExerciseAttempt)

    @Query("DELETE FROM exercise_attempts")
    suspend fun clearAll()
}
