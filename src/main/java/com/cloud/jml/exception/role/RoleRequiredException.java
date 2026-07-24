package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RoleRequiredException extends RoleRuntimeException {

    public RoleRequiredException() {
        super(
                HttpStatus.BAD_REQUEST,
                "❌ [VALIDACION] El rol del usuario es obligatorio");
    }
}
