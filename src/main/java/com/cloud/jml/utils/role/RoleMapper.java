package com.cloud.jml.utils.role;

import com.cloud.jml.dto.role.RoleRequestDTO;
import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.model.RoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class RoleMapper {

    private final RoleFormatearFecha roleFormatearFecha;

    public RoleMapper(RoleFormatearFecha roleFormatearFecha) {
        this.roleFormatearFecha = roleFormatearFecha;
        log.info("🔥 RoleMapper inicializado correctamente.");
    }

    /**
     * 📦 Convierte un DTO de solicitud de rol en una entidad lista para persistir.
     */
    public RoleEntity mapRequestDtoToEntity(RoleRequestDTO roleRequestDTO) {
        log.info("📦 [MAPEO] Iniciando mapeo DTO → Entity para role: nombre={}", roleRequestDTO.getRoleName());

        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setRoleCode(roleRequestDTO.getRoleCode());
        roleEntity.setRoleName(roleRequestDTO.getRoleName());
        roleEntity.setFechaCreacion(LocalDateTime.now());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para role: nombre={}", roleEntity.getRoleName());

        return roleEntity;
    }

    /**
     * 📦 Convierte una entidad de cliente en un DTO de respuesta.
     */
    public RoleResponseDTO mapEntityToResponseDto(RoleEntity roleEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para role: nombre={}", roleEntity.getRoleName());

        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();

        roleResponseDTO.setRoleCode(roleEntity.getRoleCode());
        roleResponseDTO.setRoleName(roleEntity.getRoleName());

        // 🕓 Formateo de fechas
        roleFormatearFecha.asignarFechasFormateadas(roleEntity, roleResponseDTO);

        log.info("✅ [MAPEO] Mapeo completado Entity → DTO para role: nombre={}", roleResponseDTO.getRoleName());

        return roleResponseDTO;
    }
}
