package com.cloud.jml.config.aws;

import com.cloud.jml.utils.aws.S3Properties;
import com.cloud.jml.utils.general.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Configuration
public class S3Config {

    private final GeneralUtils generalUtils;
    private final S3Properties s3Properties;

    public S3Config(GeneralUtils generalUtils, S3Properties s3Properties) {
        this.generalUtils = generalUtils;
        this.s3Properties = s3Properties;
        log.info("🔥 S3Config inicializado correctamente.");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            S3Client s3 = s3Client();
            Region region = detectAwsRegion();
            createBucketIfNotExists(s3, generalUtils.getEnvOrDefault("AWS_S3_BUCKET_NAME", s3Properties.getBucket()), region);
        } catch (S3Exception e) {
            log.warn("⚠️ No se pudo verificar o crear el bucket S3 (posible token expirado): {}", e.getMessage());
        }
    }

    // FIX: Un solo @Bean que decide el tipo de cliente segun s3Properties.profile
    // "local" (default) = credenciales estaticas (accessKey/secretKey de S3Properties)
    // "aws" = DefaultCredentialsProvider (IAM role, env vars, ~/.aws/credentials)
    // Se cambia con: variable de entorno CLOUD_AWS_PROFILE=aws o en application.yml cloud.aws.profile=aws
    @Bean
    public S3Client s3Client() {
        Region region = detectAwsRegion();

        if (s3Properties.isAwsProfile()) {
            // Entorno AWS: usa credenciales del entorno (IAM role, env vars, etc.)
            log.info("🔥 [AWS S3] Configurando cliente S3 con DefaultCredentialsProvider (perfil: aws)");

            S3Client s3Client = S3Client.builder()
                    .region(region)
                    .credentialsProvider(DefaultCredentialsProvider.builder().build())
                    .build();

            log.info("🔥 [AWS S3] Cliente S3 configurado correctamente en region {}", region);
            return s3Client;
        }

        // Entorno local: usa credenciales estaticas de S3Properties
        log.info("🔥 [AWS S3 Local] Configurando cliente S3 con credenciales estaticas (perfil: local)");

        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(s3Properties.getAccessKey(), s3Properties.getSecretKey())))
                .build();

        log.info("🔥 [AWS S3 Local] Cliente S3 configurado correctamente en region {}", region);
        return s3Client;
    }

    /**
     * Detecta la region de AWS usando la cadena de proveedores por defecto.
     * Si no puede detectarla, retorna us-east-1 como valor por defecto.
     */
    private Region detectAwsRegion() {
        try {
            Region region = new DefaultAwsRegionProviderChain().getRegion();
            log.info("🌍 Region detectada: {}", region);
            return region;
        } catch (Exception e) {
            Region defaultRegion = Region.US_EAST_1;
            log.warn("🚨 No se pudo detectar la region. Se usara la region por defecto: {}", defaultRegion, e);
            return defaultRegion;
        }
    }

    /**
     * Crea un bucket si no existe en S3.
     *
     * @param s3Client   Cliente S3 configurado.
     * @param bucketName Nombre del bucket a crear.
     */
    public void createBucketIfNotExists(S3Client s3Client, String bucketName, Region region) {
        log.info("🔥 Verificando si el bucket '{}' ya existe...", bucketName);

        if (bucketName == null || bucketName.trim().isEmpty()) {
            log.error("❌ El nombre del bucket no puede estar vacio.");
            throw new IllegalArgumentException("El nombre del bucket no puede estar vacio.");
        }

        if (bucketExists(s3Client, bucketName)) {
            log.info("✅ El bucket '{}' ya existe. No se crea nuevamente.", bucketName);
            return;
        }

        try {
            log.info("🚀 Creando nuevo bucket '{}' en region {}...", bucketName, region.id());

            s3Client.createBucket(b -> b
                    .bucket(bucketName)
                    .createBucketConfiguration(c -> c.locationConstraint(region.id()))
            );

            log.info("🎉 Bucket '{}' creado exitosamente en region {}.", bucketName, region.id());
        } catch (Exception e) {
            log.error("❌ Error al crear/verificar bucket '{}': {}", bucketName, e.getMessage(), e);
            throw new RuntimeException("Error creando bucket S3: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si un bucket existe en S3.
     *
     * @param s3Client   cliente S3 configurado
     * @param bucketName nombre del bucket a verificar
     * @return true si existe, false si no
     */
    private boolean bucketExists(S3Client s3Client, String bucketName) {
        try {
            s3Client.headBucket(b -> b.bucket(bucketName));
            return true;

        } catch (S3Exception e) {

            // 404 → bucket no existe
            if (e.statusCode() == 404) {
                log.info("ℹ️ El bucket '{}' no existe.", bucketName);
                return false;
            }

            // 403 o 301 → bucket existe pero no accesible o region diferente
            if (e.statusCode() == 403 || e.statusCode() == 301) {
                log.info("ℹ️ El bucket '{}' existe pero esta en otra region o con acceso restringido.", bucketName);
                return true;
            }

            log.warn("⚠️ Error verificando bucket '{}': {}", bucketName, e.awsErrorDetails().errorMessage());

            return true;
        }
    }
}
