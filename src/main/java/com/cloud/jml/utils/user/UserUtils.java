package com.cloud.jml.utils.user;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.exception.role.RoleDuplicationException;
import com.cloud.jml.exception.role.RoleNotFoundException;
import com.cloud.jml.exception.user.UserAlreadyExistsException;
import com.cloud.jml.exception.user.UserDeletionException;
import com.cloud.jml.exception.user.UserNotFoundException;
import com.cloud.jml.exception.user.UserPersistenceException;
import com.cloud.jml.model.role.RoleEntity;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.repository.role.RoleRepository;
import com.cloud.jml.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class UserUtils {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserUtils(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        log.info("🔥 UserUtils inicializado correctamente.");
    }

    public UserEntity validarExistenciaUsuario(UserRequestDTO userRequestDTO) {
        log.info("✅ Inicia validacion de existencia del usuario para actualizacion: {}", userRequestDTO.getUserName());

        // 🔹 Paso 1: Buscar el usuario actual por identificacion (el que debe existir)
        Optional<UserEntity> usuarioActualOptional = userRepository.findByIdentificacion(userRequestDTO.getIdentificacion());

        if (usuarioActualOptional.isEmpty()) {
            log.error("❌ No se encontro el usuario: {} con identificacion: {}", userRequestDTO.getUserName(), userRequestDTO.getIdentificacion());
            throw new UserNotFoundException(userRequestDTO.getUserName());
        }

        UserEntity usuarioActual = usuarioActualOptional.get();

        // 🔹 Paso 2: Buscar si existe otro usuario con el nuevo userName
        Optional<UserEntity> usuarioConNuevoUserNameOpt = userRepository.findByUserName(userRequestDTO.getUserName());

        if (usuarioConNuevoUserNameOpt.isPresent()) {
            UserEntity usuarioConNuevoUserName = usuarioConNuevoUserNameOpt.get();

            // Si el usuario con ese userName tiene otra identificacion, no se puede usar ese nombre
            Long usuarioActualIdentificacion = usuarioActual.getIdentificacion();
            if (!Objects.equals(usuarioConNuevoUserName.getIdentificacion(), usuarioActualIdentificacion)) {
                log.warn("⚠️ El userName '{}' ya pertenece a otro usuario con identificacion diferente: {}",
                        userRequestDTO.getUserName(), usuarioConNuevoUserName.getIdentificacion());
                throw new UserAlreadyExistsException(userRequestDTO.getUserName());
            }
        } else {
            log.info("ℹ️ El nuevo userName '{}' esta disponible para uso.", userRequestDTO.getUserName());
        }

        log.info("✅ [FINALIZADO] Usuario existente validado correctamente para actualizacion: {}", usuarioActual.getUserName());

        return usuarioActual;
    }

    public RoleEntity obtenerRolePorCodigo(int roleCode) {
        log.info("✅ Inicia consulta de role con codigo: {}", roleCode);

        Optional<RoleEntity> optionalRole = roleRepository.findByRoleCode(roleCode);

        if (optionalRole.isPresent()) {
            RoleEntity roleEntity = optionalRole.get();
            log.info("✅ Role encontrado: Codigo = {}, Nombre = {}", roleCode, roleEntity.getRoleName());
            return roleEntity;
        } else {
            log.warn("⚠️ Role no encontrado con codigo: {}", roleCode);
            throw new RoleNotFoundException(roleCode);
        }
    }

    public void validarUnicoAdministrador(UserEntity userEntity) {
        log.info("✅ Verificando si el usuario es unico administrador: {}", userEntity.getUserName());

        // 🚫 Validar que no haya mas de un administrador
        if (esRolAdministrador(userEntity)) {
            boolean existeAdmin = userRepository.existsByRoleNameIgnoreCase(userEntity.getRoleName());

            log.info("✅ Verificando si ya existe un usuario con el rol de administrador: {}", existeAdmin);

            if (existeAdmin) {
                // 1. Define el mensaje base
                String baseMessage = "❌ [ERROR] Ya existe un usuario con el rol de administrador ('%s'). "
                        + "No se permiten multiples usuarios administradores.";

                // 2. Formatea el mensaje, reemplazando '%s' por el valor real
                String formattedMessage = String.format(baseMessage, userEntity.getRoleName());

                // 3. Usa el mensaje formateado para el log y la excepcion
                log.warn(formattedMessage);
                throw new RoleDuplicationException(formattedMessage);
            }
        }
    }

    public boolean esRolAdministrador(UserEntity userEntity) {
        log.info("✅ Verificando si el usuario es administrador: {}", userEntity.getUserName());

        String roleName = userEntity.getRoleName();
        if (roleName == null) {
            log.warn("⚠️ El rol del usuario es nulo: {}", userEntity.getUserName());
            return false;
        }

        boolean roleMatch = Stream.of(
                        "ADMIN",
                        "ADMINISTRADOR",
                        "SUPERADMIN")
                .anyMatch(admin -> admin.equalsIgnoreCase(roleName.trim()));

        log.info("✅ Verificacion de rol completada. Es administrador: {}", roleMatch);

        return roleMatch;
    }

    public LocalDateTime parsearFechaInicio(String fecha) {
        log.info("📅 [FECHA] Intentando parsear fecha de inicio: {}", fecha);

        if (fecha == null || fecha.isBlank()) {
            log.error("⚠️ [ERROR] La fecha de inicio recibida es nula o vacia");
            throw new IllegalArgumentException("La fecha de inicio es obligatoria");
        }
        try {
            LocalDateTime res = LocalDateTime.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("✅ [PARSEADO] Fecha inicio procesada (Formato Completo): {}", res);
            return res;
        } catch (DateTimeParseException ignored) {}

        try {
            LocalDateTime res = LocalDateTime.parse(fecha);
            log.info("✅ [PARSEADO] Fecha inicio procesada (ISO): {}", res);
            return res;
        } catch (DateTimeParseException ignored) {}

        try {
            LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime res = localDate.atStartOfDay();
            log.info("✅ [PARSEADO] Fecha inicio procesada (Solo Fecha -> 00:00:00): {}", res);
            return res;
        } catch (DateTimeParseException e) {
            log.error("❌ [FORMATO INVALIDO] No se pudo parsear la fecha de inicio: {}", fecha);
            throw new IllegalArgumentException("Formato de fecha de inicio invalido. Use yyyy-MM-dd o yyyy-MM-dd HH:mm:ss");
        }
    }

    public LocalDateTime parsearFechaFin(String fecha) {
        log.info("📅 [FECHA] Intentando parsear fecha de fin: {}", fecha);

        if (fecha == null || fecha.isBlank()) {
            log.error("⚠️ [ERROR] La fecha de fin recibida es nula o vacia");
            throw new IllegalArgumentException("La fecha de fin es obligatoria");
        }
        try {
            LocalDateTime res = LocalDateTime.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("✅ [PARSEADO] Fecha fin procesada (Formato Completo): {}", res);
            return res;
        } catch (DateTimeParseException ignored) {}

        try {
            LocalDateTime res = LocalDateTime.parse(fecha);
            log.info("✅ [PARSEADO] Fecha fin procesada (ISO): {}", res);
            return res;
        } catch (DateTimeParseException ignored) {}

        try {
            LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime res = localDate.atTime(23, 59, 59);
            log.info("✅ [PARSEADO] Fecha fin procesada (Solo Fecha -> 23:59:59): {}", res);
            return res;
        } catch (DateTimeParseException e) {
            log.error("❌ [FORMATO INVALIDO] No se pudo parsear la fecha de fin: {}", fecha);
            throw new IllegalArgumentException("Formato de fecha de fin invalido. Use yyyy-MM-dd o yyyy-MM-dd HH:mm:ss");
        }
    }

    public UserEntity guardarUsuarioBD(UserEntity userEntity) {
        try {
            return userRepository.save(userEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violacion de integridad al guardar el usuario: {}", e.getMessage(), e);
            throw UserPersistenceException.integrityViolation(e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar el usuario: {}", e.getMessage(), e);
            throw UserPersistenceException.dataAccessError(e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar el usuario: {}", e.getMessage(), e);
            throw UserPersistenceException.unexpected(e);
        }
    }

    public void eliminarUsuarioBD(UserEntity userEntity) {
        try {
            userRepository.delete(userEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violacion de integridad al eliminar el usuario: {}", e.getMessage(), e);
            throw UserDeletionException.integrityViolation(e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar el usuario: {}", e.getMessage(), e);
            throw UserDeletionException.dataAccessError(e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar el usuario: {}", e.getMessage(), e);
            throw UserDeletionException.unexpected(e);
        }
    }
}
