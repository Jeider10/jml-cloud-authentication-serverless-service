package com.cloud.jml.repository;

import com.cloud.jml.model.AuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<AuthenticationEntity, Long> {

}
