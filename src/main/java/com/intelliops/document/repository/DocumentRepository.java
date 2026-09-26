package com.intelliops.document.repository;

import com.intelliops.document.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Page<Document> findByOrganizationId(UUID organizationId, Pageable pageable);
    List<Document> findByOrganizationIdAndIndexingStatus(UUID organizationId, Document.IndexingStatus indexingStatus);
    List<Document> findByProjectId(UUID projectId);
    long countByOrganizationId(UUID organizationId);
}
