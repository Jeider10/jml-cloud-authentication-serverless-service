package com.cloud.jml.service.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaPapeleraResponseDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.dto.empresa.ConfigEmpresaResponseDTO;
import com.cloud.jml.exception.empresa.ConfigEmpresaDuplicationException;
import com.cloud.jml.exception.empresa.ConfigEmpresaNotFoundException;
import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import com.cloud.jml.repository.empresa.ConfigEmpresaRepository;
import com.cloud.jml.utils.empresa.ConfigEmpresaMapper;
import com.cloud.jml.utils.empresa.ConfigEmpresaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ConfigEmpresaService {

    private final ConfigEmpresaRepository configEmpresaRepository;
    private final ConfigEmpresaMapper mapper;
    private final ConfigEmpresaUtils configEmpresaUtils;

    public ConfigEmpresaService(ConfigEmpresaRepository configEmpresaRepository,
                                ConfigEmpresaMapper mapper,
                                ConfigEmpresaUtils configEmpresaUtils) {
        this.configEmpresaRepository = configEmpresaRepository;
        this.mapper = mapper;
        this.configEmpresaUtils = configEmpresaUtils;
        log.info("🔥 ConfigEmpresaService inicializado correctamente.");
    }

    // ─── Obtener empresa activa ───────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ConfigEmpresaResponseDTO> obtenerPrimeraEmpresa() {
        log.info("🔍 [CONSULTA] Recuperando empresa activa");

        List<ConfigEmpresaEntity> entidades = configEmpresaRepository.findAllByEliminadoFalse();

        if (entidades.isEmpty()) {
            log.warn("⚠️ [RESULTADO] No se encontro ninguna empresa activa");
            return List.of();
        }

        List<ConfigEmpresaResponseDTO> respuesta = entidades.stream()
                .map(mapper::mapEntityToResponseDto)
                .toList();

        log.info("✅ [FINALIZADO] Total de empresas retornadas: {}", respuesta.size());

        return respuesta;
    }

    // ─── Buscar por NIT ───────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ConfigEmpresaResponseDTO buscarEmpresaNit(ConfigEmpresaRequestDTO dto) {
        log.info("🔍 [CONSULTA] Buscando empresa con nit: {}", dto.getNit());

        Optional<ConfigEmpresaEntity> empresa = configEmpresaRepository.findByNitAndEliminadoFalse(dto.getNit());

        if (empresa.isEmpty()) {
            log.warn("❌ [RESULTADO] Empresa no encontrada con nit: {}", dto.getNit());
            return null;
        }

        return mapper.mapEntityToResponseDto(empresa.get());
    }

    // ─── Registrar ────────────────────────────────────────────────────────────
    @Transactional
    public ConfigEmpresaResponseDTO registrarDatosEmpresa(ConfigEmpresaRequestDTO dto, MultipartFile file) {
        log.info("🔍 [SOLICITUD] Registrando empresa: {} con nit: {}", dto.getNombreEmpresa(), dto.getNit());

        Optional<ConfigEmpresaEntity> existente = configEmpresaRepository.findByNit(dto.getNit());

        if (existente.isPresent() && !existente.get().isEliminado()) {
            log.warn("❌ [DUPLICADO] Empresa ya existe con nit: {}", dto.getNit());
            throw new ConfigEmpresaDuplicationException(dto.getNombreEmpresa(), dto.getNit());
        }

        if (file != null && !file.isEmpty()) {
            String rutaLogo = configEmpresaUtils.guardarLogoEnBase64(file);
            dto.setLogo(rutaLogo);
        }

        ConfigEmpresaEntity entidad = mapper.mapRequestDtoToEntity(dto);
        ConfigEmpresaEntity guardada = configEmpresaUtils.guardarEmpresaBD(entidad);

        log.info("💾 [PERSISTENCIA] Empresa guardada: {}", guardada.getNit());

        return mapper.mapEntityToResponseDto(guardada);
    }

    // ─── Actualizar ───────────────────────────────────────────────────────────
    @Transactional
    public ConfigEmpresaResponseDTO actualizarEmpresa(ConfigEmpresaRequestDTO dto, MultipartFile file) {
        log.info("🔍 [SOLICITUD] Actualizando empresa: {}", dto.getNombreEmpresa());

        // Paso 1: Validar existencia
        ConfigEmpresaEntity entidad = configEmpresaUtils.validarExistenciaEmpresa(dto);

        // Paso 2: Si viene un archivo, subirlo y actualizar el campo logo
        if (file != null && !file.isEmpty()) {
            String rutaLogo = configEmpresaUtils.guardarLogoEnBase64(file);
            dto.setLogo(rutaLogo);
        }

        mapper.actualizarDatosEmpresa(dto, entidad);
        ConfigEmpresaEntity actualizada = configEmpresaUtils.guardarEmpresaBD(entidad);

        log.info("✅ [FINALIZADO] Empresa actualizada: {}", actualizada.getNit());

        return mapper.mapEntityToResponseDto(actualizada);
    }

    // ─── Soft delete (a papelera) ─────────────────────────────────────────────
    @Transactional
    public void eliminarEmpresa(Long nit, String eliminadoPorId, String eliminadoPorNombre) {
        log.info("🔍 [SOLICITUD] Enviando a papelera empresa con nit: {}", nit);

        ConfigEmpresaEntity entidad = configEmpresaRepository.findByNitAndEliminadoFalse(nit)
                .orElseThrow(() -> new ConfigEmpresaNotFoundException(String.valueOf(nit)));

        entidad.setEliminado(true);
        entidad.setFechaEliminacion(LocalDateTime.now());
        entidad.setEliminadoPorId(eliminadoPorId);
        entidad.setEliminadoPorNombre(eliminadoPorNombre);

        configEmpresaUtils.guardarEmpresaBD(entidad);

        log.info("🗑️ [PAPELERA] Empresa {} enviada a papelera por: {}", nit, eliminadoPorNombre);
    }

    // ─── Listar papelera ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ConfigEmpresaPapeleraResponseDTO> listarPapelera() {
        log.info("🔍 [CONSULTA] Listando empresas en papelera");

        List<ConfigEmpresaEntity> entidades = configEmpresaRepository.findAllByEliminadoTrue();

        if (entidades.isEmpty()) {
            log.warn("⚠️ [RESULTADO] No hay empresas en papelera");
            return List.of();
        }

        List<ConfigEmpresaPapeleraResponseDTO> respuesta = entidades.stream()
                .map(mapper::mapEntityToPapeleraDto)
                .toList();

        log.info("✅ [FINALIZADO] Total de empresas en papelera: {}", respuesta.size());

        return respuesta;
    }

    // ─── Restaurar desde papelera ─────────────────────────────────────────────
    @Transactional
    public ConfigEmpresaResponseDTO restaurarEmpresa(Long nit) {
        log.info("🔍 [SOLICITUD] Restaurando empresa con nit: {}", nit);

        ConfigEmpresaEntity entidad = configEmpresaRepository.findByNitAndEliminadoTrue(nit)
                .orElseThrow(() -> {
                    log.warn("❌ [RESULTADO] Empresa no encontrada en papelera: {}", nit);
                    return new ConfigEmpresaNotFoundException(String.valueOf(nit));
                });

        entidad.setEliminado(false);
        entidad.setFechaEliminacion(null);
        entidad.setEliminadoPorId(null);
        entidad.setEliminadoPorNombre(null);
        entidad.setFechaActualizacion(LocalDateTime.now());

        ConfigEmpresaEntity restaurada = configEmpresaUtils.guardarEmpresaBD(entidad);

        log.info("✅ [FINALIZADO] Empresa restaurada: {}", restaurada.getNit());

        return mapper.mapEntityToResponseDto(restaurada);
    }

    // ─── Eliminar definitivamente ─────────────────────────────────────────────
    @Transactional
    public void eliminarDefinitivo(Long nit) {
        log.info("🔍 [SOLICITUD] Eliminando definitivamente empresa con nit: {}", nit);

        ConfigEmpresaEntity entidad = configEmpresaRepository.findByNitAndEliminadoTrue(nit)
                .orElseThrow(() -> {
                    log.warn("❌ [RESULTADO] Empresa no encontrada en papelera: {}", nit);
                    return new ConfigEmpresaNotFoundException(String.valueOf(nit));
                });

        configEmpresaUtils.eliminarEmpresaBD(entidad);

        log.info("🗑️ [ELIMINADO] Empresa eliminada definitivamente: {}", nit);
    }

    // ─── Filtrar papelera por fecha de eliminacion ────────────────────────────
    @Transactional(readOnly = true)
    public List<ConfigEmpresaPapeleraResponseDTO> listarPapeleraPorFecha(String fechaInicio, String fechaFin) {

        LocalDateTime inicio = configEmpresaUtils.parsearFechaInicio(fechaInicio);
        LocalDateTime fin = configEmpresaUtils.parsearFechaFin(fechaFin);

        List<ConfigEmpresaEntity> entidades = configEmpresaRepository.findByFechaEliminacionBetweenAndEliminadoTrue(inicio, fin);

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToPapeleraDto).toList();
    }

    // ─── Filtrar papelera por quien elimino ───────────────────────────────────
    @Transactional(readOnly = true)
    public List<ConfigEmpresaPapeleraResponseDTO> listarPapeleraPorEliminadoPor(String eliminadoPorId) {

        List<ConfigEmpresaEntity> entidades = configEmpresaRepository.findByEliminadoPorIdContainingIgnoreCaseAndEliminadoTrue(eliminadoPorId);

        if (entidades.isEmpty()) {
            return List.of();
        }

        return entidades.stream().map(mapper::mapEntityToPapeleraDto).toList();
    }
}
