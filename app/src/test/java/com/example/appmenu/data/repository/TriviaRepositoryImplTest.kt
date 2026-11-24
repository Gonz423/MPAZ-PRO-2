package com.example.appmenu.data.repository

import com.example.appmenu.data.remote.external.WikipediaRemoteDataSource
import com.example.appmenu.data.remote.external.dto.WikipediaImagenDto
import com.example.appmenu.data.remote.external.dto.WikipediaResumenDto
import com.example.appmenu.domain.model.Curiosidades
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.TemaCuriosidad
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class TriviaRepositoryImplTest {

    private val remoto: WikipediaRemoteDataSource = mockk()

    private val condor = Curiosidades.temas.first { it.nombre == "Cóndor" }
    private val resumenCondor = WikipediaResumenDto(
        titulo = "Vultur gryphus",
        resumen = "El cóndor andino (Vultur gryphus) es un ave de los Andes. Es el ave nacional de Chile.",
        miniatura = WikipediaImagenDto("https://upload.wikimedia.org/condor.jpg", 320, 200)
    )

    @Test
    fun `arma la pregunta ocultando el nombre y con 3 alternativas de la misma categoria`() = runTest {
        coEvery { remoto.obtenerResumen("Vultur_gryphus") } returns Result.success(resumenCondor)
        // una lista con un solo tema fuerza que la pregunta sea del condor
        val repo = TriviaRepositoryImpl(remoto, temas = listOf(condor), aleatorio = Random(1))
        val p = repo.obtenerPregunta().getOrThrow()

        assertEquals("Cóndor", p.respuestaCorrecta)
        assertEquals("El ___ andino es un ave de los Andes.", p.pista)
        assertEquals("El cóndor andino es un ave de los Andes.", p.datoCompleto)
        assertEquals("https://upload.wikimedia.org/condor.jpg", p.imagenUrl)
        assertEquals(1.6f, p.imagenProporcion!!, 0.001f) // 320 x 200
        assertEquals(3, p.respuestasIncorrectas.size)
        assertFalse(p.respuestasIncorrectas.contains("Cóndor"))
        assertTrue(p.respuestasIncorrectas.all { nombre ->
            Curiosidades.temas.first { it.nombre == nombre }.categoria == Curiosidades.ANIMALES
        })
    }

    @Test
    fun `usa la cache y no vuelve a consultar el mismo articulo`() = runTest {
        coEvery { remoto.obtenerResumen("Vultur_gryphus") } returns Result.success(resumenCondor)
        val repo = TriviaRepositoryImpl(remoto, temas = listOf(condor), aleatorio = Random(3))

        repo.obtenerPregunta()
        repo.obtenerPregunta()

        coVerify(exactly = 1) { remoto.obtenerResumen("Vultur_gryphus") }
    }

    @Test
    fun `no repite el mismo tema dos veces seguidas`() = runTest {
        val temas = otrosAnimales().take(2)
        temas.forEach { t ->
            coEvery { remoto.obtenerResumen(t.articulo) } returns Result.success(WikipediaResumenDto(resumen = "Dato de ${t.nombre}."))
        }
        val repo = TriviaRepositoryImpl(remoto, temas = temas, aleatorio = Random(7))

        val primera = repo.obtenerPregunta().getOrThrow().respuestaCorrecta
        val segunda = repo.obtenerPregunta().getOrThrow().respuestaCorrecta

        assertNotEquals(primera, segunda)
    }

    @Test
    fun `propaga el error de red sin armar pregunta`() = runTest {
        coEvery { remoto.obtenerResumen(any()) } returns Result.failure(ErrorRed.SinConexion())

        val resultado = TriviaRepositoryImpl(remoto, temas = listOf(condor)).obtenerPregunta()

        assertTrue(resultado.exceptionOrNull() is ErrorRed.SinConexion)
    }

    @Test
    fun `un resumen vacio es un error y una imagen ausente queda en null`() = runTest {
        coEvery { remoto.obtenerResumen(any()) } returns Result.success(WikipediaResumenDto(resumen = ""))
        val vacio = TriviaRepositoryImpl(remoto, temas = listOf(condor)).obtenerPregunta()
        assertTrue(vacio.exceptionOrNull() is ErrorRed.Http)

        coEvery { remoto.obtenerResumen(any()) } returns Result.success(WikipediaResumenDto(resumen = "El sol es una estrella."))
        val sol = Curiosidades.temas.first { it.nombre == "Sol" }
        assertNull(TriviaRepositoryImpl(remoto, temas = listOf(sol)).obtenerPregunta().getOrThrow().imagenUrl)
    }

    private fun otrosAnimales(): List<TemaCuriosidad> =
        Curiosidades.temas.filter { it.categoria == Curiosidades.ANIMALES && it.nombre != "Cóndor" }
}
