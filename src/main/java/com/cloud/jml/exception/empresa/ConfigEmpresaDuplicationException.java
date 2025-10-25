package com.cloud.jml.exception.empresa;

import org.springframework.http.HttpStatus;

public class ConfigEmpresaDuplicationException extends ConfigEmpresaRuntimeException {

    public ConfigEmpresaDuplicationException(String nombreEmpresa, Long nit) {
        super(
                HttpStatus.CONFLICT,
                "⚠️ [DUPLICADO] Empresa duplicada: " + nombreEmpresa + "con nit: " + nit);
    }
}
