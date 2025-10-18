package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserDeletionException extends UserRuntimeException {

    public UserDeletionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ " + message);
    }

    public UserDeletionException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ " + message + " | Causa: " + cause.getMessage());
    }

    // 🔒 Error por violación de integridad (constraint, duplicado, etc.) al eliminar
    public static UserDeletionException integrityViolation(Throwable cause) {
        return new UserDeletionException("❌ No se pudo eliminar el usuario debido a una violación de integridad referencial", cause);
    }

    // ⚙️ Error al acceder o comunicarse con la base de datos al eliminar
    public static UserDeletionException dataAccessError(Throwable cause) {
        return new UserDeletionException("❌ Error de acceso a datos al intentar eliminar el usuario", cause);
    }

    // 💥 Error inesperado (no contemplado en los anteriores) al eliminar
    public static UserDeletionException unexpected(Throwable cause) {
        return new UserDeletionException("❌ Error inesperado al intentar eliminar el usuario", cause);
    }
}
