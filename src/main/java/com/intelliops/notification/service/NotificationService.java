package com.intelliops.notification.service;

import com.intelliops.common.dto.PageResponse;
import com.intelliops.notification.dto.NotificationResponse;
import com.intelliops.notification.entity.Notification;
import com.intelliops.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Creates and persists a notification for a specific user.
     * Called internally by other services (tasks, projects, workflows, agents).
     */
    @Transactional
    public NotificationResponse create(UUID organizationId, UUID userId,
                                       Notification.NotificationType type,
                                       String title, String body,
                                       String resourceType, UUID resourceId,
                                       Map<String, Object> metadata) {
        Notification notification = Notification.builder()
                .organizationId(organizationId)
                .userId(userId)
                .type(type)
                .title(title)
                .body(body)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .metadata(metadata)
                .read(false)
                .build();
        notification = notificationRepository.save(notification);
        log.debug("Created notification '{}' for user {}", type, userId);
        return NotificationResponse.from(notification);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> listForUser(UUID userId, boolean unreadOnly, Pageable pageable) {
        var page = unreadOnly
                ? notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId, pageable)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.from(page.map(NotificationResponse::from));
    }

    @Transactional(readOnly = true)
    public long countUnread(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(UUID notificationId, UUID userId) {
        notificationRepository.markOneRead(notificationId, userId);
    }

    @Transactional
    public int markAllRead(UUID userId) {
        return notificationRepository.markAllReadForUser(userId);
    }
}
