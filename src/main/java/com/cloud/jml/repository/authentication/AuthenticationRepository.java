package com.cloud.jml.repository.authentication;

import com.cloud.jml.model.authentication.AuthenticationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AuthenticationRepository extends JpaRepository<AuthenticationEntity, Long> {
    Page<AuthenticationEntity> findByFechaCreacionBefore(LocalDateTime cutoff, PageRequest pageRequest);
}
