package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserPersistenceException extends UserRuntimeException {

    public UserPersistenceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 [PERSISTENCIA] " + message);
    }

    public UserPersistenceException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "💾 [PERSISTENCIA] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violacion de integridad (constraint, duplicado, etc.) al guardar
    public static UserPersistenceException integrityViolation(Throwable cause) {
        return new UserPersistenceException(
                "❌ [INTEGRIDAD] Violacion de integridad en base de datos al guardar el usuario",
                cause
        );
    }

    // ⚙️ Error tecnico de acceso a datos
    public static UserPersistenceException dataAccessError(Throwable cause) {
        return new UserPersistenceException(
                "❌ [DATOS] Error de acceso a datos al intentar guardar el usuario",
                cause
        );
    }

    // 💥 Error inesperado
    public static UserPersistenceException unexpected(Throwable cause) {
        return new UserPersistenceException(
                "💥 [INESPERADO] Ocurrio un error inesperado al registrar el usuario",
                cause
        );
    }
}
