package com.cloud.jml.utils.empresa;

import com.cloud.jml.dto.empresa.ConfigEmpresaRequestDTO;
import com.cloud.jml.exception.empresa.ConfigEmpresaDeletionException;
import com.cloud.jml.exception.empresa.ConfigEmpresaLogoUploadException;
import com.cloud.jml.exception.empresa.ConfigEmpresaNotFoundException;
import com.cloud.jml.exception.empresa.ConfigEmpresaPersistenceException;
import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import com.cloud.jml.repository.empresa.ConfigEmpresaRepository;
import com.cloud.jml.utils.aws.S3Properties;
import com.cloud.jml.utils.general.GeneralUtils;
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
import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component // 🔹 Anotacion para indicar que es un componente de Spring
public class ConfigEmpresaUtils {

    private final ConfigEmpresaRepository configEmpresaRepository;
    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final GeneralUtils generalUtils;

    public ConfigEmpresaUtils(ConfigEmpresaRepository configEmpresaRepository, S3Client s3Client, S3Properties s3Properties, GeneralUtils generalUtils) {
        this.configEmpresaRepository = configEmpresaRepository;
        this.s3Client = s3Client;
        this.s3Properties = s3Properties;
        this.generalUtils = generalUtils;
        log.info("🔥 UserUtils inicializado correctamente.");
    }

    public ConfigEmpresaEntity validarExistenciaEmpresa(ConfigEmpresaRequestDTO configEmpresaRequestDTO) {
        log.info("📌 Inicia validacion de existencia de la empresa: {}", configEmpresaRequestDTO.getNombreEmpresa());

        Optional<ConfigEmpresaEntity> configEmpresaExistencia = configEmpresaRepository.findByNombreEmpresa(configEmpresaRequestDTO.getNombreEmpresa());

        if (configEmpresaExistencia.isEmpty()) {
            log.warn("⚠️ Empresa na encontrada: {}", configEmpresaRequestDTO.getNombreEmpresa());
            throw new ConfigEmpresaNotFoundException(configEmpresaRequestDTO.getNombreEmpresa());
        }

        ConfigEmpresaEntity configEmpresaEntity = configEmpresaExistencia.get();
        log.info("📦 [ENCONTRADO] Empresa localizada -> {} con nit: {}", configEmpresaEntity.getNombreEmpresa(), configEmpresaEntity.getNit());

        log.info("✅ [FINALIZADO] Empresa verificada correctamente para actualizacion: {}", configEmpresaEntity.getNombreEmpresa());

        return configEmpresaEntity;
    }

    public String subirLogoAS3(MultipartFile file) {
        log.info("📤 [S3 UPLOAD] Iniciando subida de logo a S3: {}", file.getOriginalFilename());

        String bucketName = generalUtils.getEnvOrDefault("AWS_S3_BUCKET_NAME", s3Properties.getBucket());
        String region = generalUtils.getEnvOrDefault("REGION", s3Properties.getRegion());

        try {
            log.info("📤 [S3 UPLOAD] Subiendo logo a bucket {}", bucketName);
            // 🔹 Nombre unico para el archivo
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String objectKey = "logos/" + fileName;

            // 🔹 Crear solicitud para subir a S3
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            // 🔹 Subir el archivo
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 🔹 Construir la URL publica
            String formatUrl = "https://%s.s3.%s.amazonaws.com/%s";
            String logoUrl = String.format(
                    formatUrl,
                    bucketName,
                    region,
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
            // 🔹 Limite maximo permitido (10 MB)
            final Long MAX_SIZE_BYTES = 10L * 1024 * 1024;

            Long fileSize = file.getSize();
            if (fileSize > MAX_SIZE_BYTES) {
                log.warn("⚠️ El archivo excede el tamano maximo permitido: {} bytes (limite: {})", fileSize, MAX_SIZE_BYTES);
                throw ConfigEmpresaLogoUploadException.fileTooLarge(fileSize);
            }

            // 🔹 Leer el contenido del archivo directamente en memoria
            byte[] contenido = file.getBytes();
            log.info("✅ [UPLOAD] Archivo '{}' cargado correctamente (tamano: {} bytes)",
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
            // Carpeta donde se guardaran los logos
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
