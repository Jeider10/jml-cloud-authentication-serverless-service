package com.cloud.jml.rest;

import com.cloud.jml.banner.DynamicBanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Slf4j
@SpringBootApplication(scanBasePackages = "com.cloud.jml")
@EnableJpaRepositories(basePackages = "com.cloud.jml")
@EntityScan(basePackages = "com.cloud.jml")
public class Application {

    public static final String MICRO_NAME = "M i c r o  -  A u t h e n t i c a t i o n";

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(Application.class);
        app.setBanner(new DynamicBanner(MICRO_NAME));
        app.run(args);

        log.info("=======================================================");
        log.info("  ✅  jml-cloud-authentication-serverless-service  ONLINE");
        log.info("  🔐  Servicio de autenticacion, usuarios y roles");
        log.info("  🌐  Puerto : 1081  →  http://localhost:1081");
        log.info("  📊  Actuator: http://localhost:1081/actuator/health");
        log.info("=======================================================");
    }
}
