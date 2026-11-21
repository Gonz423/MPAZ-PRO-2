package com.mpaz.progreso.controller;

import com.mpaz.progreso.dto.ResumenProgresoDto;
import com.mpaz.progreso.model.Resultado;
import com.mpaz.progreso.service.ResultadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * POST   /api/resultados                                     -> registrar resultado
 * GET    /api/resultados?estudianteId={id}                   -> historial de estudiante
 * GET    /api/resultados/{id}                                -> por id
 * PUT    /api/resultados/{id}                                -> actualizar
 * DELETE /api/resultados/{id}                                -> eliminar
 * GET    /api/resultados/progreso/{estudianteId}/{materiaId} -> resumen por asignatura
 */
@RestController @RequestMapping("/api/resultados") @CrossOrigin(origins = "*") @RequiredArgsConstructor
public class ResultadoController {
    private final ResultadoService service;

    @PostMapping public ResponseEntity<Resultado> registrar(@RequestBody Resultado r) { return ResponseEntity.ok(service.registrar(r)); }
    @GetMapping  public ResponseEntity<List<Resultado>> listar(@RequestParam Long estudianteId) { return ResponseEntity.ok(service.listarPorEstudiante(estudianteId)); }
    @GetMapping("/{id}")     public ResponseEntity<Resultado> obtener(@PathVariable Long id) { return ResponseEntity.ok(service.obtener(id)); }
    @PutMapping("/{id}")     public ResponseEntity<Resultado> actualizar(@PathVariable Long id, @RequestBody Resultado d) { return ResponseEntity.ok(service.actualizar(id, d)); }
    @DeleteMapping("/{id}")  public ResponseEntity<Void> eliminar(@PathVariable Long id) { service.eliminar(id); return ResponseEntity.noContent().build(); }
    @GetMapping("/progreso/{estudianteId}/{materiaId}")
    public ResponseEntity<ResumenProgresoDto> progreso(@PathVariable Long estudianteId, @PathVariable String materiaId) {
        return ResponseEntity.ok(service.resumenPorMateria(estudianteId, materiaId));
    }
}
