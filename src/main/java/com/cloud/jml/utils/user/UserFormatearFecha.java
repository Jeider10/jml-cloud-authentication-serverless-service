package com.cloud.jml.utils.user;

import com.cloud.jml.dto.user.UserResponseDTO;
import com.cloud.jml.model.user.UserEntity;
import com.cloud.jml.utils.date.FormatearFecha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class UserFormatearFecha {

    private final FormatearFecha formatearFecha;

    public UserFormatearFecha(FormatearFecha formatearFecha) {
        this.formatearFecha = formatearFecha;
        log.info("🔥 UserFormatearFecha inicializado correctamente.");
    }

    public void asignarFechasFormateadas(UserEntity userEntity, UserResponseDTO userResponseDTO) {
        if (userEntity == null || userResponseDTO == null) {
            log.warn("⚠️ Entidad o DTO nulos al intentar asignar fechas formateadas.");
            return;
        }

        log.info("📦 Asignando fechas formateadas al usuario: {}", userEntity.getRoleName());

        // Fecha de creacion
        String fechaCreacion = formatearFecha.formatearFecha(userEntity.getFechaCreacion());
        userResponseDTO.setFechaCreacion(fechaCreacion);
        log.info("🕓 Fecha de creacion asignada: {}", fechaCreacion);

        // Fecha de actualizacion
        String fechaActualizacion = formatearFecha.formatearFecha(userEntity.getFechaActualizacion());
        userResponseDTO.setFechaActualizacion(fechaActualizacion);
        log.info("🕓 Fecha de actualizacion asignada: {}", fechaActualizacion);
    }
}
