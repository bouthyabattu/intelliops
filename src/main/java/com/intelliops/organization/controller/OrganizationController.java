package com.intelliops.organization.controller;

import com.intelliops.common.dto.ApiResponse;
import com.intelliops.common.dto.PageResponse;
import com.intelliops.organization.dto.CreateOrganizationRequest;
import com.intelliops.organization.dto.OrganizationResponse;
import com.intelliops.organization.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "Enterprise organization management and multi-tenancy")
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @Operation(summary = "Create a new organization (enterprise tenant)")
    public ResponseEntity<ApiResponse<OrganizationResponse>> create(
            @Valid @RequestBody CreateOrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(organizationService.create(request)));
    }

    @GetMapping
    @Operation(summary = "List all active organizations")
    public ResponseEntity<ApiResponse<PageResponse<OrganizationResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(organizationService.listAll(pageRequest))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get organization by ID")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(organizationService.getById(id)));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get organization by slug")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.ok(organizationService.getBySlug(slug)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate an organization")
    public ResponseEntity<ApiResponse<Void>> deactivate(@PathVariable UUID id) {
        organizationService.deactivate(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Organization deactivated"));
    }
}
