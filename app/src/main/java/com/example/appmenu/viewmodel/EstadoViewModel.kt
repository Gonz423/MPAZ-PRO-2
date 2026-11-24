package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.PreferenciasRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** "Modo guiado con audio" del estudiante, persistido en DataStore. */
class EstadoViewModel(private val preferencias: PreferenciasRepository) : ViewModel() {

    private val _estadoActivado = MutableStateFlow(false)
    val estadoActivado: StateFlow<Boolean> = _estadoActivado.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _guardadoCorrectamente = MutableStateFlow(false)
    val guardadoCorrectamente: StateFlow<Boolean> = _guardadoCorrectamente.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    init {
        cargarEstado()
    }

    fun cargarEstado() {
        viewModelScope.launch {
            try {
                delay(1000) // muestra el indicador de carga (requisito visual de la pauta)
                _estadoActivado.value = preferencias.modoGuiadoActivado.first()
            } catch (e: Exception) {
                _mensajeError.value = "No se pudo recuperar el estado guardado."
            } finally {
                _cargando.value = false
            }
        }
    }

    fun guardarEstado(activado: Boolean) {
        viewModelScope.launch {
            _guardadoCorrectamente.value = false
            _mensajeError.value = null
            try {
                preferencias.guardarModoGuiado(activado)
                _estadoActivado.value = activado
                _guardadoCorrectamente.value = true
            } catch (e: Exception) {
                _mensajeError.value = "No se pudo guardar el estado."
            }
        }
    }

    fun ocultarMensajeGuardado() {
        _guardadoCorrectamente.value = false
    }
}
