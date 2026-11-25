package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.data.repository.ProgresoRepository
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.UsuarioSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Progreso del estudiante: combina el avance local con los resultados guardados en ms-progreso. */
class ProgresoViewModel(
    private val contenidosRepository: ContenidosRepository,
    private val progresoRepository: ProgresoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Usuario logueado, para mostrar su nombre y curso en la cabecera. */
    val sesion: StateFlow<UsuarioSesion> = authRepository.sesion
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UsuarioSesion())

    val ejercicios: StateFlow<List<EjercicioEducativo>> = contenidosRepository.ejercicios

    /** Porcentaje (0-100) de ejercicios completados por materia: "lenguaje" -> 50. */
    val porcentajes: StateFlow<Map<String, Int>> = contenidosRepository.ejercicios
        .map(::calcularPorcentajes)
        .stateIn(viewModelScope, SharingStarted.Eagerly, calcularPorcentajes(contenidosRepository.ejercicios.value))

    private val _backendConectado = MutableStateFlow(false)
    val backendConectado: StateFlow<Boolean> = _backendConectado.asStateFlow()

    init {
        cargarResultados()
    }

    fun cargarResultados() {
        viewModelScope.launch {
            val estudianteId = authRepository.sesion.first().idEstudiante
            progresoRepository.obtenerResultados(estudianteId)
                .onSuccess { resultados ->
                    contenidosRepository.aplicarResultados(resultados)
                    _backendConectado.value = true
                }
                .onFailure { _backendConectado.value = false }
        }
    }

    companion object {
        val MATERIAS = listOf("lenguaje", "matematica", "ciencias", "historia")

        fun calcularPorcentajes(ejercicios: List<EjercicioEducativo>): Map<String, Int> =
            MATERIAS.associateWith { materia ->
                val deMateria = ejercicios.filter { it.materiaId.equals(materia, ignoreCase = true) }
                if (deMateria.isEmpty()) 0 else deMateria.count { it.completado } * 100 / deMateria.size
            }
    }
}
