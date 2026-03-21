package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.*;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.repository.RefreshTokenRepository;
import com.github.seecret1.bank_card_management_system.security.CustomUserDetails;
import com.github.seecret1.bank_card_management_system.security.jwt.JwtService;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import com.github.seecret1.bank_card_management_system.service.InternalUserService;
import com.github.seecret1.bank_card_management_system.service.UserService;
import com.github.seecret1.bank_card_management_system.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final InternalUserService internalUserService;

    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public JwtAuthenticationDto signIn(SignInByEmailRequest request) {
        log.info("Sign in user by email: {}", request.getEmail());
        var user = internalUserService.findUserEntityByCriterial(request.getEmail());
        log.debug("Success sign in user by email. User: {}", user);
        return authenticate(user, request.getPassword());
    }

    @Override
    @Transactional(readOnly = true)
    public JwtAuthenticationDto signIn(SignInByUsernameRequest request) {
        log.info("Sign in user by username: {}", request.getUsername());
        var user = internalUserService.findUserEntityByCriterial(request.getUsername());
        log.debug("Success sign in user by username. User: {}", user);
        return authenticate(user, request.getPassword());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public JwtAuthenticationDto signUp(CreateUserRequest request) {
        log.info("Sign up user. User email: {}; username: {}",
                request.getEmail(), request.getUsername());

        request.setPassword(passwordEncoder
                .encode(request.getPassword())
        );
        userService.create(request);
        log.debug("User successful sign up: {}", request.getEmail());

        return jwtService.generateAuthToken(request.getEmail());
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void signOut(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new AuthException("Refresh token required for sign out");
        }

        CustomUserDetails userDetails = AuthUtils.getAuthenticatedUser();
        log.info("Sign out user: {}", userDetails.getUsername());

        refreshTokenRepository.revokeByToken(refreshToken);
        SecurityContextHolder.clearContext();

        log.debug("Successful user sign out. User: {}", userDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public JwtAuthenticationDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new AuthException("Invalid or expired refresh token");
        }

        String email = jwtService.getEmailFromToken(refreshToken);
        log.info("User requested refresh token by email: {}", email);

        return jwtService.refreshBaseToken(email, refreshToken);
    }

    private JwtAuthenticationDto authenticate(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("Invalid credentials");
        }
        log.info("User successfully authenticated: {}, created new session", user.getEmail());
        return jwtService.generateAuthToken(user.getEmail());
    }
}
