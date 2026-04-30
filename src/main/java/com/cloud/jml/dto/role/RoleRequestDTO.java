package com.cloud.jml.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class RoleRequestDTO {

    private int roleCode;

    // FIX: Se agregaron validaciones para evitar datos vacios o nulos
    @NotBlank(message = "El campo 'roleName' es obligatorio")
    @Size(max = 50, message = "El campo 'roleName' no puede exceder 50 caracteres")
    private String roleName;

    @Size(max = 200, message = "El campo 'descripcion' no puede exceder 200 caracteres")
    private String descripcion;
}
