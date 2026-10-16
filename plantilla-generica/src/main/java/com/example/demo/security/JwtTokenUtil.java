package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HexFormat;

// Crea y valida los tokens JWT (firmados con la clave jwt.secret de application.properties)
@Component
public class JwtTokenUtil {

    // Clave en HEXADECIMAL (64 caracteres, generar con: openssl rand -hex 32). Si no es hex válido, la app falla al generar el token.
    @Value("${jwt.secret}")
    private String secret;

    // Duración del token: 5 horas (en milisegundos)
    private static final long VALIDITY = 5 * 60 * 60 * 1000L; // 5 horas

    // Convierte el texto hex de jwt.secret en la clave criptográfica
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(HexFormat.of().parseHex(secret));
    }

    // Genera el token con: usuario (subject), fecha de emisión, fecha de expiración y firma
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + VALIDITY))
            .signWith(getKey())
            .compact();
    }

    // Lee el username guardado dentro del token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Token válido = el usuario coincide y no ha expirado (cualquier error -> false)
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername())
                && !extractClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica la firma y devuelve el contenido del token (lanza excepción si la firma es falsa o expiró)
    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build()
            .parseSignedClaims(token).getPayload();
    }
}
