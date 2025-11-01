package com.cloud.jml.utils.role;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.exception.role.RoleNotFoundException;
import com.cloud.jml.exception.role.RolePersistenceException;
import com.cloud.jml.model.RoleEntity;
import com.cloud.jml.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class RoleUtils {

    private final RoleRepository roleRepository;

    public RoleUtils(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        log.info("🔥 RoleUtils inicializado correctamente.");
    }

    /**
     * 💾 Guarda la orden en BD con manejo de excepciones.
     */
    public RoleEntity guardarRoleBD(RoleEntity roleEntity) {
        try {
            return roleRepository.save(roleEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al guardar el Role: {}", e.getMessage(), e);
            throw new RolePersistenceException("Error de integridad en base de datos al guardar el rol", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar el Role: {}", e.getMessage(), e);
            throw new RolePersistenceException("Error al guardar el rol en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar el Role: {}", e.getMessage(), e);
            throw new RolePersistenceException("Error inesperado al registrar el rol", e);
        }
    }

    /**
     * 🗑️ Elimina la orden de BD con manejo de excepciones.
     */
    public void eliminarRoleBD(RoleEntity roleEntity) {
        try {
            roleRepository.delete(roleEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al eliminar el Role: {}", e.getMessage(), e);
            throw new RolePersistenceException("Error de integridad en base de datos al eliminar el rol", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar el Role: {}", e.getMessage(), e);
            throw new RolePersistenceException("Error al eliminar el rol en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar el Role: {}", e.getMessage(), e);
            throw new RolePersistenceException("Error inesperado al eliminar el rol", e);
        }
    }

    public RoleEntity validarExistenciaRole(RoleRequestDTO roleRequestDTO) {
        log.info("📌 Inicia validación de existencia del role con código: {}", roleRequestDTO.getRoleCode());

        Optional<RoleEntity> optionalRole = roleRepository.findByRoleCode(roleRequestDTO.getRoleCode());

        if (optionalRole.isPresent()) {
            log.info("📌 Role encontrado con código: {}", roleRequestDTO.getRoleCode());
            return optionalRole.get();
        } else {
            log.warn("⚠️ Role no encontrado con código: {}", roleRequestDTO.getRoleCode());
            throw new RoleNotFoundException(roleRequestDTO.getRoleCode());
        }
    }
}
