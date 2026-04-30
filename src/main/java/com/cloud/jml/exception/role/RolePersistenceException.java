package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RolePersistenceException extends RoleRuntimeException {

    public RolePersistenceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 [PERSISTENCIA] " + message);
    }

    public RolePersistenceException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "💾 [PERSISTENCIA] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violacion de integridad (constraint, duplicado, etc.) al guardar
    public static RolePersistenceException integrityViolation(Throwable cause) {
        return new RolePersistenceException(
                "❌ [INTEGRIDAD] Violacion de integridad en base de datos al guardar el role",
                cause
        );
    }

    // ⚙️ Error tecnico de acceso a datos
    public static RolePersistenceException dataAccessError(Throwable cause) {
        return new RolePersistenceException(
                "❌ [DATOS] Error de acceso a datos al intentar guardar el role",
                cause
        );
    }

    // 💥 Error inesperado
    public static RolePersistenceException unexpected(Throwable cause) {
        return new RolePersistenceException(
                "💥 [INESPERADO] Ocurrio un error inesperado al registrar el role",
                cause
        );
    }
}
