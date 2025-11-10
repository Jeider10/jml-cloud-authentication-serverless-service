package com.cloud.jml.config.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("🔥 Configuración de recursos estáticos inicializada.");
        // Mapea las rutas /uploads/** a la carpeta física /uploads
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        log.info("🔥 Configuración de recursos estáticos finalizada.");
    }
}
