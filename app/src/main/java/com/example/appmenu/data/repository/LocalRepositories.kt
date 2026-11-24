package com.example.appmenu.data.repository

import com.example.appmenu.data.local.EstadoDataStore
import com.example.appmenu.data.local.room.Note
import com.example.appmenu.data.local.room.NoteDao
import kotlinx.coroutines.flow.Flow

// Repositories cuya unica fuente es el almacenamiento local del dispositivo.

/** Cuaderno de notas y observaciones guardado en Room. */
interface NotasRepository {
    val notas: Flow<List<Note>>
    suspend fun guardar(titulo: String, contenido: String)
    suspend fun eliminar(nota: Note)
}

class NotasRepositoryImpl(private val dao: NoteDao) : NotasRepository {
    override val notas: Flow<List<Note>> = dao.observeAll()

    override suspend fun guardar(titulo: String, contenido: String) {
        dao.insert(Note(title = titulo.trim(), content = contenido.trim()))
    }

    override suspend fun eliminar(nota: Note) = dao.delete(nota)
}

/** Preferencia del "modo guiado con audio" guardada en DataStore. */
interface PreferenciasRepository {
    val modoGuiadoActivado: Flow<Boolean>
    suspend fun guardarModoGuiado(activado: Boolean)
}

class PreferenciasRepositoryImpl(private val dataStore: EstadoDataStore) : PreferenciasRepository {
    override val modoGuiadoActivado: Flow<Boolean> = dataStore.estadoActivado
    override suspend fun guardarModoGuiado(activado: Boolean) = dataStore.guardarEstado(activado)
}
