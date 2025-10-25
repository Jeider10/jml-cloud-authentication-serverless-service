package com.cloud.jml.config.logo;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        // Configuración para cargar imagenes
        MultipartConfigFactory factory = new MultipartConfigFactory();

        factory.setMaxFileSize(DataSize.ofMegabytes(10));  // 10MB máximo por archivo
        factory.setMaxRequestSize(DataSize.ofMegabytes(10)); // 10MB total por petición

        return factory.createMultipartConfig();
    }
}
