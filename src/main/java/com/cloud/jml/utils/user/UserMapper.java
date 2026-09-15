package com.cloud.jml.utils.user;

import com.cloud.jml.dto.user.UserPapeleraResponseDTO;
import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.model.role.RoleEntity;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.utils.date.FormatearFecha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class UserMapper {

    private final UserUtils userUtils;
    private final UserFormatearFecha userFormatearFecha;
    private final FormatearFecha formatearFecha;
    private final PasswordEncoder passwordEncoder;

    public UserMapper(UserUtils userUtils, UserFormatearFecha userFormatearFecha,
                      FormatearFecha formatearFecha, PasswordEncoder passwordEncoder) {
        this.userUtils = userUtils;
        this.userFormatearFecha = userFormatearFecha;
        this.formatearFecha = formatearFecha;
        this.passwordEncoder = passwordEncoder;
        log.info("🔥 UserMapper inicializado correctamente.");
    }

    public UserEntity mapRequestDtoToEntity(UserRequestDTO userRequestDTO) {
        log.info("📦 [MAPEO] Iniciando mapeo DTO → Entity para usuario: nombre={}", userRequestDTO.getUserName());

        UserEntity userEntity = new UserEntity();

        userEntity.setNombres(userRequestDTO.getNombres());
        userEntity.setApellidos(userRequestDTO.getApellidos());
        userEntity.setUserName(userRequestDTO.getUserName());
//        userEntity.setPassword(userRequestDTO.getPassword());
        // Siempre encodear la contraseña con BCrypt al crear
        userEntity.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        userEntity.setIdentificacion(userRequestDTO.getIdentificacion());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setTelefono(userRequestDTO.getTelefono());
        userEntity.setDireccion(userRequestDTO.getDireccion());
        userEntity.setFechaCreacion(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
        userEntity.setEliminado(false);

        RoleEntity roleEntity = userUtils.obtenerRolePorCodigo(userRequestDTO.getRoleCode());
        userEntity.setRoleCode(roleEntity.getRoleCode());
        userEntity.setRoleName(roleEntity.getRoleName());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para usuario: nombre={}", userEntity.getUserName());

        return userEntity;
    }

    public UserResponseDTO mapEntityToResponseDto(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para usuario: nombre={}", userEntity.getUserName());

        UserResponseDTO userResponseDTO = buildUserResponseDTO(userEntity);

        // 🕓 Formateo de fechas
        userFormatearFecha.asignarFechasFormateadas(userEntity, userResponseDTO);

        log.info("✅ [MAPEO] Mapeo completado Entity → DTO para usuario: nombre={}", userResponseDTO.getUserName());

        return userResponseDTO;
    }

    public UserResponseDTO buildUserResponseDTO(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando construccion de DTO de respuesta para usuario: {}", userEntity.getUserName());

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
        userResponseDTO.setCreadoPor(userEntity.getCreadoPor());

        log.info("✅ [MAPEO] Mapeo completado de DTO de respuesta para usuario: {}", userResponseDTO.getUserName());

        return userResponseDTO;
    }

    public UserPapeleraResponseDTO mapEntityToPapeleraDto(UserEntity userEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → PapeleraDTO para usuario: {}", userEntity.getIdentificacion());

        UserPapeleraResponseDTO dto = new UserPapeleraResponseDTO();

        dto.setIdentificacion(userEntity.getIdentificacion());
        dto.setNombres(userEntity.getNombres());
        dto.setApellidos(userEntity.getApellidos());
        dto.setUserName(userEntity.getUserName());
        dto.setRoleName(userEntity.getRoleName());
        dto.setEmail(userEntity.getEmail());
        dto.setTelefono(userEntity.getTelefono());
        dto.setDireccion(userEntity.getDireccion());
        dto.setCreadoPor(userEntity.getCreadoPor());
        dto.setFechaCreacion(formatearFecha.formatearFecha(userEntity.getFechaCreacion()));
        dto.setFechaEliminacion(formatearFecha.formatearFecha(userEntity.getFechaEliminacion()));
        dto.setEliminadoPorId(userEntity.getEliminadoPorId());
        dto.setEliminadoPorNombre(userEntity.getEliminadoPorNombre());

        log.info("✅ [MAPEO] Mapeo papelera completado para usuario: {}", dto.getIdentificacion());

        return dto;
    }

    public void actualizarDatosUsuario(UserRequestDTO userRequestDTO, UserEntity userEntity, String userLogin) {
        log.info("✅ Actualizando datos del usuario: {}", userRequestDTO.getUserName());

        // Actualizamos solo los campos permitidos
        userEntity.setIdentificacion(userRequestDTO.getIdentificacion());
        userEntity.setUserName(userRequestDTO.getUserName());
        userEntity.setNombres(userRequestDTO.getNombres());
        userEntity.setApellidos(userRequestDTO.getApellidos());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setTelefono(userRequestDTO.getTelefono());
        userEntity.setDireccion(userRequestDTO.getDireccion());
        userEntity.setHistorialUltimoActualizado(userLogin);

        // Actualizar contraseña SOLO si viene una nueva (no vacía)
        String nuevaPassword = userRequestDTO.getPassword();
        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(nuevaPassword));
            log.info("🔑 Contraseña actualizada con BCrypt para usuario: {}", userEntity.getUserName());
        }

        // Actualizamos la fecha de actualizacion
        userEntity.setFechaActualizacion(LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS));

        RoleEntity roleEntity = userUtils.obtenerRolePorCodigo(userRequestDTO.getRoleCode());
        userEntity.setRoleCode(userRequestDTO.getRoleCode());
        userEntity.setRoleName(roleEntity.getRoleName());

        log.info("✅ Datos del usuario actualizados correctamente: {}", userEntity.getUserName());
    }
}
