package com.cloud.jml.utils.role;

import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.model.role.RoleEntity;
import com.cloud.jml.utils.date.FormatearFecha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class RoleFormatearFecha {

    private final FormatearFecha formatearFecha;

    public RoleFormatearFecha(FormatearFecha formatearFecha) {
        this.formatearFecha = formatearFecha;
        log.info("🔥 RoleFormatearFecha inicializado correctamente.");
    }

    public void asignarFechasFormateadas(RoleEntity roleEntity, RoleResponseDTO roleResponseDTO) {
        if (roleEntity == null || roleResponseDTO == null) {
            log.warn("⚠️ Entidad o DTO nulos al intentar asignar fechas formateadas.");
            return;
        }

        log.info("📦 Asignando fechas formateadas al role: {}", roleEntity.getRoleName());

        // Fecha de creacion
        String fechaCreacion = formatearFecha.formatearFecha(roleEntity.getFechaCreacion());
        roleResponseDTO.setFechaCreacion(fechaCreacion);
        log.info("🕓 Fecha de creacion asignada: {}", fechaCreacion);

        // Fecha de actualizacion
        String fechaActualizacion = formatearFecha.formatearFecha(roleEntity.getFechaActualizacion());
        roleResponseDTO.setFechaActualizacion(fechaActualizacion);
        log.info("🕓 Fecha de actualizacion asignada: {}", fechaActualizacion);
    }
}
