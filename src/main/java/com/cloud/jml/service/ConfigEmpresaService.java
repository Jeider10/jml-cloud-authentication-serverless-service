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
import org.springframework.web.multipart.MultipartFile;

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
    public ConfigEmpresaResponseDTO buscarEmpresaNit(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("🔍 [CONSULTA] Iniciando búsqueda de empresa con nit: {}", configEmpresaRequestDTO.getNit());

        Optional<ConfigEmpresaEntity> empresaExistente = configEmpresaRepository.findByNit(configEmpresaRequestDTO.getNit());

        if (empresaExistente.isEmpty()) {
            log.warn("❌ [RESULTADO] Empresa no encontrada con nit: {}", configEmpresaRequestDTO.getNit());
            return null;
        }

        ConfigEmpresaEntity configEmpresaEntity = empresaExistente.get();
        log.info("📦 [ENCONTRADO] Empresa encontrada -> nit: {}, nombre: {}",
                configEmpresaEntity.getNit(), configEmpresaEntity.getNombreEmpresa());

        log.info("📦 [MAPEO] Transformando entidad de empresa a DTO. (buscarEmpresaNic)");
        ConfigEmpresaResponseDTO configEmpresaResponseDTO = mapper.mapEntityToResponseDto(configEmpresaEntity);
        log.info("📦 [MAPEO] Empresa mapeado a DTO con nit: {}", configEmpresaResponseDTO.getNit());

        log.info("✅ [FINALIZADO] Empresa encontrada con nit: {}", configEmpresaResponseDTO.getNit());

        return configEmpresaResponseDTO;
    }

    @Transactional
    public ConfigEmpresaResponseDTO registrarDatosEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO, MultipartFile file) {
        log.info("🔍 [CONSULTA] Inicio de registro de datos de la empresa: {} con nit: {}", configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNit());

        Optional<ConfigEmpresaEntity> existingEmpresa = configEmpresaRepository.findByNit(configEmpresaRequestDTO.getNit());

        if (existingEmpresa.isPresent()) {
            log.warn("❌ [ERROR] Empresa: {} ya existe con ese nit: {}", configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNit());
            throw new ConfigEmpresaDuplicationException(configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNit());
        }

        if (file != null && !file.isEmpty()) {
            String rutaLogo = configEmpresaUtils.guardarLogoEnBase64(file);
            configEmpresaRequestDTO.setLogo(rutaLogo);
            log.info("🖼️ Logo cargado: {}", rutaLogo);
        }

        log.info("📦 [MAPEO] Transformando DTO a entidad de usuario");
        ConfigEmpresaEntity configEmpresaEntity = mapper.mapRequestDtoToEntity(configEmpresaRequestDTO);
        log.info("📦 [MAPEO] Empresa mapeado a entidad. nombre: {}, nit: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNit());

        ConfigEmpresaEntity guardarEmpresa = configEmpresaUtils.guardarEmpresaBD(configEmpresaEntity);
        log.info("💾 [PERSISTENCIA] Empresa guardado exitosamente. nombre: {}, nit: {}", guardarEmpresa.getNombreEmpresa(), guardarEmpresa.getNit());

        log.info("📦 [MAPEO] Transformando entidad de usuario a DTO. (registrarDatosEmpresa)");
        ConfigEmpresaResponseDTO configEmpresaResponseDTO = mapper.mapEntityToResponseDto(guardarEmpresa);
        log.info("📦 [MAPEO] Empresa mapeado a DTO. nombre: {} con nit: {}",
                configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNit());

        log.info("✅ [FINALIZADO] Empresa creada correctamente: {} con nit {}", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNit());

        return configEmpresaResponseDTO;
    }

    @Transactional
    public ConfigEmpresaResponseDTO actualizarEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO, MultipartFile file) {
        log.info("🔍 [CONSULTA] Inicio de actualización de empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        // Paso 1: Validar existencia
        ConfigEmpresaEntity configEmpresaEntity = configEmpresaUtils.validarExistenciaEmpresa(configEmpresaRequestDTO);

        // Paso 2: Si viene un archivo, subirlo y actualizar el campo logo
        if (file != null && !file.isEmpty()) {
            String rutaLogo = configEmpresaUtils.guardarLogoEnBase64(file);
            configEmpresaRequestDTO.setLogo(rutaLogo);
            log.info("🖼️ Logo actualizado: {}", rutaLogo);
        }

        // Paso 3: Actualizar datos
        configEmpresaUtils.actualizarDatosEmpresa(configEmpresaRequestDTO, configEmpresaEntity);

        // Paso 4: Guardar cambios en la BD
        ConfigEmpresaEntity actualizado = configEmpresaUtils.guardarEmpresaBD(configEmpresaEntity);
        log.info("💾 [PERSISTENCIA] Empresa actualizada correctamente: {} con nit: {}", actualizado.getNombreEmpresa(), actualizado.getNit());

        // Paso 5: Mapear a DTO
        log.info("📦 [MAPEO] Transformando entidad de empresa a DTO. (actualizarEmpresa)");
        ConfigEmpresaResponseDTO configEmpresaResponseDTO = mapper.mapEntityToResponseDto(actualizado);
        log.info("📦 [MAPEO] Empresa mapeado a DTO. nombre: {}, nit: {}, dirección: {}",
                configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNit(), configEmpresaResponseDTO.getDireccion());

        log.info("✅ [FINALIZADO] Actualización de empresa completada: {} con nit: {}", configEmpresaResponseDTO.getNombreEmpresa(), configEmpresaResponseDTO.getNit());

        return configEmpresaResponseDTO;
    }

    @Transactional
    public void eliminarEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("🔍 [CONSULTA] Inicio de eliminación de empresa: {} con nit: {}", configEmpresaRequestDTO.getNombreEmpresa(), configEmpresaRequestDTO.getNit());

        Optional<ConfigEmpresaEntity> empresaExistente = configEmpresaRepository.findByNit(configEmpresaRequestDTO.getNit());

        if (empresaExistente.isPresent()) {
            ConfigEmpresaEntity configEmpresaEntity = empresaExistente.get();
            log.info("📦 [ENCONTRADO] Empresa localizada -> {} con nit: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNit());

            configEmpresaUtils.eliminarUsuarioBD(configEmpresaEntity);
            log.info("🗑️ [ELIMINADO] Empresa eliminada correctamente -> {} con nit: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNit());
        } else {
            log.warn("❌ [NO ENCONTRADO] Empresa: {} no encontrada", configEmpresaRequestDTO.getNombreEmpresa());
            throw new ConfigEmpresaNotFoundException(configEmpresaRequestDTO.getNombreEmpresa());
        }
    }
}
