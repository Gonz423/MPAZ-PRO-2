package com.example.appmenu.data.repository

import com.example.appmenu.data.local.ContenidosLocalDataSource
import com.example.appmenu.data.remote.backend.ContenidosRemoteDataSource
import com.example.appmenu.data.remote.backend.dto.EjercicioDto
import com.example.appmenu.data.remote.backend.dto.UnidadDto
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.NuevoEjercicio
import com.example.appmenu.domain.model.ResultadoEstudiante
import com.example.appmenu.domain.model.UnidadEducativa
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.StateFlow

/**
 * Unica fuente de verdad del catalogo de unidades y ejercicios.
 * Estrategia "local primero": los cambios se aplican al instante en el catalogo local
 * y luego se envian a ms-contenidos. Si el servidor no responde, la app sigue funcionando.
 */
interface ContenidosRepository {
    val unidades: StateFlow<List<UnidadEducativa>>
    val ejercicios: StateFlow<List<EjercicioEducativo>>

    /** Descarga unidades y ejercicios de ms-contenidos y los combina con el catalogo local. */
    suspend fun sincronizar(): Result<Unit>

    suspend fun crearUnidad(materiaId: String, titulo: String, oa: String): UnidadEducativa

    suspend fun crearEjercicio(nuevo: NuevoEjercicio): EjercicioEducativo

    /** Muestra u oculta la guia a los estudiantes (PUT). El Result indica si el servidor la recibio. */
    suspend fun cambiarVisibilidad(ejercicioId: String): Result<Unit>

    /** Elimina la guia (DELETE). El Result indica si el servidor la recibio. */
    suspend fun eliminarEjercicio(ejercicioId: String): Result<Unit>

    fun alternarCompletado(ejercicioId: String)

    fun marcarCompletado(ejercicioId: String, nota: Int)

    /** Marca como completados los ejercicios que ms-progreso tiene registrados para el estudiante. */
    fun aplicarResultados(resultados: List<ResultadoEstudiante>)
}

class ContenidosRepositoryImpl(
    private val local: ContenidosLocalDataSource,
    private val remoto: ContenidosRemoteDataSource,
    private val gson: Gson = Gson()
) : ContenidosRepository {

    override val unidades: StateFlow<List<UnidadEducativa>> = local.unidades
    override val ejercicios: StateFlow<List<EjercicioEducativo>> = local.ejercicios

    override suspend fun sincronizar(): Result<Unit> {
        val unidadesRemotas = remoto.listarUnidades().getOrElse { return Result.failure(it) }
        val ejerciciosRemotos = remoto.listarEjercicios().getOrElse { return Result.failure(it) }
        unidadesRemotas.forEach(::combinarUnidad)
        ejerciciosRemotos.forEach(::combinarEjercicio)
        return Result.success(Unit)
    }

    override suspend fun crearUnidad(materiaId: String, titulo: String, oa: String): UnidadEducativa {
        val nueva = local.agregarUnidad(materiaId, titulo, oa)
        remoto.crearUnidad(UnidadDto(null, nueva.materiaId, nueva.numero, nueva.titulo, nueva.objetivoAprendizaje))
            .onSuccess { dto -> local.actualizarUnidad(nueva.id) { it.copy(remoteId = dto.id) } }
        return local.unidadesActuales().first { it.id == nueva.id }
    }

    override suspend fun crearEjercicio(nuevo: NuevoEjercicio): EjercicioEducativo {
        val creado = local.agregarEjercicio(
            materiaId = nuevo.materiaId,
            unidadId = nuevo.unidadId,
            titulo = nuevo.titulo,
            oa = nuevo.objetivoAprendizaje,
            codigoGuia = nuevo.codigoGuia,
            pregunta = nuevo.pregunta,
            opciones = nuevo.opciones,
            respuestaCorrectaIndex = nuevo.respuestaCorrectaIndex,
            disponible = nuevo.disponible,
            esTarea = nuevo.esTarea,
            fechaEntrega = nuevo.fechaEntrega
        )
        val unidadRemota = unidadRemotaPara(creado)
        if (unidadRemota != null) {
            remoto.crearEjercicio(creado.toDto(unidadRemota))
                .onSuccess { dto -> local.actualizarEjercicio(creado.id) { it.copy(remoteId = dto.id) } }
        }
        return local.ejerciciosActuales().first { it.id == creado.id }
    }

    override suspend fun cambiarVisibilidad(ejercicioId: String): Result<Unit> {
        local.actualizarEjercicio(ejercicioId) { it.copy(disponibleParaEstudiantes = !it.disponibleParaEstudiantes) }
        val actualizado = local.ejerciciosActuales().firstOrNull { it.id == ejercicioId } ?: return Result.success(Unit)
        val remoteId = actualizado.remoteId ?: return Result.success(Unit)
        val unidadRemota = unidadRemotaPara(actualizado) ?: return Result.success(Unit)
        return remoto.actualizarEjercicio(remoteId, actualizado.toDto(unidadRemota)).map { }
    }

    override suspend fun eliminarEjercicio(ejercicioId: String): Result<Unit> {
        val ejercicio = local.ejerciciosActuales().firstOrNull { it.id == ejercicioId } ?: return Result.success(Unit)
        local.eliminarEjercicio(ejercicioId)
        val remoteId = ejercicio.remoteId ?: return Result.success(Unit)
        return remoto.eliminarEjercicio(remoteId)
    }

    override fun alternarCompletado(ejercicioId: String) = local.alternarCompletado(ejercicioId)

    override fun marcarCompletado(ejercicioId: String, nota: Int) = local.marcarCompletado(ejercicioId, nota)

    override fun aplicarResultados(resultados: List<ResultadoEstudiante>) {
        resultados.forEach { resultado ->
            val ejercicio = local.ejerciciosActuales().firstOrNull {
                it.codigoGuia.toLongOrNull() == resultado.ejercicioId || it.remoteId == resultado.ejercicioId
            } ?: return@forEach
            if (!ejercicio.completado) {
                local.actualizarEjercicio(ejercicio.id) {
                    it.copy(completado = true, nota = resultado.nota, intentos = resultado.intentos)
                }
            }
        }
    }

    // ---------------- combinacion local <-> servidor ----------------

    /** Una unidad remota coincide con una local por id remoto o por (materia, numero). */
    private fun combinarUnidad(dto: UnidadDto) {
        val id = dto.id ?: return
        val existente = local.unidadesActuales().firstOrNull {
            it.remoteId == id || (it.remoteId == null && it.materiaId == dto.materiaId && it.numero == dto.numero)
        }
        if (existente != null) {
            local.actualizarUnidad(existente.id) { it.copy(remoteId = id) }
        } else {
            local.insertarUnidad(
                UnidadEducativa(
                    id = "srv_uni_$id",
                    materiaId = dto.materiaId,
                    numero = dto.numero ?: 1,
                    titulo = dto.titulo,
                    objetivoAprendizaje = dto.objetivoAprendizaje.orEmpty(),
                    remoteId = id
                )
            )
        }
    }

    /** Un ejercicio remoto coincide con uno local por id remoto o por codigo de guia. */
    private fun combinarEjercicio(dto: EjercicioDto) {
        val id = dto.id ?: return
        val existente = local.ejerciciosActuales().firstOrNull {
            it.remoteId == id || (it.remoteId == null && !dto.codigoGuia.isNullOrBlank() && it.codigoGuia == dto.codigoGuia)
        }
        if (existente != null) {
            local.actualizarEjercicio(existente.id) { it.copy(remoteId = id) }
            return
        }
        val unidadLocal = local.unidadesActuales().firstOrNull { it.remoteId == dto.unidadId }
            ?: local.unidadesActuales().firstOrNull { it.materiaId == dto.materiaId }
        local.insertarEjercicio(
            EjercicioEducativo(
                id = "srv_ej_$id",
                materiaId = dto.materiaId,
                unidadId = unidadLocal?.id ?: "uni_${dto.materiaId}_1",
                titulo = dto.titulo,
                objetivoAprendizaje = dto.objetivoAprendizaje.orEmpty(),
                codigoGuia = dto.codigoGuia.orEmpty(),
                disponibleParaEstudiantes = dto.disponible ?: true,
                esTarea = dto.esTarea ?: false,
                fechaEntrega = dto.fechaEntrega,
                pregunta = dto.pregunta ?: "¿Cuál es la respuesta correcta?",
                opciones = leerOpciones(dto.opciones),
                respuestaCorrectaIndex = dto.respuestaCorrectaIndex ?: 0,
                remoteId = id
            )
        )
    }

    /** Id en el servidor de la unidad del ejercicio; si no se conoce, la primera unidad sincronizada de la materia. */
    private fun unidadRemotaPara(ejercicio: EjercicioEducativo): Long? {
        val unidades = local.unidadesActuales()
        return unidades.firstOrNull { it.id == ejercicio.unidadId }?.remoteId
            ?: unidades.firstOrNull { it.materiaId == ejercicio.materiaId && it.remoteId != null }?.remoteId
    }

    private fun EjercicioEducativo.toDto(unidadRemotaId: Long) = EjercicioDto(
        id = remoteId,
        materiaId = materiaId,
        unidadId = unidadRemotaId,
        titulo = titulo,
        objetivoAprendizaje = objetivoAprendizaje,
        codigoGuia = codigoGuia,
        pregunta = pregunta,
        opciones = gson.toJson(opciones),
        respuestaCorrectaIndex = respuestaCorrectaIndex,
        disponible = disponibleParaEstudiantes,
        esTarea = esTarea,
        fechaEntrega = fechaEntrega
    )

    private fun leerOpciones(json: String?): List<String> = try {
        gson.fromJson<List<String>>(json, object : TypeToken<List<String>>() {}.type)
            ?.takeIf { it.isNotEmpty() } ?: OPCIONES_POR_DEFECTO
    } catch (_: Exception) {
        OPCIONES_POR_DEFECTO
    }

    companion object {
        private val OPCIONES_POR_DEFECTO = listOf("Opción 1", "Opción 2", "Opción 3")
    }
}
