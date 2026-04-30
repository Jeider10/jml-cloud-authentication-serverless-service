package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserDeletionException extends UserRuntimeException {

    public UserDeletionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ [ELIMINACION] " + message);
    }

    public UserDeletionException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "🗑️ [ELIMINACION] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violacion de integridad referencial (por constraints o dependencias)
    public static UserDeletionException integrityViolation(Throwable cause) {
        return new UserDeletionException(
                "❌ [INTEGRIDAD] No se pudo eliminar el usuario debido a una violacion de integridad referencial",
                cause
        );
    }

    // ⚙️ Error de acceso a datos
    public static UserDeletionException dataAccessError(Throwable cause) {
        return new UserDeletionException(
                "❌ [DATOS] Error de acceso a la base de datos al intentar eliminar el usuario",
                cause
        );
    }

    // 💥 Error inesperado
    public static UserDeletionException unexpected(Throwable cause) {
        return new UserDeletionException(
                "💥 [INESPERADO] Ocurrio un error inesperado al intentar eliminar el usuario",
                cause
        );
    }
}
