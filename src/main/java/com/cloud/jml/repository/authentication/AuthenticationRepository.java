package com.cloud.jml.repository.authentication;

import com.cloud.jml.model.authentication.AuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<AuthenticationEntity, Long> {

}
