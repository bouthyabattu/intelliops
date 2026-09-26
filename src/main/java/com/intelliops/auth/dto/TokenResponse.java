package com.intelliops.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private UserSummary user;

    @Data
    @Builder
    public static class UserSummary {
        private UUID id;
        private String email;
        private String firstName;
        private String lastName;
        private String avatarUrl;
    }
}
