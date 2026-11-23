package com.mpaz.usuarios.controller;

import com.mpaz.usuarios.dto.*;
import com.mpaz.usuarios.security.JwtService;
import com.mpaz.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticación", description = "Endpoints de login, registro y validación de tokens JWT")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @Operation(summary = "Iniciar sesión y obtener token JWT",
            description = "Valida las credenciales (username y contraseña) y retorna un token JWT junto con los datos del perfil.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas o usuario inactivo")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = usuarioService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("Intento de login fallido para usuario: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Credenciales inválidas",
                            "status", 401
                    ));
        }
    }

    @Operation(summary = "Registrar nuevo usuario",
            description = "Cifra la contraseña con BCrypt y persiste el nuevo usuario en base de datos H2.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o username duplicado")
    })
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest request) {
        UsuarioResponse nuevoUsuario = usuarioService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @Operation(summary = "Validar token JWT",
            description = "Verifica la firma, vigencia y contenido de un token JWT enviado en el header Authorization o parámetro.")
    @GetMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(value = "token", required = false) String paramToken) {

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7).trim();
        } else if (paramToken != null && !paramToken.isBlank()) {
            token = paramToken.trim();
        }

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token no suministrado")
                    .build());
        }

        boolean isValid = jwtService.isTokenValid(token);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token inválido o expirado")
                    .build());
        }

        String username = jwtService.extractUsername(token);
        String rol = jwtService.extractRol(token);
        Long id = jwtService.extractUserId(token);

        return ResponseEntity.ok(TokenValidationResponse.builder()
                .valid(true)
                .username(username)
                .rol(rol)
                .id(id)
                .message("Token válido")
                .build());
    }
}
