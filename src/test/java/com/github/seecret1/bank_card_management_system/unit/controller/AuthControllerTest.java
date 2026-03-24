package com.github.seecret1.bank_card_management_system.unit.controller;

import com.github.seecret1.bank_card_management_system.controller.AuthController;
import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.RefreshTokenRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByEmailRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByUsernameRequest;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private SignInByUsernameRequest signInByUsernameRequest;
    private SignInByEmailRequest signInByEmailRequest;
    private CreateUserRequest createUserRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private JwtAuthenticationDto jwtAuthenticationDto;

    private final String EMAIL = "test@example.com";
    private final String USERNAME = "testuser";
    private final String PASSWORD = "password123";
    private final String REFRESH_TOKEN = "refresh.token.123";
    private final String JWT_TOKEN = "jwt.token.123";

    @BeforeEach
    void setUp() {
        signInByUsernameRequest = new SignInByUsernameRequest(USERNAME, PASSWORD);
        signInByEmailRequest = new SignInByEmailRequest(EMAIL, PASSWORD);
        refreshTokenRequest = new RefreshTokenRequest(REFRESH_TOKEN);

        createUserRequest.setUsername(USERNAME);
        createUserRequest.setEmail(EMAIL);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setFirstName("Pavel");
        createUserRequest.setLastName("Muzhikov");
        createUserRequest.setBirthDate(LocalDate.of(2000, 1, 1));
        createUserRequest.setRole(RoleType.ROLE_USER);

        jwtAuthenticationDto = new JwtAuthenticationDto();
        jwtAuthenticationDto.setToken(JWT_TOKEN);
        jwtAuthenticationDto.setRefreshToken(REFRESH_TOKEN);
    }

    @Test
    void signUp_ReturnTokens() {
        when(authService.signUp(any(CreateUserRequest.class))).thenReturn(jwtAuthenticationDto);

        ResponseEntity<JwtAuthenticationDto> response = authController.signUp(createUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(JWT_TOKEN);
        assertThat(response.getBody().getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

    @Test
    void signInByUsername_ReturnTokens() {
        when(authService.signIn(any(SignInByUsernameRequest.class))).thenReturn(jwtAuthenticationDto);

        ResponseEntity<JwtAuthenticationDto> response = authController.signInByUsername(signInByUsernameRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(JWT_TOKEN);
        assertThat(response.getBody().getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

    @Test
    void signInByEmail_ReturnTokens() {
        when(authService.signIn(any(SignInByEmailRequest.class))).thenReturn(jwtAuthenticationDto);

        ResponseEntity<JwtAuthenticationDto> response = authController.signInByEmail(signInByEmailRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(JWT_TOKEN);
        assertThat(response.getBody().getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }

    @Test
    void refreshToken_ReturnNewTokens() {
        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(jwtAuthenticationDto);

        ResponseEntity<JwtAuthenticationDto> response = authController.refreshToken(refreshTokenRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(JWT_TOKEN);
        assertThat(response.getBody().getRefreshToken()).isEqualTo(REFRESH_TOKEN);
    }
}
