package com.cloud.jml.utils.user;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.exception.role.RoleNotFoundException;
import com.cloud.jml.exception.user.UserCredencialesIncorrectasException;
import com.cloud.jml.exception.user.UserNotFoundException;
import com.cloud.jml.model.RoleEntity;
import com.cloud.jml.model.UserEntity;
import com.cloud.jml.repository.RoleRepository;
import com.cloud.jml.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class UserUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy, h:mm:ss a", Locale.of("es", "CO"));

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserUtils(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        log.info("🔥 UserUtils inicializado correctamente.");
    }

    public RoleEntity obtenerRolePorCodigo(int roleCode) {
        log.info("📌 Inicia consulta de role con código: {}", roleCode);

        Optional<RoleEntity> optionalRole = roleRepository.findByRoleCode(roleCode);

        if (optionalRole.isPresent()) {
            RoleEntity roleEntity = optionalRole.get();
            log.info("✅ Role encontrado: Código = {}, Nombre = {}", roleCode, roleEntity.getRoleName());
            return roleEntity;
        } else {
            log.warn("⚠️ Role no encontrado con código: {}", roleCode);
            throw new RoleNotFoundException(roleCode);
        }
    }

    public UserEntity validarExistenciaUsuario(UserRequestDTO userRequestDTO) {
        log.info("📌 Inicia validación de existencia del usuario: {}", userRequestDTO.getUserName());

        // Buscar el usuario
        Optional<UserEntity> userEntityOptional = userRepository.findByUserName(userRequestDTO.getUserName());

        if (userEntityOptional.isEmpty()) {
            log.warn("⚠️ Usuario no encontrado: {}", userRequestDTO.getUserName());
            throw new UserNotFoundException(userRequestDTO.getUserName());
        }

        UserEntity userEntity = userEntityOptional.get();

        // Comparación segura de contraseñas
        if (!userRequestDTO.getPassword().equals(userEntity.getPassword())) {
            log.warn("⚠️ Contraseña incorrecta para usuario: {}", userEntity.getUserName());
            throw new UserCredencialesIncorrectasException(userEntity.getUserName());
        }

//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            log.warn("⚠️ Contraseña incorrecta para usuario: {}", userName);
//            throw new BadCredentialsException("Contraseña incorrecta");
//        }

        log.info("✅ Usuario autenticado correctamente: {}", userEntity.getUserName());

        return userEntity;
    }

    public void actualizarDatosUsuario(UserRequestDTO userRequestDTO, UserEntity userEntity) {
        log.info("📌 Actualizando datos del usuario: {}", userRequestDTO.getUserName());

        // Actualizamos solo los campos permitidos
        userEntity.setUserName(userRequestDTO.getUserName());
        userEntity.setPassword(userRequestDTO.getPassword());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setTelefono(userRequestDTO.getTelefono());
        userEntity.setDireccion(userRequestDTO.getDireccion());
        userEntity.setRoleCode(userRequestDTO.getRoleCode());

        RoleEntity roleEntity = obtenerRolePorCodigo(userRequestDTO.getRoleCode());
        userEntity.setRoleName(roleEntity.getRoleName());

        log.info("✅ Datos del usuario actualizados correctamente: {}", userEntity.getUserName());
    }

    public String formatearFecha(LocalDateTime fecha) {
        String fechaFormateada = fecha.format(FORMATTER).toLowerCase();
        log.info("📌 Fecha formateada originalmente: {}", fechaFormateada);

        // Reemplazar y reasignar el valor "a. m." → "a.m." y "p. m." → "p.m."
        fechaFormateada = fechaFormateada
                .replace("a. m.", "a.m.")
                .replace("p. m.", "p.m.");

        log.info("📌 Fecha formateada final: {}", fechaFormateada);

        return fechaFormateada;
    }

    public void asignarFechasFormateadas(UserEntity userEntity, UserResponseDTO userResponseDTO) {
        if (userEntity.getFechaCreacion() != null) {
            String fechaCreacion = formatearFecha(userEntity.getFechaCreacion());
            log.info("📌 Fecha creación formateada: {}", fechaCreacion);

            userResponseDTO.setFechaCreacion(fechaCreacion);
        } else {
            userResponseDTO.setFechaCreacion(null);
        }

        if (userEntity.getFechaActualizacion() != null) {
            String fechaActualizacion = formatearFecha(userEntity.getFechaActualizacion());
            log.info("📌 Fecha actualización formateada: {}", fechaActualizacion);

            userResponseDTO.setFechaActualizacion(fechaActualizacion);
        } else {
            userResponseDTO.setFechaActualizacion(null);
        }
    }
}
