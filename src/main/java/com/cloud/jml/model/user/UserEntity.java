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
    private String email;
    private String direccion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    private String historialUltimoActualizado;

    @Column(name = "creado_por")
    private String creadoPor;

    // ─── Soft delete (papelera) ───────────────────────────────────────────────
    @Column(name = "eliminado", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean eliminado = false;

    @Column(name = "fecha_eliminacion")
    private LocalDateTime fechaEliminacion;

    @Column(name = "eliminado_por_id", length = 150)
    private String eliminadoPorId;

    @Column(name = "eliminado_por_nombre", length = 200)
    private String eliminadoPorNombre;
}
