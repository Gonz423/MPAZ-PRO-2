package com.mpaz.contenidos.controller;

import com.mpaz.contenidos.dto.UnidadRequest;
import com.mpaz.contenidos.dto.UnidadResponse;
import com.mpaz.contenidos.service.UnidadService;
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
@RequestMapping("/api/unidades")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Unidades", description = "CRUD de unidades didácticas por asignatura")
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearerAuth")
public class UnidadController {

    private final UnidadService service;

    @Operation(summary = "Listar unidades (opcionalmente filtradas por materia)")
    @GetMapping
    public List<UnidadResponse> listar(@RequestParam(required = false) String materia) {
        return materia != null ? service.listarPorMateria(materia) : service.listar();
    }

    @Operation(summary = "Obtener una unidad por id")
    @GetMapping("/{id}")
    public UnidadResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Crear una unidad")
    @PostMapping
    public ResponseEntity<UnidadResponse> crear(@Valid @RequestBody UnidadRequest request) {
        UnidadResponse creada = service.crear(request);
        return ResponseEntity.created(URI.create("/api/unidades/" + creada.id())).body(creada);
    }

    @Operation(summary = "Actualizar una unidad")
    @PutMapping("/{id}")
    public UnidadResponse actualizar(@PathVariable Long id, @Valid @RequestBody UnidadRequest request) {
        return service.actualizar(id, request);
    }

    @Operation(summary = "Eliminar una unidad")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
