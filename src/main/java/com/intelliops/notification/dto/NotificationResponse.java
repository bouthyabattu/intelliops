package com.intelliops.notification.dto;

import com.intelliops.notification.entity.Notification;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID userId,
        String type,
        String title,
        String body,
        String resourceType,
        UUID resourceId,
        String resourceUrl,
        boolean read,
        Instant readAt,
        Map<String, Object> metadata,
        Instant createdAt
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getUserId(),
                n.getType().name(), n.getTitle(), n.getBody(),
                n.getResourceType(), n.getResourceId(), n.getResourceUrl(),
                n.isRead(), n.getReadAt(), n.getMetadata(), n.getCreatedAt()
        );
    }
}
