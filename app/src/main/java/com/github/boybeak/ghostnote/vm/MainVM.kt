package com.github.boybeak.ghostnote.vm

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.boybeak.ghostnote.db.entity.Note
import com.github.boybeak.ghostnote.ext.database
import kotlinx.coroutines.launch

class MainVM(application: Application) : AndroidViewModel(application) {
    private val db = getApplication<Application>().database // 通过 Application 获取数据库
    val notes = mutableStateListOf<Note>()
    val hasNotes get() = notes.isNotEmpty()
    val showCreateDialog = mutableStateOf(false)

    val selectedNotes = mutableStateListOf<Note>()

    val isSelectedMode get() = selectedNotes.isNotEmpty()

    init {
        // 自动开始监听数据库变化
        viewModelScope.launch {
            db.noteDao().getNotes().collect { newNotes ->
                notes.clear()
                notes.addAll(newNotes)
            }
        }
    }

    fun deleteSelectedNotes() {
        viewModelScope.launch {
            db.noteDao().deleteNotes(selectedNotes)
            selectedNotes.clear()
        }
    }

    fun showCreateDialog() {
        showCreateDialog.value = true
    }

    fun dismissCreateDialog() {
        showCreateDialog.value = false
    }

    fun isSelected(note: Note): Boolean {
        return selectedNotes.contains(note)
    }

    fun toggleSelectNote(note: Note) {
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note)
        } else {
            selectedNotes.add(note)
        }
    }

}