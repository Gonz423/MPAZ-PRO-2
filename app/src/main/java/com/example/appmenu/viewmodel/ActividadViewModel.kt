package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.data.repository.ProgresoRepository
import com.example.appmenu.domain.model.EjercicioEducativo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActividadUiState(
    val opcionSeleccionada: Int? = null,
    val mensajeError: String? = null,
    val mostrarExito: Boolean = false,
    /** Cantidad de veces que el estudiante presiono "Responder" con una alternativa elegida. */
    val intentos: Int = 0
)

enum class RespuestaActividad { SIN_SELECCION, INCORRECTA, CORRECTA }

/** Actividad interactiva: valida la respuesta y registra el resultado en ms-progreso. */
class ActividadViewModel(
    private val ejercicioId: String?,
    private val contenidosRepository: ContenidosRepository,
    private val progresoRepository: ProgresoRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    /** El ejercicio pedido, o el primero del catalogo si no se indico uno (comportamiento original). */
    val ejercicio: StateFlow<EjercicioEducativo?> = contenidosRepository.ejercicios
        .map { lista -> lista.find { it.id == ejercicioId } ?: lista.firstOrNull() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            contenidosRepository.ejercicios.value.let { l -> l.find { it.id == ejercicioId } ?: l.firstOrNull() }
        )

    private val _uiState = MutableStateFlow(ActividadUiState())
    val uiState: StateFlow<ActividadUiState> = _uiState.asStateFlow()

    fun seleccionarOpcion(indice: Int) = _uiState.update { it.copy(opcionSeleccionada = indice, mensajeError = null) }

    /** Evalua la alternativa elegida. La UI usa el resultado para la vibracion. */
    fun responder(): RespuestaActividad {
        val seleccion = _uiState.value.opcionSeleccionada
        if (seleccion == null) {
            _uiState.update { it.copy(mensajeError = "Por favor selecciona una alternativa antes de responder.") }
            return RespuestaActividad.SIN_SELECCION
        }
        val actual = ejercicio.value
        val intentos = _uiState.value.intentos + 1
        if (seleccion != (actual?.respuestaCorrectaIndex ?: 0)) {
            _uiState.update { it.copy(intentos = intentos, mensajeError = "¡Casi! Revisa bien la pregunta e inténtalo otra vez.") }
            return RespuestaActividad.INCORRECTA
        }
        _uiState.update { it.copy(intentos = intentos, mensajeError = null, mostrarExito = true) }
        if (actual != null) {
            contenidosRepository.marcarCompletado(actual.id, NOTA_MAXIMA)
            viewModelScope.launch {
                val sesion = authRepository.sesion.first()
                // si ms-progreso no responde, el avance igual queda marcado en el dispositivo
                progresoRepository.registrarResultado(
                    ejercicio = actual,
                    estudianteId = sesion.idEstudiante,
                    nota = NOTA_MAXIMA,
                    aciertos = 1,
                    totalPreguntas = 1
                )
            }
        }
        return RespuestaActividad.CORRECTA
    }

    fun cerrarExito() = _uiState.update { it.copy(mostrarExito = false) }

    companion object {
        const val NOTA_MAXIMA = 10
    }
}
