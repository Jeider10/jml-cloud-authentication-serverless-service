package com.cloud.jml.repository.empresa;

import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigEmpresaRepository extends JpaRepository<ConfigEmpresaEntity, Long> {
    Optional<ConfigEmpresaEntity> findByNit(Long nit);

    Optional<ConfigEmpresaEntity> findByNombreEmpresa(String nombreEmpresa);
}
