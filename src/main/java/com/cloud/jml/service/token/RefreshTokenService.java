package com.cloud.jml.service.token;

import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.exception.authentication.AuthenticationRefreshTokenValidationException;
import com.cloud.jml.model.token.RefreshTokenEntity;
import com.cloud.jml.utils.authentication.AuthenticationMapper;
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
    private final AuthenticationMapper mapper;

    public RefreshTokenService(RefreshTokenUtils refreshTokenUtils, GeneratorTokenService generatorTokenService, AuthenticationMapper mapper) {
        this.refreshTokenUtils = refreshTokenUtils;
        this.generatorTokenService = generatorTokenService;
        this.mapper = mapper;
        log.info("🔥 RefreshTokenService inicializado correctamente.");
    }

    @Transactional
    public AuthenticationResponseDTO refreshToken(Map<String, String> body) {
        log.info("♻️ [SERVICIO] Iniciando validación del refresh token.");

        // 1️⃣ Obtener token del cuerpo
        String refreshToken = body.get("refreshToken");
        log.info("🔑 [TOKEN] Refresh token recibido: {}", refreshToken);

        // 2️⃣ Verificar que exista el refresh token
        if (refreshToken == null || refreshToken.isBlank()) {
            log.warn("⚠️ [VALIDACIÓN] Refresh token ausente o vacío en la solicitud.");
            throw new AuthenticationRefreshTokenValidationException("Debe proporcionar un refresh token.");
        }

        // 3️⃣ Verificar expiración y validez
        RefreshTokenEntity refreshTokenEntity = refreshTokenUtils.verifyExpiration(refreshToken);
        log.info("🔐 [TOKEN] Token válido detectado. Usuario: {}, jti={}", refreshTokenEntity.getUsuario(), refreshTokenEntity.getJti());

        // 4️⃣ Generar nuevo access token
        AuthenticationRequestDTO authenticationRequestDTO = new AuthenticationRequestDTO();
        authenticationRequestDTO.setUsuario(refreshTokenEntity.getUsuario());

        String nuevoAccessToken = generatorTokenService.generarToken(
                authenticationRequestDTO,
                refreshTokenEntity.getRoleCode(),
                refreshTokenEntity.getRoleName(),
                "accessToken"
        );

        // 5️⃣ Generar nuevo access token
        String nuevoRefreshToken = refreshTokenUtils.generarRefreshToken(
                refreshTokenEntity.getUsuario(),
                refreshTokenEntity.getRoleCode(),
                refreshTokenEntity.getRoleName());

        // 6️⃣ Generar nuevo authorization token
        String nuevoAuthorization = generatorTokenService.generarToken(
                authenticationRequestDTO,
                refreshTokenEntity.getRoleCode(),
                refreshTokenEntity.getRoleName(),
                "authorization"
        );

        log.info("✅ [TOKEN] Nuevo access token generado para el usuario: {}", refreshTokenEntity.getUsuario());

        // 7️⃣ Construir respuesta option
        AuthenticationOptionsDTO authenticationOptionsDTO = mapper.mapEntityToAuthenticationOptionsDTO(refreshTokenEntity);

        // 8️⃣ Construir respuesta de refresh token
        AuthenticationResponseDTO authenticationResponseDTO = mapper.mapAuthenticationResponseDTO(
                authenticationOptionsDTO,
                nuevoAccessToken,
                nuevoRefreshToken,
                nuevoAuthorization,
                true);

        log.info("📦 [RESPUESTA] DTO de autenticación preparado correctamente para envío.");

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
