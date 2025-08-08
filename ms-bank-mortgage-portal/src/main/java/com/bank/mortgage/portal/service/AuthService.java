package com.bank.mortgage.portal.service;

import com.bank.mortgage.portal.component.JwtTokenProvider;
import com.bank.mortgage.portal.dto.LoginRequest;
import com.bank.mortgage.portal.dto.LoginResponse;
import com.bank.mortgage.portal.dto.User;
import com.bank.mortgage.portal.exception.UnauthorizedException;
import com.bank.mortgage.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Invalid password for user: {}", request.getUsername());
            throw new UnauthorizedException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            log.warn("Inactive user login attempt: {}", request.getUsername());
            throw new UnauthorizedException("Account is disabled");
        }

        String token = jwtTokenProvider.generateToken(user);
        log.info("Login successful for user: {}", request.getUsername());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }
}

