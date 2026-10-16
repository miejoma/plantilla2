package com.example.demo.security;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

// Entidad de la tabla 'users' (se llama users porque USER es palabra reservada en PostgreSQL)
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nombre de usuario (login): único y obligatorio
    @Column(unique = true, nullable = false)
    private String username;

    // Guarda el HASH BCrypt, nunca la contraseña en texto plano
    @Column(nullable = false)
    private String password;

    // Si es false, el usuario no puede ingresar
    private Boolean enabled = true;

    // Relación muchos a muchos con Role mediante la tabla intermedia user_role (user_id, role_id).
    // EAGER: los roles se cargan junto con el usuario (el filtro JWT los necesita fuera de una transacción).
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
