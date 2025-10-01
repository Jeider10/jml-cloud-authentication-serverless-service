package com.cloud.jml.repository;

import com.cloud.jml.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleCode(int roleCode);

    Optional<RoleEntity> findByRoleCodeOrRoleName(int roleCode, String roleName);
}
