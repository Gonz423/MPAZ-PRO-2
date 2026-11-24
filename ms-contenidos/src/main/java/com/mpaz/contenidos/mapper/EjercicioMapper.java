package com.mpaz.contenidos.mapper;

import com.mpaz.contenidos.dto.EjercicioRequest;
import com.mpaz.contenidos.dto.EjercicioResponse;
import com.mpaz.contenidos.model.Ejercicio;
import org.springframework.lang.NonNull;

/** Conversion entre la entidad Ejercicio y sus DTOs. */
public final class EjercicioMapper {

    private EjercicioMapper() {}

    public static EjercicioResponse toResponse(Ejercicio e) {
        return new EjercicioResponse(
                e.getId(), e.getMateriaId(), e.getUnidadId(), e.getTitulo(), e.getObjetivoAprendizaje(),
                e.getCodigoGuia(), e.getPregunta(), e.getOpciones(), e.getRespuestaCorrectaIndex(),
                e.getDisponible(), e.getEsTarea(), e.getFechaEntrega());
    }

    @NonNull
    public static Ejercicio toEntity(EjercicioRequest r) {
        Ejercicio e = new Ejercicio();
        copiar(r, e);
        return e;
    }

    /** Copia los campos del request; los opcionales nulos toman su valor por defecto. */
    public static void copiar(EjercicioRequest r, Ejercicio e) {
        e.setMateriaId(r.materiaId());
        e.setUnidadId(r.unidadId());
        e.setTitulo(r.titulo());
        e.setObjetivoAprendizaje(r.objetivoAprendizaje());
        e.setCodigoGuia(r.codigoGuia());
        e.setPregunta(r.pregunta());
        e.setOpciones(r.opciones());
        e.setRespuestaCorrectaIndex(r.respuestaCorrectaIndex() != null ? r.respuestaCorrectaIndex() : 0);
        e.setDisponible(r.disponible() != null ? r.disponible() : Boolean.TRUE);
        e.setEsTarea(r.esTarea() != null ? r.esTarea() : Boolean.FALSE);
        e.setFechaEntrega(r.fechaEntrega());
    }
}
