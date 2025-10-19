package com.cloud.jml.utils.role;

import com.cloud.jml.dto.role.RoleResponseDTO;
import com.cloud.jml.model.RoleEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class RoleFormatearFecha {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy, h:mm:ss a", Locale.of("es", "CO"));

    /**
     * 🕒 Formatea una fecha LocalDateTime al formato colombiano:
     * Ejemplo → 18/10/2025, 2:35:45 p.m.
     */
    public String formatearFecha(LocalDateTime fecha) {
        if (fecha == null) {
            log.warn("⚠️ Fecha recibida nula, se retorna null.");
            return null;
        }

        String fechaFormateada = fecha.format(FORMATTER).toLowerCase();
        log.info("🕓 Formateando fecha: {}", fechaFormateada);

        // Reemplazar expresiones locales de AM/PM con formato limpio
        fechaFormateada = fechaFormateada
                .replace("a. m.", "a.m.")
                .replace("p. m.", "p.m.");

        log.info("🕓 Fecha formateada correctamente: {}", fechaFormateada);

        return fechaFormateada;
    }

    /**
     * 🧩 Asigna las fechas formateadas (creación y actualización)
     * desde la entidad a la respuesta DTO.
     */
    public void asignarFechasFormateadas(RoleEntity roleEntity, RoleResponseDTO roleResponseDTO) {
        if (roleEntity == null || roleResponseDTO == null) {
            log.warn("⚠️ Entidad o DTO nulos al intentar asignar fechas formateadas.");
            return;
        }

        log.info("📦 Asignando fechas formateadas al role: {}", roleEntity.getRoleName());

        // Fecha de creación
        String fechaCreacion = formatearFecha(roleEntity.getFechaCreacion());
        roleResponseDTO.setFechaCreacion(fechaCreacion);
        log.debug("🕓 Fecha de creación asignada: {}", fechaCreacion);

        // Fecha de actualización
        String fechaActualizacion = formatearFecha(roleEntity.getFechaActualizacion());
        roleResponseDTO.setFechaActualizacion(fechaActualizacion);
        log.debug("🕓 Fecha de actualización asignada: {}", fechaActualizacion);
    }
}
