package com.example.appmenu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.room.AppDatabase
import com.example.appmenu.room.Note
import com.example.appmenu.room.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// VIEWMODEL MODULAR PARA CUADERNO DE TAREAS Y OBSERVACIONES (ROOM)
class NotasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    // Flujo observable de notas desde Room
    val notas: StateFlow<List<Note>>

    init {
        val database = AppDatabase.getInstance(application)
        repository = NoteRepository(database.noteDao())
        notas = repository.notes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    //  Guarda una nueva nota/tarea en la base de datos Room
    fun guardarNota(titulo: String, contenido: String, onResultado: (Boolean, String) -> Unit) {
        if (titulo.isBlank() || contenido.isBlank()) {
            onResultado(false, "Escribe el título y la observación.")
            return
        }

        viewModelScope.launch {
            repository.save(titulo.trim(), contenido.trim())
            onResultado(true, "Registro guardado correctamente en la base de datos.")
        }
    }

    //  Elimina una nota/tarea existente
    fun eliminarNota(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}
