package com.cloud.jml.utils.token;

import com.cloud.jml.utils.jwt.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class GeneratorTokenUtils {

    private static final String USUARIO = "usuario";
    private static final String TOKEN_USE = "token_use";
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

    public String generateToken(String usuario, int roleCode, String roleName, String tokenUse) {
        log.info("🔐 Generando token [{}] para usuario: {}", tokenUse.toUpperCase(), usuario);

        String jti = UUID.randomUUID().toString();
        long expiration = "refreshToken".equals(tokenUse)
                ? jwtProperties.getRefreshExpirationMs()
                : jwtProperties.getExpiration();

        var builder = Jwts.builder()
                .claims()
                .add("sub", usuario)
                .add(USUARIO, usuario)
                .add("roleCode", roleCode)
                .add("roleName", roleName)
                .id(jti)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration));

        // 🔸 Claims especificos por tipo de token
        switch (tokenUse) {
            case "accessToken" -> {
                builder.add("auth_time", Instant.now().getEpochSecond())
                        .add(TOKEN_USE, tokenUse);
                log.info("🧩 Claims anadidos para access token (roles y auth_time).");
            }
            case "authorization" -> {
                builder.add("name", usuario)
                        .add("email_verified", true)
                        .add("phone_number_verified", false)
                        .add("network_userName", usuario)
                        .add("auth_time", Instant.now().getEpochSecond())
                        .add(TOKEN_USE, tokenUse);
                log.info("🧩 Claims anadidos para authorization token (perfil del usuario).");
            }
            case "refreshToken" -> {
                builder.add("name", usuario)
                        .add(TOKEN_USE, tokenUse);
                log.info("🧩 Claims minimos anadidos para refresh token.");
            }
            // 🚨 Default: tipo de token desconocido = error explicito
            default -> {
                String errorMsg = "Tipo de token desconocido: '" + tokenUse + "'";
                log.error("❌ {}", errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
        }

        // ✅ Importante: cerrar la seccion de claims con `.and()`
        String token = builder
                .and()
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        log.info("✅ Token '{}' generado exitosamente. jti={}", tokenUse, jti);

        return token;
    }

    public void validateToken(String token) {
        try {
            // 🧩 Parsear y validar firma del token
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = claimsJws.getPayload();

            // ⏰ Verificar expiracion manualmente (aunque JJWT ya lo valida)
            if (claims.getExpiration().before(new Date())) {
                String msg = "Token expirado para el usuario: " + claims.get(USUARIO);
                log.warn("⏰ {}", msg);
                throw new ExpiredJwtException(null, claims, msg);
            }

            log.info("✅ Token valido para el usuario: {}", claims.get(USUARIO));

        } catch (ExpiredJwtException e) {
            log.warn("⏰ Token expirado: {}", e.getMessage());
            throw e; // ⚠️ Propaga la excepcion real (sera manejada como 401)
        } catch (JwtException | IllegalArgumentException e) {
            log.error("❌ Token invalido o corrupto: {}", e.getMessage());
            throw new JwtException("Token invalido o corrupto", e);
        }
    }
}
