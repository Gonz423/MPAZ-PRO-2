package com.mpaz.contenidos.controller;

import com.mpaz.contenidos.dto.EjercicioRequest;
import com.mpaz.contenidos.dto.EjercicioResponse;
import com.mpaz.contenidos.service.EjercicioService;
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

@WebMvcTest(EjercicioController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(JwtValidator.class)
class EjercicioControllerTest {

    @Autowired MockMvc mvc;
    @MockBean EjercicioService service;

    private static final EjercicioResponse EJ = new EjercicioResponse(5L, "lenguaje", 1L, "Fabula", "OA 3",
            "1001", "¿Pregunta?", "[\"A\",\"B\"]", 0, true, false, null);

    private static final String BODY_VALIDO =
            "{\"materiaId\":\"lenguaje\",\"unidadId\":1,\"titulo\":\"Fabula\",\"respuestaCorrectaIndex\":0}";

    @Test
    @DisplayName("GET /api/ejercicios aplica el filtro correcto segun los parametros")
    void listarConFiltros() throws Exception {
        when(service.listar()).thenReturn(List.of(EJ));
        when(service.listarPorUnidad(1L)).thenReturn(List.of(EJ));
        when(service.listarPorMateria("lenguaje")).thenReturn(List.of());

        mvc.perform(get("/api/ejercicios")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/ejercicios").param("unidadId", "1")).andExpect(jsonPath("$[0].id").value(5));
        mvc.perform(get("/api/ejercicios").param("materia", "lenguaje")).andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/ejercicios?unidadId=abc devuelve 400")
    void parametroInvalido() throws Exception {
        mvc.perform(get("/api/ejercicios").param("unidadId", "abc")).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/ejercicios/tareas y /{id} devuelven 200")
    void tareasYObtener() throws Exception {
        when(service.listarTareas()).thenReturn(List.of(EJ));
        when(service.obtener(5L)).thenReturn(EJ);

        mvc.perform(get("/api/ejercicios/tareas")).andExpect(status().isOk());
        mvc.perform(get("/api/ejercicios/5")).andExpect(status().isOk()).andExpect(jsonPath("$.codigoGuia").value("1001"));
    }

    @Test
    @DisplayName("POST /api/ejercicios valido devuelve 201")
    void crear() throws Exception {
        when(service.crear(any(EjercicioRequest.class))).thenReturn(EJ);

        mvc.perform(post("/api/ejercicios").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ejercicios/5"));
    }

    @Test
    @DisplayName("POST /api/ejercicios sin unidad devuelve 400")
    void crearSinUnidad() throws Exception {
        mvc.perform(post("/api/ejercicios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"materiaId\":\"lenguaje\",\"titulo\":\"Fabula\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.unidadId").value("La unidad es obligatoria"));
    }

    @Test
    @DisplayName("PUT y DELETE /api/ejercicios/{id} devuelven 200 y 204")
    void actualizarYEliminar() throws Exception {
        when(service.actualizar(eq(5L), any(EjercicioRequest.class))).thenReturn(EJ);

        mvc.perform(put("/api/ejercicios/5").contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isOk());
        mvc.perform(delete("/api/ejercicios/5")).andExpect(status().isNoContent());
        verify(service).eliminar(5L);
    }
}
