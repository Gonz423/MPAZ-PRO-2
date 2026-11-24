package com.mpaz.progreso.exception;

/** Se lanza cuando un recurso solicitado no existe (HTTP 404). */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " no encontrado/a con id " + id);
    }
}
