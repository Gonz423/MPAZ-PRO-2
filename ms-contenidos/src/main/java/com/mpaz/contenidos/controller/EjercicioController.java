package com.mpaz.contenidos.controller;

import com.mpaz.contenidos.model.Ejercicio;
import com.mpaz.contenidos.service.EjercicioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * GET    /api/ejercicios                 -> todos
 * GET    /api/ejercicios?unidadId={id}   -> por unidad
 * GET    /api/ejercicios?materia={id}    -> por materia
 * GET    /api/ejercicios/tareas          -> solo tareas
 * GET    /api/ejercicios/{id}            -> por id
 * POST   /api/ejercicios                -> crear (docente)
 * PUT    /api/ejercicios/{id}           -> actualizar
 * DELETE /api/ejercicios/{id}           -> eliminar
 */
@RestController @RequestMapping("/api/ejercicios") @CrossOrigin(origins = "*") @RequiredArgsConstructor
public class EjercicioController {
    private final EjercicioService service;

    @GetMapping
    public ResponseEntity<List<Ejercicio>> listar(
            @RequestParam(required = false) Long unidadId,
            @RequestParam(required = false) String materia) {
        if (unidadId != null) return ResponseEntity.ok(service.listarPorUnidad(unidadId));
        if (materia  != null) return ResponseEntity.ok(service.listarPorMateria(materia));
        return ResponseEntity.ok(service.listar());
    }
    @GetMapping("/tareas") public ResponseEntity<List<Ejercicio>> listarTareas() { return ResponseEntity.ok(service.listarTareas()); }
    @GetMapping("/{id}")   public ResponseEntity<Ejercicio> obtener(@PathVariable Long id)  { return ResponseEntity.ok(service.obtener(id)); }
    @PostMapping           public ResponseEntity<Ejercicio> crear(@RequestBody Ejercicio e)  { return ResponseEntity.ok(service.crear(e)); }
    @PutMapping("/{id}")   public ResponseEntity<Ejercicio> actualizar(@PathVariable Long id, @RequestBody Ejercicio d) { return ResponseEntity.ok(service.actualizar(id, d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
