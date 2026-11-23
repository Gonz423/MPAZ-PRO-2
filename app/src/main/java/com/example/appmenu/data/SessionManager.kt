package com.example.appmenu.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.appmenu.network.LoginResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "mpaz_sesion")

data class UsuarioSesion(
    val token: String = "",
    val id: Long = 0,
    val username: String = "",
    val nombreCompleto: String = "",
    val email: String = "",
    val rol: String = "ESTUDIANTE",
    val curso: String = "2° Básico A",
    val estaAutenticado: Boolean = false
)

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

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

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

    open suspend fun guardarSesion(response: LoginResponseDto) {
        context.sessionDataStore.edit { pref ->
            pref[KEY_TOKEN] = response.token
            pref[KEY_USER_ID] = response.id
            pref[KEY_USERNAME] = response.username
            pref[KEY_NOMBRE] = response.nombreCompleto
            pref[KEY_EMAIL] = response.email ?: ""
            pref[KEY_ROL] = response.rol
            pref[KEY_CURSO] = response.curso ?: "2° Básico A"
            pref[KEY_AUTENTICADO] = true
        }
    }

    open suspend fun guardarSesionLocal(
        id: Long,
        username: String,
        nombreCompleto: String,
        email: String,
        rol: String,
        curso: String
    ) {
        context.sessionDataStore.edit { pref ->
            pref[KEY_TOKEN] = "local-offline-token"
            pref[KEY_USER_ID] = id
            pref[KEY_USERNAME] = username
            pref[KEY_NOMBRE] = nombreCompleto
            pref[KEY_EMAIL] = email
            pref[KEY_ROL] = rol
            pref[KEY_CURSO] = curso
            pref[KEY_AUTENTICADO] = true
        }
    }

    open suspend fun cerrarSesion() {
        context.sessionDataStore.edit { pref ->
            pref.clear()
        }
    }
}
