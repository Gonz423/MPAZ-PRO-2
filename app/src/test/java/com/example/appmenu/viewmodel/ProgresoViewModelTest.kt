package com.example.appmenu.viewmodel

import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.data.repository.ProgresoRepository
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.ResultadoEstudiante
import com.example.appmenu.domain.model.UsuarioSesion
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgresoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val contenidos: ContenidosRepository = mockk(relaxed = true)
    private val progreso: ProgresoRepository = mockk()
    private val auth: AuthRepository = mockk()

    private val sesion = UsuarioSesion(token = "jwt", id = 5, nombreCompleto = "Sofía", estaAutenticado = true)
    private val ejercicios = MutableStateFlow(
        listOf(
            ej("l1", "lenguaje", completado = true),
            ej("l2", "lenguaje", completado = false),
            ej("m1", "Matematica", completado = true)
        )
    )

    private fun ej(id: String, materia: String, completado: Boolean) =
        EjercicioEducativo(id, materia, "u", "T", "OA", "1", completado = completado)

    @Before
    fun setUp() {
        every { contenidos.ejercicios } returns ejercicios
        every { auth.sesion } returns flowOf(sesion)
    }

    @Test
    fun `calcularPorcentajes da el avance por materia y 0 si no hay ejercicios`() {
        val p = ProgresoViewModel.calcularPorcentajes(ejercicios.value)

        assertEquals(mapOf("lenguaje" to 50, "matematica" to 100, "ciencias" to 0, "historia" to 0), p)
    }

    @Test
    fun `al iniciar aplica los resultados de ms-progreso del estudiante logueado`() = runTest {
        val resultados = listOf(ResultadoEstudiante(1, 5, 1001, 9, 1, "lenguaje"))
        coEvery { progreso.obtenerResultados(5) } returns Result.success(resultados)

        val vm = ProgresoViewModel(contenidos, progreso, auth)

        assertTrue(vm.backendConectado.value)
        verify { contenidos.aplicarResultados(resultados) }
    }

    @Test
    fun `sin servidor marca backend desconectado y no toca el avance local`() = runTest {
        coEvery { progreso.obtenerResultados(any()) } returns Result.failure(ErrorRed.SinConexion())

        val vm = ProgresoViewModel(contenidos, progreso, auth)

        assertFalse(vm.backendConectado.value)
        verify(exactly = 0) { contenidos.aplicarResultados(any()) }
    }

    @Test
    fun `porcentajes y sesion se actualizan con los datos del repositorio`() = runTest {
        coEvery { progreso.obtenerResultados(any()) } returns Result.success(emptyList())
        val vm = ProgresoViewModel(contenidos, progreso, auth)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.sesion.collect {} }

        assertEquals(50, vm.porcentajes.value["lenguaje"])
        ejercicios.value = ejercicios.value.map { it.copy(completado = true) }

        assertEquals(100, vm.porcentajes.value["lenguaje"])
        assertEquals(ejercicios.value, vm.ejercicios.value)
        assertEquals("Sofía", vm.sesion.value.nombreCompleto)
    }
}
