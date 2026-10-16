package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Tabla 'principal'. Es la tabla "del medio": cada registro principal pertenece a UN padre A y a UN padre B
 * (dos llaves foráneas). Es la entidad principal sobre la que se hace el CRUD y el reporte.
 */
@Entity
@Table(name = "principal")
// Lombok: getters, setters y constructores
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Principal {
    // PK autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cantidad (unidades, horas, piezas...) del registro (obligatorio)
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private LocalDate fecha;

    // BigDecimal para dinero (no usar Double). precision 10 = dígitos totales, scale 2 = decimales
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private Boolean estado;

    // FK hacia PadreA: muchos registros principales -> un padre A.
    // LAZY = el padre A se carga solo cuando se usa; optional=false/nullable=false = la FK es obligatoria.
    // Crea la columna 'padre_a_id' en la tabel registro principal.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "padre_a_id", nullable = false)
    private PadreA padreA;

    // FK hacia PadreB (misma idea). Crea la columna 'padre_b_id'.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "padre_b_id", nullable = false)
    private PadreB padreB;
}
