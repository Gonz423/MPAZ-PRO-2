package com.example.appmenu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.room.AppDatabase
import com.example.appmenu.room.Note
import com.example.appmenu.room.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// modelo de errores de validacion para el cuaderno de notas
data class NotasErrores(
    val titulo: String? = null,
    val contenido: String? = null
)

// viewmodel modular para cuaderno de tareas y observaciones room
class NotasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    // flujo observable de notas desde room
    val notas: StateFlow<List<Note>>

    // flujo reactivo de errores de validacion
    private val _errores = MutableStateFlow(NotasErrores())
    val errores: StateFlow<NotasErrores> = _errores.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = NoteRepository(database.noteDao())
        notas = repository.notes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun limpiarErrorTitulo() {
        if (_errores.value.titulo != null) {
            _errores.value = _errores.value.copy(titulo = null)
        }
    }

    fun limpiarErrorContenido() {
        if (_errores.value.contenido != null) {
            _errores.value = _errores.value.copy(contenido = null)
        }
    }

    fun limpiarErrores() {
        _errores.value = NotasErrores()
    }

    // valida las reglas de negocio del cuaderno de tareas y notas
    fun validar(titulo: String, contenido: String): Boolean {
        var errorTitulo: String? = null
        var errorContenido: String? = null

        if (titulo.isBlank()) {
            errorTitulo = "El título o estudiante es obligatorio"
        } else if (titulo.trim().length < 3) {
            errorTitulo = "El título debe tener al menos 3 caracteres"
        }

        if (contenido.isBlank()) {
            errorContenido = "La observación pedagógica es obligatoria"
        } else if (contenido.trim().length < 5) {
            errorContenido = "La observación debe tener al menos 5 caracteres"
        }

        val nuevosErrores = NotasErrores(titulo = errorTitulo, contenido = errorContenido)
        _errores.value = nuevosErrores
        return errorTitulo == null && errorContenido == null
    }

    // guarda una nueva nota/tarea en la base de datos room tras validar
    fun guardarNota(titulo: String, contenido: String, onResultado: (Boolean, String) -> Unit) {
        if (!validar(titulo, contenido)) {
            onResultado(false, "Por favor corrige los campos con error.")
            return
        }

        viewModelScope.launch {
            repository.save(titulo.trim(), contenido.trim())
            limpiarErrores()
            onResultado(true, "Registro guardado correctamente en la base de datos.")
        }
    }

    // elimina una nota/tarea existente
    fun eliminarNota(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}
