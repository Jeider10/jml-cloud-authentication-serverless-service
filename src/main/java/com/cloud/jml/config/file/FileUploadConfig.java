package com.cloud.jml.config.file;

import jakarta.servlet.MultipartConfigElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Slf4j
@Configuration
public class FileUploadConfig {

    // ===============================
    // 🔹 Configurar Multipart programaticamente
    // ===============================
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        log.info("🔥 Configuracion de carga de archivos inicializada.");
        // Configuracion para cargar imagenes
        MultipartConfigFactory factory = new MultipartConfigFactory();

        factory.setMaxFileSize(DataSize.ofMegabytes(10));  // 10MB maximo por archivo
        factory.setMaxRequestSize(DataSize.ofMegabytes(10)); // 10MB total por peticion

        log.info("🔥 Configuracion de carga de archivos finalizada.");

        return factory.createMultipartConfig();
    }
}
