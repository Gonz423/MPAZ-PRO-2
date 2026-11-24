package com.example.appmenu.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmenu.MpazApplication
import com.example.appmenu.viewmodel.ActividadViewModel
import com.example.appmenu.viewmodel.AuthViewModel
import com.example.appmenu.viewmodel.ContenidosViewModel
import com.example.appmenu.viewmodel.EstadoViewModel
import com.example.appmenu.viewmodel.FormularioViewModel
import com.example.appmenu.viewmodel.NotasViewModel
import com.example.appmenu.viewmodel.PerfilViewModel
import com.example.appmenu.viewmodel.ProgresoViewModel
import com.example.appmenu.viewmodel.SesionViewModel
import com.example.appmenu.viewmodel.TriviaViewModel

/** Fabrica de todos los ViewModels: les entrega sus Repositories desde AppContainer. */
object AppViewModelProvider {

    /** Argumento de navegacion que recibe ActividadViewModel. */
    const val ARG_EJERCICIO_ID = "ejercicioId"

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer { AuthViewModel(contenedor().authRepository) }
        initializer { SesionViewModel(contenedor().authRepository, contenedor().sesionExpirada) }
        initializer { ContenidosViewModel(contenedor().contenidosRepository, contenedor().authRepository) }
        initializer { FormularioViewModel(contenedor().contenidosRepository) }
        initializer {
            val c = contenedor()
            NotasViewModel(c.notasRepository, c.progresoRepository, c.authRepository)
        }
        initializer { EstadoViewModel(contenedor().preferenciasRepository) }
        initializer { PerfilViewModel(contenedor().authRepository) }
        initializer { TriviaViewModel(contenedor().triviaRepository) }
        initializer {
            val c = contenedor()
            ProgresoViewModel(c.contenidosRepository, c.progresoRepository, c.authRepository)
        }
        initializer {
            val c = contenedor()
            ActividadViewModel(
                ejercicioId = createSavedStateHandle().get<String>(ARG_EJERCICIO_ID),
                contenidosRepository = c.contenidosRepository,
                progresoRepository = c.progresoRepository,
                authRepository = c.authRepository
            )
        }
    }

    private fun CreationExtras.contenedor(): AppContainer =
        (this[APPLICATION_KEY] as MpazApplication).container
}
