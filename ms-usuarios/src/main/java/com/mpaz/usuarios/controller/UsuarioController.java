package com.mpaz.usuarios.controller;

import com.mpaz.usuarios.dto.RegistroRequest;
import com.mpaz.usuarios.model.Usuario;
import com.mpaz.usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * GET    /api/usuarios              -> listar todos
 * GET    /api/usuarios?rol=DOCENTE  -> por rol
 * GET    /api/usuarios?curso=2A     -> por curso
 * GET    /api/usuarios/{id}         -> por id
 * PUT    /api/usuarios/{id}         -> actualizar
 * DELETE /api/usuarios/{id}         -> eliminar
 */
@RestController @RequestMapping("/api/usuarios") @CrossOrigin(origins = "*") @RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService service;

    @GetMapping
    public ResponseEntity<List<Usuario>> listar(
            @RequestParam(required = false) String rol,
            @RequestParam(required = false) String curso) {
        if (rol != null) return ResponseEntity.ok(service.listarPorRol(rol));
        if (curso != null) return ResponseEntity.ok(service.listarPorCurso(curso));
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody RegistroRequest req) {
        return ResponseEntity.ok(service.actualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id); return ResponseEntity.noContent().build();
    }
}
