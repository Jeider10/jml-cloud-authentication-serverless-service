package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends RoleRuntimeException {

    public RoleNotFoundException(int roleCode) {
        super(HttpStatus.NOT_FOUND, "❌ Role no encontrado con código: " + roleCode);
    }
}
