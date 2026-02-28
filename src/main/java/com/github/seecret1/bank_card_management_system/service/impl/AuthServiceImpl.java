package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.*;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.security.jwt.JwtService;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public JwtAuthenticationDto singIn(SignInByEmailRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by email: " + request.getEmail()
                ));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return jwtService.generateAuthToken(user.getEmail());
        }
        throw new AuthException("Invalid email or password");
    }

    @Override
    public JwtAuthenticationDto singIn(SignInByUsernameRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by username: " + request.getUsername()
                ));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return jwtService.generateAuthToken(user.getEmail());
        }
        throw new AuthException("Invalid username or password");
    }

    @Override
    public JwtAuthenticationDto singUp(CreateUserRequest request) {
        String pass = request.getPassword();
        request.setPassword(passwordEncoder.encode(pass));
        userService.create(request);
        return jwtService.generateAuthToken(request.getEmail());
    }

    public JwtAuthenticationDto refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            var user = userService.findByEmail(jwtService.getEmailFromToken(refreshToken));
            log.info("User requested refresh token by email: {}", user.getEmail());
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        log.error("Invalid refresh token");
        return null;
    }
}
