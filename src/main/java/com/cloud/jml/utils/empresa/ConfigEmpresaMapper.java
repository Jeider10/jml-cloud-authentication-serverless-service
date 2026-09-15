package com.cloud.jml.utils.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaPapeleraResponseDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import com.cloud.jml.utils.date.FormatearFecha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class ConfigEmpresaMapper {

    private final ConfigEmpresaFormatearFecha configEmpresaFormatearFecha;
    private final FormatearFecha formatearFecha;

    public ConfigEmpresaMapper(ConfigEmpresaFormatearFecha configEmpresaFormatearFecha, FormatearFecha formatearFecha) {
        this.configEmpresaFormatearFecha = configEmpresaFormatearFecha;
        this.formatearFecha = formatearFecha;
        log.info("🔥 ConfigEmpresaMapper inicializado correctamente.");
    }

    public ConfigEmpresaEntity mapRequestDtoToEntity(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📦 [MAPEO] Iniciando mapeo DTO → Entity para empresa: nombre={}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaEntity entity = new ConfigEmpresaEntity();

        entity.setNit(configEmpresaRequestDTO.getNit());
        entity.setNombreEmpresa(configEmpresaRequestDTO.getNombreEmpresa());
        entity.setDireccion(configEmpresaRequestDTO.getDireccion());
        entity.setTelefono(configEmpresaRequestDTO.getTelefono());
        entity.setMensaje(configEmpresaRequestDTO.getMensaje());
        entity.setEmail(configEmpresaRequestDTO.getEmail());
        entity.setLogo(configEmpresaRequestDTO.getLogo());
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setEliminado(false);

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para empresa: nombre={}", entity.getNombreEmpresa());

        return entity;
    }

    public ConfigEmpresaResponseDTO mapEntityToResponseDto(ConfigEmpresaEntity entity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para empresa: nombre={}", entity.getNombreEmpresa());

        ConfigEmpresaResponseDTO dto = buildConfigEmpresaResponseDTO(entity);
        configEmpresaFormatearFecha.asignarFechasFormateadas(entity, dto);

        log.info("✅ [MAPEO] Mapeo completado Entity → DTO para empresa: nombre={}", dto.getNombreEmpresa());

        return dto;
    }

    public ConfigEmpresaResponseDTO buildConfigEmpresaResponseDTO(ConfigEmpresaEntity entity) {

        ConfigEmpresaResponseDTO dto = new ConfigEmpresaResponseDTO();

        dto.setNit(entity.getNit());
        dto.setNombreEmpresa(entity.getNombreEmpresa());
        dto.setDireccion(entity.getDireccion());
        dto.setTelefono(entity.getTelefono());
        dto.setMensaje(entity.getMensaje());
        dto.setEmail(entity.getEmail());
        dto.setLogo(entity.getLogo());

        return dto;
    }

    public ConfigEmpresaPapeleraResponseDTO mapEntityToPapeleraDto(ConfigEmpresaEntity entity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → PapeleraDTO para empresa: {}", entity.getNit());

        ConfigEmpresaPapeleraResponseDTO dto = new ConfigEmpresaPapeleraResponseDTO();

        dto.setNit(entity.getNit());
        dto.setNombreEmpresa(entity.getNombreEmpresa());
        dto.setDireccion(entity.getDireccion());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());
        dto.setFechaCreacion(formatearFecha.formatearFecha(entity.getFechaCreacion()));
        dto.setFechaEliminacion(formatearFecha.formatearFecha(entity.getFechaEliminacion()));
        dto.setEliminadoPorId(entity.getEliminadoPorId());
        dto.setEliminadoPorNombre(entity.getEliminadoPorNombre());

        log.info("✅ [MAPEO] Mapeo papelera completado para empresa: {}", dto.getNit());

        return dto;
    }

    public void actualizarDatosEmpresa(ConfigEmpresaRequestDTO dto, ConfigEmpresaEntity entity) {
        log.info("📌 Actualizando datos de la empresa: {}", dto.getNombreEmpresa());

        entity.setNombreEmpresa(dto.getNombreEmpresa());
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        entity.setMensaje(dto.getMensaje());
        entity.setEmail(dto.getEmail());

        if (dto.getLogo() != null) {
            entity.setLogo(dto.getLogo());
        }

        entity.setFechaActualizacion(LocalDateTime.now());

        log.info("✅ Datos de la empresa actualizados: {}", entity.getNombreEmpresa());
    }
}
