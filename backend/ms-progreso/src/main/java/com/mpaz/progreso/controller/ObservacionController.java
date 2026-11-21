package com.mpaz.progreso.controller;

import com.mpaz.progreso.model.Observacion;
import com.mpaz.progreso.service.ObservacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * GET    /api/observaciones?estudianteId={id} -> por estudiante
 * GET    /api/observaciones?docenteId={id}    -> por docente
 * GET    /api/observaciones/{id}              -> por id
 * POST   /api/observaciones                  -> crear
 * PUT    /api/observaciones/{id}             -> actualizar
 * DELETE /api/observaciones/{id}            -> eliminar
 */
@RestController @RequestMapping("/api/observaciones") @CrossOrigin(origins = "*") @RequiredArgsConstructor
public class ObservacionController {
    private final ObservacionService service;

    @GetMapping
    public ResponseEntity<List<Observacion>> listar(
            @RequestParam(required = false) Long estudianteId,
            @RequestParam(required = false) Long docenteId) {
        if (estudianteId != null) return ResponseEntity.ok(service.listarPorEstudiante(estudianteId));
        if (docenteId != null)    return ResponseEntity.ok(service.listarPorDocente(docenteId));
        return ResponseEntity.ok(List.of());
    }
    @GetMapping("/{id}")    public ResponseEntity<Observacion> obtener(@PathVariable Long id) { return ResponseEntity.ok(service.obtener(id)); }
    @PostMapping            public ResponseEntity<Observacion> crear(@RequestBody Observacion o) { return ResponseEntity.ok(service.crear(o)); }
    @PutMapping("/{id}")    public ResponseEntity<Observacion> actualizar(@PathVariable Long id, @RequestBody Observacion d) { return ResponseEntity.ok(service.actualizar(id, d)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
}
