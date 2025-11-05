package com.cloud.jml.model.token;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name = "token", columnDefinition = "LONGTEXT")
    private String token;

    @Column(nullable = false, unique = true, updatable = false)
    private String jti;

    @Column(nullable = false)
    private String usuario;

    @Column(nullable = false)
    private int roleCode;

    @Column(nullable = false)
    private String roleName;

    @Column(nullable = false)
    private String scope;

    @Column(nullable = false)
    private Instant expiryDate;

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}
