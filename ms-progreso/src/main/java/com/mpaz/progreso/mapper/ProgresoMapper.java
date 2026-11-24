package com.mpaz.progreso.mapper;

import com.mpaz.progreso.dto.ObservacionRequest;
import com.mpaz.progreso.dto.ObservacionResponse;
import com.mpaz.progreso.dto.ResultadoRequest;
import com.mpaz.progreso.dto.ResultadoResponse;
import com.mpaz.progreso.model.Observacion;
import com.mpaz.progreso.model.Resultado;
import org.springframework.lang.NonNull;

/** Conversion entre las entidades de progreso y sus DTOs. */
public final class ProgresoMapper {

    private ProgresoMapper() {}

    public static ResultadoResponse toResponse(Resultado r) {
        return new ResultadoResponse(r.getId(), r.getEstudianteId(), r.getEjercicioId(), r.getNota(),
                r.getAciertos(), r.getTotalPreguntas(), r.getIntentos(), r.getMateriaId(), r.getFechaRealizado());
    }

    /** Copia los datos calificables del request; los opcionales nulos toman su valor por defecto. */
    public static void copiar(ResultadoRequest req, Resultado r) {
        r.setEstudianteId(req.estudianteId());
        r.setEjercicioId(req.ejercicioId());
        r.setNota(req.nota());
        r.setAciertos(req.aciertos() != null ? req.aciertos() : 0);
        r.setTotalPreguntas(req.totalPreguntas() != null ? req.totalPreguntas() : 1);
        r.setMateriaId(req.materiaId());
    }

    public static ObservacionResponse toResponse(Observacion o) {
        return new ObservacionResponse(o.getId(), o.getDocenteId(), o.getEstudianteId(), o.getContenido(), o.getFecha());
    }

    @NonNull
    public static Observacion toEntity(ObservacionRequest req) {
        Observacion o = new Observacion();
        o.setDocenteId(req.docenteId());
        o.setEstudianteId(req.estudianteId());
        o.setContenido(req.contenido());
        return o;
    }
}
