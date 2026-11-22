package com.mpaz.contenidos.controller;

import com.mpaz.contenidos.model.Unidad;
import com.mpaz.contenidos.service.UnidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * GET    /api/unidades              -> todas
 * GET    /api/unidades?materia={id} -> por materia
 * GET    /api/unidades/{id}         -> por id
 * POST   /api/unidades              -> crear
 * PUT    /api/unidades/{id}         -> actualizar
 * DELETE /api/unidades/{id}         -> eliminar
 */
@RestController @RequestMapping("/api/unidades") @CrossOrigin(origins = "*") @RequiredArgsConstructor
public class UnidadController {
    private final UnidadService service;

    @GetMapping
    public ResponseEntity<List<Unidad>> listar(@RequestParam(required = false) String materia) {
        return ResponseEntity.ok(materia != null ? service.listarPorMateria(materia) : service.listar());
    }
    @GetMapping("/{id}") public ResponseEntity<Unidad> obtener(@PathVariable Long id) { return ResponseEntity.ok(service.obtener(id)); }
    @PostMapping       public ResponseEntity<Unidad> crear(@RequestBody Unidad u) { return ResponseEntity.ok(service.crear(u)); }
    @PutMapping("/{id}") public ResponseEntity<Unidad> actualizar(@PathVariable Long id, @RequestBody Unidad d) { return ResponseEntity.ok(service.actualizar(id, d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
