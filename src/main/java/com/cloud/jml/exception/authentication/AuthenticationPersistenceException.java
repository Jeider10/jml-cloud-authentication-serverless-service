package com.cloud.jml.exception.authentication;

import org.springframework.http.HttpStatus;

public class AuthenticationPersistenceException extends AuthenticationRuntimeException {

    public AuthenticationPersistenceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 [PERSISTENCIA] " + message);
    }

    public AuthenticationPersistenceException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "💾 [PERSISTENCIA] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violacion de integridad (constraint, duplicado, etc.) al guardar
    public static AuthenticationPersistenceException integrityViolation(Throwable cause) {
        return new AuthenticationPersistenceException(
                "❌ [INTEGRIDAD] Violacion de integridad en base de datos al guardar la authentication",
                cause
        );
    }

    // ⚙️ Error tecnico de acceso a datos
    public static AuthenticationPersistenceException dataAccessError(Throwable cause) {
        return new AuthenticationPersistenceException(
                "❌ [DATOS] Error de acceso a datos al intentar guardar la authentication",
                cause
        );
    }

    // 💥 Error inesperado
    public static AuthenticationPersistenceException unexpected(Throwable cause) {
        return new AuthenticationPersistenceException(
                "💥 [INESPERADO] Ocurrio un error inesperado al registrar la authentication",
                cause
        );
    }
}
