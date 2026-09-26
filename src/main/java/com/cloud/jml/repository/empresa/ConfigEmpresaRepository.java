package com.cloud.jml.repository.empresa;

import com.cloud.jml.model.empresa.ConfigEmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConfigEmpresaRepository extends JpaRepository<ConfigEmpresaEntity, Long> {

    // ─── Activos (eliminado = false) ─────────────────────────────────────────
    Optional<ConfigEmpresaEntity> findByNitAndEliminadoFalse(Long nit);

    List<ConfigEmpresaEntity> findAllByEliminadoFalse();

    // ─── Papelera (eliminado = true) ─────────────────────────────────────────
    List<ConfigEmpresaEntity> findAllByEliminadoTrue();

    Optional<ConfigEmpresaEntity> findByNitAndEliminadoTrue(Long nit);

    List<ConfigEmpresaEntity> findByFechaEliminacionBetweenAndEliminadoTrue(LocalDateTime inicio, LocalDateTime fin);

    List<ConfigEmpresaEntity> findByEliminadoPorIdContainingIgnoreCaseAndEliminadoTrue(String eliminadoPorId);

    // ─── Sin filtro (compatibilidad) ─────────────────────────────────────────
    Optional<ConfigEmpresaEntity> findByNit(Long nit);

    Optional<ConfigEmpresaEntity> findByNombreEmpresa(String nombreEmpresa);
}
