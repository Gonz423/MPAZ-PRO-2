package com.mpaz.contenidos.service;

import com.mpaz.contenidos.model.Ejercicio;
import com.mpaz.contenidos.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class EjercicioService {
    private final EjercicioRepository repo;
    public List<Ejercicio> listar()                      { return repo.findAll(); }
    public List<Ejercicio> listarPorUnidad(Long id)      { return repo.findByUnidadId(id); }
    public List<Ejercicio> listarPorMateria(String m)    { return repo.findByMateriaId(m); }
    public List<Ejercicio> listarTareas()                { return repo.findByEsTareaTrue(); }
    public Ejercicio obtener(Long id)                    { return repo.findById(id).orElseThrow(() -> new RuntimeException("Ejercicio no encontrado: " + id)); }
    public Ejercicio crear(Ejercicio e)                  { return repo.save(e); }
    public Ejercicio actualizar(Long id, Ejercicio d) {
        Ejercicio e = obtener(id);
        e.setTitulo(d.getTitulo()); e.setObjetivoAprendizaje(d.getObjetivoAprendizaje());
        e.setPregunta(d.getPregunta()); e.setOpciones(d.getOpciones());
        e.setRespuestaCorrectaIndex(d.getRespuestaCorrectaIndex());
        e.setDisponible(d.getDisponible()); e.setEsTarea(d.getEsTarea()); e.setFechaEntrega(d.getFechaEntrega());
        return repo.save(e);
    }
    public void eliminar(Long id)                       { obtener(id); repo.deleteById(id); }
}
