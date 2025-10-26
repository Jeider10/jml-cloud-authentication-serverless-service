package com.cloud.jml.config.logo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client() {
        Region region;
        try {
            region = new DefaultAwsRegionProviderChain().getRegion();
        } catch (Exception e) {
            region = Region.US_EAST_1; // región por defecto si no se detecta
        }

        return S3Client.builder()
                .region(region)
                // Usa el proveedor de credenciales por defecto (entorno, archivo, IAM role, etc.)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }
}
