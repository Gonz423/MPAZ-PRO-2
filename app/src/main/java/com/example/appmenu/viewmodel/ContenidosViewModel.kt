package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.NuevoEjercicio
import com.example.appmenu.domain.model.UnidadEducativa
import com.example.appmenu.domain.model.UsuarioSesion
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Catalogo de unidades y ejercicios para las pantallas de inicio (estudiante y docente),
 * detalle de asignatura y gestion de contenidos.
 */
class ContenidosViewModel(
    private val contenidosRepository: ContenidosRepository,
    authRepository: AuthRepository
) : ViewModel() {

    val unidades: StateFlow<List<UnidadEducativa>> = contenidosRepository.unidades
    val ejercicios: StateFlow<List<EjercicioEducativo>> = contenidosRepository.ejercicios

    val sesion: StateFlow<UsuarioSesion> = authRepository.sesion.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UsuarioSesion()
    )

    private val _sincronizado = MutableStateFlow(false)
    /** true si el catalogo se pudo actualizar desde ms-contenidos. */
    val sincronizado: StateFlow<Boolean> = _sincronizado.asStateFlow()

    private val _mensajes = Channel<String>(Channel.BUFFERED)
    /** Mensajes de una sola vez para mostrar en un Toast. */
    val mensajes: Flow<String> = _mensajes.receiveAsFlow()

    init {
        sincronizar()
    }

    fun sincronizar() {
        viewModelScope.launch {
            _sincronizado.value = contenidosRepository.sincronizar().isSuccess
        }
    }

    fun alternarCompletado(ejercicioId: String) = contenidosRepository.alternarCompletado(ejercicioId)

    /** Devuelve el error de validacion, o null si la unidad se creo. */
    fun crearUnidad(materiaId: String, titulo: String, oa: String): String? {
        if (titulo.isBlank() || oa.isBlank()) return "Debes ingresar título y objetivo de aprendizaje."
        viewModelScope.launch { contenidosRepository.crearUnidad(materiaId, titulo, oa) }
        return null
    }

    /** Devuelve el error de validacion, o null si la actividad se creo. Sin codigo se genera uno. */
    fun crearEjercicio(materiaId: String, unidadId: String, titulo: String, oa: String, codigo: String): String? {
        if (titulo.isBlank() || oa.isBlank()) return "Debes ingresar título y objetivo de aprendizaje."
        if (unidadId.isBlank()) return "Debes seleccionar una unidad asociada."
        val codigoFinal = codigo.ifBlank { "100" + (1..9).random() }
        viewModelScope.launch {
            contenidosRepository.crearEjercicio(
                NuevoEjercicio(materiaId, unidadId, titulo, oa, codigoFinal)
            )
        }
        return null
    }

    fun cambiarVisibilidad(ejercicio: EjercicioEducativo) {
        viewModelScope.launch {
            val resultado = contenidosRepository.cambiarVisibilidad(ejercicio.id)
            val texto = if (ejercicio.disponibleParaEstudiantes) "Guía #${ejercicio.codigoGuia} ocultada a estudiantes"
            else "Guía #${ejercicio.codigoGuia} activada y visible"
            _mensajes.send(if (resultado.isSuccess) texto else "$texto (solo en este dispositivo)")
        }
    }

    fun eliminarEjercicio(ejercicio: EjercicioEducativo) {
        viewModelScope.launch {
            val resultado = contenidosRepository.eliminarEjercicio(ejercicio.id)
            _mensajes.send(
                if (resultado.isSuccess) "Guía #${ejercicio.codigoGuia} eliminada"
                else "Guía #${ejercicio.codigoGuia} eliminada solo en este dispositivo"
            )
        }
    }
}
