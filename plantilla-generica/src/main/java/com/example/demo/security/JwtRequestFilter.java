package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filtro que corre en CADA petición: lee el header "Authorization: Bearer <token>" y, si es válido, deja autenticado al usuario
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUserDetailsService uds;
    private final JwtTokenUtil jwt;

    public JwtRequestFilter(JwtUserDetailsService uds, JwtTokenUtil jwt) {
        this.uds = uds;
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp,
                                    FilterChain chain) throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            // substring(7) quita el texto "Bearer "
            String token = header.substring(7);
            try {
                String username = jwt.extractUsername(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails ud = uds.loadUserByUsername(username);
                    // Token OK: se registra al usuario con sus roles como autenticado en esta petición
                    if (jwt.validateToken(token, ud)) {
                        UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            // (si el token es inválido no se autentica; luego el EntryPoint responde 401)
            } catch (Exception e) {
                // token invalido o expirado · seguimos sin autenticacion
            }
        }
        chain.doFilter(req, resp);
    }
}
