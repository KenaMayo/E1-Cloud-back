package com.tienda.chat.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    public record TokenInfo(String username, String rol) {}

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET debe tener al menos 32 caracteres");
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public Optional<TokenInfo> validar(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            String username = claims.getSubject();
            String rol = claims.get("rol", String.class);
            if (username == null || rol == null || (!rol.equals("USER") && !rol.equals("ADMIN"))) {
                return Optional.empty();
            }
            return Optional.of(new TokenInfo(username, rol));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
