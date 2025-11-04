package com.cloud.jml.utils.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long expiration = 3600000; // valor por defecto si no está en el YAML (src/main/resources/application.yml)
    private long refreshExpirationMs = 86400000; // valor por defecto si no está en el YAML (src/main/resources/application.yml)
}
