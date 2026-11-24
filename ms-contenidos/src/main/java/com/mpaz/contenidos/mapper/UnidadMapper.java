package com.mpaz.contenidos.mapper;

import com.mpaz.contenidos.dto.UnidadRequest;
import com.mpaz.contenidos.dto.UnidadResponse;
import com.mpaz.contenidos.model.Unidad;
import org.springframework.lang.NonNull;

/** Conversion entre la entidad Unidad y sus DTOs. */
public final class UnidadMapper {

    private UnidadMapper() {}

    public static UnidadResponse toResponse(Unidad u) {
        return new UnidadResponse(u.getId(), u.getMateriaId(), u.getNumero(), u.getTitulo(), u.getObjetivoAprendizaje());
    }

    @NonNull
    public static Unidad toEntity(UnidadRequest r) {
        Unidad u = new Unidad();
        copiar(r, u);
        return u;
    }

    public static void copiar(UnidadRequest r, Unidad u) {
        u.setMateriaId(r.materiaId());
        u.setNumero(r.numero());
        u.setTitulo(r.titulo());
        u.setObjetivoAprendizaje(r.objetivoAprendizaje());
    }
}
