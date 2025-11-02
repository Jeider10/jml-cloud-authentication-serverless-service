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
    private final UserFormatearFecha userFormatearFecha;
    private final PasswordEncoder passwordEncoder;

    public UserMapper(UserUtils userUtils, UserFormatearFecha userFormatearFecha, PasswordEncoder passwordEncoder) {
        this.userUtils = userUtils;
        this.userFormatearFecha = userFormatearFecha;
        this.passwordEncoder = passwordEncoder; // Encriptador de contraseñas
        log.info("🔥 UserMapper inicializado correctamente.");
    }

    /**
     * 📦 Convierte un DTO de solicitud de usuario en una entidad lista para persistir.
     */
    public UserEntity mapRequestDtoToEntity(UserRequestDTO userRequestDTO) {
        log.info("📦 [MAPEO] Iniciando mapeo DTO → Entity para usuario: nombre={}", userRequestDTO.getUserName());

        UserEntity userEntity = new UserEntity();

        userEntity.setNombres(userRequestDTO.getNombres());
        userEntity.setApellidos(userRequestDTO.getApellidos());
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

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para usuario: nombre={}", userEntity.getUserName());

        return userEntity;
    }

    /**
     * 📦 Convierte una entidad de cliente en un DTO de respuesta.
     */
    public UserResponseDTO mapEntityToResponseDto(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para usuario: nombre={}", userEntity.getUserName());

        UserResponseDTO userResponseDTO = buildUserResponseDTO(userEntity);

        // 🕓 Formateo de fechas
        userFormatearFecha.asignarFechasFormateadas(userEntity, userResponseDTO);

        log.info("✅ [MAPEO] Mapeo completado Entity → DTO para usuario: nombre={}", userResponseDTO.getUserName());

        return userResponseDTO;
    }

    public UserResponseDTO buildUserResponseDTO(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando construcción de DTO de respuesta para usuario: {}", userEntity.getUserName());

        UserResponseDTO userResponseDTO = new UserResponseDTO();

        userResponseDTO.setNombres(userEntity.getNombres());
        userResponseDTO.setApellidos(userEntity.getApellidos());
        userResponseDTO.setUserName(userEntity.getUserName());
        userResponseDTO.setIdentificacion(userEntity.getIdentificacion());
        userResponseDTO.setRoleCode(userEntity.getRoleCode());
        userResponseDTO.setRoleName(userEntity.getRoleName());
        userResponseDTO.setEmail(userEntity.getEmail());
        userResponseDTO.setTelefono(userEntity.getTelefono());
        userResponseDTO.setDireccion(userEntity.getDireccion());
        userResponseDTO.setHistorialUltimoActualizado(userEntity.getHistorialUltimoActualizado());

        log.info("✅ [MAPEO] Mapeo completado de DTO de respuesta para usuario: {}", userResponseDTO.getUserName());

        return userResponseDTO;
    }

    public void actualizarDatosUsuario(UserRequestDTO userRequestDTO, UserEntity userEntity) {
        log.info("✅ Actualizando datos del usuario: {}", userRequestDTO.getUserName());

        // Actualizamos solo los campos permitidos
        userEntity.setIdentificacion(userRequestDTO.getIdentificacion());
        userEntity.setUserName(userRequestDTO.getUserName());
        userEntity.setNombres(userRequestDTO.getNombres());
        userEntity.setApellidos(userRequestDTO.getApellidos());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setTelefono(userRequestDTO.getTelefono());
        userEntity.setDireccion(userRequestDTO.getDireccion());
        userEntity.setHistorialUltimoActualizado(userRequestDTO.getNombres());

        // Actualizamos la fecha de actualización
        userEntity.setFechaActualizacion(LocalDateTime.now());

        RoleEntity roleEntity = userUtils.obtenerRolePorCodigo(userRequestDTO.getRoleCode());
        userEntity.setRoleCode(userRequestDTO.getRoleCode());
        userEntity.setRoleName(roleEntity.getRoleName());

        log.info("✅ Datos del usuario actualizados correctamente: {}", userEntity.getUserName());
    }
}
