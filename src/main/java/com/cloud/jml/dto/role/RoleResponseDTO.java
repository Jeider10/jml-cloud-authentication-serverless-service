package com.cloud.jml.dto.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class RoleResponseDTO {

    private int roleCode;
    private String roleName;
    private String descripcion;
    private String fechaCreacion;
    private String fechaActualizacion;
}
