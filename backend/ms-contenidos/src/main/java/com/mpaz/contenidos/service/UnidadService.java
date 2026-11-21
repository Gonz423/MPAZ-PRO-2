package com.mpaz.contenidos.service;

import com.mpaz.contenidos.model.Unidad;
import com.mpaz.contenidos.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class UnidadService {
    private final UnidadRepository repo;
    public List<Unidad> listar()                        { return repo.findAll(); }
    public List<Unidad> listarPorMateria(String m)      { return repo.findByMateriaId(m); }
    public Unidad obtener(Long id)                      { return repo.findById(id).orElseThrow(() -> new RuntimeException("Unidad no encontrada: " + id)); }
    public Unidad crear(Unidad u)                       { return repo.save(u); }
    public Unidad actualizar(Long id, Unidad d) {
        Unidad u = obtener(id);
        u.setTitulo(d.getTitulo()); u.setObjetivoAprendizaje(d.getObjetivoAprendizaje()); u.setNumero(d.getNumero());
        return repo.save(u);
    }
    public void eliminar(Long id)                       { obtener(id); repo.deleteById(id); }
}
