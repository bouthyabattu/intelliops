package com.intelliops.task.entity;

import com.intelliops.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "task_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskComment extends BaseEntity {

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_internal")
    private boolean isInternal = false;
}
