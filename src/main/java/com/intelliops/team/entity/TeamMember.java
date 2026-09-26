package com.intelliops.team.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"team_id", "user_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TeamMember extends BaseEntity {

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String role = "MEMBER";   // MEMBER | LEAD

    @Builder.Default
    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt = Instant.now();

    public enum TeamRole { MEMBER, LEAD }
}
