package com.cloud.jml.service.token;

import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.exception.authentication.AuthenticationRefreshTokenValidationException;
import com.cloud.jml.model.token.RefreshTokenEntity;
import com.cloud.jml.utils.token.RefreshTokenUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RefreshTokenService {

    private final RefreshTokenUtils refreshTokenUtils;
    private final GeneratorTokenService generatorTokenService;

    public RefreshTokenService(RefreshTokenUtils refreshTokenUtils, GeneratorTokenService generatorTokenService) {
        this.refreshTokenUtils = refreshTokenUtils;
        this.generatorTokenService = generatorTokenService;
        log.info("🔥 RefreshTokenService inicializado correctamente.");
    }

    @Transactional
    public String generarRefreshToken(AuthenticationRequestDTO authenticationRequestDTO, int roleCode, String roleName) {
        log.info("🔐 [CONSULTA] Generando refresh token para usuario: {}", authenticationRequestDTO.getUsuario());

        RefreshTokenEntity refreshTokenEntity = refreshTokenUtils.createRefreshToken(authenticationRequestDTO.getUsuario(), roleCode, roleName);
        log.info("📦 [PERSISTENCIA] Refresh token generado correctamente para usuario: {}", authenticationRequestDTO.getUsuario());

        String refreshToken = refreshTokenEntity.getToken();
        log.info("✅ [FINALIZADO] Refresh token generado correctamente para usuario: {}", authenticationRequestDTO.getUsuario());

        return refreshToken;
    }

    @Transactional
    public AuthenticationResponseDTO refreshToken(Map<String, String> body) {
        log.info("♻️ [SERVICIO] Iniciando validación del refresh token.");

        // 1️⃣ Obtener token del cuerpo
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("⚠️ [VALIDACIÓN] Refresh token ausente o vacío en la solicitud.");
            throw new AuthenticationRefreshTokenValidationException("Debe proporcionar un refresh token.");
        }

        // 2️⃣ Verificar expiración y validez
        RefreshTokenEntity refreshTokenEntity = refreshTokenUtils.verifyExpiration(refreshToken);
        log.info("🔐 [TOKEN] Token válido detectado. Usuario: {}, jti={}", refreshTokenEntity.getUsuario(), refreshTokenEntity.getJti());

        // 3️⃣ Generar nuevo access token
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO();
        authenticationRequestDTO.setUsuario(refreshTokenEntity.getUsuario());

        String nuevoAccessToken = generatorTokenService.generarToken(
                authenticationRequestDTO,
                refreshTokenEntity.getRoleCode(),
                refreshTokenEntity.getRoleName()
        );

        log.info("✅ [TOKEN] Nuevo access token generado para el usuario: {}", refreshTokenEntity.getUsuario());

        // 4️⃣ Construir respuesta
        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();
        authenticationResponseDTO.setAuthorization(nuevoAccessToken);
        authenticationResponseDTO.setRefreshToken(refreshTokenEntity.getToken());

        log.debug("📦 [RESPUESTA] DTO de autenticación preparado correctamente para envío.");

        return authenticationResponseDTO;
    }

    @Transactional
    public void logout(Map<String, String> body) {
        log.info("♻️ [SERVICIO] Iniciando proceso de logout.");

        // 1️⃣ Validar token
        String refreshToken = body.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("⚠️ [VALIDACIÓN] Refresh token ausente o vacío en la solicitud de logout.");
            throw new AuthenticationRefreshTokenValidationException("Debe proporcionar un refresh token.");
        }

        // 2️⃣ Revocar token
        log.info("🔐 [TOKEN] Solicitando revocación del refresh token...");
        refreshTokenUtils.revokeToken(refreshToken);

        log.info("🚫 [TOKEN] Token de refresco revocado exitosamente.");
    }
}
