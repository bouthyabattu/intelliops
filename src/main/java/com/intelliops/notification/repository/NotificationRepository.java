package com.intelliops.notification.repository;

import com.intelliops.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUserIdAndReadFalse(UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.userId = :userId AND n.read = false")
    int markAllReadForUser(UUID userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id AND n.userId = :userId")
    int markOneRead(UUID id, UUID userId);
}
