package com.mpaz.contenidos.service;

import com.mpaz.contenidos.dto.UnidadRequest;
import com.mpaz.contenidos.dto.UnidadResponse;
import com.mpaz.contenidos.exception.RecursoNoEncontradoException;
import com.mpaz.contenidos.mapper.UnidadMapper;
import com.mpaz.contenidos.model.Unidad;
import com.mpaz.contenidos.repository.UnidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadService {

    private final UnidadRepository repo;

    @Transactional(readOnly = true)
    public List<UnidadResponse> listar() {
        return repo.findAll().stream().map(UnidadMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<UnidadResponse> listarPorMateria(String materiaId) {
        return repo.findByMateriaId(materiaId).stream().map(UnidadMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UnidadResponse obtener(Long id) {
        return UnidadMapper.toResponse(buscar(id));
    }

    @Transactional
    public UnidadResponse crear(UnidadRequest request) {
        return UnidadMapper.toResponse(repo.save(UnidadMapper.toEntity(request)));
    }

    @Transactional
    public UnidadResponse actualizar(Long id, UnidadRequest request) {
        Unidad unidad = buscar(id);
        UnidadMapper.copiar(request, unidad);
        return UnidadMapper.toResponse(repo.save(unidad));
    }

    @Transactional
    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    /** Devuelve la entidad o lanza 404; nunca devuelve null. */
    @NonNull
    private Unidad buscar(Long id) {
        if (id == null) {
            throw new RecursoNoEncontradoException("Unidad", "null");
        }
        Unidad unidad = repo.findById(id).orElse(null);
        if (unidad == null) {
            throw new RecursoNoEncontradoException("Unidad", id);
        }
        return unidad;
    }
}
