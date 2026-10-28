package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SpeechDraft
import com.example.data.SpeechDraftDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SpeechDraftViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = SpeechDraftDatabase.getDatabase(application).speechDraftDao()
    val allDrafts: StateFlow<List<SpeechDraft>> = dao.getAllDrafts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveDraft(id: Int?, title: String, content: String) {
        viewModelScope.launch {
            if (id == null || id == 0) {
                dao.insertDraft(SpeechDraft(title = title, content = content))
            } else {
                dao.updateDraft(SpeechDraft(id = id, title = title, content = content, lastModified = System.currentTimeMillis()))
            }
        }
    }

    fun deleteDraft(draft: SpeechDraft) {
        viewModelScope.launch {
            dao.deleteDraft(draft)
        }
    }
}
