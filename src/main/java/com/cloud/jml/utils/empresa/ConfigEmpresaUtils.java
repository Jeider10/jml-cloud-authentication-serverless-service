package com.cloud.jml.utils.user;

import com.cloud.jml.dto.user.UserRequestDTO;
import com.cloud.jml.exception.role.RoleNotFoundException;
import com.cloud.jml.exception.user.UserCredencialesIncorrectasException;
import com.cloud.jml.exception.user.UserNotFoundException;
import com.cloud.jml.exception.user.UserPersistenceException;
import com.cloud.jml.model.RoleEntity;
import com.cloud.jml.model.UserEntity;
import com.cloud.jml.repository.RoleRepository;
import com.cloud.jml.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    /**
     * 💾 Guarda la orden en BD con manejo de excepciones.
     */
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

    /**
     * 🗑️ Elimina la orden de BD con manejo de excepciones.
     */
    public void eliminarUsuarioBD(UserEntity userEntity) {
        try {
            userRepository.delete(userEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al eliminar el usuario: {}", e.getMessage(), e);
            throw new UserPersistenceException("Error de integridad en base de datos al eliminar el usuario", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar el usuario: {}", e.getMessage(), e);
            throw new UserPersistenceException("Error al eliminar el usuario en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar el usuario: {}", e.getMessage(), e);
            throw new UserPersistenceException("Error inesperado al eliminar el usuario", e);
        }
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
//            throw new UserCredencialesIncorrectasException(userEntity.getUserName());
//        }

        log.info("✅ [FINALIZADO] Usuario verificado correctamente para actualización: {}", userEntity.getUserName());

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
}
