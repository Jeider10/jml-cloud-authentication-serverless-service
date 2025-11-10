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

    public String extractUserName(String token) {
        log.info("🔑 Extrayendo nombre de usuario del token JWT.");

        String usuario = extractClaimValue(token, "usuario", String.class);
        log.info("✅ Nombre de usuario extraído correctamente: {}", usuario);

        return usuario;
    }

    public Integer extractRoleCode(String token) {
        log.info("🔑 Extrayendo roleCode del token JWT.");

        Integer roleCode = extractClaimValue(token, "roleCode", Integer.class);
        log.info("✅ roleCode extraído correctamente: {}", roleCode);

        return roleCode;
    }

    public String extractRoleName(String token) {
        log.info("🔑 Extrayendo roleName del token JWT.");

        String roleName = extractClaimValue(token, "roleName", String.class);
        log.info("✅ roleName extraído correctamente: {}", roleName);

        return roleName;
    }

    public String extractJti(String token) {
        log.info("🔑 Extrayendo JTI del token JWT.");

        String jti = extractClaimValue(token, "jti", String.class);
        log.info("✅ JTI extraído correctamente: {}", jti);

        return jti;
    }

    public <T> T extractClaimValue(String token, String claimKey, Class<T> type) {
        log.info("🔑 Extrayendo claim '{}' del token JWT.", claimKey);
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

    public Claims extractAllClaims(String token) {
        log.info("🔑 Extrayendo claims del token JWT.");

        Claims claims = Jwts.parser()
                .verifyWith(key) // la clave con la que firmaste el token
                .build()
                .parseSignedClaims(token)
                .getPayload();

        log.info("✅ Claims extraídos correctamente.");

        return claims;
    }
}
