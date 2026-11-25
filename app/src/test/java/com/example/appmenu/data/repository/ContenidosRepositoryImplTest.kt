package com.example.appmenu.data.repository

import com.example.appmenu.data.local.ContenidosLocalDataSource
import com.example.appmenu.data.remote.backend.ContenidosRemoteDataSource
import com.example.appmenu.data.remote.backend.dto.EjercicioDto
import com.example.appmenu.data.remote.backend.dto.UnidadDto
import com.example.appmenu.domain.model.ErrorRed
import com.example.appmenu.domain.model.NuevoEjercicio
import com.example.appmenu.domain.model.ResultadoEstudiante
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContenidosRepositoryImplTest {

    private var siguienteId = 0L
    private val local = ContenidosLocalDataSource(generarId = { ++siguienteId })
    private val remoto: ContenidosRemoteDataSource = mockk()
    private val repo = ContenidosRepositoryImpl(local, remoto)

    private fun unidad(id: String) = local.unidadesActuales().first { it.id == id }
    private fun ejercicio(id: String) = local.ejerciciosActuales().first { it.id == id }

    private fun dtoEjercicio(id: Long?, codigo: String?, unidadId: Long = 10, materia: String = "lenguaje", opciones: String? = null) =
        EjercicioDto(id = id, materiaId = materia, unidadId = unidadId, titulo = "Remoto $id", codigoGuia = codigo, opciones = opciones)

    // ---------------- sincronizar ----------------

    @Test
    fun `sincronizar devuelve el error si falla la descarga de unidades`() = runTest {
        coEvery { remoto.listarUnidades() } returns Result.failure(ErrorRed.SinConexion())

        assertTrue(repo.sincronizar().exceptionOrNull() is ErrorRed.SinConexion)
        coVerify(exactly = 0) { remoto.listarEjercicios() }
    }

    @Test
    fun `sincronizar devuelve el error si falla la descarga de ejercicios`() = runTest {
        coEvery { remoto.listarUnidades() } returns Result.success(emptyList())
        coEvery { remoto.listarEjercicios() } returns Result.failure(ErrorRed.Http(500, null))

        assertTrue(repo.sincronizar().isFailure)
    }

    @Test
    fun `sincronizar combina unidades por materia y numero e inserta las nuevas`() = runTest {
        coEvery { remoto.listarUnidades() } returns Result.success(
            listOf(
                UnidadDto(id = 10, materiaId = "lenguaje", numero = 1, titulo = "Lectura"),
                UnidadDto(id = 11, materiaId = "lenguaje", numero = null, titulo = "Poesía", objetivoAprendizaje = null),
                UnidadDto(id = null, materiaId = "historia", numero = 9, titulo = "Sin id")
            )
        )
        coEvery { remoto.listarEjercicios() } returns Result.success(emptyList())

        assertTrue(repo.sincronizar().isSuccess)
        assertEquals(10L, unidad("uni_len_1").remoteId)
        val insertada = unidad("srv_uni_11")
        assertEquals(1, insertada.numero)
        assertEquals("", insertada.objetivoAprendizaje)
        assertEquals(5, local.unidadesActuales().size)
    }

    @Test
    fun `sincronizar combina ejercicios por codigo e inserta los nuevos con su unidad`() = runTest {
        coEvery { remoto.listarUnidades() } returns Result.success(
            listOf(UnidadDto(id = 10, materiaId = "lenguaje", numero = 1, titulo = "Lectura"))
        )
        coEvery { remoto.listarEjercicios() } returns Result.success(
            listOf(
                dtoEjercicio(id = 100, codigo = "1001"),
                dtoEjercicio(id = 101, codigo = "1099", opciones = "[\"A\",\"B\",\"C\"]"),
                dtoEjercicio(id = 102, codigo = null, unidadId = 999, materia = "ciencias", opciones = "no es json"),
                dtoEjercicio(id = 103, codigo = "5000", unidadId = 999, materia = "arte", opciones = "[]"),
                dtoEjercicio(id = null, codigo = "1002")
            )
        )

        repo.sincronizar()

        assertEquals(100L, ejercicio("len_1").remoteId)
        assertNull(ejercicio("len_2").remoteId)

        val conUnidadRemota = ejercicio("srv_ej_101")
        assertEquals("uni_len_1", conUnidadRemota.unidadId)
        assertEquals(listOf("A", "B", "C"), conUnidadRemota.opciones)
        assertEquals("¿Cuál es la respuesta correcta?", conUnidadRemota.pregunta)

        val porMateria = ejercicio("srv_ej_102")
        assertEquals("uni_cie_1", porMateria.unidadId)
        assertEquals("", porMateria.codigoGuia)
        assertEquals(listOf("Opción 1", "Opción 2", "Opción 3"), porMateria.opciones)

        assertEquals("uni_arte_1", ejercicio("srv_ej_103").unidadId)
        assertEquals(listOf("Opción 1", "Opción 2", "Opción 3"), ejercicio("srv_ej_103").opciones)
    }

    // ---------------- crear ----------------

    @Test
    fun `crearUnidad guarda el id remoto si el servidor la acepta`() = runTest {
        coEvery { remoto.crearUnidad(any()) } returns Result.success(UnidadDto(id = 55, materiaId = "historia", titulo = "x"))

        val creada = repo.crearUnidad("historia", "Pueblos", "OA 2")

        assertEquals(55L, creada.remoteId)
        assertEquals(2, creada.numero)
    }

    @Test
    fun `crearUnidad queda solo local si el servidor no responde`() = runTest {
        coEvery { remoto.crearUnidad(any()) } returns Result.failure(ErrorRed.SinConexion())

        val creada = repo.crearUnidad("historia", "Pueblos", "OA 2")

        assertNull(creada.remoteId)
        assertTrue(local.unidadesActuales().any { it.id == creada.id })
    }

    @Test
    fun `crearEjercicio lo envia al servidor cuando su unidad esta sincronizada`() = runTest {
        local.actualizarUnidad("uni_mat_1") { it.copy(remoteId = 20) }
        val enviado = slot<EjercicioDto>()
        coEvery { remoto.crearEjercicio(capture(enviado)) } returns
            Result.success(dtoEjercicio(id = 300, codigo = "2003", unidadId = 20, materia = "matematica"))

        val creado = repo.crearEjercicio(NuevoEjercicio("matematica", "uni_mat_1", "Restas", "OA 9", "2003"))

        assertEquals(300L, creado.remoteId)
        assertEquals(20L, enviado.captured.unidadId)
        assertEquals("[\"Opción A\",\"Opción B\",\"Opción C\"]", enviado.captured.opciones)
    }

    @Test
    fun `crearEjercicio usa otra unidad sincronizada de la materia si la suya no lo esta`() = runTest {
        local.actualizarUnidad("uni_mat_1") { it.copy(remoteId = 20) }
        val enviado = slot<EjercicioDto>()
        coEvery { remoto.crearEjercicio(capture(enviado)) } returns Result.failure(ErrorRed.SinConexion())

        val creado = repo.crearEjercicio(NuevoEjercicio("matematica", "uni_inexistente", "Restas", "OA 9", "2003"))

        assertEquals(20L, enviado.captured.unidadId)
        assertNull(creado.remoteId)
    }

    @Test
    fun `crearEjercicio no llama al servidor si la materia no tiene unidades sincronizadas`() = runTest {
        val creado = repo.crearEjercicio(NuevoEjercicio("ciencias", "uni_cie_1", "Plantas", "OA 1", "3003"))

        assertNull(creado.remoteId)
        coVerify(exactly = 0) { remoto.crearEjercicio(any()) }
    }

    // ---------------- visibilidad y eliminacion ----------------

    @Test
    fun `cambiarVisibilidad de un ejercicio sin id remoto solo cambia el local`() = runTest {
        assertTrue(repo.cambiarVisibilidad("len_2").isSuccess)
        assertFalse(ejercicio("len_2").disponibleParaEstudiantes)
        coVerify(exactly = 0) { remoto.actualizarEjercicio(any(), any()) }
    }

    @Test
    fun `cambiarVisibilidad de un ejercicio inexistente no falla`() = runTest {
        assertTrue(repo.cambiarVisibilidad("no_existe").isSuccess)
    }

    @Test
    fun `cambiarVisibilidad sin unidad remota no llama al servidor`() = runTest {
        local.actualizarEjercicio("len_2") { it.copy(remoteId = 7) }

        assertTrue(repo.cambiarVisibilidad("len_2").isSuccess)
        coVerify(exactly = 0) { remoto.actualizarEjercicio(any(), any()) }
    }

    @Test
    fun `cambiarVisibilidad sincronizada envia el PUT y devuelve su resultado`() = runTest {
        local.actualizarUnidad("uni_len_1") { it.copy(remoteId = 10) }
        local.actualizarEjercicio("len_2") { it.copy(remoteId = 7) }
        coEvery { remoto.actualizarEjercicio(7, any()) } returns Result.failure(ErrorRed.SinConexion())

        assertTrue(repo.cambiarVisibilidad("len_2").isFailure)
        coVerify { remoto.actualizarEjercicio(7, match { it.disponible == false }) }
    }

    @Test
    fun `eliminarEjercicio inexistente no hace nada`() = runTest {
        assertTrue(repo.eliminarEjercicio("no_existe").isSuccess)
        assertEquals(8, local.ejerciciosActuales().size)
    }

    @Test
    fun `eliminarEjercicio local no llama al servidor`() = runTest {
        assertTrue(repo.eliminarEjercicio("len_2").isSuccess)
        assertNull(local.ejerciciosActuales().firstOrNull { it.id == "len_2" })
        coVerify(exactly = 0) { remoto.eliminarEjercicio(any()) }
    }

    @Test
    fun `eliminarEjercicio sincronizado envia el DELETE`() = runTest {
        local.actualizarEjercicio("len_2") { it.copy(remoteId = 7) }
        coEvery { remoto.eliminarEjercicio(7) } returns Result.success(Unit)

        assertTrue(repo.eliminarEjercicio("len_2").isSuccess)
        coVerify { remoto.eliminarEjercicio(7) }
    }

    // ---------------- avance del estudiante ----------------

    @Test
    fun `alternarCompletado y marcarCompletado delegan en el catalogo local`() {
        repo.alternarCompletado("len_2")
        assertTrue(ejercicio("len_2").completado)

        repo.marcarCompletado("mat_2", 8)
        assertEquals(8, ejercicio("mat_2").nota)
    }

    @Test
    fun `aplicarResultados marca los ejercicios pendientes por codigo o id remoto`() {
        local.actualizarEjercicio("mat_2") { it.copy(remoteId = 900) }
        repo.aplicarResultados(
            listOf(
                ResultadoEstudiante(1, 1, ejercicioId = 1002, nota = 6, intentos = 3, materiaId = "lenguaje"),
                ResultadoEstudiante(2, 1, ejercicioId = 900, nota = 9, intentos = 1, materiaId = "matematica"),
                ResultadoEstudiante(3, 1, ejercicioId = 1001, nota = 1, intentos = 5, materiaId = "lenguaje"),
                ResultadoEstudiante(4, 1, ejercicioId = 777, nota = 7, intentos = 1, materiaId = "otra")
            )
        )

        assertTrue(ejercicio("len_2").completado)
        assertEquals(6, ejercicio("len_2").nota)
        assertEquals(3, ejercicio("len_2").intentos)
        assertEquals(9, ejercicio("mat_2").nota)
        // ya estaba completado: no se sobrescribe
        assertEquals(0, ejercicio("len_1").intentos)
    }
}
