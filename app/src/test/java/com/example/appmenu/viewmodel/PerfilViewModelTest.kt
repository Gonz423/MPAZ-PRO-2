package com.example.appmenu.viewmodel

import android.net.Uri
import com.example.appmenu.data.repository.AuthRepository
import com.example.appmenu.data.repository.FotoPerfilRepository
import com.example.appmenu.domain.model.UsuarioSesion
import com.example.appmenu.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepo: AuthRepository = mockk(relaxed = true)
    private val fotoRepo: FotoPerfilRepository = mockk(relaxed = true)
    private val uriFoto: Uri = mockk()

    private val sesionUsuario = UsuarioSesion(token = "jwt", id = 7, estaAutenticado = true)

    @Test
    fun `clavePara usa el id del usuario autenticado y invitado en el acceso rapido`() {
        assertEquals("usuario_7", PerfilViewModel.clavePara(sesionUsuario))
        assertEquals("invitado", PerfilViewModel.clavePara(UsuarioSesion()))
    }

    @Test
    fun `imagenUri carga la foto guardada del usuario al abrir el perfil`() = runTest {
        every { authRepo.sesion } returns flowOf(sesionUsuario)
        every { fotoRepo.foto("usuario_7") } returns flowOf(uriFoto)
        val viewModel = PerfilViewModel(authRepo, fotoRepo)

        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.imagenUri.collect {} }

        assertEquals(uriFoto, viewModel.imagenUri.value)
    }

    @Test
    fun `foto de la camara se guarda de forma persistente para el usuario`() = runTest {
        every { authRepo.sesion } returns flowOf(sesionUsuario)
        coEvery { fotoRepo.guardar("usuario_7", uriFoto) } returns true
        val viewModel = PerfilViewModel(authRepo, fotoRepo)
        viewModel.uriPendienteCamara = uriFoto

        var resultado: Boolean? = null
        viewModel.actualizarImagenDesdeCamara(true) { resultado = it }

        assertEquals(true, resultado)
        coVerify { fotoRepo.guardar("usuario_7", uriFoto) }
    }

    @Test
    fun `captura cancelada no guarda nada`() = runTest {
        every { authRepo.sesion } returns flowOf(sesionUsuario)
        val viewModel = PerfilViewModel(authRepo, fotoRepo)
        viewModel.uriPendienteCamara = uriFoto

        var resultado: Boolean? = null
        viewModel.actualizarImagenDesdeCamara(false) { resultado = it }

        assertNull(resultado)
        coVerify(exactly = 0) { fotoRepo.guardar(any(), any()) }
    }

    @Test
    fun `imagen de galeria se guarda e informa si fallo`() = runTest {
        every { authRepo.sesion } returns flowOf(UsuarioSesion())
        coEvery { fotoRepo.guardar("invitado", uriFoto) } returns false
        val viewModel = PerfilViewModel(authRepo, fotoRepo)

        var resultado: Boolean? = null
        viewModel.actualizarImagenDesdeGaleria(uriFoto) { resultado = it }

        assertEquals(false, resultado)
    }

    @Test
    fun `galeria sin seleccion no guarda nada`() = runTest {
        val viewModel = PerfilViewModel(authRepo, fotoRepo)

        viewModel.actualizarImagenDesdeGaleria(null)

        coVerify(exactly = 0) { fotoRepo.guardar(any(), any()) }
    }
}
