package com.cloud.jml.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class AuthenticationOptionsDTO {

    // Getters y Setters
    private String login;
    private int roleCode;
    private String roleName;
}
