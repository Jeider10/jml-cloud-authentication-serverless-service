package com.cloud.jml.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationInvalidCredentialsException extends AuthenticationRuntimeException {

    public AuthenticationInvalidCredentialsException(String userName) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Usuario: " + userName + " no encontrado para autenticación");
    }

    public AuthenticationInvalidCredentialsException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "❌ [CONSULTA] Contraseña incorrecta, por favor verifique");
    }
}
