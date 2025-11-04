package com.cloud.jml.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationRefreshTokenValidationException extends AuthenticationRuntimeException {

    public AuthenticationRefreshTokenValidationException(String message) {
        super(
                HttpStatus.BAD_REQUEST,
                "❌ [REFRESH TOKEN] " + message);
    }

    public AuthenticationRefreshTokenValidationException(String message, Throwable cause) {
        super(
                HttpStatus.BAD_REQUEST,
                "❌ [REFRESH TOKEN] " + message, cause);
    }

    public AuthenticationRefreshTokenValidationException() {
        super(
                HttpStatus.BAD_REQUEST,
                "❌ [REFRESH TOKEN] No se proporcionó el refresh token en la solicitud.");
    }
}
