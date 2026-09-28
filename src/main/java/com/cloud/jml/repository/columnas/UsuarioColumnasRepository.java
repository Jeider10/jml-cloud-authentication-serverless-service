package com.cloud.jml.repository.columnas;

import com.cloud.jml.model.columnas.UsuarioColumnasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioColumnasRepository extends JpaRepository<UsuarioColumnasEntity, Long> {

    /**
     * Obtiene la config de columnas de un usuario para una seccion especifica.
     */
    Optional<UsuarioColumnasEntity> findByIdentificacionUsuarioAndSeccion(Long identificacionUsuario, String seccion);

    /**
     * Obtiene todas las configuraciones de columnas de un usuario (todas las secciones).
     */
    List<UsuarioColumnasEntity> findAllByIdentificacionUsuario(Long identificacionUsuario);
}
