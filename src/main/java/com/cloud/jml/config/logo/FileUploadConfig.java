package com.cloud.jml.config.logo;

import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Slf4j
@Configuration
public class FileUploadConfig {

    // ===============================
    // 🔹 Configurar Multipart programáticamente
    // ===============================
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        log.info("🔥 Configuración de carga de archivos inicializada.");
        // Configuración para cargar imagenes
        MultipartConfigFactory factory = new MultipartConfigFactory();

        factory.setMaxFileSize(DataSize.ofMegabytes(10));  // 10MB máximo por archivo
        factory.setMaxRequestSize(DataSize.ofMegabytes(10)); // 10MB total por petición

        log.info("🔥 Configuración de carga de archivos finalizada.");

        return factory.createMultipartConfig();
    }
}
