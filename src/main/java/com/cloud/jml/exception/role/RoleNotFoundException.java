package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends RoleRuntimeException {

    public RoleNotFoundException(int roleCode) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Role no encontrado con codigo: " + roleCode);
    }

    public RoleNotFoundException(String roleName) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Role no encontrado con nombre: " + roleName);
    }
}
