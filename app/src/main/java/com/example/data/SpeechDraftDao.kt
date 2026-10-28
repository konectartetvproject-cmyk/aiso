package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeechDraftDao {
    @Query("SELECT * FROM speech_drafts ORDER BY lastModified DESC")
    fun getAllDrafts(): Flow<List<SpeechDraft>>

    @Query("SELECT * FROM speech_drafts WHERE id = :id")
    suspend fun getDraftById(id: Int): SpeechDraft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: SpeechDraft)

    @Update
    suspend fun updateDraft(draft: SpeechDraft)

    @Delete
    suspend fun deleteDraft(draft: SpeechDraft)
}
