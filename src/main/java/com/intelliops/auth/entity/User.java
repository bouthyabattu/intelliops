package com.intelliops.auth.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Builder.Default
    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;

    @Column(name = "keycloak_id", unique = true)
    private String keycloakId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Builder.Default
    @Column(nullable = false)
    private String timezone = "UTC";

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
