package com.cloud.jml.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class AuthenticationOptionsDTO {

    private String login;
    private int roleCode;
    private String roleName;
    private Long identificacion;
    private String nombres;
    private String apellidos;

    // ─── Permisos granulares (viajan en el token de respuesta al login) ───────
    private boolean permisoClientes;
    private boolean permisoProveedores;
    private boolean permisoProductos;
    private boolean permisoHistorial;
    private boolean permisoNuevaVenta;
    private boolean permisoPapelera;

    // ─── Columnas ocultas por seccion (viajan en el token de respuesta al login) ──
    // Mapa seccion → lista de nombres de columnas ocultas
    // ej: {"clientes": ["Telefono","Direccion"], "productos": ["Precio"]}
    private Map<String, List<String>> columnasOcultas;
}
