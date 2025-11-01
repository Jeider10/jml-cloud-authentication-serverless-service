package com.cloud.jml.service;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.exception.role.RoleDuplicationException;
import com.cloud.jml.exception.role.RoleNotFoundException;
import com.cloud.jml.model.RoleEntity;
import com.cloud.jml.repository.RoleRepository;
import com.cloud.jml.utils.role.RoleMapper;
import com.cloud.jml.utils.role.RoleUtils;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional(readOnly = true)
    public List<RoleResponseDTO> listarRoles() {
        log.info("🔍 [CONSULTA] Recuperando todos los roles desde la base de datos.");

        List<RoleEntity> rolesEntity = roleRepository.findAll();

        if (rolesEntity.isEmpty()) {
            log.warn("⚠️ [RESULTADO] No se encontraron roles registrados en la base de datos");
            return List.of();
        }

        log.info("📦 [MAPEO] Transformando {} entidades de roles a DTOs", rolesEntity.size());

        // convertir a stream
        Stream<RoleEntity> entityStream = rolesEntity.stream();

        // mapear entidades a DTOs
        Stream<RoleResponseDTO> streamDto = entityStream.map(mapper::mapEntityToResponseDto);

        // recolectar en lista
        List<RoleResponseDTO> rolesResponse = streamDto.toList();

        log.info("✅ [FINALIZADO] Total de roles mapeados y retornados: {}", rolesResponse.size());

        return rolesResponse;
    }

    @Transactional
    public RoleResponseDTO registrarRole(RoleRequestDTO roleRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de creación de role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        Optional<RoleEntity> roleExistente = roleRepository.findByRoleCodeOrRoleName(roleRequestDTO.getRoleCode(), roleRequestDTO.getRoleName());

        if (roleExistente.isPresent()) {
            log.warn("❌ [ERROR] Role duplicado detectado: {}", roleRequestDTO.getRoleCode());
            throw new RoleDuplicationException(roleRequestDTO.getRoleCode());
        }

        log.info("📦 [MAPEO] Transformando DTO a entidad de role");
        RoleEntity roleEntity = mapper.mapRequestDtoToEntity(roleRequestDTO);
        log.info("📦 [MAPEO] Role mapeado a entidad. role: {} con código: {}", roleEntity.getRoleName(), roleEntity.getRoleCode());

        RoleEntity guardarRole = roleUtils.guardarRoleBD(roleEntity);
        log.info("💾 [PERSISTENCIA] Role guardado exitosamente. role: {} con código: {}", guardarRole.getRoleName(), guardarRole.getRoleCode());

        log.info("📦 [MAPEO] Transformando entidad de role a DTO. (registrarRole)");
        RoleResponseDTO roleResponseDTO = mapper.mapEntityToResponseDto(guardarRole);
        log.info("📦 [MAPEO] Role mapeado a DTO. nombre: {} con código: {}",
                roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        log.info("✅ [FINALIZADO] Role creado correctamente: {} con código {}", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return roleResponseDTO;
    }

    @Transactional(readOnly = true)
    public RoleResponseDTO buscarRolePorCodigo(RoleRequestDTO roleRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de búsqueda de role por código: {}", roleRequestDTO.getRoleCode());

        Optional<RoleEntity> roleEntity = roleRepository.findByRoleCode(roleRequestDTO.getRoleCode());

        if (roleEntity.isEmpty()) {
            log.warn("❌ [NO ENCONTRADO] Role: {} no encontrado con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());
            throw new RoleNotFoundException(roleRequestDTO.getRoleCode());
        }

        log.info("📦 [ENCONTRADO] Role encontrado -> {} con código: {}", roleEntity.get().getRoleName(), roleEntity.get().getRoleCode());

        log.info("📦 [MAPEO] Transformando entidad de role a DTO. (buscarRolePorCodigo)");
        RoleResponseDTO roleResponseDTO = mapper.mapEntityToResponseDto(roleEntity.get());
        log.info("📦 [MAPEO] Role mapeado a DTO. nombre: {} con código: {} descripción: {}",
                roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode(), roleResponseDTO.getDescripcion());

        log.info("✅ [FINALIZADO] Búsqueda de role por código completada: {} con código: {}", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return roleResponseDTO;
    }

    @Transactional
    public RoleResponseDTO actualizarRole(RoleRequestDTO roleRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de actualización de role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        // Paso 1: Validar existencia
        RoleEntity roleEntity = roleUtils.validarExistenciaRole(roleRequestDTO);

        // Paso 2: Actualizar datos
        mapper.actualizarDatosRole(roleRequestDTO, roleEntity);

        // Paso 3: Guardar cambios en la BD
        RoleEntity actualizado = roleUtils.guardarRoleBD(roleEntity);
        log.info("💾 [PERSISTENCIA] Role actualizado con código: {}", actualizado.getRoleCode());

        // Paso 4: Mapear a DTO
        log.info("📦 [MAPEO] Transformando entidad de role a DTO. (actualizarRole)");
        RoleResponseDTO roleResponseDTO = mapper.mapEntityToResponseDto(actualizado);
        log.info("📦 [MAPEO] Role mapeado a DTO. nombre: {}, código: {}",
                roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        log.info("✅ [FINALIZADO] Actualización de role completada: {} con código: {}", roleResponseDTO.getRoleName(), roleResponseDTO.getRoleCode());

        return roleResponseDTO;
    }

    @Transactional
    public void eliminarRole(RoleRequestDTO roleRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de eliminación de  role: {} con código: {}", roleRequestDTO.getRoleName(), roleRequestDTO.getRoleCode());

        Optional<RoleEntity> roleExistente = roleRepository.findByRoleCode(roleRequestDTO.getRoleCode());

        if (roleExistente.isPresent()) {
            RoleEntity roleEntity = roleExistente.get();
            log.info("📦 [ENCONTRADO] Role localizado -> {} con código: {}", roleEntity.getRoleName(), roleEntity.getRoleCode());

            roleUtils.eliminarRoleBD(roleEntity);
            log.info("🗑️ [ELIMINADO] Role eliminado correctamente -> {} con código: {}", roleEntity.getRoleName(), roleEntity.getRoleCode());
        } else {
            log.warn("❌ [NO ENCONTRADO] Role no encontrado con código: {}", roleRequestDTO.getRoleCode());
            throw new RoleNotFoundException(roleRequestDTO.getRoleCode());
        }
    }
}
