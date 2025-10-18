package com.cloud.jml.service;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.exception.role.RoleDuplicationException;
import com.cloud.jml.exception.role.RoleNotFoundException;
import com.cloud.jml.exception.role.RolePersistenceException;
import com.cloud.jml.model.RoleEntity;
import com.cloud.jml.repository.RoleRepository;
import com.cloud.jml.utils.role.RoleMapper;
import com.cloud.jml.utils.role.RoleUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper mapper;
    private final RoleUtils roleUtils;

    public RoleService(RoleRepository roleRepository, RoleMapper mapper, RoleUtils roleUtils) {
        this.roleRepository = roleRepository;
        this.mapper = mapper;
        this.roleUtils = roleUtils;
        log.info("🔥 RoleService inicializado correctamente.");
    }

    @Transactional
    public RoleResponseDTO registrarRole(RoleRequestDTO roleRequestDTO) {
        log.info("📌 Intentando registrar el role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        // Buscar si ya existe un rol con el mismo nombre o código
        Optional<RoleEntity> existingRole = roleRepository.findByRoleCodeOrRoleName(roleRequestDTO.getRoleCode(), roleRequestDTO.getRoleName());

        if (existingRole.isPresent()) {
            log.warn("⚠️ Role duplicado: {}", roleRequestDTO.getRoleCode());
            throw new RoleDuplicationException(roleRequestDTO.getRoleCode());
        }

        // Mapeo de DTO a Entity
        RoleEntity roleEntity = mapper.mapRequestDtoToEntity(roleRequestDTO);

        // Guardamos en la base de datos
        RoleEntity guardarRole = guardarRoleBD(roleEntity);

        // Mapeo de Entity DTO
        RoleResponseDTO roleResponseDTO = mapper.mapEntityToResponseDto(guardarRole);
        log.info("📌 Finaliza creación de Role: {} con código: {}", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return roleResponseDTO;
    }

    private RoleEntity guardarRoleBD(RoleEntity roleEntity) {
        try {
            RoleEntity guardado = roleRepository.save(roleEntity);
            log.info("✅ Role: {} guardado con código: {}", roleEntity.getRoleName(), roleEntity.getRoleCode());
            return guardado;

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

    @Transactional
    public RoleResponseDTO actualizarRole(RoleRequestDTO roleRequestDTO) {
        log.info("📌 Intentando actualizar role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        // Paso 1: Validar existencia
        RoleEntity roleEntity = roleUtils.validarExistenciaRole(roleRequestDTO);

        // Paso 2: Actualizar datos
        roleUtils.actualizarDatosRole(roleRequestDTO, roleEntity);

        // Paso 3: Guardar cambios en la BD
        RoleEntity actualizado = roleRepository.save(roleEntity);
        log.info("✅ Role: {} actualizado con código: {}", actualizado.getRoleName(), actualizado.getRoleCode());

        // Paso 4: Mapear a DTO
        RoleResponseDTO roleResponseDTO = mapper.mapEntityToResponseDto(actualizado);
        log.info("📌 Finaliza actualización de Role: {} con código: {}", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return roleResponseDTO;
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDTO> listarRoles() {
        log.info("📌 Obteniendo todos los roles existentes.");

        // Paso 1: Obtener entidades desde la BD
        List<RoleEntity> rolesEntity = roleRepository.findAll();

        // Paso 2: Convertir a Stream
        Stream<RoleEntity> entityStream = rolesEntity.stream();

        // Paso 3: Mapear cada entidad a DTO
        Stream<RoleResponseDTO> streamDto = entityStream.map(mapper::mapEntityToResponseDto);

        // Paso 4: Convertir a lista final
        List<RoleResponseDTO> rolesResponseDTO = streamDto.toList();

        log.info("📌 Finaliza petición para obtener todos los roles: {}", rolesResponseDTO.size());

        return rolesResponseDTO;
    }

    @Transactional
    public void eliminarRole(RoleRequestDTO roleRequestDTO) {
        log.info("📌 Intentando eliminar role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        Optional<RoleEntity> roleOptional = roleRepository.findByRoleCode(roleRequestDTO.getRoleCode());

        if (roleOptional.isPresent()) {
            RoleEntity roleEntity = roleOptional.get();
            log.info("📌 Role: {} encontrado con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

            roleRepository.delete(roleEntity);
            log.info("✅ Role: {} con código: {} eliminado exitosamente.", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());
        } else {
            log.warn("⚠️ Role: {} no encontrado con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());
            throw new RoleNotFoundException(roleRequestDTO.getRoleCode());
        }
    }
}
