package com.cloud.jml.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "empresa")
public class ConfigEmpresaEntity {

    @Id
    @Column(nullable = false)
    private long nit;

    @Column(nullable = false)
    private String nombreEmpresa;

    private String direccion;
    private String telefono;
    private String mensaje;

    @Column(name = "logo", columnDefinition = "TEXT")
    private String logo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
