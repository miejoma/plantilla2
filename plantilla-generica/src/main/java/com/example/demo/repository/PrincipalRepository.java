package com.example.demo.repository;

import com.example.demo.entity.Principal;
import com.example.demo.repository.projection.ReporteProjection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio = acceso a datos. Al extender JpaRepository ya se tienen sin escribir código:
 * save, findAll, findById, existsById, deleteById, count...
 */
public interface PrincipalRepository extends JpaRepository<Principal, Long> {
    // REPORTE (consulta nativa SQL): agrupa por padre A y categoría de padre B y suma cantidad*monto.
    // OJO: en nativeQuery se usan nombres de TABLAS/COLUMNAS (padre_a_id), no de clases/atributos.
    // Los alias (AS nombrePadreA...) deben coincidir con los getters de ReporteProjection (getNombrePadreA...).
    // Para otras variantes de reporte ver MODIFICACIONES.md (sección 3).
    @Query(value = """
            SELECT a.nombre AS nombrePadreA, t.categoria AS categoriaPadreB,
                   SUM(o.cantidad * o.monto) AS montoTotal
            FROM principal o
            INNER JOIN padre_a a ON a.id = o.padre_a_id
            INNER JOIN padre_b t ON t.id = o.padre_b_id
            GROUP BY a.nombre, t.categoria
            ORDER BY a.nombre, t.categoria
            """, nativeQuery = true)
    List<ReporteProjection> obtenerReporteAgrupado();
}
