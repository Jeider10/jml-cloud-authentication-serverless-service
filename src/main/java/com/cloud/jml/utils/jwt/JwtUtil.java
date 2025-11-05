package com.cloud.jml.utils.jwt;

import com.cloud.jml.exception.authentication.AuthenticationTokenValidationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtUtil {

    private static final String TOKEN_EXPIRADO = "⚠️ Token expirado: {}";
    private final JwtProperties jwtProperties;
    private SecretKey key;

    public JwtUtil(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        log.info("🔥 JwtUtil inicializado correctamente.");
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        try {
            return extractAllClaims(token).get("usuario", String.class);
        } catch (ExpiredJwtException e) {
            log.warn(TOKEN_EXPIRADO, e.getMessage());
            throw new AuthenticationTokenValidationException("Token expirado", e);
        }
    }

    public Integer extractRoleCode(String token) {
        try {
            return extractAllClaims(token).get("roleCode", Integer.class);
        } catch (ExpiredJwtException e) {
            log.warn(TOKEN_EXPIRADO, e.getMessage());
            throw new AuthenticationTokenValidationException("Token expirado", e);
        }
    }

    public String extractRoleName(String token) {
        try {
            return extractAllClaims(token).get("roleName", String.class);
        } catch (ExpiredJwtException e) {
            log.warn(TOKEN_EXPIRADO, e.getMessage());
            throw new AuthenticationTokenValidationException("Token expirado", e);
        }
    }

    public String extractJti(String token) {
        try {
            return extractAllClaims(token).get("jti", String.class);
        } catch (ExpiredJwtException e) {
            log.warn(TOKEN_EXPIRADO, e.getMessage());
            throw new AuthenticationTokenValidationException("Token expirado", e);
        }
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key) // la clave con la que firmaste el token
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.error("❌ Error al extraer claims del token: {}", e.getMessage());
            throw new AuthenticationTokenValidationException("Token inválido", e);
        }
    }

    public String extractClaim(String token, String claimKey) {
        Claims claims = extractAllClaims(token);
        return claims.get(claimKey, String.class);
    }
}
