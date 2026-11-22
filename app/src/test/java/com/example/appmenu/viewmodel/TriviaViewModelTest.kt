package com.example.appmenu.viewmodel

import com.example.appmenu.network.TriviaApi
import com.example.appmenu.network.TriviaQuestion
import com.example.appmenu.network.TriviaResponse
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class TriviaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val triviaApi: TriviaApi = mockk()

    private val sampleQuestion = TriviaQuestion(
        category = "Science & Nature",
        type = "multiple",
        difficulty = "easy",
        question = "¿Cuál es el planeta más grande del sistema solar?",
        correctAnswer = "Júpiter",
        incorrectAnswers = listOf("Marte", "Venus", "Saturno")
    )

    @Test
    fun `cargarPregunta exitosa actualiza pregunta y limpia errores`() = runTest {
        val mockResponse = TriviaResponse(
            responseCode = 0,
            results = listOf(sampleQuestion)
        )
        coEvery { triviaApi.obtenerPregunta(any(), any(), any()) } returns mockResponse

        val viewModel = TriviaViewModel(triviaApi)

        assertNotNull(viewModel.pregunta.value)
        assertEquals("Júpiter", viewModel.pregunta.value?.correctAnswer)
        assertEquals("¿Cuál es el planeta más grande del sistema solar?", viewModel.pregunta.value?.question)
        assertFalse(viewModel.cargando.value)
        assertNull(viewModel.error.value)

        coVerify(exactly = 1) { triviaApi.obtenerPregunta(any(), any(), any()) }
    }

    @Test
    fun `cargarPregunta con lista vacia deja pregunta en null`() = runTest {
        val mockResponse = TriviaResponse(
            responseCode = 0,
            results = emptyList()
        )
        coEvery { triviaApi.obtenerPregunta(any(), any(), any()) } returns mockResponse

        val viewModel = TriviaViewModel(triviaApi)

        assertNull(viewModel.pregunta.value)
        assertFalse(viewModel.cargando.value)
        assertNull(viewModel.error.value)
    }

    @Test
    fun `cargarPregunta con fallo de red asigna mensaje de error apropiado`() = runTest {
        coEvery { triviaApi.obtenerPregunta(any(), any(), any()) } throws IOException("Sin conexión")

        val viewModel = TriviaViewModel(triviaApi)

        assertNull(viewModel.pregunta.value)
        assertFalse(viewModel.cargando.value)
        assertEquals("No se pudo cargar la pregunta. Verifica tu conexión a internet.", viewModel.error.value)
    }

    @Test
    fun `reintento explicito de cargarPregunta vuelve a consultar API`() = runTest {
        coEvery { triviaApi.obtenerPregunta(any(), any(), any()) } throws IOException("Error inicial")

        val viewModel = TriviaViewModel(triviaApi)
        assertEquals("No se pudo cargar la pregunta. Verifica tu conexión a internet.", viewModel.error.value)

        val mockResponse = TriviaResponse(
            responseCode = 0,
            results = listOf(sampleQuestion)
        )
        coEvery { triviaApi.obtenerPregunta(any(), any(), any()) } returns mockResponse

        viewModel.cargarPregunta()

        assertNotNull(viewModel.pregunta.value)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.cargando.value)

        coVerify(exactly = 2) { triviaApi.obtenerPregunta(any(), any(), any()) }
    }
}
