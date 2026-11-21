package com.mpaz.progreso.service;

import com.mpaz.progreso.model.Observacion;
import com.mpaz.progreso.repository.ObservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class ObservacionService {
    private final ObservacionRepository repo;
    public List<Observacion> listarPorEstudiante(Long id) { return repo.findByEstudianteId(id); }
    public List<Observacion> listarPorDocente(Long id)    { return repo.findByDocenteId(id); }
    public Observacion obtener(Long id)                   { return repo.findById(id).orElseThrow(() -> new RuntimeException("Observacion no encontrada")); }
    public Observacion crear(Observacion o)               { o.setFecha(System.currentTimeMillis()); return repo.save(o); }
    public Observacion actualizar(Long id, Observacion d) { Observacion o = obtener(id); o.setContenido(d.getContenido()); return repo.save(o); }
    public void eliminar(Long id)                         { obtener(id); repo.deleteById(id); }
}
