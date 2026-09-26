package com.intelliops.auth.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCredential extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Builder.Default
    @Column(name = "is_mfa_enabled", nullable = false)
    private boolean isMfaEnabled = false;

    @Column(name = "mfa_secret")
    private String mfaSecret;
}
