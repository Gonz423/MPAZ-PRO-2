package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.TriviaRepository
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.PreguntaTrivia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Desafio cientifico con la API externa (Wikipedia en español). Solo depende de TriviaRepository. */
class TriviaViewModel(private val triviaRepository: TriviaRepository) : ViewModel() {

    private val _pregunta = MutableStateFlow<PreguntaTrivia?>(null)
    val pregunta: StateFlow<PreguntaTrivia?> = _pregunta.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarPregunta()
    }

    fun cargarPregunta() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value = null
            triviaRepository.obtenerPregunta()
                .onSuccess { _pregunta.value = it }
                .onFailure { _error.value = mensajeError(it) }
            _cargando.value = false
        }
    }

    private fun mensajeError(error: Throwable): String = when {
        error is ErrorRed.Http && error.codigo == 429 ->
            "Wikipedia recibió muchas consultas seguidas. Espera unos segundos y vuelve a intentarlo."
        error is ErrorRed.Http && error.codigo == 200 -> error.detalle ?: MENSAJE_SIN_CONEXION
        else -> MENSAJE_SIN_CONEXION
    }

    companion object {
        const val MENSAJE_SIN_CONEXION = "No se pudo cargar la pregunta. Verifica tu conexión a internet."
    }
}
