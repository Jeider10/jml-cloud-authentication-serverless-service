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
    private String accessToken; // JWT de acceso (token_use = "accessToken")
    private Long expiresIn; // Tiempo de expiración en segundos
    private String tokenType; // Tipo de token "Bearer"
    private String refreshToken;  // Token de refresco (token_use = "refreshToken")
    private String authorization; // JWT de identidad (token_use = "authorization")
}
