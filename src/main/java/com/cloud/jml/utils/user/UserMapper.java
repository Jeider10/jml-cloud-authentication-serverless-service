package com.cloud.jml.utils.user;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.model.RoleEntity;
import com.cloud.jml.model.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class UserMapper {

    private final UserUtils userUtils;
    private final PasswordEncoder passwordEncoder;

    public UserMapper(UserUtils userUtils, PasswordEncoder passwordEncoder) {
        this.userUtils = userUtils;
        this.passwordEncoder = passwordEncoder; // Encriptador de contraseñas
        log.info("🔥 UserMapper inicializado correctamente.");
    }

    public UserEntity mapRequestDtoToEntity(UserRequestDTO userRequestDTO) {
        log.info("📌 Iniciando mapeo DTO a Entity para crear Usuario: {}", userRequestDTO.getUserName());

        UserEntity userEntity = new UserEntity();

        // ✅ Encriptar la contraseña antes de guardarla
//        String passwordEncriptada = passwordEncoder.encode(userRequestDTO.getPassword());

        userEntity.setUserName(userRequestDTO.getUserName());
        userEntity.setPassword(userRequestDTO.getPassword());
        userEntity.setIdentificacion(userRequestDTO.getIdentificacion());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setTelefono(userRequestDTO.getTelefono());
        userEntity.setDireccion(userRequestDTO.getDireccion());
        userEntity.setFechaCreacion(LocalDateTime.now());

        RoleEntity roleEntity = userUtils.obtenerRolePorCodigo(userRequestDTO.getRoleCode());
        userEntity.setRoleCode(roleEntity.getRoleCode());
        userEntity.setRoleName(roleEntity.getRoleName());

        log.info("📌 Finalizando mapeo DTO a Entity para crear Usuario: {}", userRequestDTO.getUserName());

        return userEntity;
    }

    public UserResponseDTO mapEntityToResponseDto(UserEntity userEntity) {
        log.info("📌 Iniciando mapeo Entity a DTO para crear Usuario: {}", userEntity.getUserName());

        UserResponseDTO userResponseDTO = buildUserResponseDTO(userEntity);

        // 🔹 Formatear fechas
        userUtils.asignarFechasFormateadas(userEntity, userResponseDTO);

        log.info("📌 Finalizando mapeo Entity a DTO para crear Usuario: {}", userEntity.getUserName());

        return userResponseDTO;
    }

    public UserResponseDTO buildUserResponseDTO(UserEntity userEntity) {
        log.info("📌 Iniciando construcción de DTO de respuesta para Usuario: {}", userEntity.getUserName());

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setUserName(userEntity.getUserName());
        userResponseDTO.setIdentificacion(userEntity.getIdentificacion());
        userResponseDTO.setRoleCode(userEntity.getRoleCode());
        userResponseDTO.setRoleName(userEntity.getRoleName());
        userResponseDTO.setEmail(userEntity.getEmail());
        userResponseDTO.setTelefono(userEntity.getTelefono());
        userResponseDTO.setDireccion(userEntity.getDireccion());

        log.info("📌 Finalizando construcción de DTO de respuesta para Usuario: {}", userEntity.getUserName());

        return userResponseDTO;
    }
}
