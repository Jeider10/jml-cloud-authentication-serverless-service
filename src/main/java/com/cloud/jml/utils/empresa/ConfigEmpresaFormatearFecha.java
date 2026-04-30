package com.cloud.jml.utils.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import com.cloud.jml.utils.date.FormatearFecha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class ConfigEmpresaFormatearFecha {

    private final FormatearFecha formatearFecha;

    public ConfigEmpresaFormatearFecha(FormatearFecha formatearFecha) {
        this.formatearFecha = formatearFecha;
        log.info("🔥 ConfigEmpresaFormatearFecha inicializado correctamente.");
    }

    public void asignarFechasFormateadas(ConfigEmpresaEntity configEmpresaEntity, ConfigEmpresaResponseDTO configEmpresaResponseDTO) {
        if (configEmpresaEntity == null || configEmpresaResponseDTO == null) {
            log.warn("⚠️ Entidad o DTO nulos al intentar asignar fechas formateadas.");
            return;
        }

        log.info("📦 Asignando fechas formateadas a la empresa: {}", configEmpresaEntity.getNombreEmpresa());

        // Fecha de creacion
        String fechaCreacion = formatearFecha.formatearFecha(configEmpresaEntity.getFechaCreacion());
        configEmpresaResponseDTO.setFechaCreacion(fechaCreacion);
        log.info("🕓 Fecha de creacion asignada: {}", fechaCreacion);

        // Fecha de actualizacion
        String fechaActualizacion = formatearFecha.formatearFecha(configEmpresaEntity.getFechaActualizacion());
        configEmpresaResponseDTO.setFechaActualizacion(fechaActualizacion);
        log.info("🕓 Fecha de actualizacion asignada: {}", fechaActualizacion);
    }
}
