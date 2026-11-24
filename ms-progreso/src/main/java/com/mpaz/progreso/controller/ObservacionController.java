package com.mpaz.progreso.controller;

import com.mpaz.progreso.dto.ObservacionRequest;
import com.mpaz.progreso.dto.ObservacionResponse;
import com.mpaz.progreso.security.Permisos;
import com.mpaz.progreso.security.UsuarioAutenticado;
import com.mpaz.progreso.service.ObservacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/observaciones")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Observaciones", description = "Observaciones pedagógicas del docente sobre los estudiantes")
@SecurityRequirement(name = "bearerAuth")
public class ObservacionController {

    private final ObservacionService service;

    @Operation(summary = "Listar observaciones (docente: filtros estudianteId o docenteId; estudiante: solo las suyas)")
    @GetMapping
    public List<ObservacionResponse> listar(@RequestParam(required = false) Long estudianteId,
                                            @RequestParam(required = false) Long docenteId,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        if (usuario != null && usuario.esDocente()) {
            if (estudianteId != null) return service.listarPorEstudiante(estudianteId);
            if (docenteId != null) return service.listarPorDocente(docenteId);
            return service.listar();
        }
        return service.listarPorEstudiante(Permisos.estudianteVisible(usuario, estudianteId));
    }

    @Operation(summary = "Obtener una observacion por id")
    @GetMapping("/{id}")
    public ObservacionResponse obtener(@PathVariable Long id,
                                       @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        ObservacionResponse o = service.obtener(id);
        Permisos.verificarAccesoEstudiante(usuario, o.estudianteId());
        return o;
    }

    @Operation(summary = "Crear una observacion (docente)")
    @PostMapping
    public ResponseEntity<ObservacionResponse> crear(@Valid @RequestBody ObservacionRequest request) {
        ObservacionResponse o = service.crear(request);
        return ResponseEntity.created(URI.create("/api/observaciones/" + o.id())).body(o);
    }

    @Operation(summary = "Actualizar el contenido de una observacion (docente)")
    @PutMapping("/{id}")
    public ObservacionResponse actualizar(@PathVariable Long id, @Valid @RequestBody ObservacionRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar una observacion (docente)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
