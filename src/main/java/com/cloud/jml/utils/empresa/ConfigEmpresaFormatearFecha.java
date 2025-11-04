package com.cloud.jml.utils.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class ConfigEmpresaFormatearFecha {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("d/M/yyyy, h:mm:ss a", Locale.of("es", "CO"));

    public ConfigEmpresaFormatearFecha() {
        log.info("🔥 ConfigEmpresaFormatearFecha inicializado correctamente.");
    }

    /**
     * 🧩 Asigna las fechas formateadas (creación y actualización)
     * desde la entidad a la respuesta DTO.
     */
    public void asignarFechasFormateadas(ConfigEmpresaEntity configEmpresaEntity, ConfigEmpresaResponseDTO configEmpresaResponseDTO) {
        if (configEmpresaEntity == null || configEmpresaResponseDTO == null) {
            log.warn("⚠️ Entidad o DTO nulos al intentar asignar fechas formateadas.");
            return;
        }

        log.info("📦 Asignando fechas formateadas a la empresa: {}", configEmpresaEntity.getNombreEmpresa());

        // Fecha de creación
        String fechaCreacion = formatearFecha(configEmpresaEntity.getFechaCreacion());
        configEmpresaResponseDTO.setFechaCreacion(fechaCreacion);
        log.debug("🕓 Fecha de creación asignada: {}", fechaCreacion);

        // Fecha de actualización
        String fechaActualizacion = formatearFecha(configEmpresaEntity.getFechaActualizacion());
        configEmpresaResponseDTO.setFechaActualizacion(fechaActualizacion);
        log.debug("🕓 Fecha de actualización asignada: {}", fechaActualizacion);
    }

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
}
