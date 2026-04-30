package com.cloud.jml.dto.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class ConfigEmpresaRequestDTO {

    // FIX: Se agregaron validaciones para evitar datos vacios o nulos
    @NotNull(message = "El campo 'nit' es obligatorio")
    private Long nit;

    @NotBlank(message = "El campo 'nombreEmpresa' es obligatorio")
    @Size(max = 150, message = "El campo 'nombreEmpresa' no puede exceder 150 caracteres")
    private String nombreEmpresa;

    @Size(max = 200, message = "El campo 'direccion' no puede exceder 200 caracteres")
    private String direccion;

    @Size(max = 20, message = "El campo 'telefono' no puede exceder 20 caracteres")
    private String telefono;

    private String mensaje;
    private String logo;
}
