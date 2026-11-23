package com.example.appmenu.viewmodel

import android.app.Application
import com.example.appmenu.network.ProgresoApiService
import com.example.appmenu.room.Note
import com.example.appmenu.room.NoteRepository
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NotasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: Application = mockk(relaxed = true)
    private val repository: NoteRepository = mockk(relaxed = true)
    private val progresoApi: ProgresoApiService = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { repository.notes } returns flowOf(emptyList())
    }

    @Test
    fun `validar con campos vacios retorna false y asigna mensajes de error`() {
        val viewModel = NotasViewModel(application, repository, progresoApi)

        val resultado = viewModel.validar("", "")

        assertFalse(resultado)
        assertEquals("El título o estudiante es obligatorio", viewModel.errores.value.titulo)
        assertEquals("La observación pedagógica es obligatoria", viewModel.errores.value.contenido)
    }

    @Test
    fun `validar con campos cortos retorna false y asigna mensajes de longitud minima`() {
        val viewModel = NotasViewModel(application, repository, progresoApi)

        val resultado = viewModel.validar("Ab", "Hola")

        assertFalse(resultado)
        assertEquals("El título debe tener al menos 3 caracteres", viewModel.errores.value.titulo)
        assertEquals("La observación debe tener al menos 5 caracteres", viewModel.errores.value.contenido)
    }

    @Test
    fun `validar con datos correctos retorna true y sin errores`() {
        val viewModel = NotasViewModel(application, repository, progresoApi)

        val resultado = viewModel.validar("Mateo Silva", "Excelente participación en clase de Ciencias")

        assertTrue(resultado)
        assertNull(viewModel.errores.value.titulo)
        assertNull(viewModel.errores.value.contenido)
    }

    @Test
    fun `limpiarErrorTitulo y limpiarErrorContenido limpian errores de forma independiente`() {
        val viewModel = NotasViewModel(application, repository, progresoApi)
        viewModel.validar("", "")

        assertNotNull(viewModel.errores.value.titulo)
        assertNotNull(viewModel.errores.value.contenido)

        viewModel.limpiarErrorTitulo()
        assertNull(viewModel.errores.value.titulo)
        assertNotNull(viewModel.errores.value.contenido)

        viewModel.limpiarErrorContenido()
        assertNull(viewModel.errores.value.contenido)
    }

    @Test
    fun `limpiarErrores restablece todos los errores a null`() {
        val viewModel = NotasViewModel(application, repository, progresoApi)
        viewModel.validar("", "")

        assertNotNull(viewModel.errores.value.titulo)
        assertNotNull(viewModel.errores.value.contenido)

        viewModel.limpiarErrores()
        assertNull(viewModel.errores.value.titulo)
        assertNull(viewModel.errores.value.contenido)
    }

    @Test
    fun `guardarNota con datos invalidos no invoca repositorio y reporta fallo en callback`() {
        val viewModel = NotasViewModel(application, repository, progresoApi)
        var exitoCallback = false
        var mensajeCallback = ""

        viewModel.guardarNota("", "") { exito, mensaje ->
            exitoCallback = exito
            mensajeCallback = mensaje
        }

        assertFalse(exitoCallback)
        assertEquals("Por favor corrige los campos con error.", mensajeCallback)
        coVerify(exactly = 0) { repository.save(any(), any()) }
    }

    @Test
    fun `guardarNota con datos validos persiste en repositorio y reporta exito`() = runTest {
        coEvery { repository.save(any(), any()) } returns Unit
        val viewModel = NotasViewModel(application, repository, progresoApi)
        var exitoCallback = false
        var mensajeCallback = ""

        viewModel.guardarNota("Lenguaje y Comunicación", "Comprensión de lectura evaluada con 100%") { exito, mensaje ->
            exitoCallback = exito
            mensajeCallback = mensaje
        }
        advanceUntilIdle()

        assertTrue(exitoCallback)
        assertEquals("Registro guardado correctamente en la base de datos.", mensajeCallback)
        coVerify(exactly = 1) { repository.save("Lenguaje y Comunicación", "Comprensión de lectura evaluada con 100%") }
    }

    @Test
    fun `eliminarNota delega en repositorio delete con la nota entregada`() = runTest {
        val nota = Note(id = 5, title = "Tarea Matemática", content = "Completada")
        coEvery { repository.delete(nota) } returns Unit

        val viewModel = NotasViewModel(application, repository, progresoApi)
        viewModel.eliminarNota(nota)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.delete(nota) }
    }
}
