package com.cloud.jml.dto.user;

import jakarta.validation.constraints.Email;
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
public class UserRequestDTO {

    @NotNull(message = "La identificacion es obligatoria")
    private Long identificacion;

    @NotBlank(message = "El campo 'nombres' es obligatorio")
    @Size(max = 100, message = "El campo 'nombres' no puede exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "El campo 'apellidos' es obligatorio")
    @Size(max = 100, message = "El campo 'apellidos' no puede exceder 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El campo 'userName' es obligatorio")
    @Size(max = 50, message = "El campo 'userName' no puede exceder 50 caracteres")
    private String userName;

    // La contraseña es opcional en actualizaciones — si viene vacia, no se modifica.
    // Solo se valida el tamaño minimo cuando si se proporciona un valor.
    @Size(min = 3, message = "El campo 'password' debe tener al menos 3 caracteres si se proporciona")
    private String password;

    private int roleCode;

    @Size(max = 20, message = "El campo 'telefono' no puede exceder 20 caracteres")
    private String telefono;

    @Email(message = "El campo 'correo' debe ser un correo electronico valido")
    private String correo;

    @Size(max = 200, message = "El campo 'direccion' no puede exceder 200 caracteres")
    private String direccion;

    @Size(max = 150, message = "El campo 'actualizadoPor' no puede exceder 150 caracteres")
    private String actualizadoPor;
}
