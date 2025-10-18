package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RoleDeletionException extends RoleRuntimeException {

    public RoleDeletionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ " + message);
    }

    public RoleDeletionException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ " + message + " | Causa: " + cause.getMessage());
    }

    // 🔒 Error por violación de integridad (constraint, duplicado, etc.) al eliminar
    public static RoleDeletionException integrityViolation(Throwable cause) {
        return new RoleDeletionException("❌ No se pudo eliminar el rol debido a una violación de integridad referencial", cause);
    }

    // ⚙️ Error al acceder o comunicarse con la base de datos al eliminar
    public static RoleDeletionException dataAccessError(Throwable cause) {
        return new RoleDeletionException("❌ Error de acceso a datos al intentar eliminar el rol", cause);
    }

    // 💥 Error inesperado (no contemplado en los anteriores) al eliminar
    public static RoleDeletionException unexpected(Throwable cause) {
        return new RoleDeletionException("❌ Error inesperado al intentar eliminar el rol", cause);
    }
}
