package com.example.demo.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Manejador global de errores: convierte excepciones en respuestas JSON ordenadas.
 * ⚠ IMPORTANTE (Spring Security): NO agregues aquí un @ExceptionHandler(Exception.class) ni (RuntimeException.class).
 * Si lo haces, se tragaría el 403 (AccessDeniedException) y el 401 (AuthenticationException) y ya no responderían bien.
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    // Id inexistente -> 404 Not Found
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException e) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // Falla @Valid en el DTO (campo faltante, negativo...) -> 400 Bad Request con el detalle de cada campo
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // Arma el JSON de error: timestamp, status, error y message
    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of(
                "timestamp", Instant.now(),
                "status", estado.value(),
                "error", estado.getReasonPhrase(),
                "message", mensaje
        ));
    }
}
