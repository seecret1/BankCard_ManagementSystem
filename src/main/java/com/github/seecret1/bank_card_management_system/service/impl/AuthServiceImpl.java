package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.*;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.security.jwt.JwtService;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public JwtAuthenticationDto singIn(SignInByEmailRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        return getJwtAuthenticationDto(optionalUser, request.getPassword());
    }

    @Override
    public JwtAuthenticationDto singIn(SignInByUsernameRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getUsername());
        return getJwtAuthenticationDto(optionalUser, request.getPassword());
    }

    @Override
    public JwtAuthenticationDto singUp(SignUpRequest request) {
        CreateUserRequest userRequest = new CreateUserRequest();
        String email = request.getEmail();

        userRequest.setUsername(request.getUsername());
        userRequest.setEmail(email);
        userRequest.setPassword(passwordEncoder.encode(request.getPassword()));
        userRequest.setRole(RoleType.ROLE_USER);

        userService.create(userRequest);
        return jwtService.generateAuthToken(email);
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

    private JwtAuthenticationDto getJwtAuthenticationDto(Optional<User> optionalUser, String password) {
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return jwtService.generateAuthToken(user.getEmail());
            }
        }
        throw new AuthException("Invalid email or password");
    }
}
