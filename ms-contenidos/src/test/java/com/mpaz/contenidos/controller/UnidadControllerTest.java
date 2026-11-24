package com.mpaz.contenidos.controller;

import com.mpaz.contenidos.dto.UnidadRequest;
import com.mpaz.contenidos.dto.UnidadResponse;
import com.mpaz.contenidos.exception.RecursoNoEncontradoException;
import com.mpaz.contenidos.service.UnidadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import com.mpaz.contenidos.security.JwtValidator;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UnidadController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JwtValidator.class)
class UnidadControllerTest {

    @Autowired MockMvc mvc;
    @MockBean UnidadService service;

    private static final UnidadResponse UNIDAD = new UnidadResponse(1L, "lenguaje", 1, "Lectura", "OA 3");

    @Test
    @DisplayName("GET /api/unidades sin filtro devuelve 200 y la lista")
    void listar() throws Exception {
        when(service.listar()).thenReturn(List.of(UNIDAD));

        mvc.perform(get("/api/unidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Lectura"));
    }

    @Test
    @DisplayName("GET /api/unidades?materia= filtra por materia")
    void listarPorMateria() throws Exception {
        when(service.listarPorMateria("lenguaje")).thenReturn(List.of(UNIDAD));

        mvc.perform(get("/api/unidades").param("materia", "lenguaje"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].materiaId").value("lenguaje"));
        verify(service, never()).listar();
    }

    @Test
    @DisplayName("GET /api/unidades/{id} inexistente devuelve 404 con ProblemDetail")
    void obtenerInexistente() throws Exception {
        when(service.obtener(9L)).thenThrow(new RecursoNoEncontradoException("Unidad", 9L));

        mvc.perform(get("/api/unidades/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Unidad no encontrado/a con id 9"));
    }

    @Test
    @DisplayName("GET /api/unidades/{id} existente devuelve 200")
    void obtener() throws Exception {
        when(service.obtener(1L)).thenReturn(UNIDAD);

        mvc.perform(get("/api/unidades/1")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/unidades valido devuelve 201 con Location")
    void crear() throws Exception {
        when(service.crear(any(UnidadRequest.class))).thenReturn(UNIDAD);

        mvc.perform(post("/api/unidades").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materiaId\":\"lenguaje\",\"numero\":1,\"titulo\":\"Lectura\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/unidades/1"));
    }

    @Test
    @DisplayName("POST /api/unidades sin titulo devuelve 400 con el error del campo")
    void crearInvalido() throws Exception {
        mvc.perform(post("/api/unidades").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materiaId\":\"lenguaje\",\"titulo\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.titulo").exists());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("POST /api/unidades con JSON mal formado devuelve 400")
    void crearMalFormado() throws Exception {
        mvc.perform(post("/api/unidades").contentType(MediaType.APPLICATION_JSON).content("{no es json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/unidades/{id} devuelve 200 con la unidad actualizada")
    void actualizar() throws Exception {
        when(service.actualizar(eq(1L), any(UnidadRequest.class))).thenReturn(UNIDAD);

        mvc.perform(put("/api/unidades/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materiaId\":\"lenguaje\",\"numero\":1,\"titulo\":\"Lectura\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/unidades/{id} devuelve 204")
    void eliminar() throws Exception {
        mvc.perform(delete("/api/unidades/1")).andExpect(status().isNoContent());
        verify(service).eliminar(1L);
    }

    @Test
    @DisplayName("Un error inesperado devuelve 500 sin exponer detalles")
    void errorInesperado() throws Exception {
        when(service.listar()).thenThrow(new IllegalStateException("fallo interno"));

        mvc.perform(get("/api/unidades"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Error interno del servidor"));
    }
}
