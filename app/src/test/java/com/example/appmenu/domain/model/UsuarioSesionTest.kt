package com.example.appmenu.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioSesionTest {

    private val logueado = UsuarioSesion(
        token = "jwt", id = 9, username = "sofia_morales", nombreCompleto = "Sofía Morales",
        email = "sofia@marcelapaz.cl", rol = "ESTUDIANTE", curso = "2° Básico B", estaAutenticado = true
    )

    @Test
    fun `usuario logueado muestra sus propios datos`() {
        assertEquals("Sofía Morales", logueado.nombreParaMostrar(esDocente = false))
        assertEquals("sofia_morales", logueado.usuarioParaMostrar(esDocente = false))
        assertEquals("2° Básico B", logueado.cursoParaMostrar())
    }

    @Test
    fun `acceso rapido sin login muestra el perfil de demostracion`() {
        val invitado = UsuarioSesion()
        assertEquals("Martina González", invitado.nombreParaMostrar(esDocente = false))
        assertEquals("martina", invitado.usuarioParaMostrar(esDocente = false))
        assertEquals("Prof. Andrea Valenzuela", invitado.nombreParaMostrar(esDocente = true))
        assertEquals("prof.andrea", invitado.usuarioParaMostrar(esDocente = true))
        assertEquals("2° Básico A", invitado.cursoParaMostrar())
    }

    @Test
    fun `datos vacios del backend usan el valor de demostracion`() {
        val incompleto = logueado.copy(nombreCompleto = "", username = "", curso = "")
        assertEquals("Martina González", incompleto.nombreParaMostrar(esDocente = false))
        assertEquals("martina", incompleto.usuarioParaMostrar(esDocente = false))
        assertEquals("2° Básico A", incompleto.cursoParaMostrar())
    }
}
