package com.cloud.jml.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPermisosRequestDTO {

    @NotNull(message = "La identificacion es obligatoria")
    private Long identificacion;

    private boolean permisoNuevaVenta;
    private boolean permisoClientes;
    private boolean permisoProveedores;
    private boolean permisoProductos;
    private boolean permisoHistorial;
    private boolean permisoPapelera;
}
