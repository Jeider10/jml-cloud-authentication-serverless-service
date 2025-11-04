package com.cloud.jml.service.token;

import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.model.authentication.AuthenticationEntity;
import com.cloud.jml.utils.authentication.AuthenticationMapper;
import com.cloud.jml.utils.authentication.AuthenticationUtils;
import com.cloud.jml.utils.jwt.JwtUtil;
import com.cloud.jml.utils.token.GeneratorTokenUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeneratorTokenService {

    private final GeneratorTokenUtils generatorTokenUtils;
    private final JwtUtil jwtUtil;
    private final AuthenticationMapper mapper;
    private final AuthenticationUtils authenticationUtils;

    public GeneratorTokenService(GeneratorTokenUtils generatorTokenUtils, JwtUtil jwtUtil, AuthenticationMapper mapper, AuthenticationUtils authenticationUtils) {
        this.generatorTokenUtils = generatorTokenUtils;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.authenticationUtils = authenticationUtils;
        log.info("🔥 GeneratorTokenService inicializado correctamente.");
    }

    @Transactional
    public String generarToken(AuthenticationRequestDTO authenticationRequestDTO, int roleCode, String roleName) {
        log.info("🔑 [SERVICIO] Generando nuevo token JWT para usuario: {}", authenticationRequestDTO.getUsuario());

        // 1️⃣ Generar token
        String token = generatorTokenUtils.generateToken(authenticationRequestDTO.getUsuario(), roleCode, roleName, false);

        // 2️⃣ Extraer JTI
        String jti = jwtUtil.extractJti(token);
        log.debug("🧩 [TOKEN] JTI extraído: {}", jti);

        // 3️⃣ Mapeo de DTO a Entity
        log.info("📦 [MAPEO] Transformando DTO a entidad de autenticación...");
        AuthenticationEntity authenticationEntity = mapper.mapRequestDtoToEntity(authenticationRequestDTO, roleCode, roleName, jti);
        log.debug("📦 [MAPEO] Entidad creada. Usuario: {}", authenticationEntity.getUsuario());

        // 4️⃣ Guardar en BD
        AuthenticationEntity entidadGuardada = authenticationUtils.guardarAuthenticationBD(authenticationEntity);
        log.info("💾 [PERSISTENCIA] Token guardado exitosamente. Usuario: {}", entidadGuardada.getUsuario());

        // 5️⃣ Retornar token
        log.info("📤 [RESPUESTA] Token JWT generado correctamente para el usuario: {}", authenticationRequestDTO.getUsuario());

        return token;
    }
}
