package com.cloud.jml.exception.empresa;

import org.springframework.http.HttpStatus;

public class ConfigEmpresaDeletionException extends ConfigEmpresaRuntimeException {

    public ConfigEmpresaDeletionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ [ELIMINACION] " + message);
    }

    public ConfigEmpresaDeletionException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "🗑️ [ELIMINACION] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violacion de integridad referencial (por constraints o dependencias)
    public static ConfigEmpresaDeletionException integrityViolation(Throwable cause) {
        return new ConfigEmpresaDeletionException(
                "❌ [INTEGRIDAD] No se pudo eliminar la empresa debido a una violacion de integridad referencial",
                cause
        );
    }

    // ⚙️ Error de acceso a datos
    public static ConfigEmpresaDeletionException dataAccessError(Throwable cause) {
        return new ConfigEmpresaDeletionException(
                "❌ [DATOS] Error de acceso a la base de datos al intentar eliminar la empresa",
                cause
        );
    }

    // 💥 Error inesperado
    public static ConfigEmpresaDeletionException unexpected(Throwable cause) {
        return new ConfigEmpresaDeletionException(
                "💥 [INESPERADO] Ocurrio un error inesperado al intentar eliminar la empresa",
                cause
        );
    }
}
