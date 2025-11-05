package com.cloud.jml.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationTokenValidationException extends AuthenticationRuntimeException {

    public AuthenticationTokenValidationException(String message) {
        super(
                HttpStatus.UNAUTHORIZED,
                "❌ [TOKEN] " + message);
    }

    public AuthenticationTokenValidationException(String message, Throwable cause) {
        super(
                HttpStatus.UNAUTHORIZED,
                "❌ [TOKEN] " + message, cause);
    }

    public AuthenticationTokenValidationException() {
        super(
                HttpStatus.UNAUTHORIZED,
                "❌ [TOKEN] Token expirado.");
    }
}
