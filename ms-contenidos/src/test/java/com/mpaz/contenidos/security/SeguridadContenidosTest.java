package com.mpaz.contenidos.security;

import com.mpaz.contenidos.config.SecurityConfig;
import com.mpaz.contenidos.controller.UnidadController;
import com.mpaz.contenidos.dto.UnidadRequest;
import com.mpaz.contenidos.dto.UnidadResponse;
import com.mpaz.contenidos.service.UnidadService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Prueba las reglas de acceso con el filtro JWT activo y tokens firmados como los de ms-usuarios. */
@WebMvcTest(UnidadController.class)
@Import({SecurityConfig.class, JwtValidator.class})
class SeguridadContenidosTest {

    static final String SECRETO = "mpaz-test-secret-0123456789-0123456789-0123456789";

    @Autowired MockMvc mvc;
    @Autowired JwtValidator validator;
    @MockBean UnidadService service;

    static String token(String secreto, String rol, long expiraEnMs) {
        return "Bearer " + Jwts.builder()
                .claims(Map.of("id", 2, "rol", rol))
                .subject("usuario")
                .expiration(new Date(System.currentTimeMillis() + expiraEnMs))
                .signWith(Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private static final String BODY = "{\"materiaId\":\"lenguaje\",\"numero\":2,\"titulo\":\"Nueva\"}";

    @Test
    @DisplayName("GET del catalogo es publico (sin token)")
    void getPublico() throws Exception {
        when(service.listar()).thenReturn(List.of());
        mvc.perform(get("/api/unidades")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST sin token devuelve 401")
    void escrituraSinToken() throws Exception {
        mvc.perform(post("/api/unidades").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST con token de ESTUDIANTE devuelve 403")
    void escrituraEstudiante() throws Exception {
        mvc.perform(post("/api/unidades").header("Authorization", token(SECRETO, "ESTUDIANTE", 60_000))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST con token de DOCENTE o UTP crea la unidad (201)")
    void escrituraDocente() throws Exception {
        when(service.crear(any(UnidadRequest.class))).thenReturn(new UnidadResponse(9L, "lenguaje", 2, "Nueva", null));

        mvc.perform(post("/api/unidades").header("Authorization", token(SECRETO, "DOCENTE", 60_000))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated());
        mvc.perform(delete("/api/unidades/9").header("Authorization", token(SECRETO, "utp", 60_000)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Tokens expirados, firmados con otra clave o con otro esquema se rechazan (401)")
    void tokensInvalidos() throws Exception {
        mvc.perform(delete("/api/unidades/1").header("Authorization", token(SECRETO, "DOCENTE", -60_000)))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/unidades/1")
                        .header("Authorization", token("otra-clave-distinta-0123456789-0123456789-0123", "DOCENTE", 60_000)))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/unidades/1").header("Authorization", "Basic abc"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("JwtValidator extrae id, usuario y rol; un token sin rol no da permisos de docente")
    void validador() {
        String sinRol = Jwts.builder().subject("x")
                .signWith(Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8))).compact();

        UsuarioAutenticado u = validator.validar(token(SECRETO, "DOCENTE", 60_000).substring(7)).orElseThrow();
        assertThat(u.id()).isEqualTo(2L);
        assertThat(u.esDocente()).isTrue();
        assertThat(validator.validar(sinRol).orElseThrow().esDocente()).isFalse();
        assertThat(validator.validar("no-es-un-jwt")).isEmpty();
    }

    @Test
    @DisplayName("POST con token valido pero sin rol devuelve 403")
    void tokenSinRol() throws Exception {
        String sinRol = "Bearer " + Jwts.builder().subject("x")
                .signWith(Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8))).compact();
        mvc.perform(post("/api/unidades").header("Authorization", sinRol)
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
    }
}
