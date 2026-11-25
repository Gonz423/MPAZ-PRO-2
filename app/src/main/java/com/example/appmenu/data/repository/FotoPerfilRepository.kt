package com.example.appmenu.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File

/** Foto de perfil persistente: se guarda una copia en el almacenamiento interno de la app, una por usuario. */
interface FotoPerfilRepository {
    /** Emite la foto guardada para [clave] (o null) y se actualiza cada vez que se guarda una nueva. */
    fun foto(clave: String): Flow<Uri?>

    /** Copia la imagen de [origen] (camara o galeria) como foto de [clave]. Devuelve false si fallo. */
    suspend fun guardar(clave: String, origen: Uri): Boolean
}

class FotoPerfilRepositoryImpl(private val context: Context) : FotoPerfilRepository {

    private val directorio: File get() = File(context.filesDir, "perfil").apply { mkdirs() }

    /** Se incrementa al guardar para que [foto] vuelva a leer el disco. */
    private val cambios = MutableStateFlow(0)

    override fun foto(clave: String): Flow<Uri?> = cambios
        .map { archivosDe(clave).maxByOrNull { it.lastModified() }?.let(Uri::fromFile) }
        .flowOn(Dispatchers.IO)

    override suspend fun guardar(clave: String, origen: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val anteriores = archivosDe(clave)
            // nombre unico por foto: evita que Coil muestre la imagen anterior desde su cache
            val destino = File(directorio, "${prefijo(clave)}${System.currentTimeMillis()}.jpg")
            val entrada = context.contentResolver.openInputStream(origen)
                ?: return@withContext false
            entrada.use { input -> destino.outputStream().use { input.copyTo(it) } }
            anteriores.forEach { it.delete() }
            cambios.value++
            true
        } catch (e: Exception) {
            Log.e("FotoPerfilRepository", "No se pudo guardar la foto de perfil", e)
            false
        }
    }

    private fun prefijo(clave: String) = "foto_${clave}_"

    private fun archivosDe(clave: String): List<File> =
        directorio.listFiles { f -> f.name.startsWith(prefijo(clave)) }?.toList().orEmpty()
}
