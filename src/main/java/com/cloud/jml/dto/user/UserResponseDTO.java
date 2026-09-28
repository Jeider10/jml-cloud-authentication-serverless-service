package com.cloud.jml.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class UserResponseDTO {

    private Long identificacion;
    private String nombres;
    private String apellidos;
    private String userName;
    private Integer roleCode;
    private String roleName;
    private String telefono;
    private String correo;
    private String direccion;
    private String fechaCreacion;
    private String fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    // ─── Permisos granulares ──────────────────────────────────────────────────
    private boolean permisoClientes;
    private boolean permisoProveedores;
    private boolean permisoProductos;
    private boolean permisoHistorial;
    private boolean permisoNuevaVenta;
    private boolean permisoPapelera;
}
