package com.example.appmenu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.domain.model.UsuarioSesion
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de la navegacion principal: conoce la sesion y avisa cuando el backend
 * rechazo el token (401) para volver al login.
 */
class SesionViewModel(
    private val authRepository: AuthRepository,
    sesionExpirada: Flow<Unit>
) : ViewModel() {

    val sesion: StateFlow<UsuarioSesion> = authRepository.sesion.stateIn(
        viewModelScope, SharingStarted.Eagerly, UsuarioSesion()
    )

    private val _irAlLogin = Channel<String>(Channel.CONFLATED)
    /** Emite un mensaje cuando hay que volver al login porque la sesion vencio. */
    val irAlLogin: Flow<String> = _irAlLogin.receiveAsFlow()

    init {
        viewModelScope.launch {
            sesionExpirada.collect {
                authRepository.cerrarSesion()
                _irAlLogin.send("Tu sesión expiró. Inicia sesión nuevamente.")
            }
        }
    }

    /** "Cambiar usuario": borra la sesion guardada. */
    fun cerrarSesion(onListo: () -> Unit = {}) {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            onListo()
        }
    }
}
