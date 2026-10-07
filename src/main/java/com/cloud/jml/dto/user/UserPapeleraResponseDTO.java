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
    // Fecha formateada hasta la que el registro se conserva en papelera (calculada como fechaEliminacion + 2 meses)
    private String fechaExpiracion;
    // Dias restantes hasta que expire — negativo si ya expiro (calculado al vuelo en el mapper, no persistido)
    private Long diasRestantes;
    private String eliminadoPorId;
    private String eliminadoPorNombre;
    // Rol del usuario que envio el registro a papelera
    private String eliminadoPorRol;
    // Motivo de eliminacion ingresado por el usuario
    private String motivo;
}
