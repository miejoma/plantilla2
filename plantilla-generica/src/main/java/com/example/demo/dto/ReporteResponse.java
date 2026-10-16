package com.example.demo.dto;

import java.math.BigDecimal;

/** DTO de SALIDA del reporte agregado: una fila por (padre A, categoría) con la suma cantidad*monto. */
public record ReporteResponse(
        String nombrePadreA,
        String categoriaPadreB,
        BigDecimal montoTotal
) {
}
