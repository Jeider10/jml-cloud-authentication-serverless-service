package com.cloud.jml.repository;

import com.cloud.jml.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByIdentificacion(Long identificacion);

    Optional<UserEntity> findByUserNameAndRoleCode(String userName, int roleCode);

    boolean existsByRoleNameIgnoreCase(String roleName);
}
