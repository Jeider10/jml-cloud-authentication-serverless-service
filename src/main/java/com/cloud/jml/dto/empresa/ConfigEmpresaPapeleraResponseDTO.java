package com.cloud.jml.dto.empresa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class ConfigEmpresaPapeleraResponseDTO {

    private Long nit;
    private String nombreEmpresa;
    private String direccion;
    private String telefono;
    private String email;
    private String fechaCreacion;
    private String fechaEliminacion;
    private String eliminadoPorId;
    private String eliminadoPorNombre;
}
