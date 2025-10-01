package com.cloud.jml.utils.token;

import com.cloud.jml.config.jwt.JwtUtil;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.model.AuthenticationEntity;
import com.cloud.jml.repository.AuthenticationRepository;
import com.cloud.jml.utils.authentication.AuthenticationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeneratorToken {

    private final JwtUtil jwtUtil;
    private final AuthenticationMapper mapper;
    private final AuthenticationRepository authenticationRepository;

    public GeneratorToken(JwtUtil jwtUtil, AuthenticationMapper mapper, AuthenticationRepository authenticationRepository) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        this.authenticationRepository = authenticationRepository;
        log.info("🔥 GeneratorToken inicializado correctamente.");
    }

    public String generarToken(AuthenticationRequestDTO authenticationRequestDTO, int roleCode, String roleName) {
        log.info("🔑 Generando token para usuario: {}", authenticationRequestDTO.getUsuario());

        // ✅ Generar token JWT
        String token = jwtUtil.generateToken(authenticationRequestDTO.getUsuario(), roleCode, roleName);

        // ✅ Extraer JTI usando JwtUtil
        String jti = jwtUtil.extractJti(token);

        // Mapeo de DTO a Entity
        AuthenticationEntity entity = mapper.mapRequestDtoToEntity(authenticationRequestDTO, roleCode, roleName, jti);

        // ✅ Guardar en BD
        authenticationRepository.save(entity);
        log.info("📝 Guardando token en BD para usuario: {}", authenticationRequestDTO.getUsuario());

        // ✅ Retorna token
        return token;
    }
}
