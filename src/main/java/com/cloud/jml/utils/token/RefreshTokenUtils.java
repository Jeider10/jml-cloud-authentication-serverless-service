package com.cloud.jml.utils.token;

import com.cloud.jml.exception.authentication.AuthenticationRefreshTokenDeletionException;
import com.cloud.jml.exception.authentication.AuthenticationRefreshTokenPersistenceException;
import com.cloud.jml.exception.authentication.AuthenticationTokenValidationException;
import com.cloud.jml.model.token.RefreshTokenEntity;
import com.cloud.jml.repository.token.RefreshTokenRepository;
import com.cloud.jml.utils.jwt.JwtProperties;
import com.cloud.jml.utils.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class RefreshTokenUtils {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final GeneratorTokenUtils generatorTokenUtils;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenUtils(JwtUtil jwtUtil, JwtProperties jwtProperties, GeneratorTokenUtils generatorTokenUtils, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.jwtProperties = jwtProperties;
        this.generatorTokenUtils = generatorTokenUtils;
        this.refreshTokenRepository = refreshTokenRepository;
        log.info("🔥 RefreshTokenUtils inicializado correctamente.");
    }

    public String generarRefreshToken(String usuario, int roleCode, String roleName) {
        log.info("🔐 Generando refresh token JWT para el usuario: {}", usuario);

        String token = generatorTokenUtils.generateToken(usuario, roleCode, roleName, "refreshToken");
        String jti = jwtUtil.extractJti(token);

        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .token(token)
                .jti(jti)
                .usuario(usuario)
                .roleCode(roleCode)
                .roleName(roleName)
                .expiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpirationMs()))
                .revoked(false)
                .fechaCreacion(LocalDateTime.now())
                .build();

        RefreshTokenEntity guardadoRefreshToken = guardarRefreshTokenBD(refreshTokenEntity);
        log.info("📦 [PERSISTENCIA] Refresh token generado y guardado correctamente para usuario: {}", guardadoRefreshToken.getUsuario());

        String refreshToken = guardadoRefreshToken.getToken();
        log.info("✅ [FINALIZADO] Refresh token generado correctamente para usuario: {}", guardadoRefreshToken.getUsuario());

        return refreshToken;
    }

    public RefreshTokenEntity verifyExpiration(String token) {
        log.info("🔍 [UTILS] Verificando validez y expiración del token de refresco...");

        Optional<RefreshTokenEntity> optionalToken = refreshTokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            log.error("❌ [ERROR] Token de refresco no encontrado en BD. token={}", token);
            throw new AuthenticationTokenValidationException("Token de refresco no encontrado.");
        }

        RefreshTokenEntity refreshToken = optionalToken.get();

        if (refreshToken.isRevoked()) {
            log.warn("⚠️ [TOKEN] Token revocado detectado. jti={}", refreshToken.getJti());
            throw new AuthenticationTokenValidationException("El token de refresco ha sido revocado.");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("⚠️ [TOKEN] Token expirado detectado. jti={} | Eliminando registro.", refreshToken.getJti());
            eliminarRefreshTokenBD(refreshToken);
            throw new AuthenticationTokenValidationException("El token de refresco ha expirado.");
        }

        log.info("✅ [TOKEN] Token válido y vigente. jti={}", refreshToken.getJti());

        return refreshToken;
    }

    public void revokeToken(String token) {
        log.info("🔍 [UTILS] Iniciando revocación del refresh token...");

        Optional<RefreshTokenEntity> optionalToken = refreshTokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            log.error("❌ [ERROR] No se encontró el refresh token especificado. token={}", token);
            throw new AuthenticationTokenValidationException("Token de refresco no encontrado.");
        }

        RefreshTokenEntity refreshToken = optionalToken.get();
        refreshToken.setRevoked(true);
        log.info("🔐 Token de refresco con jti={} revocado.", refreshToken.getJti());

        guardarRefreshTokenBD(refreshToken);

        log.info("🚫 [TOKEN] Token de refresco revocado exitosamente. jti={}", refreshToken.getJti());
    }

    public RefreshTokenEntity guardarRefreshTokenBD(RefreshTokenEntity refreshTokenEntity) {
        try {
            return refreshTokenRepository.save(refreshTokenEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al guardar el refresh token: {}", e.getMessage(), e);
            throw new AuthenticationRefreshTokenPersistenceException("Error de integridad en base de datos al guardar el refresh token", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar el refresh token: {}", e.getMessage(), e);
            throw new AuthenticationRefreshTokenPersistenceException("Error al guardar el refresh token en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar el refresh token: {}", e.getMessage(), e);
            throw new AuthenticationRefreshTokenPersistenceException("Error inesperado al registrar el refresh token", e);
        }
    }

    public void eliminarRefreshTokenBD(RefreshTokenEntity refreshTokenEntity) {
        try {
            refreshTokenRepository.delete(refreshTokenEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al eliminar el refresh token: {}", e.getMessage(), e);
            throw new AuthenticationRefreshTokenDeletionException("Error de integridad en base de datos al eliminar el refresh token", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar el refresh token: {}", e.getMessage(), e);
            throw new AuthenticationRefreshTokenDeletionException("Error al eliminar el refresh token en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar el refresh token: {}", e.getMessage(), e);
            throw new AuthenticationRefreshTokenDeletionException("Error inesperado al eliminar el refresh token", e);
        }
    }
}
