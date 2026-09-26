package com.intelliops.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI intelliOpsOpenAPI() {
        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()
                .info(new Info()
                        .title("IntelliOps Core API")
                        .description("""
                                Enterprise AI-Powered Operations Platform API.
                                
                                Provides REST endpoints for:
                                - Authentication & Identity (JWT + Keycloak OIDC)
                                - Multi-tenant Organization Management
                                - Projects, Sprints & Milestones
                                - Task & Issue Tracking
                                - Document Management & Versioning
                                - Workflow Automation Engine
                                - Analytics & KPI Dashboards
                                - GitHub/Slack/Jira Integrations
                                - Enterprise Audit Logs
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("IntelliOps Engineering")
                                .email("engineering@intelliops.ai"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Provide a valid JWT access token obtained from POST /api/v1/auth/login")));
    }
}
