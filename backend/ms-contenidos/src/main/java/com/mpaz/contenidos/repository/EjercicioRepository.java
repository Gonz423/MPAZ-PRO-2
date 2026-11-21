package com.mpaz.contenidos.repository;

import com.mpaz.contenidos.model.Ejercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {
    List<Ejercicio> findByMateriaId(String materiaId);
    List<Ejercicio> findByUnidadId(Long unidadId);
    List<Ejercicio> findByEsTareaTrue();
}
