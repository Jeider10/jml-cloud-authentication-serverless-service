package com.cloud.jml.exception.authentication;

import com.cloud.jml.exception.empresa.ConfigEmpresaRuntimeException;
import org.springframework.http.HttpStatus;

public class AuthenticationRefreshTokenDeletionException extends ConfigEmpresaRuntimeException {

    public AuthenticationRefreshTokenDeletionException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "🗑️ [ELIMINACIÓN] " + message);
    }

    public AuthenticationRefreshTokenDeletionException(String message, Throwable cause) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "🗑️ [ELIMINACIÓN] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 🔒 Violación de integridad referencial (por constraints o dependencias)
    public static AuthenticationRefreshTokenDeletionException integrityViolation(Throwable cause) {
        return new AuthenticationRefreshTokenDeletionException(
                "❌ [INTEGRIDAD] No se pudo eliminar la empresa debido a una violación de integridad referencial",
                cause
        );
    }

    // ⚙️ Error de acceso a datos
    public static AuthenticationRefreshTokenDeletionException dataAccessError(Throwable cause) {
        return new AuthenticationRefreshTokenDeletionException(
                "❌ [DATOS] Error de acceso a la base de datos al intentar eliminar la empresa",
                cause
        );
    }

    // 💥 Error inesperado
    public static AuthenticationRefreshTokenDeletionException unexpected(Throwable cause) {
        return new AuthenticationRefreshTokenDeletionException(
                "💥 [INESPERADO] Ocurrió un error inesperado al intentar eliminar la empresa",
                cause
        );
    }
}
