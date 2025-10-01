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

    public AuthenticationEntity mapRequestDtoToEntity(AuthenticationRequestDTO authenticationRequestDTO, int roleCode, String roleName, String jti) {
        log.info("📌 Iniciando mapeo DTO a Entity para autenticación de usuario: {}", authenticationRequestDTO.getUsuario());

        AuthenticationEntity authenticationEntity = new AuthenticationEntity();

        authenticationEntity.setUsuario(authenticationRequestDTO.getUsuario());
        authenticationEntity.setRoleCode(roleCode);
        authenticationEntity.setRoleName(roleName);
        authenticationEntity.setJti(jti);
        authenticationEntity.setFechaCreacion(LocalDateTime.now());

        log.info("📌 Finalizando mapeo DTO a Entity para autenticación de usuario: {}", authenticationRequestDTO.getUsuario());

        return authenticationEntity;
    }

    public AuthenticationOptionsDTO mapEntityToAuthenticationOptionsDTO(UserEntity userEntity) {
        log.info("📝 Construyendo DTO de opciones para usuario: {}", userEntity.getUserName());

        AuthenticationOptionsDTO options = new AuthenticationOptionsDTO();

        options.setLogin(userEntity.getUserName());
        options.setRoleCode(userEntity.getRoleCode());
        options.setRoleName(userEntity.getRoleName());

        log.info("✅ DTO de opciones construido para usuario: {}", userEntity.getUserName());

        return options;
    }

    public AuthenticationResponseDTO mapAuthenticationResponseDTO(AuthenticationOptionsDTO options, String token) {
        log.info("📝 Construyendo DTO de respuesta para usuario: {}", options.getLogin());

        AuthenticationResponseDTO response = new AuthenticationResponseDTO();

        response.setOptions(options);
        response.setAuthorization(token);

        log.info("✅ DTO de respuesta construido para usuario: {}", options.getLogin());

        return response;
    }
}
