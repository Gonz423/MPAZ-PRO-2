package com.example.appmenu.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.FotoPerfilRepository
import com.example.appmenu.domain.model.UsuarioSesion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Perfil: datos de la sesion, foto de credencial (galeria o camara) y cierre de sesion. */
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(
    private val authRepository: AuthRepository,
    private val fotoPerfilRepository: FotoPerfilRepository
) : ViewModel() {

    val sesion: StateFlow<UsuarioSesion> = authRepository.sesion.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), UsuarioSesion()
    )

    /** Foto guardada del usuario actual; se mantiene al salir del perfil y al cerrar la app. */
    val imagenUri: StateFlow<Uri?> = authRepository.sesion
        .map { clavePara(it) }
        .distinctUntilChanged()
        .flatMapLatest { fotoPerfilRepository.foto(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Uri donde la app de camara escribira la foto mientras toma el control. */
    var uriPendienteCamara: Uri? = null

    fun actualizarImagenDesdeGaleria(uri: Uri?, onResultado: (Boolean) -> Unit = {}) {
        if (uri != null) guardar(uri, onResultado)
    }

    fun actualizarImagenDesdeCamara(exito: Boolean, onResultado: (Boolean) -> Unit = {}) {
        val uri = uriPendienteCamara
        if (exito && uri != null) guardar(uri, onResultado)
    }

    private fun guardar(origen: Uri, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            val clave = clavePara(authRepository.sesion.first())
            onResultado(fotoPerfilRepository.guardar(clave, origen))
        }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            authRepository.cerrarSesion()
            onListo()
        }
    }

    companion object {
        /** Cada cuenta tiene su propia foto; el acceso rapido sin login comparte la de invitado. */
        fun clavePara(sesion: UsuarioSesion): String =
            if (sesion.estaAutenticado && sesion.id > 0) "usuario_${sesion.id}" else "invitado"
    }
}
