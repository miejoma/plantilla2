package com.example.demo.repository.projection;

import java.math.BigDecimal;

/** PROYECCIÓN: interfaz que recibe las columnas de la consulta nativa. Cada getter = un alias del SELECT. */
public interface ReporteProjection {
    String getNombrePadreA();
    String getCategoriaPadreB();
    BigDecimal getMontoTotal();
}
