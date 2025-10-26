package com.cloud.jml.utils.empresa;

import com.cloud.jml.config.logo.S3Properties;
import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.exception.empresa.ConfigEmpresaLogoUploadException;
import com.cloud.jml.exception.empresa.ConfigEmpresaNotFoundException;
import com.cloud.jml.exception.empresa.ConfigEmpresaPersistenceException;
import com.cloud.jml.model.ConfigEmpresaEntity;
import com.cloud.jml.repository.ConfigEmpresaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component // 🔹 Anotación para indicar que es un componente de Spring
public class ConfigEmpresaUtils {

    private final ConfigEmpresaRepository configEmpresaRepository;
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    public ConfigEmpresaUtils(ConfigEmpresaRepository configEmpresaRepository, S3Client s3Client, S3Properties s3Properties) {
        this.configEmpresaRepository = configEmpresaRepository;
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
        log.info("🔥 UserUtils inicializado correctamente.");
    }

    /**
     * 💾 Guarda la orden en BD con manejo de excepciones.
     */
    public ConfigEmpresaEntity guardarEmpresaBD(ConfigEmpresaEntity configEmpresaEntity) {
        try {
            return configEmpresaRepository.save(configEmpresaEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al guardar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error de integridad en base de datos al guardar la empresa", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al guardar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error al guardar la empresa en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al guardar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error inesperado al registrar la empresa", e);
        }
    }

    /**
     * 🗑️ Elimina la orden de BD con manejo de excepciones.
     */
    public void eliminarUsuarioBD(ConfigEmpresaEntity configEmpresaEntity) {
        try {
            configEmpresaRepository.delete(configEmpresaEntity);

        } catch (DataIntegrityViolationException e) {
            log.error("🚨 Violación de integridad al eliminar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error de integridad en base de datos al eliminar la empresa", e);

        } catch (DataAccessException e) {
            log.error("🚨 Error de acceso a datos al eliminar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error al eliminar la empresa en la base de datos", e);

        } catch (Exception e) {
            log.error("🚨 Error inesperado al eliminar la empresa: {}", e.getMessage(), e);
            throw new ConfigEmpresaPersistenceException("Error inesperado al eliminar la empresa", e);
        }
    }

    public ConfigEmpresaEntity validarExistenciaEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📌 Inicia validación de existencia de la empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        Optional<ConfigEmpresaEntity> configEmpresaExistencia = configEmpresaRepository.findByNombreEmpresa(configEmpresaRequestDTO.getNombreEmpresa());

        if (configEmpresaExistencia.isEmpty()) {
            log.warn("⚠️ Empresa na encontrada: {}", configEmpresaRequestDTO.getNombreEmpresa());
            throw new ConfigEmpresaNotFoundException(configEmpresaRequestDTO.getNombreEmpresa());
        }

        ConfigEmpresaEntity configEmpresaEntity = configEmpresaExistencia.get();
        log.info("📦 [ENCONTRADO] Empresa localizada -> {} con nit: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNit());

        log.info("✅ [FINALIZADO] Empresa verificada correctamente para actualización: {}", configEmpresaEntity.getNombreEmpresa());

        return configEmpresaEntity;
    }

    public void actualizarDatosEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO, ConfigEmpresaEntity configEmpresaEntity) {
        log.info("📌 Actualizando datos de la empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        // Actualizamos solo los campos permitidos
        configEmpresaEntity.setNit(configEmpresaRequestDTO.getNit());
        configEmpresaEntity.setNombreEmpresa(configEmpresaRequestDTO.getNombreEmpresa());
        configEmpresaEntity.setDireccion(configEmpresaRequestDTO.getDireccion());
        configEmpresaEntity.setTelefono(configEmpresaRequestDTO.getTelefono());
        configEmpresaEntity.setMensaje(configEmpresaRequestDTO.getMensaje());
        configEmpresaEntity.setLogo(configEmpresaRequestDTO.getLogo());

        // Actualiza fecha
        configEmpresaEntity.setFechaActualizacion(LocalDateTime.now());

        log.info("✅ Datos de la empresa actualizados correctamente: {}", configEmpresaEntity.getNombreEmpresa());
    }

    public String subirLogoAS3(MultipartFile file) {
        log.info("📤 [S3 UPLOAD] Subiendo logo a bucket {}", s3Properties.getBucket());

        try {
            // 🔹 Nombre único para el archivo
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String objectKey = "logos/" + fileName;

            // 🔹 Crear solicitud para subir a S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            // 🔹 Subir el archivo
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 🔹 Construir la URL pública
            String logoUrl = String.format(
                    "https://%s.s3.%s.amazonaws.com/%s",
                    s3Properties.getBucket(),
                    s3Properties.getRegion(),
                    objectKey
            );

            log.info("✅ [S3 UPLOAD] Logo subido correctamente a: {}", logoUrl);

            return logoUrl;

        } catch (ConfigEmpresaLogoUploadException e) {
            log.error("❌ Error controlado al subir logo a S3", e);
            throw e;
        } catch (Exception e) {
            log.error("❌ Error al subir logo a S3", e);
            throw ConfigEmpresaLogoUploadException.s3UploadError(e);
        }
    }

    public String guardarLogoEnBase64(MultipartFile file) {
        log.info("📂 [UPLOAD] Iniciando proceso de carga del logo: {} en la base", file.getOriginalFilename());

        try {
            // 🔹 Límite máximo permitido (10 MB)
            final long MAX_SIZE_BYTES = 10L * 1024 * 1024;

            long fileSize = file.getSize();
            if (fileSize > MAX_SIZE_BYTES) {
                log.warn("⚠️ El archivo excede el tamaño máximo permitido: {} bytes (límite: {})", fileSize, MAX_SIZE_BYTES);
                throw ConfigEmpresaLogoUploadException.fileTooLarge(fileSize);
            }

            // 🔹 Leer el contenido del archivo directamente en memoria
            byte[] contenido = file.getBytes();
            log.info("✅ [UPLOAD] Archivo '{}' cargado correctamente (tamaño: {} bytes)",
                    file.getOriginalFilename(), contenido.length);

            // 🔹 Codificar en Base64
            String encodedToStringLogo = Base64.getEncoder().encodeToString(contenido);
            log.info("✅ [UPLOAD] Logo codificado en Base64 ({} caracteres Base64)", encodedToStringLogo.length());

            return encodedToStringLogo;

        } catch (ConfigEmpresaLogoUploadException e) {
            // ⚠️ Excepciones personalizadas controladas
            log.error("❌ [UPLOAD ERROR] Error controlado al procesar el archivo", e);
            throw e;
        } catch (Exception e) {
            log.error("💥 [UPLOAD ERROR] Error inesperado al cargar el logo", e);
            throw ConfigEmpresaLogoUploadException.unexpected(e);
        }
    }

    /**
     * Crea carpeta local y guarda el logo en la carpeta correspondiente
     */
    public String subirLogo(MultipartFile file) {
        log.info("📂 [UPLOAD] Iniciando proceso de carga del logo: {}", file.getOriginalFilename());
        try {
            // Carpeta donde se guardarán los logos
            String uploadDir = "uploads/logos/";
            File directorio = new File(uploadDir);
            if (!directorio.exists()) {
                directorio.mkdirs();
                log.info("📁 Carpeta creada: {}", directorio.getAbsolutePath());
            }

            // Renombrar archivo para evitar colisiones
            String nombreArchivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path destino = Paths.get(uploadDir, nombreArchivo);
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            String rutaRelativa = "/uploads/logos/" + nombreArchivo;
            log.info("✅ [UPLOAD] Logo guardado correctamente en: {}", rutaRelativa);

            return rutaRelativa;

        } catch (IOException e) {
            log.error("❌ [UPLOAD ERROR] Error al guardar el logo", e);
            throw new RuntimeException("❌ Error al guardar el logo", e);
        }
    }
}
