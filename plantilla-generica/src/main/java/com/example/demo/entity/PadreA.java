package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ENTIDAD = una tabla de la BD. Esta clase crea la tabla 'padreA' (Hibernate la genera sola
 * por spring.jpa.hibernate.ddl-auto=update). Es el lado "uno" de la relación PadreA 1 - N Principal.
 */
@Entity
@Table(name = "padre_a")
// Lombok: genera getters, setters, constructor vacío y constructor con todos los campos
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PadreA {
    // Llave primaria autoincremental (serial/identity en PostgreSQL)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false  ->  columna NOT NULL (obligatoria)
    @Column(nullable = false)
    private String nombre;

    // NOT NULL + UNIQUE: obligatorio y no se puede repetir (si se repite: error de clave duplicada)
    @Column(nullable = false, unique = true)
    private String email;

    // Sin @Column: opcional (acepta null)
    private String telefono;
}
