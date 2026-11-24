package com.mpaz.progreso.repository;

import com.mpaz.progreso.model.Observacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ObservacionRepository extends JpaRepository<Observacion, Long> {
    List<Observacion> findByEstudianteId(Long estudianteId);
    List<Observacion> findByDocenteId(Long docenteId);
}
