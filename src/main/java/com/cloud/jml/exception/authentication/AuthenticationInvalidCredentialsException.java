package com.cloud.jml.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationInvalidCredentialsException extends AuthenticationRuntimeException {

    public AuthenticationInvalidCredentialsException(String userName) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Usuario: " + userName + " no encontrado para autenticacion");
    }

    public AuthenticationInvalidCredentialsException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "❌ [CONSULTA] Contrasena incorrecta, por favor verifique");
    }
}
