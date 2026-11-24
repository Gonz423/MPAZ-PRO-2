package com.example.appmenu.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appmenu.domain.model.UsuarioSesion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "mpaz_sesion")

/**
 * Fuente de datos local de la sesion (DataStore).
 * Ademas guarda el token en memoria para que AuthInterceptor lo lea sin bloquear.
 */
open class SessionManager(private val context: Context) {

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("jwt_token")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_NOMBRE = stringPreferencesKey("nombre_completo")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_ROL = stringPreferencesKey("rol")
        private val KEY_CURSO = stringPreferencesKey("curso")
        private val KEY_AUTENTICADO = booleanPreferencesKey("esta_autenticado")
    }

    /** Ultimo token conocido; lo actualiza AppContainer al observar sesionFlow y al guardar. */
    @Volatile
    var tokenEnMemoria: String? = null
        private set

    open val sesionFlow: Flow<UsuarioSesion> = context.sessionDataStore.data.map { pref ->
        UsuarioSesion(
            token = pref[KEY_TOKEN] ?: "",
            id = pref[KEY_USER_ID] ?: 0L,
            username = pref[KEY_USERNAME] ?: "",
            nombreCompleto = pref[KEY_NOMBRE] ?: "",
            email = pref[KEY_EMAIL] ?: "",
            rol = pref[KEY_ROL] ?: "ESTUDIANTE",
            curso = pref[KEY_CURSO] ?: "2° Básico A",
            estaAutenticado = pref[KEY_AUTENTICADO] ?: false
        )
    }

    open suspend fun guardarSesion(sesion: UsuarioSesion) {
        context.sessionDataStore.edit { pref ->
            pref[KEY_TOKEN] = sesion.token
            pref[KEY_USER_ID] = sesion.id
            pref[KEY_USERNAME] = sesion.username
            pref[KEY_NOMBRE] = sesion.nombreCompleto
            pref[KEY_EMAIL] = sesion.email
            pref[KEY_ROL] = sesion.rol
            pref[KEY_CURSO] = sesion.curso
            pref[KEY_AUTENTICADO] = true
        }
        tokenEnMemoria = sesion.token
    }

    open suspend fun cerrarSesion() {
        context.sessionDataStore.edit { pref -> pref.clear() }
        tokenEnMemoria = null
    }

    /** Sincroniza el token en memoria con lo guardado en disco (al iniciar la app). */
    fun recordarToken(token: String?) {
        tokenEnMemoria = token?.takeIf { it.isNotBlank() }
    }
}
