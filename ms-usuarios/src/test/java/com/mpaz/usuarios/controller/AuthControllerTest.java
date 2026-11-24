package com.mpaz.usuarios.controller;

import com.mpaz.usuarios.dto.LoginRequest;
import com.mpaz.usuarios.dto.LoginResponse;
import com.mpaz.usuarios.dto.RegisterRequest;
import com.mpaz.usuarios.dto.UsuarioResponse;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @MockBean UsuarioService usuarioService;
    @MockBean JwtService jwtService;

    @Test
    @DisplayName("POST /api/auth/login correcto devuelve 200 con el token")
    void loginOk() throws Exception {
        when(usuarioService.login(any(LoginRequest.class)))
                .thenReturn(LoginResponse.builder().token("T").id(1L).username("estudiante").rol("ESTUDIANTE").build());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"estudiante\",\"password\":\"1234\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("T"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/auth/login con clave incorrecta devuelve 401")
    void loginIncorrecto() throws Exception {
        when(usuarioService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("x"));

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"estudiante\",\"password\":\"mala\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("POST /api/auth/login sin clave devuelve 400 con el error del campo")
    void loginSinClave() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.password").value("La contraseña es obligatoria"));
        verifyNoInteractions(usuarioService);
    }

    @Test
    @DisplayName("POST /api/auth/register devuelve 201 y un duplicado devuelve 400")
    void register() throws Exception {
        String body = "{\"username\":\"nuevo\",\"password\":\"c\",\"nombreCompleto\":\"N\",\"rol\":\"DOCENTE\"}";
        when(usuarioService.register(any(RegisterRequest.class)))
                .thenReturn(UsuarioResponse.builder().id(4L).username("nuevo").rol(Rol.DOCENTE).activo(true).build())
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de usuario ya está registrado"));

        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(4));
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El nombre de usuario ya está registrado"));
    }

    @Test
    @DisplayName("GET /api/auth/validate con token valido (header o parametro) devuelve los datos")
    void validateOk() throws Exception {
        when(jwtService.isTokenValid("abc")).thenReturn(true);
        when(jwtService.extractUsername("abc")).thenReturn("docente");
        when(jwtService.extractRol("abc")).thenReturn("DOCENTE");
        when(jwtService.extractUserId("abc")).thenReturn(2L);

        mvc.perform(get("/api/auth/validate").header("Authorization", "Bearer abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.rol").value("DOCENTE"));
        mvc.perform(get("/api/auth/validate").param("token", "abc"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/auth/validate sin token devuelve 400 y con token invalido 401")
    void validateErrores() throws Exception {
        when(jwtService.isTokenValid("malo")).thenReturn(false);

        mvc.perform(get("/api/auth/validate")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/auth/validate").param("token", " ")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/auth/validate").header("Authorization", "Bearer malo"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false));
    }
}
