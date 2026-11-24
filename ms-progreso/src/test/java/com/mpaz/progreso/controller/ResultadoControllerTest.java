package com.mpaz.progreso.controller;

import com.mpaz.progreso.config.SecurityConfig;
import com.mpaz.progreso.dto.ResultadoRequest;
import com.mpaz.progreso.dto.ResultadoResponse;
import com.mpaz.progreso.dto.ResumenProgresoDto;
import com.mpaz.progreso.exception.RecursoNoEncontradoException;
import com.mpaz.progreso.security.JwtValidator;
import com.mpaz.progreso.service.ResultadoService;
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

/** Endpoints de resultados con la seguridad JWT activa (estudiante id 1, docente id 2). */
@WebMvcTest(ResultadoController.class)
@Import({SecurityConfig.class, JwtValidator.class})
class ResultadoControllerTest {

    @Autowired MockMvc mvc;
    @MockBean ResultadoService service;

    private static final ResultadoResponse RES = new ResultadoResponse(1L, 1L, 1001L, 10, 1, 1, 1, "lenguaje", 5L);
    private static final ResultadoResponse RES_OTRO = new ResultadoResponse(7L, 3L, 1001L, 6, 1, 1, 1, "lenguaje", 5L);
    private static final String BODY = "{\"estudianteId\":1,\"ejercicioId\":1001,\"nota\":10,\"aciertos\":1,\"totalPreguntas\":1,\"materiaId\":\"lenguaje\"}";
    private static final String BODY_OTRO = BODY.replace("\"estudianteId\":1", "\"estudianteId\":3");

    @Test
    @DisplayName("Sin token cualquier endpoint devuelve 401")
    void sinToken() throws Exception {
        mvc.perform(get("/api/resultados")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/resultados").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Un estudiante registra su propio resultado (201) pero no el de otro (403)")
    void registrarEstudiante() throws Exception {
        when(service.registrar(any(ResultadoRequest.class))).thenReturn(RES);

        mvc.perform(post("/api/resultados").header("Authorization", Tokens.estudiante())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nota").value(10));
        mvc.perform(post("/api/resultados").header("Authorization", Tokens.estudiante())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY_OTRO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("No puedes acceder a los datos de otro estudiante"));
        verify(service, times(1)).registrar(any());
    }

    @Test
    @DisplayName("Nota fuera de rango devuelve 400")
    void notaInvalida() throws Exception {
        mvc.perform(post("/api/resultados").header("Authorization", Tokens.docente())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estudianteId\":1,\"ejercicioId\":101,\"nota\":11}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nota").value("La nota maxima es 10"));
        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Listado: el estudiante solo ve lo suyo; el docente ve todo o filtra")
    void listar() throws Exception {
        when(service.listar()).thenReturn(List.of(RES, RES_OTRO));
        when(service.listarPorEstudiante(1L)).thenReturn(List.of(RES));
        when(service.listarPorEstudiante(3L)).thenReturn(List.of(RES_OTRO));

        // estudiante sin filtro -> se aplica su propio id
        mvc.perform(get("/api/resultados").header("Authorization", Tokens.estudiante()))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estudianteId").value(1));
        // estudiante pidiendo a otro -> 403
        mvc.perform(get("/api/resultados").param("estudianteId", "3").header("Authorization", Tokens.estudiante()))
                .andExpect(status().isForbidden());
        // docente
        mvc.perform(get("/api/resultados").header("Authorization", Tokens.docente()))
                .andExpect(jsonPath("$.length()").value(2));
        mvc.perform(get("/api/resultados").param("estudianteId", "3").header("Authorization", Tokens.docente()))
                .andExpect(jsonPath("$[0].estudianteId").value(3));
    }

    @Test
    @DisplayName("GET /{id}: propio 200, ajeno 403, inexistente 404")
    void obtener() throws Exception {
        when(service.obtener(1L)).thenReturn(RES);
        when(service.obtener(7L)).thenReturn(RES_OTRO);
        when(service.obtener(9L)).thenThrow(new RecursoNoEncontradoException("Resultado", 9L));

        mvc.perform(get("/api/resultados/1").header("Authorization", Tokens.estudiante())).andExpect(status().isOk());
        mvc.perform(get("/api/resultados/7").header("Authorization", Tokens.estudiante())).andExpect(status().isForbidden());
        mvc.perform(get("/api/resultados/7").header("Authorization", Tokens.docente())).andExpect(status().isOk());
        mvc.perform(get("/api/resultados/9").header("Authorization", Tokens.docente())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT y DELETE solo para docente")
    void editarYEliminar() throws Exception {
        when(service.actualizar(eq(1L), any(ResultadoRequest.class))).thenReturn(RES);

        mvc.perform(put("/api/resultados/1").header("Authorization", Tokens.estudiante())
                .contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isForbidden());
        mvc.perform(put("/api/resultados/1").header("Authorization", Tokens.docente())
                .contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isOk());
        mvc.perform(delete("/api/resultados/1").header("Authorization", Tokens.docente()))
                .andExpect(status().isNoContent());
        verify(service).eliminar(1L);
    }

    @Test
    @DisplayName("Resumen de progreso: propio 200, ajeno 403")
    void progreso() throws Exception {
        when(service.resumenPorMateria(1L, "lenguaje")).thenReturn(new ResumenProgresoDto(1L, "lenguaje", 8.0, 2, 10, 20));

        mvc.perform(get("/api/resultados/progreso/1/lenguaje").header("Authorization", Tokens.estudiante()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeCompletado").value(20));
        mvc.perform(get("/api/resultados/progreso/3/lenguaje").header("Authorization", Tokens.estudiante()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("JSON mal formado devuelve 400 y un error inesperado 500")
    void errores() throws Exception {
        mvc.perform(post("/api/resultados").header("Authorization", Tokens.docente())
                .contentType(MediaType.APPLICATION_JSON).content("{")).andExpect(status().isBadRequest());

        when(service.listar()).thenThrow(new IllegalStateException("boom"));
        mvc.perform(get("/api/resultados").header("Authorization", Tokens.docente()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Un token sin id de usuario no permite ver resultados (403)")
    void tokenSinId() throws Exception {
        String sinId = "Bearer " + io.jsonwebtoken.Jwts.builder().subject("x").claim("rol", "ESTUDIANTE")
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        Tokens.SECRETO.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                .compact();
        mvc.perform(get("/api/resultados").header("Authorization", sinId)).andExpect(status().isForbidden());
        mvc.perform(get("/api/resultados").param("estudianteId", "1").header("Authorization", sinId))
                .andExpect(status().isForbidden());
    }
}
