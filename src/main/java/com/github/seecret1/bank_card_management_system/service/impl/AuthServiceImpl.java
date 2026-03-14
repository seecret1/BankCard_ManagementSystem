package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.*;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.security.jwt.JwtService;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import com.github.seecret1.bank_card_management_system.service.InternalUserService;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final InternalUserService internalUserService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtAuthenticationDto signIn(SignInByEmailRequest request) {
        var user = internalUserService.findUserEntityByCriterial(request.getEmail());
        return authenticate(user, request.getPassword());
    }

    @Override
    public JwtAuthenticationDto signIn(SignInByUsernameRequest request) {
        var user = internalUserService.findUserEntityByCriterial(request.getUsername());
        return authenticate(user, request.getPassword());
    }

    @Override
    @Transactional
    public JwtAuthenticationDto signUp(CreateUserRequest request) {
        String pass = request.getPassword();
        request.setPassword(passwordEncoder.encode(pass));
        userService.create(request);
        return jwtService.generateAuthToken(request.getEmail());
    }

    @Override
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
        return jwtService.generateAuthToken(user.getEmail());
    }
}
