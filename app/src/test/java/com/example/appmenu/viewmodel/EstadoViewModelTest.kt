package com.example.appmenu.viewmodel

import android.app.Application
import com.example.appmenu.data.EstadoDataStore
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EstadoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application: Application = mockk(relaxed = true)
    private val dataStore: EstadoDataStore = mockk()

    @Test
    fun `cargarEstado exitoso actualiza estadoActivado a true y desactiva cargando`() = runTest {
        every { dataStore.estadoActivado } returns flowOf(true)

        val viewModel = EstadoViewModel(application, dataStore)
        advanceUntilIdle()

        assertTrue(viewModel.estadoActivado.value)
        assertFalse(viewModel.cargando.value)
        assertNull(viewModel.mensajeError.value)
    }

    @Test
    fun `cargarEstado con excepcion asigna mensaje de error y desactiva cargando`() = runTest {
        every { dataStore.estadoActivado } returns flow { throw RuntimeException("Error en DataStore") }

        val viewModel = EstadoViewModel(application, dataStore)
        advanceUntilIdle()

        assertFalse(viewModel.estadoActivado.value)
        assertFalse(viewModel.cargando.value)
        assertEquals("No se pudo recuperar el estado guardado.", viewModel.mensajeError.value)
    }

    @Test
    fun `guardarEstado exitoso persiste el valor y marca guardadoCorrectamente en true`() = runTest {
        every { dataStore.estadoActivado } returns flowOf(false)
        coEvery { dataStore.guardarEstado(true) } returns Unit

        val viewModel = EstadoViewModel(application, dataStore)
        advanceUntilIdle()
        assertFalse(viewModel.estadoActivado.value)

        viewModel.guardarEstado(true)
        advanceUntilIdle()

        assertTrue(viewModel.estadoActivado.value)
        assertTrue(viewModel.guardadoCorrectamente.value)
        assertNull(viewModel.mensajeError.value)

        coVerify(exactly = 1) { dataStore.guardarEstado(true) }
    }

    @Test
    fun `guardarEstado con fallo asigna mensaje de error`() = runTest {
        every { dataStore.estadoActivado } returns flowOf(false)
        coEvery { dataStore.guardarEstado(true) } throws RuntimeException("Fallo de escritura")

        val viewModel = EstadoViewModel(application, dataStore)
        advanceUntilIdle()

        viewModel.guardarEstado(true)
        advanceUntilIdle()

        assertFalse(viewModel.guardadoCorrectamente.value)
        assertEquals("No se pudo guardar el estado.", viewModel.mensajeError.value)
    }

    @Test
    fun `ocultarMensajeGuardado cambia guardadoCorrectamente a false`() = runTest {
        every { dataStore.estadoActivado } returns flowOf(false)
        coEvery { dataStore.guardarEstado(true) } returns Unit

        val viewModel = EstadoViewModel(application, dataStore)
        advanceUntilIdle()
        viewModel.guardarEstado(true)
        advanceUntilIdle()
        assertTrue(viewModel.guardadoCorrectamente.value)

        viewModel.ocultarMensajeGuardado()

        assertFalse(viewModel.guardadoCorrectamente.value)
    }
}
