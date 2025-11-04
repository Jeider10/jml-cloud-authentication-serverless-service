package com.cloud.jml.repository.token;

import com.cloud.jml.model.token.RefreshTokenEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    Page<RefreshTokenEntity> findByExpiryDateBefore(Instant now, PageRequest of);

    int deleteAllByExpiryDateBefore(Instant now);
}

