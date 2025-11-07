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

    public <T> T extractClaimValue(String token, String claimKey, Class<T> type) {
        try {
            return extractAllClaims(token).get(claimKey, type);
        } catch (ExpiredJwtException e) {
            log.warn("⚠️ Token expirado: {}", e.getMessage());
            throw new AuthenticationTokenValidationException("Token expirado", e);
        } catch (JwtException e) {
            log.error("❌ Error al extraer claim '{}': {}", claimKey, e.getMessage());
            throw new AuthenticationTokenValidationException("Token inválido", e);
        }
    }

    public String extractUserName(String token) {
        return extractClaimValue(token, "usuario", String.class);
    }

    public Integer extractRoleCode(String token) {
        return extractClaimValue(token, "roleCode", Integer.class);
    }

    public String extractRoleName(String token) {
        return extractClaimValue(token, "roleName", String.class);
    }

    public String extractJti(String token) {
        return extractClaimValue(token, "jti", String.class);
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
