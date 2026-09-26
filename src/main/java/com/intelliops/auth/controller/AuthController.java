package com.intelliops.auth.controller;

import com.intelliops.auth.dto.LoginRequest;
import com.intelliops.auth.dto.RegisterRequest;
import com.intelliops.auth.dto.TokenResponse;
import com.intelliops.auth.service.AuthService;
import com.intelliops.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, and token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<TokenResponse>> register(@Valid @RequestBody RegisterRequest request) {
        TokenResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password — returns JWT access + refresh tokens")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Login successful"));
    }

    @GetMapping("/health")
    @Operation(summary = "Auth service health check")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.ok("Auth service is operational"));
    }
}
