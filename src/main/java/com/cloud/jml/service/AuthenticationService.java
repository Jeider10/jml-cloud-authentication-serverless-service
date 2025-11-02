package com.cloud.jml.service;

import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.model.UserEntity;
import com.cloud.jml.utils.authentication.AuthenticationMapper;
import com.cloud.jml.utils.authentication.AuthenticationUtils;
import com.cloud.jml.utils.token.GeneratorToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationService {

    private final AuthenticationUtils authenticationUtils;
    private final AuthenticationMapper mapper;
    private final GeneratorToken generatorToken;

    public AuthenticationService(AuthenticationUtils authenticationUtils, AuthenticationMapper mapper, GeneratorToken generatorToken) {
        this.authenticationUtils = authenticationUtils;
        this.mapper = mapper;
        this.generatorToken = generatorToken;
        log.info("🔥 AuthenticationService inicializado correctamente.");
    }

    @Transactional
    public AuthenticationResponseDTO authenticationLogin(AuthenticationRequestDTO authenticationRequestDTO) {
        log.info("🔐 [CONSULTA] Validando usuario: {}", authenticationRequestDTO.getUsuario());

        // 1️⃣ Buscar y validar usuario y contraseña
        UserEntity userEntity = authenticationUtils.validarUsuario(authenticationRequestDTO);

        // 2️⃣ Generar token y persistir en auth_login
        String token = generatorToken.generarToken(authenticationRequestDTO, userEntity.getRoleCode(), userEntity.getRoleName());

        // 3️⃣ Construir DTO de options
        AuthenticationOptionsDTO options = mapper.mapEntityToAuthenticationOptionsDTO(userEntity);

        // 4️⃣ Devolver respuesta final
        AuthenticationResponseDTO authenticationResponseDTO = mapper.mapAuthenticationResponseDTO(options, token);

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
