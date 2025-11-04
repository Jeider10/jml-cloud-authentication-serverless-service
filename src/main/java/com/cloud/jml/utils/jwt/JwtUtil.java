package com.cloud.jml.utils.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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

    // ✅ Extraer claims
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUserName(String token) {
        return getClaims(token).get("usuario", String.class);
    }

    public Integer extractRoleCode(String token) {
        return getClaims(token).get("roleCode", Integer.class);
    }

    public String extractRoleName(String token) {
        return getClaims(token).get("roleName", String.class);
    }

    public String extractJti(String token) {
        return getClaims(token).get("jti", String.class);
    }

    // ✅ Extraer un claim específico por clave
    public String extractClaim(String token, String claimKey) {
        Claims claims = parseClaims(token);
        return claims.get(claimKey, String.class);
    }

    // ✅ Parsear claims de un token
    public Claims parseClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parser()
                .verifyWith(key)   // Usa la clave HMAC
                .build()
                .parseSignedClaims(token);

        return claimsJws.getPayload();
    }
}
