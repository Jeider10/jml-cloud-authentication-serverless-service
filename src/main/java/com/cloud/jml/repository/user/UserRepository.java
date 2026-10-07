package com.cloud.jml.repository.user;

import com.cloud.jml.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // ─── Activos (eliminado = false) ─────────────────────────────────────────
    Optional<UserEntity> findByIdentificacionAndEliminadoFalse(Long identificacion);

    List<UserEntity> findAllByEliminadoFalse();

    Optional<UserEntity> findByUserNameAndEliminadoFalse(String userName);

    List<UserEntity> findByUserNameContainingIgnoreCaseAndEliminadoFalse(String userName);

    List<UserEntity> findByNombresContainingIgnoreCaseAndEliminadoFalse(String nombres);

    List<UserEntity> findByApellidosContainingIgnoreCaseAndEliminadoFalse(String apellidos);

    List<UserEntity> findByRoleNameContainingIgnoreCaseAndEliminadoFalse(String roleName);

    List<UserEntity> findByFechaCreacionBetweenAndEliminadoFalse(LocalDateTime inicio, LocalDateTime fin);

    List<UserEntity> findByFechaActualizacionBetweenAndEliminadoFalse(LocalDateTime inicio, LocalDateTime fin);

    // ─── Papelera (eliminado = true) ─────────────────────────────────────────
    List<UserEntity> findAllByEliminadoTrue();

    Optional<UserEntity> findByIdentificacionAndEliminadoTrue(Long identificacion);

    List<UserEntity> findByFechaEliminacionBetweenAndEliminadoTrue(LocalDateTime inicio, LocalDateTime fin);

    List<UserEntity> findByEliminadoPorIdContainingIgnoreCaseAndEliminadoTrue(String eliminadoPorId);

    // ─── Sin filtro: para auth, resetPassword, validaciones existentes ────────
    Optional<UserEntity> findByIdentificacion(Long identificacion);

    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByRoleCode(int roleCode);

    List<UserEntity> findByUserNameContainingIgnoreCase(String userName);

    List<UserEntity> findByNombresContainingIgnoreCase(String nombres);

    List<UserEntity> findByApellidosContainingIgnoreCase(String apellidos);

    List<UserEntity> findByRoleNameContainingIgnoreCase(String roleName);

    List<UserEntity> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);

    List<UserEntity> findByFechaActualizacionBetween(LocalDateTime inicio, LocalDateTime fin);

    Optional<UserEntity> findByUserNameAndRoleCode(String userName, int roleCode);

    boolean existsByRoleNameIgnoreCase(String roleName);

    long countByRoleNameIgnoreCase(String roleName);
}
