package com.intelliops.document.dto;

import com.intelliops.document.entity.Document;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class DocumentResponse {
    private UUID id;
    private UUID organizationId;
    private UUID folderId;
    private UUID projectId;
    private String title;
    private String fileType;
    private Long fileSizeBytes;
    private String storagePath;
    private UUID uploadedBy;
    private Boolean isIndexedForRag;
    private Document.IndexingStatus indexingStatus;
    private Instant createdAt;
    private Instant updatedAt;
}
