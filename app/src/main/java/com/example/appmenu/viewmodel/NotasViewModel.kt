package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.local.room.Note
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.NotasRepository
import com.example.appmenu.data.repository.ProgresoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Errores de validacion del cuaderno de notas. */
data class NotasErrores(
    val titulo: String? = null,
    val contenido: String? = null
)

/**
 * Cuaderno de notas y observaciones. Las notas se guardan en Room; si quien escribe es docente,
 * ademas se envian como observacion a ms-progreso.
 */
class NotasViewModel(
    private val notasRepository: NotasRepository,
    private val progresoRepository: ProgresoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val notas: StateFlow<List<Note>> = notasRepository.notas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _errores = MutableStateFlow(NotasErrores())
    val errores: StateFlow<NotasErrores> = _errores.asStateFlow()

    fun limpiarErrorTitulo() {
        if (_errores.value.titulo != null) _errores.update { it.copy(titulo = null) }
    }

    fun limpiarErrorContenido() {
        if (_errores.value.contenido != null) _errores.update { it.copy(contenido = null) }
    }

    fun limpiarErrores() {
        _errores.value = NotasErrores()
    }

    /** Reglas de negocio del cuaderno: titulo >= 3 y observacion >= 5 caracteres. */
    fun validar(titulo: String, contenido: String): Boolean {
        val errorTitulo = when {
            titulo.isBlank() -> "El título o estudiante es obligatorio"
            titulo.trim().length < 3 -> "El título debe tener al menos 3 caracteres"
            else -> null
        }
        val errorContenido = when {
            contenido.isBlank() -> "La observación pedagógica es obligatoria"
            contenido.trim().length < 5 -> "La observación debe tener al menos 5 caracteres"
            else -> null
        }
        _errores.value = NotasErrores(titulo = errorTitulo, contenido = errorContenido)
        return errorTitulo == null && errorContenido == null
    }

    fun guardarNota(titulo: String, contenido: String, onResultado: (Boolean, String) -> Unit) {
        if (!validar(titulo, contenido)) {
            onResultado(false, "Por favor corrige los campos con error.")
            return
        }
        viewModelScope.launch {
            notasRepository.guardar(titulo.trim(), contenido.trim())
            val sesion = authRepository.sesion.first()
            if (sesion.esDocente) {
                // si el servidor no responde la nota igual queda guardada en el dispositivo
                progresoRepository.crearObservacion(
                    docenteId = sesion.id,
                    estudianteId = null,
                    contenido = "${titulo.trim()}: ${contenido.trim()}"
                )
            }
            limpiarErrores()
            onResultado(true, "Registro guardado correctamente en la base de datos.")
        }
    }

    fun eliminarNota(note: Note) {
        viewModelScope.launch { notasRepository.eliminar(note) }
    }
}
