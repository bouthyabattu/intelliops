package com.intelliops.auth.service;

import com.intelliops.auth.dto.LoginRequest;
import com.intelliops.auth.dto.RegisterRequest;
import com.intelliops.auth.dto.TokenResponse;
import com.intelliops.auth.entity.User;
import com.intelliops.auth.entity.UserCredential;
import com.intelliops.auth.repository.UserCredentialRepository;
import com.intelliops.auth.repository.UserRepository;
import com.intelliops.common.exception.BusinessException;
import com.intelliops.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Value("${intelliops.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("An account already exists for this email address", "EMAIL_ALREADY_EXISTS");
        }

        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(true)
                .isEmailVerified(false)
                .build();
        user = userRepository.save(user);

        UserCredential credential = UserCredential.builder()
                .userId(user.getId())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isMfaEnabled(false)
                .build();
        credentialRepository.save(credential);

        log.info("New user registered: {}", user.getEmail());
        return buildTokenResponse(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));

        log.info("User authenticated: {}", user.getEmail());
        return buildTokenResponse(user);
    }

    private TokenResponse buildTokenResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs / 1000)
                .user(TokenResponse.UserSummary.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .avatarUrl(user.getAvatarUrl())
                        .build())
                .build();
    }
}
