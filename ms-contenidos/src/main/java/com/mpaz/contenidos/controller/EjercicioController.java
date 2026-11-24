package com.mpaz.contenidos.controller;

import com.mpaz.contenidos.dto.EjercicioRequest;
import com.mpaz.contenidos.dto.EjercicioResponse;
import com.mpaz.contenidos.service.EjercicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ejercicios")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Ejercicios", description = "CRUD de ejercicios y tareas")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class EjercicioController {

    private final EjercicioService service;

    @Operation(summary = "Listar ejercicios (filtros opcionales: unidadId o materia)")
    @GetMapping
    public List<EjercicioResponse> listar(@RequestParam(required = false) Long unidadId,
                                          @RequestParam(required = false) String materia) {
        if (unidadId != null) return service.listarPorUnidad(unidadId);
        if (materia != null) return service.listarPorMateria(materia);
        return service.listar();
    }

    @Operation(summary = "Listar solo los ejercicios marcados como tarea")
    @GetMapping("/tareas")
    public List<EjercicioResponse> listarTareas() {
        return service.listarTareas();
    }

    @Operation(summary = "Obtener un ejercicio por id")
    @GetMapping("/{id}")
    public EjercicioResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear un ejercicio")
    @PostMapping
    public ResponseEntity<EjercicioResponse> crear(@Valid @RequestBody EjercicioRequest request) {
        EjercicioResponse creado = service.crear(request);
        return ResponseEntity.created(URI.create("/api/ejercicios/" + creado.id())).body(creado);
    }

    @Operation(summary = "Actualizar un ejercicio")
    @PutMapping("/{id}")
    public EjercicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody EjercicioRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar un ejercicio")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
