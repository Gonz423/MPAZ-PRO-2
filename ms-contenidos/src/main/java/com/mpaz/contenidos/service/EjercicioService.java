package com.mpaz.contenidos.service;

import com.mpaz.contenidos.dto.EjercicioRequest;
import com.mpaz.contenidos.dto.EjercicioResponse;
import com.mpaz.contenidos.exception.RecursoNoEncontradoException;
import com.mpaz.contenidos.mapper.EjercicioMapper;
import com.mpaz.contenidos.model.Ejercicio;
import com.mpaz.contenidos.repository.EjercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EjercicioService {

    private final EjercicioRepository repo;

    @Transactional(readOnly = true)
    public List<EjercicioResponse> listar() {
        return mapear(repo.findAll());
    }

    @Transactional(readOnly = true)
    public List<EjercicioResponse> listarPorUnidad(Long unidadId) {
        return mapear(repo.findByUnidadId(unidadId));
    }

    @Transactional(readOnly = true)
    public List<EjercicioResponse> listarPorMateria(String materiaId) {
        return mapear(repo.findByMateriaId(materiaId));
    }

    @Transactional(readOnly = true)
    public List<EjercicioResponse> listarTareas() {
        return mapear(repo.findByEsTareaTrue());
    }

    @Transactional(readOnly = true)
    public EjercicioResponse obtener(Long id) {
        return EjercicioMapper.toResponse(buscar(id));
    }

    @Transactional
    public EjercicioResponse crear(EjercicioRequest request) {
        return EjercicioMapper.toResponse(repo.save(EjercicioMapper.toEntity(request)));
    }

    @Transactional
    public EjercicioResponse actualizar(Long id, EjercicioRequest request) {
        Ejercicio ejercicio = buscar(id);
        EjercicioMapper.copiar(request, ejercicio);
        return EjercicioMapper.toResponse(repo.save(ejercicio));
    }

    @Transactional
    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    /** Devuelve la entidad o lanza 404; nunca devuelve null. */
    @NonNull
    private Ejercicio buscar(Long id) {
        if (id == null) {
            throw new RecursoNoEncontradoException("Ejercicio", "null");
        }
        Ejercicio ejercicio = repo.findById(id).orElse(null);
        if (ejercicio == null) {
            throw new RecursoNoEncontradoException("Ejercicio", id);
        }
        return ejercicio;
    }

    private static List<EjercicioResponse> mapear(List<Ejercicio> lista) {
        return lista.stream().map(EjercicioMapper::toResponse).toList();
    }
}
