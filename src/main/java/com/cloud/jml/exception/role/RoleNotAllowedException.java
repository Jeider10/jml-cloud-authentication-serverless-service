package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RoleNotAllowedException extends RoleRuntimeException {

    public RoleNotAllowedException(String roleName) {
        super(
                HttpStatus.BAD_REQUEST,
                "❌ [VALIDACION] El rol '" + roleName + "' no es valido. Roles permitidos: ADMIN, USER, CAJERO");
    }
}
