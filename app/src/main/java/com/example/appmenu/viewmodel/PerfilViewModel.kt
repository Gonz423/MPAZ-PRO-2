package com.example.appmenu.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.domain.model.UsuarioSesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Perfil: datos de la sesion, foto de credencial (galeria o camara) y cierre de sesion. */
class PerfilViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val sesion: StateFlow<UsuarioSesion> = authRepository.sesion.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UsuarioSesion()
    )

    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    /** Uri donde la app de camara escribira la foto mientras toma el control. */
    var uriPendienteCamara: Uri? = null

    fun actualizarImagenDesdeGaleria(uri: Uri?) {
        if (uri != null) _imagenUri.value = uri
    }

    fun actualizarImagenDesdeCamara(exito: Boolean) {
        if (exito && uriPendienteCamara != null) _imagenUri.value = uriPendienteCamara
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            onListo()
        }
    }
}
