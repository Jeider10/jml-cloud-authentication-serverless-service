package com.cloud.jml.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class UserRequestDTO {

    // Getters y Setters
    private Long identificacion;
    private String nombres;
    private String apellidos;
    private String userName;
    private String password;
    private int roleCode;
    private String telefono;
    private String email;
    private String direccion;
}
