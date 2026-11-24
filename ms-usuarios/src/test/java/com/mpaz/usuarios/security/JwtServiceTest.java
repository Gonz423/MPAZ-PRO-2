package com.mpaz.usuarios.security;

import com.mpaz.usuarios.model.Rol;
import com.mpaz.usuarios.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwt;

    @BeforeEach
    void setUp() {
        jwt = new JwtService();
        ReflectionTestUtils.setField(jwt, "secretKey", "mpaz-test-secret-0123456789-0123456789-0123456789");
        ReflectionTestUtils.setField(jwt, "expirationMs", 60_000L);
    }

    private static Usuario usuario(String email, Rol rol) {
        return Usuario.builder().id(7L).username("docente").nombreCompleto("Profesora").email(email).rol(rol).curso("2°A").build();
    }

    @Test
    @DisplayName("generateToken incluye usuario, id y rol en los claims")
    void generaToken() {
        String token = jwt.generateToken(usuario("d@x.cl", Rol.DOCENTE));

        assertThat(jwt.isTokenValid(token)).isTrue();
        assertThat(jwt.isTokenValid(token, "DOCENTE")).isTrue();
        assertThat(jwt.isTokenValid(token, "otro")).isFalse();
        assertThat(jwt.extractUsername(token)).isEqualTo("docente");
        assertThat(jwt.extractRol(token)).isEqualTo("DOCENTE");
        assertThat(jwt.extractUserId(token)).isEqualTo(7L);
        assertThat(jwt.extractAllClaims(token).get("email")).isEqualTo("d@x.cl");
    }

    @Test
    @DisplayName("sin email ni rol el token usa ESTUDIANTE y omite el email")
    void sinEmailNiRol() {
        String token = jwt.generateToken(usuario(null, null));

        assertThat(jwt.extractRol(token)).isEqualTo("ESTUDIANTE");
        assertThat(jwt.extractAllClaims(token).get("email")).isNull();
    }

    @Test
    @DisplayName("un token sin claim id devuelve id nulo")
    void sinId() {
        String token = jwt.buildToken(Map.of(), "anonimo", 60_000L);
        assertThat(jwt.extractUserId(token)).isNull();
    }

    @Test
    @DisplayName("tokens expirados, alterados o vacios no son validos")
    void tokensInvalidos() {
        String expirado = jwt.buildToken(Map.of(), "docente", -1_000L);
        JwtService otraClave = new JwtService();
        ReflectionTestUtils.setField(otraClave, "secretKey", "otra-clave-distinta-0123456789-0123456789-0123");
        ReflectionTestUtils.setField(otraClave, "expirationMs", 60_000L);
        String alterado = otraClave.generateToken(usuario(null, Rol.UTP));

        assertThat(jwt.isTokenValid(expirado)).isFalse();
        assertThat(jwt.isTokenValid(alterado)).isFalse();
        assertThat(jwt.isTokenValid("")).isFalse();
        assertThat(jwt.isTokenValid(alterado, "docente")).isFalse();
    }
}
