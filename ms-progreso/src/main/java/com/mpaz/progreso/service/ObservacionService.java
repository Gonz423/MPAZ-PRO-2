package com.mpaz.progreso.service;

import com.mpaz.progreso.dto.ObservacionRequest;
import com.mpaz.progreso.dto.ObservacionResponse;
import com.mpaz.progreso.exception.RecursoNoEncontradoException;
import com.mpaz.progreso.mapper.ProgresoMapper;
import com.mpaz.progreso.model.Observacion;
import com.mpaz.progreso.repository.ObservacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ObservacionService {

    private final ObservacionRepository repo;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<ObservacionResponse> listar() {
        return mapear(repo.findAll());
    }

    @Transactional(readOnly = true)
    public List<ObservacionResponse> listarPorEstudiante(Long estudianteId) {
        return mapear(repo.findByEstudianteId(estudianteId));
    }

    @Transactional(readOnly = true)
    public List<ObservacionResponse> listarPorDocente(Long docenteId) {
        return mapear(repo.findByDocenteId(docenteId));
    }

    @Transactional(readOnly = true)
    public ObservacionResponse obtener(Long id) {
        return ProgresoMapper.toResponse(buscar(id));
    }

    @Transactional
    public ObservacionResponse crear(ObservacionRequest request) {
        Observacion o = ProgresoMapper.toEntity(request);
        o.setFecha(clock.millis());
        return ProgresoMapper.toResponse(repo.save(o));
    }

    /** Solo el contenido de una observacion es editable; autor, estudiante y fecha se conservan. */
    @Transactional
    public ObservacionResponse actualizar(Long id, ObservacionRequest request) {
        Observacion o = buscar(id);
        o.setContenido(request.contenido());
        return ProgresoMapper.toResponse(repo.save(o));
    }

    @Transactional
    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    /** Devuelve la observacion o lanza 404; nunca devuelve null. */
    @NonNull
    private Observacion buscar(Long id) {
        if (id == null) {
            throw new RecursoNoEncontradoException("Observacion", "null");
        }
        Observacion observacion = repo.findById(id).orElse(null);
        if (observacion == null) {
            throw new RecursoNoEncontradoException("Observacion", id);
        }
        return observacion;
    }

    private static List<ObservacionResponse> mapear(List<Observacion> lista) {
        return lista.stream().map(ProgresoMapper::toResponse).toList();
    }
}
