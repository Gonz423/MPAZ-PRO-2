package com.mpaz.usuarios.controller;

import com.mpaz.usuarios.dto.*;
import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * POST /api/auth/login    -> obtener JWT
 * POST /api/auth/registro -> registrar nuevo usuario
 */
@RestController @RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {
    private final UsuarioService service;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(service.login(req));
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.ok(service.registrar(req));
    }
}
