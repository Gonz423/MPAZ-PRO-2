package com.example.appmenu.data.repository

import com.example.appmenu.data.remote.backend.ProgresoRemoteDataSource
import com.example.appmenu.data.remote.backend.dto.ObservacionDto
import com.example.appmenu.data.remote.backend.dto.ResultadoDto
import com.example.appmenu.data.remote.backend.dto.ResultadoRequestDto
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.ErrorRed
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class ProgresoRepositoryImplTest {

    private val remoto: ProgresoRemoteDataSource = mockk()
    private val repo = ProgresoRepositoryImpl(remoto)

    private val ejercicio = EjercicioEducativo("len_1", "lenguaje", "uni_len_1", "Fábula", "OA 3", "1001")

    @Test
    fun `registrarResultado ajusta nota y total de preguntas a rangos validos`() = runTest {
        val enviado = slot<ResultadoRequestDto>()
        coEvery { remoto.registrarResultado(capture(enviado)) } returns Result.success(ResultadoDto(id = 1))

        val resultado = repo.registrarResultado(ejercicio, estudianteId = 3, nota = 15, aciertos = 1, totalPreguntas = 0)

        assertTrue(resultado.isSuccess)
        with(enviado.captured) {
            assertEquals(3L, estudianteId)
            assertEquals(1001L, ejercicioId)
            assertEquals(10, nota)
            assertEquals(1, totalPreguntas)
            assertEquals("lenguaje", materiaId)
        }
    }

    @Test
    fun `registrarResultado devuelve el error de red`() = runTest {
        coEvery { remoto.registrarResultado(any()) } returns Result.failure(ErrorRed.SinConexion())

        assertTrue(repo.registrarResultado(ejercicio, 3, 10, 1, 1).isFailure)
    }

    @Test
    fun `obtenerResultados convierte los DTO completando los valores nulos`() = runTest {
        coEvery { remoto.listarResultados(3) } returns Result.success(
            listOf(ResultadoDto(id = 8, estudianteId = 3, ejercicioId = 1001, nota = 9, intentos = null, materiaId = null))
        )

        val lista = repo.obtenerResultados(3).getOrThrow()

        assertEquals(1, lista.size)
        assertEquals(1, lista[0].intentos)
        assertEquals("", lista[0].materiaId)
        assertEquals(9, lista[0].nota)
    }

    @Test
    fun `crearObservacion envia el contenido al servidor`() = runTest {
        val enviado = slot<ObservacionDto>()
        coEvery { remoto.crearObservacion(capture(enviado)) } returns Result.success(ObservacionDto(id = 1, contenido = "ok"))

        assertTrue(repo.crearObservacion(docenteId = 2, estudianteId = null, contenido = "Buen avance").isSuccess)
        assertEquals("Buen avance", enviado.captured.contenido)
        assertEquals(2L, enviado.captured.docenteId)
    }

    @Test
    fun `idEjercicioProgreso usa el codigo, luego el id remoto y por ultimo un hash del id`() {
        assertEquals(1001L, ProgresoRepositoryImpl.idEjercicioProgreso(ejercicio))
        assertEquals(44L, ProgresoRepositoryImpl.idEjercicioProgreso(ejercicio.copy(codigoGuia = "A1", remoteId = 44)))

        val sinCodigo = ejercicio.copy(id = "ej_x", codigoGuia = "", remoteId = null)
        assertEquals(abs("ej_x".hashCode().toLong()).coerceAtLeast(1L), ProgresoRepositoryImpl.idEjercicioProgreso(sinCodigo))
    }
}
