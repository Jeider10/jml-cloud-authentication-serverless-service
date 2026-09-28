package com.cloud.jml.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class UserPapeleraResponseDTO {

    private Long identificacion;
    private String nombres;
    private String apellidos;
    private String userName;
    private String roleName;
    private String correo;
    private String telefono;
    private String direccion;
    private String creadoPor;
    private String fechaCreacion;
    private String fechaEliminacion;
    private String eliminadoPorId;
    private String eliminadoPorNombre;
}
