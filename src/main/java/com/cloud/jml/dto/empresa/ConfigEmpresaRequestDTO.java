package com.cloud.jml.dto.empresa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class ConfigEmpresaRequestDTO {

    // Getters y Setters
    private Long nit;
    private String nombreEmpresa;
    private String direccion;
    private String telefono;
    private String mensaje;
    private String logo;
}
