package com.cloud.jml.exception.user;

import org.springframework.http.HttpStatus;

public class UserPersistenceException extends UserRuntimeException {

    public UserPersistenceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 " + message);
    }

    public UserPersistenceException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 " + message + " | Causa: " + cause.getMessage());
    }

    // 🔒 Error por violación de integridad (constraint, duplicado, etc.) al guardar
    public static UserPersistenceException integrityViolation(Throwable cause) {
        return new UserPersistenceException("❌ Violación de integridad en base de datos al guardar el usuario", cause);
    }

    // ⚙️ Error al acceder o comunicarse con la base de datos al guardar
    public static UserPersistenceException dataAccessError(Throwable cause) {
        return new UserPersistenceException("❌ Error de acceso a datos al intentar guardar el usuario", cause);
    }

    // 💥 Error inesperado (no contemplado en los anteriores) al guardar
    public static UserPersistenceException unexpected(Throwable cause) {
        return new UserPersistenceException("❌ Error inesperado al registrar el usuario", cause);
    }
}
