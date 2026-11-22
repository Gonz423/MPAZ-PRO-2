package com.mpaz.contenidos.repository;

import com.mpaz.contenidos.model.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {
    List<Unidad> findByMateriaId(String materiaId);
}
