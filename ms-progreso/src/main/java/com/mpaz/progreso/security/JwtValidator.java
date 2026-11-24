package com.mpaz.progreso.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Valida los JWT emitidos por ms-usuarios. Este servicio no emite tokens: solo verifica
 * la firma (con la misma clave compartida JWT_SECRET) y la fecha de expiracion.
 */
@Component
public class JwtValidator {

    private final SecretKey clave;

    public JwtValidator(@Value("${app.jwt.secret}") String secreto) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
    }

    /** Devuelve el usuario del token, o vacio si la firma no es valida o el token expiro. */
    public Optional<UsuarioAutenticado> validar(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
            Object id = claims.get("id");
            Long idUsuario = id instanceof Number n ? n.longValue() : null;
            return Optional.of(new UsuarioAutenticado(idUsuario, claims.getSubject(), claims.get("rol", String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
