package com.mpaz.progreso.controller;

import com.mpaz.progreso.config.SecurityConfig;
import com.mpaz.progreso.dto.ObservacionRequest;
import com.mpaz.progreso.dto.ObservacionResponse;
import com.mpaz.progreso.security.JwtValidator;
import com.mpaz.progreso.service.ObservacionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Endpoints de observaciones con la seguridad JWT activa. */
@WebMvcTest(ObservacionController.class)
@Import({SecurityConfig.class, JwtValidator.class})
class ObservacionControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ObservacionService service;

    private static final ObservacionResponse OBS = new ObservacionResponse(1L, 2L, 1L, "Buen avance", 5L);
    private static final ObservacionResponse OBS_OTRO = new ObservacionResponse(4L, 2L, 3L, "Otro alumno", 5L);

    @Test
    @DisplayName("El docente lista todas o filtra; el estudiante solo ve las suyas")
    void listar() throws Exception {
        when(service.listar()).thenReturn(List.of(OBS, OBS_OTRO));
        when(service.listarPorEstudiante(1L)).thenReturn(List.of(OBS));
        when(service.listarPorDocente(2L)).thenReturn(List.of());

        mvc.perform(get("/api/observaciones").header("Authorization", Tokens.docente()))
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/observaciones").param("estudianteId", "1").header("Authorization", Tokens.docente()))
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/observaciones").param("docenteId", "2").header("Authorization", Tokens.docente()))
                .andExpect(jsonPath("$.length()").value(0));
        mvc.perform(get("/api/observaciones").header("Authorization", Tokens.estudiante()))
                .andExpect(jsonPath("$[0].estudianteId").value(1));
        mvc.perform(get("/api/observaciones").param("estudianteId", "3").header("Authorization", Tokens.estudiante()))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/observaciones")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST: el docente crea (201, valida el contenido); el estudiante no puede (403)")
    void crear() throws Exception {
        when(service.crear(any(ObservacionRequest.class))).thenReturn(OBS);
        String body = "{\"docenteId\":2,\"estudianteId\":1,\"contenido\":\"Buen avance\"}";

        mvc.perform(post("/api/observaciones").header("Authorization", Tokens.docente())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/observaciones/1"));
        mvc.perform(post("/api/observaciones").header("Authorization", Tokens.docente())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"contenido\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.contenido").exists());
        mvc.perform(post("/api/observaciones").header("Authorization", Tokens.estudiante())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /{id} respeta la propiedad; PUT y DELETE son de docente")
    void crudPorId() throws Exception {
        when(service.obtener(1L)).thenReturn(OBS);
        when(service.obtener(4L)).thenReturn(OBS_OTRO);
        when(service.actualizar(eq(1L), any(ObservacionRequest.class))).thenReturn(OBS);

        mvc.perform(get("/api/observaciones/1").header("Authorization", Tokens.estudiante())).andExpect(status().isOk());
        mvc.perform(get("/api/observaciones/4").header("Authorization", Tokens.estudiante())).andExpect(status().isForbidden());
        mvc.perform(put("/api/observaciones/1").header("Authorization", Tokens.docente())
                .contentType(MediaType.APPLICATION_JSON).content("{\"contenido\":\"Editado\"}")).andExpect(status().isOk());
        mvc.perform(delete("/api/observaciones/1").header("Authorization", Tokens.docente()))
                .andExpect(status().isNoContent());
        verify(service).eliminar(1L);
    }
}
