package com.intelliops.notification.controller;

import com.intelliops.common.dto.ApiResponse;
import com.intelliops.common.dto.PageResponse;
import com.intelliops.notification.dto.NotificationResponse;
import com.intelliops.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "User notification feed — list, mark-read, unread count")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List notifications for a user (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> list(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        return ResponseEntity.ok(ApiResponse.ok(
                notificationService.listForUser(userId, unreadOnly, PageRequest.of(page, size))));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get count of unread notifications for a user")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unreadCount(@RequestParam UUID userId) {
        long count = notificationService.countUnread(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", count)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a specific notification as read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        notificationService.markRead(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Notification marked as read"));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read for a user")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> markAllRead(@RequestParam UUID userId) {
        int count = notificationService.markAllRead(userId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("markedCount", count)));
    }
}
