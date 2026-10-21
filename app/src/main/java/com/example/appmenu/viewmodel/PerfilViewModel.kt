package com.example.appmenu.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// viewmodel para perfil y foto de credencial

class PerfilViewModel : ViewModel() {
    private val _imagenUri = MutableStateFlow<Uri?>(null)
    val imagenUri: StateFlow<Uri?> = _imagenUri.asStateFlow()

    // guardamos la uri temporal aqui en el viewmodel para que no se pierda
    // cuando la app de camara externa tome el control del dispositivo
    var uriPendienteCamara: Uri? = null

    fun actualizarImagenDesdeGaleria(uri: Uri?) {
        if (uri != null) {
            _imagenUri.value = uri
        }
    }

    fun actualizarImagenDesdeCamara(exito: Boolean) {
        if (exito && uriPendienteCamara != null) {
            _imagenUri.value = uriPendienteCamara
        }
    }
}
