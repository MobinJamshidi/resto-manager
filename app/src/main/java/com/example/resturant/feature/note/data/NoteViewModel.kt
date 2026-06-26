package com.example.resturant.feature.note

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.resturant.feature.note.data.Note
import com.example.resturant.feature.note.data.NoteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = NoteDatabase.getInstance(application).noteDao()

    val notes: Flow<List<Note>> = dao.getAll()

    fun save(note: Note) = viewModelScope.launch {
        if (note.id == 0L) dao.insert(note) else dao.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        dao.delete(note)
    }
}