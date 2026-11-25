package com.example.appmenu.viewmodel

import com.example.appmenu.data.repository.ContenidosRepository
import com.example.appmenu.domain.model.NuevoEjercicio
import com.example.appmenu.domain.model.PreguntaFormulario
import com.example.appmenu.domain.model.UnidadEducativa
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FormularioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repo: ContenidosRepository = mockk(relaxed = true)
    private val unidades = MutableStateFlow(
        listOf(UnidadEducativa("uni_mat_1", "matematica", 1, "Números", "OA 1"))
    )
    private lateinit var vm: FormularioViewModel

    @Before
    fun setUp() {
        every { repo.unidades } returns unidades
        vm = FormularioViewModel(repo)
    }

    private fun llenarValido(esTarea: Boolean = false) = with(vm) {
        actualizarEsTarea(esTarea)
        actualizarNombre("Sumas")
        actualizarDescripcion("OA 1 sumar")
        actualizarCantidad("1500")
        actualizarAceptaTerminos(true)
        actualizarActivo(true)
        actualizarFecha(123L)
        actualizarCategoria("Matemática")
    }

    @Test
    fun `formulario vacio marca todos los errores de actividad`() {
        assertFalse(vm.validarFormulario())

        with(vm.errores.value) {
            assertEquals("El nombre de la actividad es obligatorio", nombre)
            assertEquals("La descripción / OA es obligatoria", descripcion)
            assertEquals("Debe ingresar un número", cantidad)
            assertEquals("Debe aceptar y aprobar el registro pedagógico", aceptaTerminos)
            assertEquals("Debe seleccionar una fecha", fecha)
            assertEquals("Debe seleccionar una asignatura", categoria)
        }
    }

    @Test
    fun `formulario vacio de tarea usa los mensajes de tarea`() {
        vm.actualizarEsTarea(true)
        vm.validarFormulario()

        with(vm.errores.value) {
            assertEquals("El título de la tarea es obligatorio", nombre)
            assertEquals("Las instrucciones de la tarea son obligatorias", descripcion)
            assertEquals("Debe seleccionar una fecha límite de entrega", fecha)
        }
    }

    @Test
    fun `valida largo minimo y rango de la cantidad`() {
        llenarValido()
        vm.actualizarNombre("ab")
        vm.actualizarDescripcion("abcd")
        vm.actualizarCantidad("999")

        assertFalse(vm.validarFormulario())
        assertEquals("El título debe tener al menos 3 caracteres", vm.errores.value.nombre)
        assertEquals("Debe tener al menos 5 caracteres", vm.errores.value.descripcion)
        assertEquals("El valor debe estar entre 1000 y 10000", vm.errores.value.cantidad)

        vm.actualizarCantidad("10001")
        vm.validarFormulario()
        assertEquals("El valor debe estar entre 1000 y 10000", vm.errores.value.cantidad)
    }

    @Test
    fun `al corregir un campo se borra su error`() {
        vm.validarFormulario()
        llenarValido()
        vm.actualizarUnidadId("uni_mat_1")

        with(vm.errores.value) {
            assertNull(nombre); assertNull(descripcion); assertNull(cantidad)
            assertNull(aceptaTerminos); assertNull(fecha); assertNull(categoria); assertNull(unidad)
        }
    }

    @Test
    fun `los campos vacios no borran el error y la cantidad solo acepta digitos`() {
        vm.validarFormulario()
        vm.actualizarNombre(" ")
        vm.actualizarDescripcion("")
        vm.actualizarCantidad("")
        vm.actualizarAceptaTerminos(false)
        vm.actualizarFecha(null)
        vm.actualizarCantidad("12a")

        assertEquals("", vm.formulario.value.cantidad)
        with(vm.errores.value) {
            assertEquals("El nombre de la actividad es obligatorio", nombre)
            assertEquals("La descripción / OA es obligatoria", descripcion)
            assertEquals("Debe ingresar un número", cantidad)
            assertEquals("Debe aceptar y aprobar el registro pedagógico", aceptaTerminos)
            assertEquals("Debe seleccionar una fecha", fecha)
        }
    }

    @Test
    fun `aplicarCategoriaInicial normaliza el nombre y ignora valores vacios`() {
        vm.aplicarCategoriaInicial(null)
        vm.aplicarCategoriaInicial(" ")
        assertEquals("", vm.formulario.value.categoria)

        vm.aplicarCategoriaInicial("Ciencias")
        assertEquals("Ciencias Naturales", vm.formulario.value.categoria)
    }

    @Test
    fun `guardar con errores no crea nada`() {
        assertFalse(vm.guardar())
        assertFalse(vm.guardarGuia(emptyList()))
        coVerify(exactly = 0) { repo.crearEjercicio(any()) }
    }

    @Test
    fun `guardar tarea agrega el prefijo y usa la unidad de la materia`() = runTest {
        val enviado = slot<NuevoEjercicio>()
        coEvery { repo.crearEjercicio(capture(enviado)) } returns mockk()
        llenarValido(esTarea = true)

        assertTrue(vm.guardar())

        with(enviado.captured) {
            assertEquals("Tarea: Sumas", titulo)
            assertEquals("matematica", materiaId)
            assertEquals("uni_mat_1", unidadId)
            assertEquals("1500", codigoGuia)
            assertTrue(esTarea)
            assertEquals(123L, fechaEntrega)
        }
        // se reinicia conservando asignatura y tipo
        assertEquals("", vm.formulario.value.nombre)
        assertEquals("Matemática", vm.formulario.value.categoria)
        assertTrue(vm.formulario.value.esTarea)
    }

    @Test
    fun `guardar respeta el prefijo existente, la unidad elegida y crea actividades sin prefijo`() = runTest {
        val enviados = mutableListOf<NuevoEjercicio>()
        coEvery { repo.crearEjercicio(capture(enviados)) } returns mockk()

        llenarValido(esTarea = true)
        vm.actualizarNombre("tarea: Restas")
        vm.actualizarUnidadId("uni_elegida")
        vm.guardar()

        llenarValido(esTarea = false)
        vm.actualizarCategoria("Historia y Geografía")
        vm.guardar()

        assertEquals("tarea: Restas", enviados[0].titulo)
        assertEquals("uni_elegida", enviados[0].unidadId)
        assertEquals("Sumas", enviados[1].titulo)
        // la materia no tiene unidades cargadas: usa la unidad 1 por convencion
        assertEquals("uni_historia_1", enviados[1].unidadId)
    }

    @Test
    fun `guardarGuia empaqueta las preguntas con textos por defecto`() = runTest {
        val enviado = slot<NuevoEjercicio>()
        coEvery { repo.crearEjercicio(capture(enviado)) } returns mockk()
        llenarValido()

        val ok = vm.guardarGuia(
            listOf(
                PreguntaFormulario(1, "¿2+2?", "4", "", "5"),
                PreguntaFormulario(2)
            )
        )

        assertTrue(ok)
        with(enviado.captured) {
            assertEquals(
                "¿2+2?;;;4;;;Alternativa B;;;5|||¿Cuál es la respuesta correcta?;;;Alternativa A ;;;Alternativa B;;;Alternativa C",
                pregunta
            )
            assertEquals(listOf("4", "Opción 2", "5"), opciones)
            assertFalse(esTarea)
        }
        assertEquals("", vm.formulario.value.nombre)
        assertEquals("Matemática", vm.formulario.value.categoria)
    }

    @Test
    fun `guardarGuia sin preguntas usa opciones por defecto`() = runTest {
        val enviado = slot<NuevoEjercicio>()
        coEvery { repo.crearEjercicio(capture(enviado)) } returns mockk()
        llenarValido()

        vm.guardarGuia(emptyList())

        assertEquals("", enviado.captured.pregunta)
        assertEquals(listOf("Opción 1", "Opción 2", "Opción 3"), enviado.captured.opciones)
    }

    @Test
    fun `crearUnidad valida los campos y deja seleccionada la unidad nueva`() = runTest {
        coEvery { repo.crearUnidad("matematica", "Restas", "OA 2") } returns
            UnidadEducativa("uni_mat_9", "matematica", 2, "Unidad 2 - Restas", "OA 2")
        vm.actualizarCategoria("Matemática")

        assertEquals("Ingrese título y objetivo de aprendizaje.", vm.crearUnidad("", "OA 2"))
        assertEquals("Ingrese título y objetivo de aprendizaje.", vm.crearUnidad("Restas", " "))
        assertNull(vm.crearUnidad("Restas", "OA 2"))

        assertEquals("uni_mat_9", vm.formulario.value.unidadId)
        assertEquals(unidades.value, vm.unidades.value)
    }
}
