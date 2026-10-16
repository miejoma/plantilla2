package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de SALIDA (lo que se devuelve al cliente). Se aplana la información de las relaciones
 * (nombre del padre A, nombre y categoría del padre B) para no exponer las entidades JPA directamente.
 */
public record PrincipalResponse(
        Long id,
        Integer cantidad,
        LocalDate fecha,
        BigDecimal monto,
        Boolean estado,
        Long padreAId,
        String padreANombre,
        Long padreBId,
        String padreBNombre,
        String categoriaPadreB
) {
}
