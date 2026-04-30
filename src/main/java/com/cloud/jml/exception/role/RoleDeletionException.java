package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RoleDeletionException extends RoleRuntimeException {

    public RoleDeletionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ [ELIMINACION] " + message);
    }

    public RoleDeletionException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "🗑️ [ELIMINACION] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violacion de integridad referencial (por constraints o dependencias)
    public static RoleDeletionException integrityViolation(Throwable cause) {
        return new RoleDeletionException(
                "❌ [INTEGRIDAD] No se pudo eliminar el role debido a una violacion de integridad referencial",
                cause
        );
    }

    // ⚙️ Error de acceso a datos
    public static RoleDeletionException dataAccessError(Throwable cause) {
        return new RoleDeletionException(
                "❌ [DATOS] Error de acceso a la base de datos al intentar eliminar el role",
                cause
        );
    }

    // 💥 Error inesperado
    public static RoleDeletionException unexpected(Throwable cause) {
        return new RoleDeletionException(
                "💥 [INESPERADO] Ocurrio un error inesperado al intentar eliminar el role",
                cause
        );
    }
}
