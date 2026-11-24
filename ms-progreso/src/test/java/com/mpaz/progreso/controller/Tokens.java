package com.mpaz.progreso.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/** Genera headers Authorization con JWT firmados igual que ms-usuarios (clave de src/test/resources). */
final class Tokens {

    static final String SECRETO = "mpaz-test-secret-0123456789-0123456789-0123456789";

    private Tokens() {}

    static String de(long id, String rol) {
        return "Bearer " + Jwts.builder()
                .claims(Map.of("id", id, "rol", rol))
                .subject("usuario" + id)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRETO.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /** Estudiante con id 1. */
    static String estudiante() {
        return de(1L, "ESTUDIANTE");
    }

    /** Docente con id 2. */
    static String docente() {
        return de(2L, "DOCENTE");
    }
}
