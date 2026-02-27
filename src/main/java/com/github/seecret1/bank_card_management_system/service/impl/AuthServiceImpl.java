package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.RefreshTokenRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UserCredentialsRequest;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.security.jwt.JwtService;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationDto singIn(UserCredentialsRequest credentialsRequest) {
        User user = findByCredentials(credentialsRequest);
        log.info("Sing in user: {}", user);
        return jwtService.generateAuthToken(user.getEmail());
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

    private User findByCredentials(UserCredentialsRequest credentialsRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(credentialsRequest.getEmail());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(credentialsRequest.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationCredentialsNotFoundException("Invalid email or password");
    }
}
