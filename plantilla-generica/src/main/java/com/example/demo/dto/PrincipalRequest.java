package com.example.demo.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de ENTRADA (lo que el cliente envía en el JSON del POST y del PUT).
 * 'record' = clase inmutable compacta (Java 16+). Las anotaciones validan el JSON; si fallan -> 400 Bad Request
 * (lo traduce ApiExceptionHandler).
 *   @NotNull  -> el campo no puede faltar        @Positive -> debe ser mayor a 0
 * Las FK llegan como ids (padreAId, padreBId), NO como objetos completos.
 */
public record PrincipalRequest(
        @NotNull @Positive Integer cantidad,
        @NotNull LocalDate fecha,
        @NotNull @Positive BigDecimal monto,
        @NotNull Boolean estado,
        @NotNull @Positive Long padreAId,
        @NotNull @Positive Long padreBId
) {
}
