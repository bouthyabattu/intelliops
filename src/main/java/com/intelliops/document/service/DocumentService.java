package com.intelliops.document.service;

import com.intelliops.common.exception.ResourceNotFoundException;
import com.intelliops.document.dto.CreateDocumentRequest;
import com.intelliops.document.dto.DocumentResponse;
import com.intelliops.document.entity.Document;
import com.intelliops.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Transactional
    public DocumentResponse create(CreateDocumentRequest request) {
        Document document = Document.builder()
                .folderId(request.getFolderId())
                .projectId(request.getProjectId())
                .title(request.getTitle())
                .fileType(request.getFileType())
                .fileSizeBytes(request.getFileSizeBytes())
                .storagePath(request.getStoragePath())
                .uploadedBy(request.getUploadedBy())
                .isIndexedForRag(false)
                .indexingStatus(Document.IndexingStatus.PENDING)
                .build();
        document.setOrganizationId(request.getOrganizationId());

        document = documentRepository.save(document);
        log.info("Document registered: {} ({}) in org {}", document.getTitle(), document.getFileType(), document.getOrganizationId());
        return toResponse(document);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> listByOrganization(UUID organizationId, Pageable pageable) {
        return documentRepository.findByOrganizationId(organizationId, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> listByProject(UUID projectId) {
        return documentRepository.findByProjectId(projectId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DocumentResponse getById(UUID id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        return toResponse(doc);
    }

    @Transactional
    public DocumentResponse updateIndexingStatus(UUID id, Document.IndexingStatus status, boolean isIndexed) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));
        doc.setIndexingStatus(status);
        doc.setIsIndexedForRag(isIndexed);
        doc = documentRepository.save(doc);
        log.info("Document {} indexing status updated to {}", id, status);
        return toResponse(doc);
    }

    @Transactional
    public void delete(UUID id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document", id);
        }
        documentRepository.deleteById(id);
        log.info("Document deleted: {}", id);
    }

    private DocumentResponse toResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .organizationId(document.getOrganizationId())
                .folderId(document.getFolderId())
                .projectId(document.getProjectId())
                .title(document.getTitle())
                .fileType(document.getFileType())
                .fileSizeBytes(document.getFileSizeBytes())
                .storagePath(document.getStoragePath())
                .uploadedBy(document.getUploadedBy())
                .isIndexedForRag(document.getIsIndexedForRag())
                .indexingStatus(document.getIndexingStatus())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
