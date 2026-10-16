package com.example.demo.security;

import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a la tabla users. findByUsername: lo usan el login y el filtro JWT para cargar al usuario
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
