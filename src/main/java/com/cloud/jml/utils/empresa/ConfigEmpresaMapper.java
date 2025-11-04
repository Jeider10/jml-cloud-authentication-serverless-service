package com.cloud.jml.utils.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class ConfigEmpresaMapper {

    private final ConfigEmpresaFormatearFecha configEmpresaFormatearFecha;

    public ConfigEmpresaMapper(ConfigEmpresaFormatearFecha configEmpresaFormatearFecha) {
        this.configEmpresaFormatearFecha = configEmpresaFormatearFecha;
        log.info("🔥 ConfigEmpresaMapper inicializado correctamente.");
    }

    /**
     * 📦 Convierte un DTO de solicitud de empresa en una entidad lista para persistir.
     */
    public ConfigEmpresaEntity mapRequestDtoToEntity(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📦 [MAPEO] Iniciando mapeo DTO → Entity para empresa: nombre={}", configEmpresaRequestDTO.getNombreEmpresa());

        ConfigEmpresaEntity configEmpresaEntity = new ConfigEmpresaEntity();

        configEmpresaEntity.setNit(configEmpresaRequestDTO.getNit());
        configEmpresaEntity.setNombreEmpresa(configEmpresaRequestDTO.getNombreEmpresa());
        configEmpresaEntity.setDireccion(configEmpresaRequestDTO.getDireccion());
        configEmpresaEntity.setTelefono(configEmpresaRequestDTO.getTelefono());
        configEmpresaEntity.setMensaje(configEmpresaRequestDTO.getMensaje());
        configEmpresaEntity.setLogo(configEmpresaRequestDTO.getLogo());
        configEmpresaEntity.setFechaCreacion(LocalDateTime.now());

        log.info("✅ [MAPEO] Mapeo completado DTO → Entity para empresa: nombre={}", configEmpresaEntity.getNombreEmpresa());

        return configEmpresaEntity;
    }

    /**
     * 📦 Convierte una entidad de empresa en un DTO de respuesta.
     */
    public ConfigEmpresaResponseDTO mapEntityToResponseDto(ConfigEmpresaEntity configEmpresaEntity) {
        log.info("📦 [MAPEO] Iniciando mapeo Entity → DTO para empresa: nombre={}", configEmpresaEntity.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = buildConfigEmpresaResponseDTO(configEmpresaEntity);

        // 🕓 Formateo de fechas
        configEmpresaFormatearFecha.asignarFechasFormateadas(configEmpresaEntity, configEmpresaResponseDTO);

        log.info("✅ [MAPEO] Mapeo completado Entity → DTO para empresa: nombre={}", configEmpresaResponseDTO.getNombreEmpresa());

        return configEmpresaResponseDTO;
    }

    public ConfigEmpresaResponseDTO buildConfigEmpresaResponseDTO(ConfigEmpresaEntity configEmpresaEntity) {
        log.info("📦 [MAPEO] Iniciando construcción de DTO de respuesta para empresa: {}", configEmpresaEntity.getNombreEmpresa());

        ConfigEmpresaResponseDTO configEmpresaResponseDTO = new ConfigEmpresaResponseDTO();

        configEmpresaResponseDTO.setNit(configEmpresaEntity.getNit());
        configEmpresaResponseDTO.setNombreEmpresa(configEmpresaEntity.getNombreEmpresa());
        configEmpresaResponseDTO.setDireccion(configEmpresaEntity.getDireccion());
        configEmpresaResponseDTO.setTelefono(configEmpresaEntity.getTelefono());
        configEmpresaResponseDTO.setMensaje(configEmpresaEntity.getMensaje());
        configEmpresaResponseDTO.setLogo(configEmpresaEntity.getLogo());

        log.info("✅ [MAPEO] Mapeo completado de DTO de respuesta para empresa: {}", configEmpresaResponseDTO.getNombreEmpresa());

        return configEmpresaResponseDTO;
    }
}
