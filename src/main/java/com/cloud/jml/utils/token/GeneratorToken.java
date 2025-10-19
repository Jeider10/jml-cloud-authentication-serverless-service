package com.cloud.jml.utils.token;

import com.cloud.jml.config.jwt.JwtUtil;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.model.AuthenticationEntity;
import com.cloud.jml.utils.authentication.AuthenticationMapper;
import com.cloud.jml.utils.authentication.AuthenticationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeneratorToken {

    private final JwtUtil jwtUtil;
    private final AuthenticationMapper mapper;
    private final AuthenticationUtils authenticationUtils;

    public GeneratorToken(JwtUtil jwtUtil, AuthenticationMapper mapper, AuthenticationUtils authenticationUtils) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.authenticationUtils = authenticationUtils;
        log.info("🔥 GeneratorToken inicializado correctamente.");
    }

    public String generarToken(AuthenticationRequestDTO authenticationRequestDTO, int roleCode, String roleName) {
        log.info("🔑 [SOLICITUD] Generando token para usuario: {}", authenticationRequestDTO.getUsuario());

        // ✅ Generar token JWT
        String token = jwtUtil.generateToken(authenticationRequestDTO.getUsuario(), roleCode, roleName);

        // ✅ Extraer JTI usando JwtUtil
        String jti = jwtUtil.extractJti(token);

        // Mapeo de DTO a Entity
        log.info("📦 [MAPEO] Transformando DTO a entidad de authentication");
        AuthenticationEntity authenticationEntity = mapper.mapRequestDtoToEntity(authenticationRequestDTO, roleCode, roleName, jti);
        log.info("📦 [MAPEO] Authentication mapeado a entidad. usuario: {}", authenticationEntity.getUsuario());

        // ✅ Guardar en BD
        AuthenticationEntity guardarAuthentication = authenticationUtils.guardarAuthenticationBD(authenticationEntity);
        log.info("💾 [PERSISTENCIA] Authentication guardado exitosamente. usuario: {}", guardarAuthentication.getUsuario());

        // ✅ Retorna token
        return token;
    }
}
