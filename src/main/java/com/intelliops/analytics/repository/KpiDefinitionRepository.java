package com.intelliops.analytics.repository;

import com.intelliops.analytics.entity.KpiDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KpiDefinitionRepository extends JpaRepository<KpiDefinition, UUID> {
    List<KpiDefinition> findByOrganizationId(UUID organizationId);
    Optional<KpiDefinition> findByOrganizationIdAndCode(UUID organizationId, String code);
    List<KpiDefinition> findByOrganizationIdAndCategory(UUID organizationId, KpiDefinition.KpiCategory category);
}
