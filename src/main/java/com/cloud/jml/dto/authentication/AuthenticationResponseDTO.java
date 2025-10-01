package com.cloud.jml.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class AuthenticationResponseDTO {

    // Getters y Setters
    private AuthenticationOptionsDTO options;
    private String authorization;
}
