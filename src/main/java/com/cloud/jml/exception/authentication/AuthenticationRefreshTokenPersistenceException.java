package com.cloud.jml.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationRefreshTokenPersistenceException extends AuthenticationRuntimeException {

    public AuthenticationRefreshTokenPersistenceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 [PERSISTENCIA] " + message);
    }

    public AuthenticationRefreshTokenPersistenceException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "💾 [PERSISTENCIA] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violación de integridad (constraint, duplicado, etc.) al guardar
    public static AuthenticationRefreshTokenPersistenceException integrityViolation(Throwable cause) {
        return new AuthenticationRefreshTokenPersistenceException(
                "❌ [INTEGRIDAD] Violación de integridad en base de datos al guardar la authentication",
                cause
        );
    }

    // ⚙️ Error técnico de acceso a datos
    public static AuthenticationRefreshTokenPersistenceException dataAccessError(Throwable cause) {
        return new AuthenticationRefreshTokenPersistenceException(
                "❌ [DATOS] Error de acceso a datos al intentar guardar la authentication",
                cause
        );
    }

    // 💥 Error inesperado
    public static AuthenticationRefreshTokenPersistenceException unexpected(Throwable cause) {
        return new AuthenticationRefreshTokenPersistenceException(
                "💥 [INESPERADO] Ocurrió un error inesperado al registrar la authentication",
                cause
        );
    }
}
