package com.cloud.jml.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class AuthenticationRequestDTO {

    // FIX: Se agregaron validaciones para evitar datos vacios o nulos en el login
    @NotBlank(message = "El campo 'usuario' es obligatorio")
    private String usuario;

    @NotBlank(message = "El campo 'password' es obligatorio")
    private String password;
}
