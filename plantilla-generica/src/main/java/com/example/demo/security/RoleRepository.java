package com.example.demo.security;

import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a la tabla roles. findByName: buscar un rol por su nombre
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
