package com.cloud.jml.exception.authentication;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AuthenticationRuntimeException extends RuntimeException {

    private final HttpStatus status;

    protected AuthenticationRuntimeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
