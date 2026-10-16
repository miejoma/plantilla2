package com.example.demo.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

// Le enseña a Spring Security a cargar usuarios desde NUESTRA base de datos
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public JwtUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Busca al usuario y lo convierte al formato de Spring Security.
    // A cada rol se le antepone ROLE_ para que hasRole('X') / hasAnyRole('X') funcionen.
    @Override
    public UserDetails loadUserByUsername(String username) {
        User u = userRepo.findByUsername(username);
        if (u == null) throw new UsernameNotFoundException("Usuario no existe: " + username);
        return new org.springframework.security.core.userdetails.User(
            u.getUsername(),
            u.getPassword(),
            u.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
                .collect(Collectors.toList())
        );
    }
}
