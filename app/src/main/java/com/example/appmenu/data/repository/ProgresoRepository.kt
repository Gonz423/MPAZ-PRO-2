package com.example.appmenu.data.repository

import com.example.appmenu.data.remote.backend.ProgresoRemoteDataSource
import com.example.appmenu.data.remote.backend.dto.ObservacionDto
import com.example.appmenu.data.remote.backend.dto.ResultadoRequestDto
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.ResultadoEstudiante
import kotlin.math.abs

interface ProgresoRepository {
    suspend fun registrarResultado(
        ejercicio: EjercicioEducativo,
        estudianteId: Long,
        nota: Int,
        aciertos: Int,
        totalPreguntas: Int
    ): Result<Unit>

    suspend fun obtenerResultados(estudianteId: Long): Result<List<ResultadoEstudiante>>

    suspend fun crearObservacion(docenteId: Long, estudianteId: Long?, contenido: String): Result<Unit>
}

class ProgresoRepositoryImpl(private val remoto: ProgresoRemoteDataSource) : ProgresoRepository {

    override suspend fun registrarResultado(
        ejercicio: EjercicioEducativo,
        estudianteId: Long,
        nota: Int,
        aciertos: Int,
        totalPreguntas: Int
    ): Result<Unit> = remoto.registrarResultado(
        ResultadoRequestDto(
            estudianteId = estudianteId,
            ejercicioId = idEjercicioProgreso(ejercicio),
            nota = nota.coerceIn(0, 10),
            aciertos = aciertos,
            totalPreguntas = totalPreguntas.coerceAtLeast(1),
            materiaId = ejercicio.materiaId
        )
    ).map { }

    override suspend fun obtenerResultados(estudianteId: Long): Result<List<ResultadoEstudiante>> =
        remoto.listarResultados(estudianteId).map { lista ->
            lista.map {
                ResultadoEstudiante(
                    id = it.id,
                    estudianteId = it.estudianteId,
                    ejercicioId = it.ejercicioId,
                    nota = it.nota,
                    intentos = it.intentos ?: 1,
                    materiaId = it.materiaId.orEmpty()
                )
            }
        }

    override suspend fun crearObservacion(docenteId: Long, estudianteId: Long?, contenido: String): Result<Unit> =
        remoto.crearObservacion(ObservacionDto(docenteId = docenteId, estudianteId = estudianteId, contenido = contenido)).map { }

    companion object {
        /** En ms-progreso un ejercicio se identifica por su codigo de guia numerico (ej. 1001). */
        fun idEjercicioProgreso(ejercicio: EjercicioEducativo): Long =
            ejercicio.codigoGuia.toLongOrNull()
                ?: ejercicio.remoteId
                ?: abs(ejercicio.id.hashCode().toLong()).coerceAtLeast(1L)
    }
}
