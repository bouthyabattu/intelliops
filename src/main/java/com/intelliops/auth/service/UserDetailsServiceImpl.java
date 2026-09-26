package com.intelliops.auth.service;

import com.intelliops.auth.entity.User;
import com.intelliops.auth.entity.UserCredential;
import com.intelliops.auth.repository.UserCredentialRepository;
import com.intelliops.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserCredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        UserCredential credential = credentialRepository.findByUserId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException("No credentials found for user: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                credential.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                Collections.emptyList()
        );
    }
}
