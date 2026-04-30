package com.cloud.jml.exception.empresa;

import org.springframework.http.HttpStatus;

public class ConfigEmpresaPersistenceException extends ConfigEmpresaRuntimeException {

    public ConfigEmpresaPersistenceException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "💾 [PERSISTENCIA] " + message);
    }

    public ConfigEmpresaPersistenceException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "💾 [PERSISTENCIA] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violacion de integridad (constraint, duplicado, etc.) al guardar
    public static ConfigEmpresaPersistenceException integrityViolation(Throwable cause) {
        return new ConfigEmpresaPersistenceException(
                "❌ [INTEGRIDAD] Violacion de integridad en base de datos al guardar la empresa",
                cause
        );
    }

    // ⚙️ Error tecnico de acceso a datos
    public static ConfigEmpresaPersistenceException dataAccessError(Throwable cause) {
        return new ConfigEmpresaPersistenceException(
                "❌ [DATOS] Error de acceso a datos al intentar guardar la empresa",
                cause
        );
    }

    // 💥 Error inesperado
    public static ConfigEmpresaPersistenceException unexpected(Throwable cause) {
        return new ConfigEmpresaPersistenceException(
                "💥 [INESPERADO] Ocurrio un error inesperado al registrar la empresa",
                cause
        );
    }
}
