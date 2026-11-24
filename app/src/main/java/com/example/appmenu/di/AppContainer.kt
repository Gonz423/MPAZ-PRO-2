package com.example.appmenu.di

import android.content.Context
import com.example.appmenu.BuildConfig
import com.example.appmenu.data.local.ContenidosLocalDataSource
import com.example.appmenu.data.local.EstadoDataStore
import com.example.appmenu.data.local.SessionManager
import com.example.appmenu.data.local.room.AppDatabase
import com.example.appmenu.data.remote.backend.AuthInterceptor
import com.example.appmenu.data.remote.backend.BackendRetrofit
import com.example.appmenu.data.remote.backend.ContenidosRemoteDataSource
import com.example.appmenu.data.remote.backend.ProgresoRemoteDataSource
import com.example.appmenu.data.remote.backend.UsuariosRemoteDataSource
import com.example.appmenu.data.remote.external.ExternalRetrofit
import com.example.appmenu.data.remote.external.WikipediaRemoteDataSource
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.AuthRepositoryImpl
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.data.repository.ContenidosRepositoryImpl
import com.example.appmenu.data.repository.NotasRepository
import com.example.appmenu.data.repository.NotasRepositoryImpl
import com.example.appmenu.data.repository.PreferenciasRepository
import com.example.appmenu.data.repository.PreferenciasRepositoryImpl
import com.example.appmenu.data.repository.ProgresoRepository
import com.example.appmenu.data.repository.ProgresoRepositoryImpl
import com.example.appmenu.data.repository.TriviaRepository
import com.example.appmenu.data.repository.TriviaRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Contenedor de dependencias manual (sin Hilt). Crea una sola vez cada DataSource y Repository.
 * Las dos capas de red (backend propio y API externa) se construyen por separado.
 */
class AppContainer(context: Context) {

    private val appScope = CoroutineScope(SupervisorJob())

    // ---------------- local ----------------
    val sessionManager = SessionManager(context)
    private val database = AppDatabase.getInstance(context)
    private val estadoDataStore = EstadoDataStore(context)
    private val contenidosLocal = ContenidosLocalDataSource()

    // ---------------- remoto: microservicios propios ----------------
    private val _sesionExpirada = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    /** Emite cuando el backend rechaza el JWT (401): la UI vuelve al login. */
    val sesionExpirada: SharedFlow<Unit> = _sesionExpirada.asSharedFlow()

    private val backend = BackendRetrofit(
        urlUsuarios = BuildConfig.URL_USUARIOS,
        urlContenidos = BuildConfig.URL_CONTENIDOS,
        urlProgreso = BuildConfig.URL_PROGRESO,
        authInterceptor = AuthInterceptor(
            tokenActual = { sessionManager.tokenEnMemoria },
            onNoAutorizado = { _sesionExpirada.tryEmit(Unit) }
        ),
        registrarTrafico = BuildConfig.DEBUG
    )

    // ---------------- remoto: API externa Wikipedia en español (aislada) ----------------
    private val external = ExternalRetrofit(BuildConfig.URL_WIKIPEDIA, registrarTrafico = BuildConfig.DEBUG)

    // ---------------- repositories ----------------
    val authRepository: AuthRepository =
        AuthRepositoryImpl(UsuariosRemoteDataSource(backend.usuariosApi), sessionManager)

    val contenidosRepository: ContenidosRepository =
        ContenidosRepositoryImpl(contenidosLocal, ContenidosRemoteDataSource(backend.contenidosApi))

    val progresoRepository: ProgresoRepository =
        ProgresoRepositoryImpl(ProgresoRemoteDataSource(backend.progresoApi))

    val notasRepository: NotasRepository = NotasRepositoryImpl(database.noteDao())

    val preferenciasRepository: PreferenciasRepository = PreferenciasRepositoryImpl(estadoDataStore)

    val triviaRepository: TriviaRepository = TriviaRepositoryImpl(WikipediaRemoteDataSource(external.wikipediaApi))

    init {
        // mantiene el token en memoria al dia con lo guardado en DataStore (al abrir la app)
        appScope.launch {
            sessionManager.sesionFlow.collect { sessionManager.recordarToken(it.token) }
        }
    }
}
