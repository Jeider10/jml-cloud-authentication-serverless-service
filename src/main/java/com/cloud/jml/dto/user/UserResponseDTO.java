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

    // Getters y Setters
    private String userName;
    private long identificacion;
    private String nombres;
    private String apellidos;
    private int roleCode;
    private String roleName;
    private String email;
    private String telefono;
    private String direccion;
    private String fechaCreacion;
    private String fechaActualizacion;
}
