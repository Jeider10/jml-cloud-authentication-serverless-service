package com.cloud.jml.exception.user;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class UserRuntimeException extends RuntimeException {

    private final HttpStatus status;

    protected UserRuntimeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
