package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/** Tabla 'padre B'. Lado "uno" de la relación PadreB 1 - N Principal. */
@Entity
@Table(name = "padre_b")
// Lombok: getters, setters y constructores
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PadreB {
    // PK autoincremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Los tres campos son obligatorios (NOT NULL)
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String ubicacion;
}
