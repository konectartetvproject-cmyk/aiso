package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SpeechDraft::class], version = 1, exportSchema = false)
abstract class SpeechDraftDatabase : RoomDatabase() {
    abstract fun speechDraftDao(): SpeechDraftDao

    companion object {
        @Volatile
        private var INSTANCE: SpeechDraftDatabase? = null

        fun getDatabase(context: Context): SpeechDraftDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpeechDraftDatabase::class.java,
                    "speech_draft_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
