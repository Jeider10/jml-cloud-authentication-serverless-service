package com.cloud.jml.utils.authentication;

import com.cloud.jml.dto.authentication.AuthenticationOptionsDTO;
import com.cloud.jml.dto.authentication.AuthenticationRequestDTO;
import com.cloud.jml.dto.authentication.AuthenticationResponseDTO;
import com.cloud.jml.model.AuthenticationEntity;
import com.cloud.jml.model.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class AuthenticationMapper {

    /**
     * 📦 Convierte un DTO de solicitud de autenticación en una entidad lista para persistir.
     */
    public AuthenticationEntity mapRequestDtoToEntity(AuthenticationRequestDTO authenticationRequestDTO, int roleCode, String roleName, String jti) {
        log.info("📦 [MAPEO] Iniciando mapeo DTO → Entity para autenticación: usuario={}", authenticationRequestDTO.getUsuario());

        AuthenticationEntity authenticationEntity = new AuthenticationEntity();

        authenticationEntity.setUsuario(authenticationRequestDTO.getUsuario());
        authenticationEntity.setRoleCode(roleCode);
        authenticationEntity.setRoleName(roleName);
        authenticationEntity.setJti(jti);
        authenticationEntity.setFechaCreacion(LocalDateTime.now());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para autenticación: usuario={}", authenticationEntity.getUsuario());

        return authenticationEntity;
    }

    /**
     * 📦 Convierte una entidad de cliente en un DTO Option de respuesta.
     */
    public AuthenticationOptionsDTO mapEntityToAuthenticationOptionsDTO(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para autenticación de usuario: nombre={}", userEntity.getUserName());

        AuthenticationOptionsDTO authenticationOptionsDTO = new AuthenticationOptionsDTO();

        authenticationOptionsDTO.setLogin(userEntity.getUserName());
        authenticationOptionsDTO.setRoleCode(userEntity.getRoleCode());
        authenticationOptionsDTO.setRoleName(userEntity.getRoleName());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para autenticación de usuario: nombre={}", authenticationOptionsDTO.getLogin());

        return authenticationOptionsDTO;
    }

    /**
     * 📦 Convierte una entidad de cliente en un DTO de respuesta.
     */
    public AuthenticationResponseDTO mapAuthenticationResponseDTO(AuthenticationOptionsDTO options, String token) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para autenticación: login={}", options.getLogin());

        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();

        authenticationResponseDTO.setOptions(options);
        authenticationResponseDTO.setAuthorization(token);

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para autenticación: login={}", authenticationResponseDTO.getOptions().getLogin());

        return authenticationResponseDTO;
    }
}
