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

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class UserUtils {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserUtils(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        log.info("🔥 UserUtils inicializado correctamente.");
    }

    public UserEntity validarExistenciaUsuario(UserRequestDTO userRequestDTO) {
        log.info("✅ Inicia validación de existencia del usuario para actualización: {}", userRequestDTO.getUserName());

        // 🔹 Paso 1: Buscar el usuario actual por identificación (el que debe existir)
        Optional<UserEntity> usuarioActualOptional = userRepository.findByIdentificacion(userRequestDTO.getIdentificacion());

        if (usuarioActualOptional.isEmpty()) {
            log.error("❌ No se encontró el usuario: {} con identificación: {}", userRequestDTO.getUserName(), userRequestDTO.getIdentificacion());
            throw new UserNotFoundException(userRequestDTO.getUserName());
        }

        UserEntity usuarioActual = usuarioActualOptional.get();

        // 🔹 Paso 2: Buscar si existe otro usuario con el nuevo userName
        Optional<UserEntity> usuarioConNuevoUserNameOpt = userRepository.findByUserName(userRequestDTO.getUserName());

        if (usuarioConNuevoUserNameOpt.isPresent()) {
            UserEntity usuarioConNuevoUserName = usuarioConNuevoUserNameOpt.get();

            // Si el usuario con ese userName tiene otra identificación, no se puede usar ese nombre
            Long usuarioActualIdentificacion = usuarioActual.getIdentificacion();
            if (!Objects.equals(usuarioConNuevoUserName.getIdentificacion(), usuarioActualIdentificacion)) {
                log.warn("⚠️ El userName '{}' ya pertenece a otro usuario con identificación diferente: {}",
                        userRequestDTO.getUserName(), usuarioConNuevoUserName.getIdentificacion());
                throw new UserAlreadyExistsException(userRequestDTO.getUserName());
            }
        } else {
            log.info("ℹ️ El nuevo userName '{}' está disponible para uso.", userRequestDTO.getUserName());
        }

        log.info("✅ [FINALIZADO] Usuario existente validado correctamente para actualización: {}", usuarioActual.getUserName());

        return usuarioActual;
    }

    public RoleEntity obtenerRolePorCodigo(int roleCode) {
        log.info("✅ Inicia consulta de role con código: {}", roleCode);

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

    public void validarUnicoAdministrador(UserEntity userEntity) {
        log.info("✅ Verificando si el usuario es único administrador: {}", userEntity.getUserName());

        // 🚫 Validar que no haya más de un administrador
        if (esRolAdministrador(userEntity)) {
            boolean existeAdmin = userRepository.existsByRoleNameIgnoreCase(userEntity.getRoleName());

            log.info("✅ Verificando si ya existe un usuario con el rol de administrador: {}", existeAdmin);

            if (existeAdmin) {
                // 1. Define el mensaje base
                String baseMessage = "❌ [ERROR] Ya existe un usuario con el rol de administrador ('%s'). "
                        + "No se permiten múltiples usuarios administradores.";

                // 2. Formatea el mensaje, reemplazando '%s' por el valor real
                String formattedMessage = String.format(baseMessage, userEntity.getRoleName());

                // 3. Usa el mensaje formateado para el log y la excepción
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

        log.info("✅ Verificación de rol completada. Es administrador: {}", roleMatch);

        return roleMatch;
    }

    public UserEntity guardarUsuarioBD(UserEntity userEntity) {
        try {
            return userRepository.save(userEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al guardar el usuario: {}", e.getMessage(), e);
            throw new UserPersistenceException("Error de integridad en base de datos al guardar el usuario", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar el usuario: {}", e.getMessage(), e);
            throw new UserPersistenceException("Error al guardar el usuario en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar el usuario: {}", e.getMessage(), e);
            throw new UserPersistenceException("Error inesperado al registrar el usuario", e);
        }
    }

    public void eliminarUsuarioBD(UserEntity userEntity) {
        try {
            userRepository.delete(userEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al eliminar el usuario: {}", e.getMessage(), e);
            throw new UserDeletionException("Error de integridad en base de datos al eliminar el usuario", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar el usuario: {}", e.getMessage(), e);
            throw new UserDeletionException("Error al eliminar el usuario en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar el usuario: {}", e.getMessage(), e);
            throw new UserDeletionException("Error inesperado al eliminar el usuario", e);
        }
    }
}
