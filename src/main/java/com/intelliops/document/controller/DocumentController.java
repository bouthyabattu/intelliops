package com.intelliops.document.controller;

import com.intelliops.common.dto.ApiResponse;
import com.intelliops.common.dto.PageResponse;
import com.intelliops.document.dto.CreateDocumentRequest;
import com.intelliops.document.dto.DocumentResponse;
import com.intelliops.document.entity.Document;
import com.intelliops.document.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Document management and RAG ingestion endpoints")
@SecurityRequirement(name = "bearerAuth")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    @Operation(summary = "Register document", description = "Registers document metadata for indexing and storage")
    public ResponseEntity<ApiResponse<DocumentResponse>> create(@Valid @RequestBody CreateDocumentRequest request) {
        DocumentResponse response = documentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @GetMapping("/organization/{organizationId}")
    @Operation(summary = "List documents by organization", description = "Retrieves paginated document library for tenant")
    public ResponseEntity<ApiResponse<PageResponse<DocumentResponse>>> listByOrganization(
            @PathVariable UUID organizationId,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<DocumentResponse> response = PageResponse.of(documentService.listByOrganization(organizationId, pageable));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "List documents by project", description = "Retrieves all documents associated with a project")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> listByProject(@PathVariable UUID projectId) {
        List<DocumentResponse> response = documentService.listByProject(projectId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieves document metadata by UUID")
    public ResponseEntity<ApiResponse<DocumentResponse>> getById(@PathVariable UUID id) {
        DocumentResponse response = documentService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}/indexing-status")
    @Operation(summary = "Update RAG indexing status", description = "Callback used by AI service worker when vectorization completes")
    public ResponseEntity<ApiResponse<DocumentResponse>> updateIndexingStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        String statusStr = (String) body.get("status");
        Document.IndexingStatus status = Document.IndexingStatus.valueOf(statusStr);
        boolean isIndexed = Boolean.TRUE.equals(body.get("isIndexed"));
        DocumentResponse response = documentService.updateIndexingStatus(id, status, isIndexed);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Deletes document record")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.ok(ApiResponse.okMsg("Document deleted successfully"));
    }
}
