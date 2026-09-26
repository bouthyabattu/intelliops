package com.intelliops.organization.service;

import com.intelliops.common.exception.BusinessException;
import com.intelliops.common.exception.ResourceNotFoundException;
import com.intelliops.organization.dto.CreateOrganizationRequest;
import com.intelliops.organization.dto.OrganizationResponse;
import com.intelliops.organization.entity.Organization;
import com.intelliops.organization.repository.OrganizationMemberRepository;
import com.intelliops.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository memberRepository;

    @Transactional
    public OrganizationResponse create(CreateOrganizationRequest request) {
        if (organizationRepository.existsBySlug(request.getSlug())) {
            throw new BusinessException(
                "An organization with slug '" + request.getSlug() + "' already exists",
                "SLUG_ALREADY_EXISTS"
            );
        }
        Organization org = Organization.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .logoUrl(request.getLogoUrl())
                .isActive(true)
                .planTier("ENTERPRISE")
                .build();
        org = organizationRepository.save(org);
        log.info("Organization created: {} ({})", org.getName(), org.getSlug());
        return toResponse(org);
    }

    @Transactional(readOnly = true)
    public Page<OrganizationResponse> listAll(Pageable pageable) {
        return organizationRepository.findByIsActiveTrue(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getById(UUID id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
        return toResponse(org);
    }

    @Transactional(readOnly = true)
    public OrganizationResponse getBySlug(String slug) {
        Organization org = organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found with slug: " + slug));
        return toResponse(org);
    }

    @Transactional
    public void deactivate(UUID id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", id));
        org.setActive(false);
        organizationRepository.save(org);
        log.info("Organization deactivated: {}", id);
    }

    private OrganizationResponse toResponse(Organization org) {
        int memberCount = memberRepository.findByOrganizationIdAndIsActiveTrue(org.getId()).size();
        return OrganizationResponse.builder()
                .id(org.getId())
                .name(org.getName())
                .slug(org.getSlug())
                .logoUrl(org.getLogoUrl())
                .planTier(org.getPlanTier())
                .isActive(org.isActive())
                .memberCount(memberCount)
                .createdAt(org.getCreatedAt())
                .build();
    }
}
