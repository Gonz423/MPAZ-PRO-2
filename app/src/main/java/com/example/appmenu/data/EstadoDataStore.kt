package com.example.appmenu.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = "preferencias_estado"
)

open class EstadoDataStore(private val context: Context) {

    companion object {
        private val ESTADO_ACTIVADO =
            booleanPreferencesKey("estado_activado")
    }

    open val estadoActivado: Flow<Boolean> =
        context.dataStore.data.map { preferencias ->
            preferencias[ESTADO_ACTIVADO] ?: false
        }

    open suspend fun guardarEstado(activado: Boolean) {
        context.dataStore.edit { preferencias ->
            preferencias[ESTADO_ACTIVADO] = activado
        }
    }
}
