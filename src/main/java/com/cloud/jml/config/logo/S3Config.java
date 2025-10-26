package com.cloud.jml.config.logo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        log.info("🔥 Configuración de cliente S3 inicializada.");

        Region region;
        try {
            region = new DefaultAwsRegionProviderChain().getRegion();
            log.info("🌍 Región detectada: {}", region);
        } catch (Exception e) {
            region = Region.US_EAST_1; // región por defecto si no se detecta
            log.warn("🚨 No se pudo detectar la región. Se usará la región por defecto: {}", region, e);
        }

        S3Client s3Client = S3Client.builder()
                .region(region)
                // Usa el proveedor de credenciales por defecto (entorno, archivo, IAM role, etc.)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();

        log.info("🔥 Configuración de cliente S3 finalizada.");

        return s3Client;
    }
}
