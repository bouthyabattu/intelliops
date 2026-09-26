package com.intelliops.organization.repository;

import com.intelliops.organization.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {
    List<OrganizationMember> findByOrganizationIdAndIsActiveTrue(UUID organizationId);
    Optional<OrganizationMember> findByOrganizationIdAndUserId(UUID organizationId, UUID userId);
    boolean existsByOrganizationIdAndUserId(UUID organizationId, UUID userId);
    List<OrganizationMember> findByUserIdAndIsActiveTrue(UUID userId);
}
