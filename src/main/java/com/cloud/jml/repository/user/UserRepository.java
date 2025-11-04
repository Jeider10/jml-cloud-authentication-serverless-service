package com.cloud.jml.repository.user;

import com.cloud.jml.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByIdentificacion(Long identificacion);

    Optional<UserEntity> findByUserName(String userName);

    Optional<UserEntity> findByRoleCode(int roleCode);

    List<UserEntity> findByUserNameContainingIgnoreCase(String userName);

    List<UserEntity> findByNombresContainingIgnoreCase(String nombres);

    List<UserEntity> findByApellidosContainingIgnoreCase(String apellidos);

    List<UserEntity> findByRoleNameContainingIgnoreCase(String roleName);

    Optional<UserEntity> findByUserNameAndRoleCode(String userName, int roleCode);

    boolean existsByRoleNameIgnoreCase(String roleName);
}
