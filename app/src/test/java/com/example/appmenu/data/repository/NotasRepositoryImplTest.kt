package com.example.appmenu.data.repository

import com.example.appmenu.data.local.room.Note
import com.example.appmenu.data.local.room.NoteDao
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NotasRepositoryImplTest {

    private val dao: NoteDao = mockk(relaxed = true)
    private lateinit var repository: NotasRepositoryImpl

    @Before
    fun setUp() {
        every { dao.observeAll() } returns flowOf(emptyList())
        repository = NotasRepositoryImpl(dao)
    }

    @Test
    fun `notas flow emite la lista obtenida de dao observeAll`() = runTest {
        val notasEsperadas = listOf(
            Note(id = 1, title = "Revisión Lenguaje", content = "Lectura completada con fluidez"),
            Note(id = 2, title = "Tarea Matemática", content = "Sumas y restas resueltas correctamente")
        )
        every { dao.observeAll() } returns flowOf(notasEsperadas)
        repository = NotasRepositoryImpl(dao)

        val resultado = repository.notas.first()

        assertEquals(2, resultado.size)
        assertEquals("Revisión Lenguaje", resultado[0].title)
        assertEquals("Tarea Matemática", resultado[1].title)
    }

    @Test
    fun `guardar recorta espacios en blanco e invoca dao insert con Note valida`() = runTest {
        val slot = slot<Note>()
        coEvery { dao.insert(capture(slot)) } returns Unit

        repository.guardar("  Ciencias Naturales  ", "   Animales vertebrados  ")

        coVerify(exactly = 1) { dao.insert(any()) }
        assertEquals("Ciencias Naturales", slot.captured.title)
        assertEquals("Animales vertebrados", slot.captured.content)
    }

    @Test
    fun `eliminar delega directamente en dao delete con la nota entregada`() = runTest {
        val nota = Note(id = 10, title = "Nota a borrar", content = "Contenido")
        coEvery { dao.delete(nota) } returns Unit

        repository.eliminar(nota)

        coVerify(exactly = 1) { dao.delete(nota) }
    }
}
