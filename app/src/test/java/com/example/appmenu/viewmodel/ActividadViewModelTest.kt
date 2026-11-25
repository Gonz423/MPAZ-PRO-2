package com.example.appmenu.viewmodel

import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.data.repository.ProgresoRepository
import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.UsuarioSesion
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActividadViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val contenidos: ContenidosRepository = mockk(relaxed = true)
    private val progreso: ProgresoRepository = mockk(relaxed = true)
    private val auth: AuthRepository = mockk()

    private val primero = EjercicioEducativo("len_1", "lenguaje", "u", "Fábula", "OA", "1001", respuestaCorrectaIndex = 0)
    private val segundo = EjercicioEducativo("mat_1", "matematica", "u", "Sumas", "OA", "2001", respuestaCorrectaIndex = 2)
    private val ejercicios = MutableStateFlow(listOf(primero, segundo))

    @Before
    fun setUp() {
        every { contenidos.ejercicios } returns ejercicios
        every { auth.sesion } returns flowOf(UsuarioSesion(token = "jwt", id = 8, estaAutenticado = true))
    }

    private fun vm(id: String?) = ActividadViewModel(id, contenidos, progreso, auth)

    @Test
    fun `carga el ejercicio pedido o el primero del catalogo`() {
        assertEquals(segundo, vm("mat_1").ejercicio.value)
        assertEquals(primero, vm(null).ejercicio.value)
        assertEquals(primero, vm("no_existe").ejercicio.value)

        ejercicios.value = emptyList()
        assertNull(vm("mat_1").ejercicio.value)
    }

    @Test
    fun `responder sin elegir alternativa muestra un error y no cuenta intento`() {
        val vm = vm("mat_1")

        assertEquals(RespuestaActividad.SIN_SELECCION, vm.responder())
        assertEquals("Por favor selecciona una alternativa antes de responder.", vm.uiState.value.mensajeError)
        assertEquals(0, vm.uiState.value.intentos)
    }

    @Test
    fun `respuesta incorrecta suma intento y no registra resultado`() {
        val vm = vm("mat_1")
        vm.seleccionarOpcion(0)

        assertEquals(RespuestaActividad.INCORRECTA, vm.responder())
        assertEquals(1, vm.uiState.value.intentos)
        assertFalse(vm.uiState.value.mostrarExito)
        verify(exactly = 0) { contenidos.marcarCompletado(any(), any()) }
    }

    @Test
    fun `respuesta correcta marca el avance y lo registra en ms-progreso`() = runTest {
        val vm = vm("mat_1")
        vm.responder() // sin seleccion: deja un error
        vm.seleccionarOpcion(2)
        assertNull(vm.uiState.value.mensajeError)

        assertEquals(RespuestaActividad.CORRECTA, vm.responder())
        assertTrue(vm.uiState.value.mostrarExito)
        verify { contenidos.marcarCompletado("mat_1", ActividadViewModel.NOTA_MAXIMA) }
        coVerify { progreso.registrarResultado(segundo, 8, 10, 1, 1) }

        vm.cerrarExito()
        assertFalse(vm.uiState.value.mostrarExito)
    }

    @Test
    fun `sin ejercicio la alternativa 0 se acepta sin registrar nada`() {
        ejercicios.value = emptyList()
        val vm = vm(null)
        vm.seleccionarOpcion(0)

        assertEquals(RespuestaActividad.CORRECTA, vm.responder())
        verify(exactly = 0) { contenidos.marcarCompletado(any(), any()) }
    }
}
