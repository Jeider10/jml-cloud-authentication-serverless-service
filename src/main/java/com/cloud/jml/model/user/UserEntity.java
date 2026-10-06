package com.cloud.jml.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
@Entity
@Table(name = "usuarios")
public class UserEntity {

    @Id
    @Column(nullable = false)
    private Long identificacion;

    @Column(nullable = false)
    private String nombres;

    @Column(nullable = false)
    private String apellidos;

    @Column(unique = true, nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int roleCode;

    @Column(nullable = false)
    private String roleName;

    private String telefono;
    private String correo;
    private String direccion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "creado_por")
    private String creadoPor;

    @Column(name = "actualizado_por", length = 150)
    private String actualizadoPor;

    // ─── Permisos granulares (aplica solo a rol CAJERO) ──────────────────────
    // true = habilitado, false = deshabilitado por el administrador
    @Column(name = "permiso_clientes", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean permisoClientes = true;

    @Column(name = "permiso_proveedores", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean permisoProveedores = true;

    @Column(name = "permiso_productos", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean permisoProductos = true;

    @Column(name = "permiso_historial", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean permisoHistorial = true;

    @Column(name = "permiso_nueva_venta", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean permisoNuevaVenta = true;

    @Column(name = "permiso_papelera", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean permisoPapelera = true;

    // ─── Soft delete (papelera) ───────────────────────────────────────────────
    @Column(name = "eliminado", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean eliminado = false;

    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;

    @Column(name = "eliminado_por_id", length = 150)
    private String eliminadoPorId;

    @Column(name = "eliminado_por_nombre", length = 200)
    private String eliminadoPorNombre;

    // ─── Foto de perfil (base64) ──────────────────────────────────────────────
    // Se almacena como texto largo para evitar dependencia de almacenamiento externo.
    // El frontend envia la imagen codificada en base64 y la muestra directamente con <img src=...>
    @Column(name = "foto", columnDefinition = "TEXT")
    private String foto;
}
