package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserCredencialesIncorrectasException extends UserRuntimeException {

    public UserCredencialesIncorrectasException(String userName) {
        super(HttpStatus.UNAUTHORIZED, "🔒 Contraseña incorrecta para usuario: " + userName);
    }
}
