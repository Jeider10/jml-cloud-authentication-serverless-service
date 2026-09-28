package com.cloud.jml.dto.columnas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Payload para guardar/actualizar las columnas ocultas de un usuario en una seccion.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioColumnasRequestDTO {

    @NotNull(message = "La identificacion del usuario es obligatoria")
    private Long identificacionUsuario;

    @NotBlank(message = "La seccion es obligatoria")
    private String seccion;

    /**
     * Lista de nombres de columnas que deben ocultarse para este usuario en esta seccion.
     */
    private List<String> columnasOcultas;
}
