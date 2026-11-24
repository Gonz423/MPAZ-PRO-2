package com.mpaz.progreso.service;

import com.mpaz.progreso.dto.ResultadoRequest;
import com.mpaz.progreso.dto.ResultadoResponse;
import com.mpaz.progreso.dto.ResumenProgresoDto;
import com.mpaz.progreso.exception.RecursoNoEncontradoException;
import com.mpaz.progreso.mapper.ProgresoMapper;
import com.mpaz.progreso.model.Resultado;
import com.mpaz.progreso.repository.ResultadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultadoService {

    /** Cantidad de ejercicios por asignatura usada para calcular el porcentaje de avance. */
    static final int EJERCICIOS_POR_MATERIA = 10;

    private final ResultadoRepository repo;
    private final Clock clock;

    /**
     * Registra el resultado de un ejercicio. Si el estudiante ya lo habia realizado,
     * se actualiza el mismo registro y se incrementa el numero de intentos.
     */
    @Transactional
    public ResultadoResponse registrar(ResultadoRequest request) {
        Resultado resultado = repo.findByEstudianteIdAndEjercicioId(request.estudianteId(), request.ejercicioId())
                .orElse(null);
        if (resultado == null) {
            resultado = new Resultado();
            resultado.setIntentos(1);
        } else {
            resultado.setIntentos(resultado.getIntentos() + 1);
        }
        ProgresoMapper.copiar(request, resultado);
        resultado.setFechaRealizado(clock.millis());
        return ProgresoMapper.toResponse(repo.save(resultado));
    }

    @Transactional(readOnly = true)
    public List<ResultadoResponse> listar() {
        return repo.findAll().stream().map(ProgresoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ResultadoResponse> listarPorEstudiante(Long estudianteId) {
        return repo.findByEstudianteId(estudianteId).stream().map(ProgresoMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ResultadoResponse obtener(Long id) {
        return ProgresoMapper.toResponse(buscar(id));
    }

    @Transactional
    public ResultadoResponse actualizar(Long id, ResultadoRequest request) {
        Resultado resultado = buscar(id);
        ProgresoMapper.copiar(request, resultado);
        return ProgresoMapper.toResponse(repo.save(resultado));
    }

    @Transactional
    public void eliminar(Long id) {
        repo.delete(buscar(id));
    }

    @Transactional(readOnly = true)
    public ResumenProgresoDto resumenPorMateria(Long estudianteId, String materiaId) {
        List<Resultado> resultados = repo.findByEstudianteIdAndMateriaId(estudianteId, materiaId);
        double promedio = resultados.stream().mapToInt(Resultado::getNota).average().orElse(0.0);
        int realizados = resultados.size();
        int porcentaje = Math.min(100, realizados * 100 / EJERCICIOS_POR_MATERIA);
        return new ResumenProgresoDto(estudianteId, materiaId, promedio, realizados, EJERCICIOS_POR_MATERIA, porcentaje);
    }

    /** Devuelve el resultado o lanza 404; nunca devuelve null. */
    @NonNull
    private Resultado buscar(Long id) {
        if (id == null) {
            throw new RecursoNoEncontradoException("Resultado", "null");
        }
        Resultado resultado = repo.findById(id).orElse(null);
        if (resultado == null) {
            throw new RecursoNoEncontradoException("Resultado", id);
        }
        return resultado;
    }
}
