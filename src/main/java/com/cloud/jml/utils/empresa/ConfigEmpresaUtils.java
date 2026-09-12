package com.cloud.jml.utils.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.exception.empresa.ConfigEmpresaDeletionException;
import com.cloud.jml.exception.empresa.ConfigEmpresaLogoUploadException;
import com.cloud.jml.exception.empresa.ConfigEmpresaNotFoundException;
import com.cloud.jml.exception.empresa.ConfigEmpresaPersistenceException;
import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import com.cloud.jml.repository.empresa.ConfigEmpresaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
public class ConfigEmpresaUtils {

    private final ConfigEmpresaRepository configEmpresaRepository;

    public ConfigEmpresaUtils(ConfigEmpresaRepository configEmpresaRepository) {
        this.configEmpresaRepository = configEmpresaRepository;
        log.info("🔥 ConfigEmpresaUtils inicializado correctamente.");
    }

    public ConfigEmpresaEntity validarExistenciaEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📌 Inicia validacion de existencia de la empresa con nit: {}", configEmpresaRequestDTO.getNit());

        // Buscar por NIT (es el @Id) — no por nombre, porque el nombre puede cambiar en el update
        Optional<ConfigEmpresaEntity> configEmpresaExistencia = configEmpresaRepository.findByNit(configEmpresaRequestDTO.getNit());

        if (configEmpresaExistencia.isEmpty()) {
            log.warn("⚠️ Empresa no encontrada con nit: {}", configEmpresaRequestDTO.getNit());
            throw new ConfigEmpresaNotFoundException(String.valueOf(configEmpresaRequestDTO.getNit()));
        }

        ConfigEmpresaEntity configEmpresaEntity = configEmpresaExistencia.get();
        log.info("📦 [ENCONTRADO] Empresa localizada -> {} con nit: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNit());

        log.info("✅ [FINALIZADO] Empresa verificada correctamente para actualizacion: {}", configEmpresaEntity.getNombreEmpresa());

        return configEmpresaEntity;
    }

    /**
     * Codifica el logo recibido en Base64 y lo retorna como String para guardarlo en BD.
     * Limite maximo: 10 MB.
     */
    public String guardarLogoEnBase64(MultipartFile file) {
        log.info("📂 [UPLOAD] Iniciando proceso de carga del logo: {} en base de datos", file.getOriginalFilename());

        try {
            // 🔹 Limite maximo permitido (10 MB)
            final long MAX_SIZE_BYTES = 10L * 1024 * 1024;

            long fileSize = file.getSize();
            if (fileSize > MAX_SIZE_BYTES) {
                log.warn("⚠️ El archivo excede el tamano maximo permitido: {} bytes (limite: {})", fileSize, MAX_SIZE_BYTES);
                throw ConfigEmpresaLogoUploadException.fileTooLarge(fileSize);
            }

            // 🔹 Leer el contenido del archivo directamente en memoria
            byte[] contenido = file.getBytes();
            log.info("✅ [UPLOAD] Archivo '{}' cargado correctamente (tamano: {} bytes)", file.getOriginalFilename(), contenido.length);

            // 🔹 Codificar en Base64
            String encodedLogo = Base64.getEncoder().encodeToString(contenido);
            log.info("✅ [UPLOAD] Logo codificado en Base64 ({} caracteres)", encodedLogo.length());

            return encodedLogo;

        } catch (ConfigEmpresaLogoUploadException e) {
            // ⚠️ Excepciones personalizadas controladas
            log.error("❌ [UPLOAD ERROR] Error controlado al procesar el archivo", e);
            throw e;
        } catch (Exception e) {
            log.error("💥 [UPLOAD ERROR] Error inesperado al cargar el logo", e);
            throw ConfigEmpresaLogoUploadException.unexpected(e);
        }
    }

    public ConfigEmpresaEntity guardarEmpresaBD(ConfigEmpresaEntity configEmpresaEntity) {
        try {
            return configEmpresaRepository.save(configEmpresaEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violacion de integridad al guardar la empresa: {}", e.getMessage(), e);
            throw ConfigEmpresaPersistenceException.integrityViolation(e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar la empresa: {}", e.getMessage(), e);
            throw ConfigEmpresaPersistenceException.dataAccessError(e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar la empresa: {}", e.getMessage(), e);
            throw ConfigEmpresaPersistenceException.unexpected(e);
        }
    }

    public void eliminarEmpresaBD(ConfigEmpresaEntity configEmpresaEntity) {
        try {
            configEmpresaRepository.delete(configEmpresaEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violacion de integridad al eliminar la empresa: {}", e.getMessage(), e);
            throw ConfigEmpresaDeletionException.integrityViolation(e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar la empresa: {}", e.getMessage(), e);
            throw ConfigEmpresaDeletionException.dataAccessError(e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar la empresa: {}", e.getMessage(), e);
            throw ConfigEmpresaDeletionException.unexpected(e);
        }
    }
}
