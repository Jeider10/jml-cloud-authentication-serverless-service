package com.cloud.jml.utils.role;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.exception.role.RoleDeletionException;
import com.cloud.jml.exception.role.RoleNotFoundException;
import com.cloud.jml.exception.role.RolePersistenceException;
import com.cloud.jml.model.role.RoleEntity;
import com.cloud.jml.repository.role.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class RoleUtils {

    private final RoleRepository roleRepository;

    public RoleUtils(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        log.info("🔥 RoleUtils inicializado correctamente.");
    }

    public RoleEntity validarExistenciaRole(RoleRequestDTO roleRequestDTO) {
        log.info("📌 Inicia validacion de existencia del role con codigo: {}", roleRequestDTO.getRoleCode());

        Optional<RoleEntity> optionalRole = roleRepository.findByRoleCode(roleRequestDTO.getRoleCode());

        if (optionalRole.isPresent()) {
            log.info("📌 Role encontrado con codigo: {}", roleRequestDTO.getRoleCode());
            return optionalRole.get();
        } else {
            log.warn("⚠️ Role no encontrado con codigo: {}", roleRequestDTO.getRoleCode());
            throw new RoleNotFoundException(roleRequestDTO.getRoleCode());
        }
    }

    public RoleEntity guardarRoleBD(RoleEntity roleEntity) {
        try {
            return roleRepository.save(roleEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violacion de integridad al guardar el Role: {}", e.getMessage(), e);
            throw RolePersistenceException.integrityViolation(e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar el Role: {}", e.getMessage(), e);
            throw RolePersistenceException.dataAccessError(e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar el Role: {}", e.getMessage(), e);
            throw RolePersistenceException.unexpected(e);
        }
    }

    public void eliminarRoleBD(RoleEntity roleEntity) {
        try {
            roleRepository.delete(roleEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violacion de integridad al eliminar el Role: {}", e.getMessage(), e);
            throw RoleDeletionException.integrityViolation(e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar el Role: {}", e.getMessage(), e);
            throw RoleDeletionException.dataAccessError(e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar el Role: {}", e.getMessage(), e);
            throw RoleDeletionException.unexpected(e);
        }
    }
}
