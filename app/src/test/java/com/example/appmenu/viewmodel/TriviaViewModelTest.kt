package com.example.appmenu.viewmodel

import com.example.appmenu.data.repository.TriviaRepository
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.PreguntaTrivia
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TriviaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo: TriviaRepository = mockk()

    private val pregunta = PreguntaTrivia(
        categoria = "Animales",
        pista = "El ___ andino es una especie de ave.",
        pregunta = "¿De qué se trata este dato?",
        respuestaCorrecta = "Cóndor",
        respuestasIncorrectas = listOf("Puma", "Pudú", "Huemul"),
        imagenUrl = "https://upload.wikimedia.org/condor.jpg",
        datoCompleto = "El cóndor andino es una especie de ave."
    )

    @Test
    fun `al crearse carga una pregunta y limpia errores`() = runTest {
        coEvery { repo.obtenerPregunta() } returns Result.success(pregunta)

        val viewModel = TriviaViewModel(repo)

        assertEquals("Cóndor", viewModel.pregunta.value?.respuestaCorrecta)
        assertFalse(viewModel.cargando.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `sin conexion muestra el mensaje de conexion`() = runTest {
        coEvery { repo.obtenerPregunta() } returns Result.failure(ErrorRed.SinConexion())

        val viewModel = TriviaViewModel(repo)

        assertNull(viewModel.pregunta.value)
        assertFalse(viewModel.cargando.value)
        assertEquals(TriviaViewModel.MENSAJE_SIN_CONEXION, viewModel.error.value)
    }

    @Test
    fun `demasiadas consultas (429) pide esperar unos segundos`() = runTest {
        coEvery { repo.obtenerPregunta() } returns Result.failure(ErrorRed.Http(429, null))

        val viewModel = TriviaViewModel(repo)

        assertEquals(
            "Wikipedia recibió muchas consultas seguidas. Espera unos segundos y vuelve a intentarlo.",
            viewModel.error.value
        )
    }

    @Test
    fun `resumen vacio muestra el detalle del repositorio`() = runTest {
        coEvery { repo.obtenerPregunta() } returns Result.failure(ErrorRed.Http(200, "Sin resumen"))

        assertEquals("Sin resumen", TriviaViewModel(repo).error.value)
    }

    @Test
    fun `reintentar despues de un error vuelve a consultar y muestra la pregunta`() = runTest {
        coEvery { repo.obtenerPregunta() } returns Result.failure(ErrorRed.SinConexion())
        val viewModel = TriviaViewModel(repo)

        coEvery { repo.obtenerPregunta() } returns Result.success(pregunta)
        viewModel.cargarPregunta()

        assertEquals(pregunta, viewModel.pregunta.value)
        assertNull(viewModel.error.value)
        coVerify(exactly = 2) { repo.obtenerPregunta() }
    }
}
