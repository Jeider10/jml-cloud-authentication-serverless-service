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

    private final RoleUtils roleUtils;

    public RoleMapper(RoleUtils roleUtils) {
        this.roleUtils = roleUtils;
        log.info("🔥 RoleMapper inicializado correctamente.");
    }

    public RoleEntity mapRequestDtoToEntity(RoleRequestDTO roleRequestDTO) {
        log.info("📌 Iniciando mapeo DTO a Entity para crear Role");

        RoleEntity roleEntity = new RoleEntity();

        roleEntity.setRoleCode(roleRequestDTO.getRoleCode());
        roleEntity.setRoleName(roleRequestDTO.getRoleName());
        roleEntity.setFechaCreacion(LocalDateTime.now());

        log.info("📌 Finalizando mapeo DTO a Entity para crear Role");

        return roleEntity;
    }

    public RoleResponseDTO mapEntityToResponseDto(RoleEntity roleEntity) {
        log.info("📌 Iniciando mapeo Entity a DTO para crear Role");

        RoleResponseDTO roleResponseDTO = new RoleResponseDTO();

        roleResponseDTO.setRoleCode(roleEntity.getRoleCode());
        roleResponseDTO.setRoleName(roleEntity.getRoleName());

        // 🔹 Formatear fechas
        roleUtils.asignarFechasFormateadas(roleEntity, roleResponseDTO);

        log.info("📌 Finalizando mapeo Entity a DTO para crear Role");

        return roleResponseDTO;
    }
}
