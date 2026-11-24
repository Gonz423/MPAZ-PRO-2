package com.example.appmenu.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class CuriosidadesTest {

    @Test
    fun `ocultar reemplaza la palabra y sus variantes sin distinguir mayusculas ni tildes`() {
        assertEquals("Las ___ pertenecen a los insectos.", Curiosidades.ocultar("Las abejas pertenecen a los insectos.", listOf("abeja")))
        assertEquals("El ___, ___ o ciervo.", Curiosidades.ocultar("El huemul, güemul o ciervo.", listOf("huemul", "güemul")))
        assertEquals("___ es el órgano.", Curiosidades.ocultar("CORAZÓN es el órgano.", listOf("corazón")))
    }

    @Test
    fun `ocultar no toca palabras que solo contienen el texto en medio`() {
        // "sol" no debe ocultarse dentro de "girasol"
        assertEquals("El girasol mira al ___.", Curiosidades.ocultar("El girasol mira al sol.", listOf("sol")))
    }

    @Test
    fun `ocultar prioriza la expresion mas larga`() {
        assertEquals("Un ___ es un arco.", Curiosidades.ocultar("Un arco iris es un arco.", listOf("arco iris", "arcoíris")))
    }

    @Test
    fun `primeraFrase quita parentesis, toma una oracion y limita el largo`() {
        assertEquals(
            "El cóndor andino es un ave.",
            Curiosidades.primeraFrase("El cóndor andino (Vultur gryphus) es un ave. Vive en los Andes.")
        )
        val larga = Curiosidades.primeraFrase("palabra ".repeat(60))
        assertTrue(larga.length <= 231)
        assertTrue(larga.endsWith("…"))
        assertEquals("", Curiosidades.primeraFrase(""))
    }

    @Test
    fun `los temas tienen al menos 4 por categoria para armar 3 alternativas`() {
        Curiosidades.temas.groupBy { it.categoria }.forEach { (_, lista) -> assertTrue(lista.size >= 4) }
        val tema = Curiosidades.temas.first()
        val alternativas = Curiosidades.alternativasIncorrectas(tema, Random(2))
        assertEquals(3, alternativas.toSet().size)
        assertTrue(tema.nombre !in alternativas)
    }
}
