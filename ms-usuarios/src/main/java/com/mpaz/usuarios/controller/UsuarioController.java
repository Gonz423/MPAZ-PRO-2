package com.mpaz.usuarios.controller;

import com.mpaz.usuarios.dto.UsuarioResponse;
import com.mpaz.usuarios.dto.UsuarioUpdateRequest;
import com.mpaz.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Endpoints de gestión y consulta de perfiles de usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Listar usuarios activos",
            description = "Retorna el listado completo de usuarios activos en la plataforma.")
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios(
            @Parameter(description = "Si es true, incluye usuarios inactivos")
            @RequestParam(value = "todos", defaultValue = "false") boolean todos) {
        if (todos) {
            return ResponseEntity.ok(usuarioService.getAllUsuarios());
        }
        return ResponseEntity.ok(usuarioService.getUsuariosActivos());
    }

    @Operation(summary = "Obtener detalle de usuario por ID",
            description = "Retorna los datos del perfil del usuario según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @Operation(summary = "Filtrar usuarios por rol",
            description = "Retorna el listado de usuarios con el rol indicado (ESTUDIANTE, DOCENTE, UTP).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de usuarios por rol"),
            @ApiResponse(responseCode = "400", description = "Rol inválido")
    })
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponse>> listarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.getUsuariosByRol(rol));
    }

    @Operation(summary = "Actualizar datos de perfil",
            description = "Permite actualizar nombre completo, curso, correo, estado activo o contraseña de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        return ResponseEntity.ok(usuarioService.updateUsuario(id, request));
    }

    @Operation(summary = "Desactivar / borrado lógico de usuario",
            description = "Cambia el estado del usuario a inactivo (activo = false) sin eliminar el registro físico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable Long id) {
        UsuarioResponse desactivado = usuarioService.deactivateUsuario(id);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Usuario desactivado exitosamente (borrado lógico)",
                "id", desactivado.getId(),
                "username", desactivado.getUsername(),
                "activo", desactivado.getActivo()
        ));
    }
}
