package com.example.appmenu.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MateriasTest {

    @Test
    fun `idDesdeCategoria convierte el nombre visible en el id interno`() {
        assertEquals("lenguaje", Materias.idDesdeCategoria("Lenguaje y Comunicación"))
        assertEquals("matematica", Materias.idDesdeCategoria("Matemática"))
        assertEquals("matematica", Materias.idDesdeCategoria("matematica"))
        assertEquals("ciencias", Materias.idDesdeCategoria("Ciencias Naturales"))
        assertEquals("historia", Materias.idDesdeCategoria("Historia y Geografía"))
        assertEquals("lenguaje", Materias.idDesdeCategoria("Arte"))
    }

    @Test
    fun `normalizarCategoria devuelve el texto exacto del selector`() {
        assertEquals("Lenguaje y Comunicación", Materias.normalizarCategoria("lenguaje"))
        assertEquals("Matemática", Materias.normalizarCategoria("Matematica"))
        assertEquals("Matemática", Materias.normalizarCategoria("MATEMÁTICA"))
        assertEquals("Ciencias Naturales", Materias.normalizarCategoria("ciencias"))
        assertEquals("Historia y Geografía", Materias.normalizarCategoria("Historia"))
        assertEquals("Arte", Materias.normalizarCategoria("Arte"))
    }
}
