package com.cloud.jml.service;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.exception.empresa.ConfigEmpresaDuplicationException;
import com.cloud.jml.exception.empresa.ConfigEmpresaNotFoundException;
import com.cloud.jml.model.ConfigEmpresaEntity;
import com.cloud.jml.repository.ConfigEmpresaRepository;
import com.cloud.jml.utils.empresa.ConfigEmpresaMapper;
import com.cloud.jml.utils.empresa.ConfigEmpresaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class ConfigEmpresaService {

    private final ConfigEmpresaRepository configEmpresaRepository;
    private final ConfigEmpresaMapper mapper;
    private final ConfigEmpresaUtils configEmpresaUtils;

    public ConfigEmpresaService(ConfigEmpresaRepository configEmpresaRepository, ConfigEmpresaMapper mapper, ConfigEmpresaUtils configEmpresaUtils) {
        this.configEmpresaRepository = configEmpresaRepository;
        this.mapper = mapper;
        this.configEmpresaUtils = configEmpresaUtils;
        log.info("🔥 ConfigEmpresaService inicializado correctamente.");
    }

    @Transactional(readOnly = true)
    public List<ConfigEmpresaResponseDTO> obtenerPrimeraEmpresa() {
        log.info("🔍 [CONSULTA] Recuperando la primera empresa registrada");

        List<ConfigEmpresaEntity> empresaExistente = configEmpresaRepository.findAll();

        if (empresaExistente.isEmpty()) {
            log.warn("⚠️ [RESULTADO] No se encontró ninguna empresa registrada en la base de datos");
            return List.of();
        }

        log.info("📦 [MAPEO] Transformando {} entidades de empresa a DTOs", empresaExistente.size());

        // convertir a stream
        Stream<ConfigEmpresaEntity> entityStream = empresaExistente.stream();

        // mapear entidades a DTOs
        Stream<ConfigEmpresaResponseDTO> streamDto = entityStream.map(mapper::mapEntityToResponseDto);

        // recolectar en lista
        List<ConfigEmpresaResponseDTO> empleadoResponse = streamDto.toList();

        log.info("✅ [FINALIZADO] Total de empresas mapeados y retornados: {}", empleadoResponse.size());

        return empleadoResponse;
    }

    @Transactional(readOnly = true)
    public ConfigEmpresaResponseDTO buscarEmpresaNic(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("🔍 [CONSULTA] Iniciando búsqueda de empresa con nic: {}", configEmpresaRequestDTO.getNic());

        Optional<ConfigEmpresaEntity> empresaExistente = configEmpresaRepository.findByNic(configEmpresaRequestDTO.getNic());

        if (empresaExistente.isEmpty()) {
            log.warn("❌ [RESULTADO] Empresa no encontrada con nic: {}", configEmpresaRequestDTO.getNic());
            return null;
        }

        ConfigEmpresaEntity configEmpresaEntity = empresaExistente.get();
        log.info("📦 [ENCONTRADO] Empresa encontrada -> nic: {}, nombre: {}",
                configEmpresaEntity.getNic(), configEmpresaEntity.getNombreEmpresa());

        log.info("📦 [MAPEO] Transformando entidad de empresa a DTO. (buscarEmpresaNic)");
        ConfigEmpresaResponseDTO configEmpresaResponseDTO = mapper.mapEntityToResponseDto(configEmpresaEntity);
        log.info("📦 [MAPEO] Empresa mapeado a DTO con nic: {}", configEmpresaResponseDTO.getNic());

        log.info("✅ [FINALIZADO] Empresa encontrada con nic: {}", configEmpresaResponseDTO.getNic());

        return configEmpresaResponseDTO;
    }

    @Transactional
    public ConfigEmpresaResponseDTO registrarDatosEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de registro de datos de la empresa: {} con nic: {}", configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNic());

        Optional<ConfigEmpresaEntity> existingEmpresa = configEmpresaRepository.findByNic(configEmpresaRequestDTO.getNic());

        if (existingEmpresa.isPresent()) {
            log.warn("❌ [ERROR] Empresa: {} ya existe con ese nic: {}", configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNic());
            throw new ConfigEmpresaDuplicationException(configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNic());
        }

        log.info("📦 [MAPEO] Transformando DTO a entidad de usuario");
        ConfigEmpresaEntity configEmpresaEntity = mapper.mapRequestDtoToEntity(configEmpresaRequestDTO);
        log.info("📦 [MAPEO] Empresa mapeado a entidad. nombre: {}, nic: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNic());

        ConfigEmpresaEntity guardarEmpresa = configEmpresaUtils.guardarEmpresaBD(configEmpresaEntity);
        log.info("💾 [PERSISTENCIA] Empresa guardado exitosamente. nombre: {}, nic: {}", guardarEmpresa.getNombreEmpresa(), guardarEmpresa.getNic());

        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (registrarDatosEmpresa)");
        ConfigEmpresaResponseDTO configEmpresaResponseDTO = mapper.mapEntityToResponseDto(guardarEmpresa);
        log.info("📦 [MAPEO] Empresa mapeado a DTO. nombre: {} con nic: {}",
                configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNic());

        log.info("✅ [FINALIZADO] Empresa creada correctamente: {} con nic {}", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNic());

        return configEmpresaResponseDTO;
    }

    @Transactional
    public ConfigEmpresaResponseDTO actualizarEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de actualización de empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        // Paso 1: Validar existencia
        ConfigEmpresaEntity configEmpresaEntity = configEmpresaUtils.validarExistenciaEmpresa(configEmpresaRequestDTO);

        // Paso 2: Actualizar datos
        configEmpresaUtils.actualizarDatosEmpresa(configEmpresaRequestDTO, configEmpresaEntity);

        // Paso 3: Guardar cambios en la BD
        ConfigEmpresaEntity actualizado = configEmpresaUtils.guardarEmpresaBD(configEmpresaEntity);
        log.info("💾 [PERSISTENCIA] Empresa actualizada correctamente: {} con nic: {}", actualizado.getNombreEmpresa(), actualizado.getNic());

        // Paso 4: Mapear a DTO
        log.info("📦 [MAPEO] Transformando entidad de empresa a DTO. (actualizarEmpresa)");
        ConfigEmpresaResponseDTO configEmpresaResponseDTO = mapper.mapEntityToResponseDto(actualizado);
        log.info("📦 [MAPEO] Empresa mapeado a DTO. nombre: {}, nic: {}, dirección: {}",
                configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNic(), configEmpresaResponseDTO.getDireccion());

        log.info("✅ [FINALIZADO] Actualización de empresa completada: {} con nic: {}", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNic());

        return configEmpresaResponseDTO;
    }

    @Transactional
    public void eliminarEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de eliminación de empresa: {} con nic: {}", configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNic());

        Optional<ConfigEmpresaEntity> empresaExistente = configEmpresaRepository.findByNic(configEmpresaRequestDTO.getNic());

        if (empresaExistente.isPresent()) {
            ConfigEmpresaEntity configEmpresaEntity = empresaExistente.get();
            log.info("📦 [ENCONTRADO] Empresa localizada -> {} con nic: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNic());

            configEmpresaUtils.eliminarUsuarioBD(configEmpresaEntity);
            log.info("🗑️ [ELIMINADO] Empresa eliminada correctamente -> {} con nic: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNic());
        } else {
            log.warn("❌ [NO ENCONTRADO] Empresa: {} no encontrada", configEmpresaRequestDTO.getNombreEmpresa());
            throw new ConfigEmpresaNotFoundException(configEmpresaRequestDTO.getNombreEmpresa());
        }
    }
}
