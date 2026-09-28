package com.cloud.jml.model.columnas;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Almacena las columnas ocultas de un usuario en una seccion determinada.
 * Una fila = un usuario + una seccion.
 * columnasOcultas es un JSON array serializado como String, ej: ["Precio","Proveedor"]
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuario_columnas_config",
        uniqueConstraints = @UniqueConstraint(columnNames = {"identificacion_usuario", "seccion"}))
public class UsuarioColumnasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identificacion_usuario", nullable = false)
    private Long identificacionUsuario;

    /**
     * Nombre de la seccion: clientes | proveedores | productos | historial | usuarios | papelera
     */
    @Column(name = "seccion", nullable = false, length = 50)
    private String seccion;

    /**
     * JSON array con los nombres de las columnas ocultas, ej: ["Precio","Proveedor"]
     */
    @Column(name = "columnas_ocultas", nullable = false, columnDefinition = "TEXT")
    private String columnasOcultas = "[]";
}
