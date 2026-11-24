package com.mpaz.usuarios.controller;

import com.mpaz.usuarios.dto.UsuarioResponse;
import com.mpaz.usuarios.dto.UsuarioUpdateRequest;
import com.mpaz.usuarios.model.Rol;
import com.mpaz.usuarios.security.JwtService;
import com.mpaz.usuarios.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired MockMvc mvc;
    @MockBean UsuarioService service;
    @MockBean JwtService jwtService;

    private static final UsuarioResponse DOCENTE =
            UsuarioResponse.builder().id(2L).username("docente").rol(Rol.DOCENTE).activo(true).build();

    @Test
    @DisplayName("GET /api/usuarios lista activos y con todos=true lista todos")
    void listar() throws Exception {
        when(service.getUsuariosActivos()).thenReturn(List.of(DOCENTE));
        when(service.getAllUsuarios()).thenReturn(List.of(DOCENTE, DOCENTE));

        mvc.perform(get("/api/usuarios")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/usuarios").param("todos", "true")).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} devuelve 200 y un id inexistente 404")
    void obtener() throws Exception {
        when(service.getUsuarioById(2L)).thenReturn(DOCENTE);
        when(service.getUsuarioById(9L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: 9"));

        mvc.perform(get("/api/usuarios/2")).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("docente"));
        mvc.perform(get("/api/usuarios/9")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Usuario no encontrado con ID: 9"));
    }

    @Test
    @DisplayName("GET /api/usuarios/rol/{rol} filtra y un rol invalido devuelve 400")
    void porRol() throws Exception {
        when(service.getUsuariosByRol("DOCENTE")).thenReturn(List.of(DOCENTE));
        when(service.getUsuariosByRol("DIRECTOR")).thenThrow(new IllegalArgumentException("Rol no válido: DIRECTOR"));

        mvc.perform(get("/api/usuarios/rol/DOCENTE")).andExpect(status().isOk());
        mvc.perform(get("/api/usuarios/rol/DIRECTOR")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} actualiza y DELETE hace borrado logico")
    void actualizarYDesactivar() throws Exception {
        when(service.updateUsuario(eq(2L), any(UsuarioUpdateRequest.class))).thenReturn(DOCENTE);
        when(service.deactivateUsuario(2L))
                .thenReturn(UsuarioResponse.builder().id(2L).username("docente").activo(false).build());

        mvc.perform(put("/api/usuarios/2").contentType(MediaType.APPLICATION_JSON).content("{\"curso\":\"2°B\"}"))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/usuarios/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    @DisplayName("Errores inesperados y peticiones mal formadas")
    void errores() throws Exception {
        when(service.getUsuariosActivos()).thenThrow(new IllegalStateException("boom"));

        mvc.perform(get("/api/usuarios")).andExpect(status().isInternalServerError());
        mvc.perform(get("/api/usuarios/abc")).andExpect(status().isBadRequest());
        mvc.perform(put("/api/usuarios/2").contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest());
    }
}
