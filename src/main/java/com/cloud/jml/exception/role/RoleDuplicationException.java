package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RoleDuplicationException extends RoleRuntimeException {

    public RoleDuplicationException(int roleCode) {
        super(HttpStatus.CONFLICT, "⚠️ Role duplicado con código: " + roleCode);
    }
}
