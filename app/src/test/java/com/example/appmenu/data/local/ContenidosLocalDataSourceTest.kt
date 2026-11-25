package com.example.appmenu.data.local

import com.example.appmenu.domain.model.EjercicioEducativo
import com.example.appmenu.domain.model.UnidadEducativa
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContenidosLocalDataSourceTest {

    private val local = ContenidosLocalDataSource(generarId = { 77 })

    private fun ejercicio(id: String) = local.ejerciciosActuales().first { it.id == id }

    @Test
    fun `catalogo inicial tiene una unidad y dos ejercicios por materia`() {
        assertEquals(4, local.unidadesActuales().size)
        assertEquals(8, local.ejerciciosActuales().size)
        assertEquals(local.unidadesActuales(), local.unidades.value)
        assertEquals(local.ejerciciosActuales(), local.ejercicios.value)
    }

    @Test
    fun `agregarUnidad numera a continuacion de las de la materia y limpia el texto`() {
        val nueva = local.agregarUnidad("Lenguaje", "  Poemas ", " OA 5 ")

        assertEquals("uni_lenguaje_77", nueva.id)
        assertEquals("lenguaje", nueva.materiaId)
        assertEquals(2, nueva.numero)
        assertEquals("Unidad 2 - Poemas", nueva.titulo)
        assertEquals("OA 5", nueva.objetivoAprendizaje)
        assertTrue(local.unidadesActuales().contains(nueva))
    }

    @Test
    fun `agregarEjercicio aplica valores por defecto cuando faltan pregunta u opciones`() {
        val nuevo = local.agregarEjercicio(
            materiaId = "MATEMATICA", unidadId = "uni_mat_1", titulo = " Restas ", oa = " OA 9 ",
            codigoGuia = " 2003 ", pregunta = " ", opciones = emptyList(), respuestaCorrectaIndex = 7
        )

        assertEquals("ej_77", nuevo.id)
        assertEquals("matematica", nuevo.materiaId)
        assertEquals("Restas", nuevo.titulo)
        assertEquals("2003", nuevo.codigoGuia)
        assertEquals("¿Cuál es la respuesta correcta?", nuevo.pregunta)
        assertEquals(listOf("Opción 1", "Opción 2", "Opción 3"), nuevo.opciones)
        assertEquals(2, nuevo.respuestaCorrectaIndex)
        assertFalse(nuevo.completado)
    }

    @Test
    fun `agregarEjercicio conserva la pregunta y opciones entregadas`() {
        val nuevo = local.agregarEjercicio(
            materiaId = "ciencias", unidadId = "uni_cie_1", titulo = "Plantas", oa = "OA 1",
            codigoGuia = "3003", pregunta = " ¿Qué necesita una planta? ", opciones = listOf("Agua", "Arena", "Sal"),
            disponible = false, esTarea = true, fechaEntrega = 99L
        )

        assertEquals("¿Qué necesita una planta?", nuevo.pregunta)
        assertEquals(listOf("Agua", "Arena", "Sal"), nuevo.opciones)
        assertFalse(nuevo.disponibleParaEstudiantes)
        assertTrue(nuevo.esTarea)
        assertEquals(99L, nuevo.fechaEntrega)
    }

    @Test
    fun `insertar actualizar y eliminar modifican el catalogo`() {
        local.insertarUnidad(UnidadEducativa("u_x", "historia", 2, "Pueblos", "OA 2"))
        local.insertarEjercicio(EjercicioEducativo("e_x", "historia", "u_x", "Mapas", "OA 2", "4003"))
        local.actualizarUnidad("u_x") { it.copy(remoteId = 5) }
        local.actualizarEjercicio("e_x") { it.copy(remoteId = 6) }

        assertEquals(5L, local.unidadesActuales().first { it.id == "u_x" }.remoteId)
        assertEquals(6L, ejercicio("e_x").remoteId)

        local.eliminarEjercicio("e_x")
        assertNull(local.ejerciciosActuales().firstOrNull { it.id == "e_x" })
    }

    @Test
    fun `alternarCompletado cambia entre completado con nota 10 y pendiente con nota 0`() {
        local.alternarCompletado("len_2")
        assertTrue(ejercicio("len_2").completado)
        assertEquals(10, ejercicio("len_2").nota)

        local.alternarCompletado("len_2")
        assertFalse(ejercicio("len_2").completado)
        assertEquals(0, ejercicio("len_2").nota)
    }

    @Test
    fun `marcarCompletado guarda la nota y suma un intento`() {
        local.marcarCompletado("mat_2", 7)
        local.marcarCompletado("mat_2")

        assertTrue(ejercicio("mat_2").completado)
        assertEquals(10, ejercicio("mat_2").nota)
        assertEquals(2, ejercicio("mat_2").intentos)
    }
}
