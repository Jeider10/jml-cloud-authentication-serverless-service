package com.cloud.jml.model.empresa;

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
@Table(name = "empresa")
public class ConfigEmpresaEntity {

    @Id
    @Column(nullable = false)
    private Long nit;

    @Column(nullable = false)
    private String nombreEmpresa;

    private String direccion;
    private String telefono;
    private String mensaje;

    @Column(name = "logo", columnDefinition = "LONGTEXT")
    private String logo;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
