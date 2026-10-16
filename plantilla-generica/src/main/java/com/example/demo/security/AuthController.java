package com.example.demo.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// HU de autenticación: genera el token. Ruta PÚBLICA: POST /ingresos
@RestController
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUserDetailsService uds;
    private final JwtTokenUtil jwt;

    public AuthController(AuthenticationManager am, JwtUserDetailsService uds, JwtTokenUtil jwt) {
        this.authManager = am;
        this.uds = uds;
        this.jwt = jwt;
    }

    // Body: {"username":"admin1","password":"12345"}  ->  Respuesta: {"token":"eyJ..."}
    // authenticate() verifica usuario y contraseña (BCrypt); si fallan -> 401 automáticamente
    @PostMapping("/ingresos")
    public Map<String, String> login(@RequestBody Map<String, String> body) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(
            body.get("username"), body.get("password")));
        UserDetails ud = uds.loadUserByUsername(body.get("username"));
        return Map.of("token", jwt.generateToken(ud));
    }
}
