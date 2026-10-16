package com.example.demo.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// Entidad de la tabla 'roles' (ADMIN, USUARIO...). El nombre va SIN el prefijo ROLE_.
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre del rol; único y obligatorio. Debe coincidir con el usado en @PreAuthorize
    @Column(unique = true, nullable = false)
    private String name;
}
