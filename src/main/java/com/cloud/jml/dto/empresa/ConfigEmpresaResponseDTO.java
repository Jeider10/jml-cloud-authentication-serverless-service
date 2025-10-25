package com.cloud.jml.dto.empresa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class ConfigEmpresaResponseDTO {

    // Getters y Setters
    private long nit;
    private String nombreEmpresa;
    private String direccion;
    private String telefono;
    private String mensaje;
    private String logo;
    private String fechaCreacion;
    private String fechaActualizacion;
}
