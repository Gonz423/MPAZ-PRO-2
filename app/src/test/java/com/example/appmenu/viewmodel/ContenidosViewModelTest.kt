package com.example.appmenu.viewmodel

import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.NuevoEjercicio
import com.example.appmenu.domain.model.UsuarioSesion
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContenidosViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo: ContenidosRepository = mockk(relaxed = true)
    private val authRepo: AuthRepository = mockk(relaxed = true)
    private val sesion = UsuarioSesion(token = "jwt", id = 2, rol = "DOCENTE", estaAutenticado = true)

    private val guia = EjercicioEducativo("len_1", "lenguaje", "uni_len_1", "Fábula", "OA 3", "1001")

    @Before
    fun setUp() {
        every { repo.unidades } returns MutableStateFlow(emptyList())
        every { repo.ejercicios } returns MutableStateFlow(listOf(guia))
        every { authRepo.sesion } returns flowOf(sesion)
        coEvery { repo.sincronizar() } returns Result.success(Unit)
    }

    @Test
    fun `al iniciar sincroniza el catalogo y expone sus datos`() = runTest {
        val vm = ContenidosViewModel(repo, authRepo)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sesion.collect {} }

        assertTrue(vm.sincronizado.value)
        assertEquals(listOf(guia), vm.ejercicios.value)
        assertEquals(emptyList<Any>(), vm.unidades.value)
        assertEquals(sesion, vm.sesion.value)
    }

    @Test
    fun `sincronizado queda en false si el servidor no responde`() = runTest {
        coEvery { repo.sincronizar() } returns Result.failure(ErrorRed.SinConexion())

        assertFalse(ContenidosViewModel(repo, authRepo).sincronizado.value)
    }

    @Test
    fun `alternarCompletado delega en el repositorio`() {
        ContenidosViewModel(repo, authRepo).alternarCompletado("len_1")
        verify { repo.alternarCompletado("len_1") }
    }

    @Test
    fun `crearUnidad valida titulo y objetivo`() = runTest {
        val vm = ContenidosViewModel(repo, authRepo)

        assertEquals("Debes ingresar título y objetivo de aprendizaje.", vm.crearUnidad("lenguaje", " ", "OA 1"))
        assertEquals("Debes ingresar título y objetivo de aprendizaje.", vm.crearUnidad("lenguaje", "Poemas", ""))
        coVerify(exactly = 0) { repo.crearUnidad(any(), any(), any()) }

        assertNull(vm.crearUnidad("lenguaje", "Poemas", "OA 5"))
        coVerify { repo.crearUnidad("lenguaje", "Poemas", "OA 5") }
    }

    @Test
    fun `crearEjercicio valida campos y unidad`() = runTest {
        val vm = ContenidosViewModel(repo, authRepo)

        assertEquals("Debes ingresar título y objetivo de aprendizaje.", vm.crearEjercicio("lenguaje", "u1", "", "OA", "1"))
        assertEquals("Debes ingresar título y objetivo de aprendizaje.", vm.crearEjercicio("lenguaje", "u1", "T", " ", "1"))
        assertEquals("Debes seleccionar una unidad asociada.", vm.crearEjercicio("lenguaje", "", "T", "OA", "1"))
        coVerify(exactly = 0) { repo.crearEjercicio(any()) }
    }

    @Test
    fun `crearEjercicio usa el codigo ingresado o genera uno`() = runTest {
        val vm = ContenidosViewModel(repo, authRepo)
        val enviados = mutableListOf<NuevoEjercicio>()
        coEvery { repo.crearEjercicio(capture(enviados)) } returns guia

        assertNull(vm.crearEjercicio("lenguaje", "uni_len_1", "Fábula", "OA 3", "1005"))
        assertNull(vm.crearEjercicio("lenguaje", "uni_len_1", "Fábula", "OA 3", ""))

        assertEquals("1005", enviados[0].codigoGuia)
        assertTrue(enviados[1].codigoGuia.matches(Regex("100[1-9]")))
    }

    @Test
    fun `cambiarVisibilidad informa si se oculto o activo y si llego al servidor`() = runTest {
        val vm = ContenidosViewModel(repo, authRepo)
        coEvery { repo.cambiarVisibilidad("len_1") } returns Result.success(Unit)

        vm.cambiarVisibilidad(guia)
        assertEquals("Guía #1001 ocultada a estudiantes", vm.mensajes.first())

        coEvery { repo.cambiarVisibilidad("len_1") } returns Result.failure(ErrorRed.SinConexion())
        vm.cambiarVisibilidad(guia.copy(disponibleParaEstudiantes = false))
        assertEquals("Guía #1001 activada y visible (solo en este dispositivo)", vm.mensajes.first())
    }

    @Test
    fun `eliminarEjercicio informa si se elimino en el servidor o solo en el dispositivo`() = runTest {
        val vm = ContenidosViewModel(repo, authRepo)
        coEvery { repo.eliminarEjercicio("len_1") } returns Result.success(Unit)

        vm.eliminarEjercicio(guia)
        assertEquals("Guía #1001 eliminada", vm.mensajes.first())

        coEvery { repo.eliminarEjercicio("len_1") } returns Result.failure(ErrorRed.SinConexion())
        vm.eliminarEjercicio(guia)
        assertEquals("Guía #1001 eliminada solo en este dispositivo", vm.mensajes.first())
    }
}
