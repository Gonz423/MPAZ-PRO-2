package com.example.appmenu.viewmodel

import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.domain.model.UsuarioSesion
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo: AuthRepository = mockk(relaxed = true)
    private val sesion = UsuarioSesion(token = "jwt", id = 1, estaAutenticado = true)

    @Test
    fun `expone la sesion guardada`() {
        every { repo.sesion } returns flowOf(sesion)

        assertEquals(sesion, SesionViewModel(repo, emptyFlow()).sesion.value)
    }

    @Test
    fun `cuando el backend rechaza el token cierra la sesion y pide volver al login`() = runTest {
        every { repo.sesion } returns flowOf(sesion)
        val expirada = MutableSharedFlow<Unit>()
        val vm = SesionViewModel(repo, expirada)

        expirada.emit(Unit)

        assertEquals("Tu sesión expiró. Inicia sesión nuevamente.", vm.irAlLogin.first())
        coVerify { repo.cerrarSesion() }
    }

    @Test
    fun `cerrarSesion borra la sesion y avisa al terminar`() = runTest {
        every { repo.sesion } returns flowOf(sesion)
        val vm = SesionViewModel(repo, emptyFlow())

        var listo = false
        vm.cerrarSesion { listo = true }

        assertTrue(listo)
        coVerify { repo.cerrarSesion() }
    }
}
