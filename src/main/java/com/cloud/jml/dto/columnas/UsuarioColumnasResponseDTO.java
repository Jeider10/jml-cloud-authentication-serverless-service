package com.cloud.jml.dto.columnas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Respuesta con la configuracion de columnas ocultas de un usuario en una seccion.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioColumnasResponseDTO {

    private Long identificacionUsuario;
    private String seccion;
    private List<String> columnasOcultas;
}
