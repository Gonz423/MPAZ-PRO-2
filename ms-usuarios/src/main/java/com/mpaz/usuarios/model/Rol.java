package com.mpaz.usuarios.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Rol {
    ESTUDIANTE,
    DOCENTE,
    UTP;

    @JsonCreator
    public static Rol fromString(String value) {
        if (value == null || value.isBlank()) {
            return ESTUDIANTE;
        }
        for (Rol r : Rol.values()) {
            if (r.name().equalsIgnoreCase(value.trim())) {
                return r;
            }
        }
        throw new IllegalArgumentException("Rol no válido: " + value + ". Roles permitidos: ESTUDIANTE, DOCENTE, UTP");
    }
}
