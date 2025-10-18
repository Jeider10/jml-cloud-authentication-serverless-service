package com.cloud.jml.exception.role;

import org.springframework.http.HttpStatus;

public class RolePersistenceException extends RoleRuntimeException {

    public RolePersistenceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 " + message);
    }

    public RolePersistenceException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 " + message + " | Causa: " + cause.getMessage());
    }

    // 🔒 Error por violación de integridad (constraint, duplicado, etc.)
    public static RolePersistenceException integrityViolation(Throwable cause) {
        return new RolePersistenceException("❌ Violación de integridad en base de datos al guardar el rol", cause);
    }

    // ⚙️ Error al acceder o comunicarse con la base de datos
    public static RolePersistenceException dataAccessError(Throwable cause) {
        return new RolePersistenceException("❌ Error de acceso a datos al intentar guardar el rol", cause);
    }

    // 💥 Error inesperado (no contemplado en los anteriores)
    public static RolePersistenceException unexpected(Throwable cause) {
        return new RolePersistenceException("❌ Error inesperado al registrar el rol", cause);
    }
}
