package com.cloud.jml.exception.empresa;

import org.springframework.http.HttpStatus;

public class ConfigEmpresaNotFoundException extends ConfigEmpresaRuntimeException {

    public ConfigEmpresaNotFoundException(String nombreEmpresa) {
        super(
                HttpStatus.NOT_FOUND,
                "❌ [CONSULTA] Empresa no encontrada: " + nombreEmpresa);
    }
}
