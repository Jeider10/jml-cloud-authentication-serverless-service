package com.cloud.jml.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class UserResponseDTO {

    private Long identificacion;
    private String nombres;
    private String apellidos;
    private String userName;
    private Integer roleCode;
    private String roleName;
    private String telefono;
    private String email;
    private String direccion;
    private String fechaCreacion;
    private String fechaActualizacion;
    private String historialUltimoActualizado;
}
