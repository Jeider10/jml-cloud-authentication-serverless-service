package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends UserRuntimeException {

    public UserAlreadyExistsException(String userName) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Ya existe un usuario con el nombre de usuario: " + userName);
    }
}
