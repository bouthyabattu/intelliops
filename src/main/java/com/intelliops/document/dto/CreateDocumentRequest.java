package com.intelliops.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateDocumentRequest {
    @NotNull
    private UUID organizationId;

    private UUID folderId;
    private UUID projectId;

    @NotBlank
    private String title;

    @NotBlank
    private String fileType;

    @NotNull
    private Long fileSizeBytes;

    @NotBlank
    private String storagePath;

    private UUID uploadedBy;
}
