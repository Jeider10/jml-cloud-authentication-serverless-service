package com.cloud.jml.config.jpa;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class CustomJpaConfig {

    public CustomJpaConfig(JpaProperties properties) {
        properties.setOpenInView(false);
        log.info("spring.jpa.open-in-view: {}", properties.getOpenInView());
    }

    @Bean
    @ConditionalOnMissingBean(OpenEntityManagerInViewInterceptor.class)
    public WebMvcConfigurer disableOpenInViewInterceptor() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(@NotNull InterceptorRegistry registry) {
                // No registrar nada
            }
        };
    }
}
