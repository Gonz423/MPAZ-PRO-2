package com.mpaz.progreso.service;

import com.mpaz.progreso.dto.ResumenProgresoDto;
import com.mpaz.progreso.model.Resultado;
import com.mpaz.progreso.repository.ResultadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.OptionalDouble;

@Service @RequiredArgsConstructor
public class ResultadoService {
    private final ResultadoRepository repo;

    public Resultado registrar(Resultado r) {
        repo.findByEstudianteIdAndEjercicioId(r.getEstudianteId(), r.getEjercicioId())
                .ifPresent(e -> { r.setId(e.getId()); r.setIntentos(e.getIntentos() + 1); });
        r.setFechaRealizado(System.currentTimeMillis());
        return repo.save(r);
    }

    public List<Resultado> listarPorEstudiante(Long id) { return repo.findByEstudianteId(id); }
    public Resultado obtener(Long id) { return repo.findById(id).orElseThrow(() -> new RuntimeException("Resultado no encontrado")); }
    public Resultado actualizar(Long id, Resultado d) { Resultado r = obtener(id); r.setNota(d.getNota()); r.setAciertos(d.getAciertos()); return repo.save(r); }
    public void eliminar(Long id)  { obtener(id); repo.deleteById(id); }

    public ResumenProgresoDto resumenPorMateria(Long estudianteId, String materiaId) {
        List<Resultado> res = repo.findByEstudianteIdAndMateriaId(estudianteId, materiaId);
        OptionalDouble promedio = res.stream().mapToInt(Resultado::getNota).average();
        int realizados = res.size();
        return new ResumenProgresoDto(estudianteId, materiaId, promedio.orElse(0.0),
                realizados, 10, Math.min(100, realizados * 10));
    }
}
