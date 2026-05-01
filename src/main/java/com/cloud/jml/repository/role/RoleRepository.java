package com.cloud.jml.repository.role;

import com.cloud.jml.model.role.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleCode(int roleCode);

    List<RoleEntity> findByRoleNameContainingIgnoreCase(String roleName);

    List<RoleEntity> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}
