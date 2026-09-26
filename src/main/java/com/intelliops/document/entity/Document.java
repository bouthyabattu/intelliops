package com.intelliops.document.entity;

import com.intelliops.common.entity.TenantEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends TenantEntity {

    @Column(name = "folder_id")
    private UUID folderId;

    @Column(name = "project_id")
    private UUID projectId;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(name = "file_type", nullable = false, length = 50)
    private String fileType;

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "storage_path", nullable = false, columnDefinition = "TEXT")
    private String storagePath;

    @Column(name = "uploaded_by")
    private UUID uploadedBy;

    @Column(name = "is_indexed_for_rag", nullable = false)
    private Boolean isIndexedForRag = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "indexing_status", nullable = false, length = 50)
    private IndexingStatus indexingStatus = IndexingStatus.PENDING;

    public enum IndexingStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}
