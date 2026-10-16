package com.example.demo.exception;

/** Excepción propia: se lanza cuando un id no existe (registro principal, padre A o padre B). Se traduce a 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
