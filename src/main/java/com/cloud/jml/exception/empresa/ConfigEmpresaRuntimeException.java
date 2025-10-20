package com.cloud.jml.exception.empresa;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ConfigEmpresaRuntimeException extends RuntimeException {

    private final HttpStatus status;

    protected ConfigEmpresaRuntimeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
