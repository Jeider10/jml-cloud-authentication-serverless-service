package com.cloud.jml.service.authentication;

import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.service.token.GeneratorTokenService;
import com.cloud.jml.utils.authentication.AuthenticationMapper;
import com.cloud.jml.utils.authentication.AuthenticationUtils;
import com.cloud.jml.utils.token.RefreshTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationService {

    private final AuthenticationUtils authenticationUtils;
    private final AuthenticationMapper mapper;
    private final GeneratorTokenService generatorTokenService;
    private final RefreshTokenUtils refreshTokenUtils;

    public AuthenticationService(AuthenticationUtils authenticationUtils, AuthenticationMapper mapper, GeneratorTokenService generatorTokenService, RefreshTokenUtils refreshTokenUtils) {
        this.authenticationUtils = authenticationUtils;
        this.mapper = mapper;
        this.generatorTokenService = generatorTokenService;
        this.refreshTokenUtils = refreshTokenUtils;
        log.info("🔥 AuthenticationService inicializado correctamente.");
    }

    @Transactional
    public AuthenticationResponseDTO authenticationLogin(AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("🔐 [CONSULTA] Validando usuario: {}", authenticationRequestDTO.getUsuario());

        // 1️⃣ Validar usuario y contraseña
        UserEntity userEntity = authenticationUtils.validarUsuario(authenticationRequestDTO);

        // 2️⃣ Generar access token (token_use = "accessToken")
        String accessToken = generatorTokenService.generarToken(
                authenticationRequestDTO,
                userEntity.getRoleCode(),
                userEntity.getRoleName(),
                "accessToken"
        );

        // 3️⃣ Generar ID token (authorization) (token_use = "authorization")
        String tokenAuthorization = generatorTokenService.generarToken(
                authenticationRequestDTO,
                userEntity.getRoleCode(),
                userEntity.getRoleName(),
                "authorization"
        );

        // 4️⃣ Generar refresh token persistente
        String refreshToken = refreshTokenUtils.generarRefreshToken(
                authenticationRequestDTO,
                userEntity.getRoleCode(),
                userEntity.getRoleName());

        // 5️⃣ Construir DTO de options
        AuthenticationOptionsDTO options = mapper.mapEntityToAuthenticationOptionsDTO(userEntity);

        // 6️⃣ Mapear respuesta completa con ambos tokens
//        boolean isRefreshToken = false;
        AuthenticationResponseDTO authenticationResponseDTO = mapper.mapAuthenticationResponseDTO(
                options,
                accessToken,
                refreshToken,
                tokenAuthorization,
                false);

        log.info("✅ [FINALIZADO] Usuario {} autenticado correctamente con roleCode: {} y roleName: {}",
                userEntity.getUserName(), userEntity.getRoleCode(), userEntity.getRoleName());

        return authenticationResponseDTO;
    }

    @Transactional
    public AuthenticationResponseDTO obtenerUsuarioActual(String refreshTokenHeader) {
        log.info("🔐 [CONSULTA] Obteniendo usuario actual.");

        // 1️⃣ Obtener usuario actual
        AuthenticationOptionsDTO options = authenticationUtils.obtenerUsuarioActual(refreshTokenHeader);

        // 2️⃣ Devolver respuesta final
        AuthenticationResponseDTO authenticationResponseDTO = mapper.mapAuthenticationResponseDTO(options, refreshTokenHeader);

        log.info("✅ [FINALIZADO] Usuario actual obtenido correctamente.");

        return authenticationResponseDTO;
    }
}
