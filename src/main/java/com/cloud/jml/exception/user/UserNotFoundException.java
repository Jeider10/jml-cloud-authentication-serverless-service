package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserRuntimeException {

    public UserNotFoundException(String userName) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Usuario no encontrado: " + userName);
    }

    public UserNotFoundException(Long identificacion) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Usuario no encontrado: " + identificacion);
    }
}
