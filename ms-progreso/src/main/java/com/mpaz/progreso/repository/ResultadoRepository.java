package com.mpaz.progreso.repository;

import com.mpaz.progreso.model.Resultado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResultadoRepository extends JpaRepository<Resultado, Long> {
    List<Resultado> findByEstudianteId(Long estudianteId);
    List<Resultado> findByEstudianteIdAndMateriaId(Long estudianteId, String materiaId);
    Optional<Resultado> findByEstudianteIdAndEjercicioId(Long estudianteId, Long ejercicioId);
}
