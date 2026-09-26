package com.intelliops.task.repository;

import com.intelliops.task.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByProjectId(UUID projectId, Pageable pageable);

    List<Task> findByProjectIdAndStatus(UUID projectId, Task.TaskStatus status);

    List<Task> findByOrganizationId(UUID organizationId);

    @Query("SELECT t FROM Task t WHERE t.projectId = :projectId AND t.sprintId = :sprintId")
    List<Task> findByProjectIdAndSprintId(@Param("projectId") UUID projectId,
                                           @Param("sprintId") UUID sprintId);

    @Query("SELECT MAX(t.taskNumber) FROM Task t WHERE t.projectId = :projectId")
    Integer findMaxTaskNumberByProjectId(@Param("projectId") UUID projectId);

    long countByProjectIdAndStatus(UUID projectId, Task.TaskStatus status);
}
