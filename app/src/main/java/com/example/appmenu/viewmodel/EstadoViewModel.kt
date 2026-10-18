package com.example.appmenu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.EstadoDataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EstadoViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repositorio = EstadoDataStore(application)

    private val _estadoActivado = MutableStateFlow(false)
    val estadoActivado = _estadoActivado.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando = _cargando.asStateFlow()

    private val _guardadoCorrectamente = MutableStateFlow(false)
    val guardadoCorrectamente =
        _guardadoCorrectamente.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError = _mensajeError.asStateFlow()

    init {
        cargarEstado()
    }

    private fun cargarEstado() {
        viewModelScope.launch {
            try {
                delay(1000)
                _estadoActivado.value =
                    repositorio.estadoActivado.first()
            } catch (e: Exception) {
                _mensajeError.value =
                    "No se pudo recuperar el estado guardado."
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
                repositorio.guardarEstado(activado)
                _estadoActivado.value = activado
                _guardadoCorrectamente.value = true
            } catch (e: Exception) {
                _mensajeError.value =
                    "No se pudo guardar el estado."
            }
        }
    }

    fun ocultarMensajeGuardado() {
        _guardadoCorrectamente.value = false
    }
}
