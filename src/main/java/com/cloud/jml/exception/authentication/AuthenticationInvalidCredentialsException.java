package com.cloud.jml.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationInvalidCredentialsException extends AuthenticationRuntimeException {

    public AuthenticationInvalidCredentialsException(String userName) {
        super(HttpStatus.NOT_FOUND, "❌ Usuario: " + userName + " no encontrado para autenticación");
    }

    public AuthenticationInvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "⚠️ Contraseña incorrecta, por favor verifique");
    }
}
