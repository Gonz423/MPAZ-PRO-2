package com.mpaz.progreso.controller;

import com.mpaz.progreso.dto.ResultadoRequest;
import com.mpaz.progreso.dto.ResultadoResponse;
import com.mpaz.progreso.dto.ResumenProgresoDto;
import com.mpaz.progreso.security.Permisos;
import com.mpaz.progreso.security.UsuarioAutenticado;
import com.mpaz.progreso.service.ResultadoService;
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
@RequestMapping("/api/resultados")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Resultados", description = "Calificaciones de los ejercicios realizados por los estudiantes")
@SecurityRequirement(name = "bearerAuth")
public class ResultadoController {

    private final ResultadoService service;

    @Operation(summary = "Registrar un resultado (si ya existe para el mismo ejercicio, suma un intento)")
    @PostMapping
    public ResponseEntity<ResultadoResponse> registrar(@Valid @RequestBody ResultadoRequest request,
                                                       @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Permisos.verificarAccesoEstudiante(usuario, request.estudianteId());
        ResultadoResponse r = service.registrar(request);
        return ResponseEntity.created(URI.create("/api/resultados/" + r.id())).body(r);
    }

    @Operation(summary = "Listar resultados (docente: filtro opcional por estudianteId; estudiante: solo los propios)")
    @GetMapping
    public List<ResultadoResponse> listar(@RequestParam(required = false) Long estudianteId,
                                          @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Long filtro = Permisos.estudianteVisible(usuario, estudianteId);
        return filtro != null ? service.listarPorEstudiante(filtro) : service.listar();
    }

    @Operation(summary = "Obtener un resultado por id")
    @GetMapping("/{id}")
    public ResultadoResponse obtener(@PathVariable Long id,
                                     @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        ResultadoResponse r = service.obtener(id);
        Permisos.verificarAccesoEstudiante(usuario, r.estudianteId());
        return r;
    }

    @Operation(summary = "Actualizar un resultado (docente)")
    @PutMapping("/{id}")
    public ResultadoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ResultadoRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar un resultado (docente)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }

    @Operation(summary = "Resumen de progreso de un estudiante en una asignatura")
    @GetMapping("/progreso/{estudianteId}/{materiaId}")
    public ResumenProgresoDto progreso(@PathVariable Long estudianteId, @PathVariable String materiaId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal UsuarioAutenticado usuario) {
        Permisos.verificarAccesoEstudiante(usuario, estudianteId);
        return service.resumenPorMateria(estudianteId, materiaId);
    }
}
