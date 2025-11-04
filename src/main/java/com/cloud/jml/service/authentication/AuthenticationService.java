package com.cloud.jml.service.authentication;

import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.service.token.GeneratorTokenService;
import com.cloud.jml.service.token.RefreshTokenService;
import com.cloud.jml.utils.authentication.AuthenticationMapper;
import com.cloud.jml.utils.authentication.AuthenticationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationService {

    private final AuthenticationUtils authenticationUtils;
    private final AuthenticationMapper mapper;
    private final GeneratorTokenService generatorTokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(AuthenticationUtils authenticationUtils, AuthenticationMapper mapper, GeneratorTokenService generatorTokenService, RefreshTokenService refreshTokenService) {
        this.authenticationUtils = authenticationUtils;
        this.mapper = mapper;
        this.generatorTokenService = generatorTokenService;
        this.refreshTokenService = refreshTokenService;
        log.info("🔥 AuthenticationService inicializado correctamente.");
    }

    @Transactional
    public AuthenticationResponseDTO authenticationLogin(AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("🔐 [CONSULTA] Validando usuario: {}", authenticationRequestDTO.getUsuario());

        // 1️⃣ Validar usuario y contraseña
        UserEntity userEntity = authenticationUtils.validarUsuario(authenticationRequestDTO);

        // 2️⃣ Generar access token y persistir en auth_login
        String accessToken = generatorTokenService.generarToken(authenticationRequestDTO, userEntity.getRoleCode(), userEntity.getRoleName());

        // 3️⃣ Generar refresh token persistente
        String refreshToken = refreshTokenService.generarRefreshToken(authenticationRequestDTO, userEntity.getRoleCode(), userEntity.getRoleName());

        // 4️⃣ Construir DTO de options
        AuthenticationOptionsDTO options = mapper.mapEntityToAuthenticationOptionsDTO(userEntity);

        // 5️⃣ Mapear respuesta completa con ambos tokens
        AuthenticationResponseDTO authenticationResponseDTO = mapper.mapAuthenticationResponseDTO(options, accessToken, refreshToken);

        log.info("✅ [FINALIZADO] Usuario {} autenticado correctamente con roleCode: {} y roleName: {}",
                userEntity.getUserName(), userEntity.getRoleCode(), userEntity.getRoleName());

        return authenticationResponseDTO;
    }

    @Transactional
    public AuthenticationResponseDTO obtenerUsuarioActual(String authorizationHeader) {
        log.info("🔐 [CONSULTA] Obteniendo usuario actual.");

        // 1️⃣ Obtener usuario actual
        AuthenticationOptionsDTO options = authenticationUtils.obtenerUsuarioActual(authorizationHeader);

        // 2️⃣ Devolver respuesta final
        AuthenticationResponseDTO authenticationResponseDTO = mapper.mapAuthenticationResponseDTO(options, authorizationHeader);

        log.info("✅ [FINALIZADO] Usuario actual obtenido correctamente.");

        return authenticationResponseDTO;
    }
}
