package com.mpaz.progreso.exception;

/** El usuario esta autenticado pero no puede acceder a ese recurso (HTTP 403). */
public class AccesoDenegadoException extends RuntimeException {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
