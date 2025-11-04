package com.cloud.jml.utils.token;

import com.cloud.jml.utils.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class GeneratorTokenUtils {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    public GeneratorTokenUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        log.info("🔥 GeneratorTokenUtils inicializado correctamente.");
    }

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String usuario, int roleCode, String roleName, boolean isRefreshToken) {
        log.info("🔐 Generando {} token para el usuario: {}", isRefreshToken ? "refresh" : "access", usuario);

        String jti = UUID.randomUUID().toString();
        long expiration = isRefreshToken ? jwtProperties.getRefreshExpirationMs() : jwtProperties.getExpiration();

        return Jwts.builder()
                .claims()
                .add("usuario", usuario)
                .add("roleCode", roleCode)
                .add("roleName", roleName)
                .add("type", isRefreshToken ? "refresh" : "access")
                .id(jti)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .and()
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("❌ Token inválido: {}", e.getMessage());
            return false;
        }
    }
}
