package com.cloud.jml.exception.empresa;

import org.springframework.http.HttpStatus;

public class ConfigEmpresaLogoUploadException extends ConfigEmpresaRuntimeException {

    public ConfigEmpresaLogoUploadException(String message) {
        super(HttpStatus.BAD_REQUEST, "🖼️ [LOGO] " + message);
    }

    public ConfigEmpresaLogoUploadException(String message, Throwable cause) {
        super(
                HttpStatus.BAD_REQUEST,
                "🖼️ [LOGO] " + message +
                        (cause != null ? " | 💥 Causa: " + cause.getMessage() : "")
        );
    }

    // 📦 Archivo excede tamaño
    public static ConfigEmpresaLogoUploadException fileTooLarge(long size) {
        return new ConfigEmpresaLogoUploadException(
                "⚠️ El archivo excede el tamaño máximo permitido (10 MB). Tamaño recibido: " + size + " bytes"
        );
    }

    // ❌ Error al subir a S3
    public static ConfigEmpresaLogoUploadException s3UploadError(Throwable cause) {
        return new ConfigEmpresaLogoUploadException(
                "❌ Error al subir el logo al bucket S3",
                cause
        );
    }

    // 💥 Error inesperado
    public static ConfigEmpresaLogoUploadException unexpected(Throwable cause) {
        return new ConfigEmpresaLogoUploadException(
                "💥 Error inesperado durante la carga del logo",
                cause
        );
    }
}
