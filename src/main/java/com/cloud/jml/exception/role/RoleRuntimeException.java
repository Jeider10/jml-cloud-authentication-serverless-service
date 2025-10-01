package com.cloud.jml.exception.role;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class RoleRuntimeException extends RuntimeException {

    private final HttpStatus status;

    protected RoleRuntimeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
