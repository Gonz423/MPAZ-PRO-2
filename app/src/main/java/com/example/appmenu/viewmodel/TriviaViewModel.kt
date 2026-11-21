package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.network.RetrofitClient
import com.example.appmenu.network.TriviaQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// viewmodel para la pantalla de trivia de ciencias - mpaz pro 2.°

class TriviaViewModel : ViewModel() {

    private val _pregunta  = MutableStateFlow<TriviaQuestion?>(null)
    val pregunta: StateFlow<TriviaQuestion?> = _pregunta.asStateFlow()

    private val _cargando  = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error     = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        cargarPregunta()
    }

    // obtiene una nueva pregunta de ciencias desde open trivia db
    fun cargarPregunta() {
        viewModelScope.launch {
            _cargando.value = true
            _error.value    = null
            try {
                val respuesta = RetrofitClient.triviaApi.obtenerPregunta()
                _pregunta.value = respuesta.results.firstOrNull()
            } catch (e: Exception) {
                _error.value = "No se pudo cargar la pregunta. Verifica tu conexión a internet."
            } finally {
                _cargando.value = false
            }
        }
    }
}
