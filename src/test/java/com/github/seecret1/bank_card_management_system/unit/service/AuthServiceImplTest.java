package com.github.seecret1.bank_card_management_system.unit.service;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.RefreshTokenRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByEmailRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByUsernameRequest;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.repository.RefreshTokenRepository;
import com.github.seecret1.bank_card_management_system.security.jwt.JwtService;
import com.github.seecret1.bank_card_management_system.service.InternalUserService;
import com.github.seecret1.bank_card_management_system.service.UserService;
import com.github.seecret1.bank_card_management_system.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private InternalUserService internalUserService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private CreateUserRequest createUserRequest;
    private SignInByEmailRequest signInByEmailRequest;
    private SignInByUsernameRequest signInByUsernameRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private JwtAuthenticationDto jwtAuthenticationDto;

    private final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private final String USERNAME = "testuser123";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";
    private final String JWT_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.test";
    private final String REFRESH_TOKEN = "refresh.token.123";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        user.setFirstName("Pavel");
        user.setLastName("Muzhikov");
        user.setMiddleName("Ivanovich");
        user.setBirthDate(LocalDate.of(1990, 1, 15));
        user.setRole(RoleType.ROLE_USER);

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setEmail(EMAIL);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setFirstName("Pavel");
        createUserRequest.setLastName("Muzhikov");
        createUserRequest.setMiddleName("Ivanovich");
        createUserRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        createUserRequest.setRole(RoleType.ROLE_USER);

        signInByEmailRequest = new SignInByEmailRequest(EMAIL, PASSWORD);
        signInByUsernameRequest = new SignInByUsernameRequest(USERNAME, PASSWORD);
        refreshTokenRequest = new RefreshTokenRequest(REFRESH_TOKEN);

        jwtAuthenticationDto = new JwtAuthenticationDto();
        jwtAuthenticationDto.setToken(JWT_TOKEN);
        jwtAuthenticationDto.setRefreshToken(REFRESH_TOKEN);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should successfully sign in user by email")
    void signInByEmail_ValidCredentials_ReturnsTokens() {
        when(internalUserService.findUserEntityByCriterial(EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateAuthToken(EMAIL)).thenReturn(jwtAuthenticationDto);

        JwtAuthenticationDto result = authService.signIn(signInByEmailRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(JWT_TOKEN);
        assertThat(result.getRefreshToken()).isEqualTo(REFRESH_TOKEN);

        verify(internalUserService).findUserEntityByCriterial(EMAIL);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateAuthToken(EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when user not found by email during sign in")
    void signInByEmail_UserNotFound_ThrowsException() {
        when(internalUserService.findUserEntityByCriterial(EMAIL))
                .thenThrow(new UserNotFoundException("User not found by email: " + EMAIL));

        assertThatThrownBy(() -> authService.signIn(signInByEmailRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found by email: " + EMAIL);

        verify(internalUserService).findUserEntityByCriterial(EMAIL);
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Should throw exception when password is invalid for email sign in")
    void signInByEmail_InvalidPassword_ThrowsException() {
        when(internalUserService.findUserEntityByCriterial(EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> authService.signIn(signInByEmailRequest))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid credentials");

        verify(internalUserService).findUserEntityByCriterial(EMAIL);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should successfully sign in user by username")
    void signInByUsername_ValidCredentials_ReturnsTokens() {
        when(internalUserService.findUserEntityByCriterial(USERNAME)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(jwtService.generateAuthToken(EMAIL)).thenReturn(jwtAuthenticationDto);

        JwtAuthenticationDto result = authService.signIn(signInByUsernameRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(JWT_TOKEN);
        assertThat(result.getRefreshToken()).isEqualTo(REFRESH_TOKEN);

        verify(internalUserService).findUserEntityByCriterial(USERNAME);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verify(jwtService).generateAuthToken(EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when user not found by username during sign in")
    void signInByUsername_UserNotFound_ThrowsException() {
        when(internalUserService.findUserEntityByCriterial(USERNAME))
                .thenThrow(new UserNotFoundException("User not found by username: " + USERNAME));

        assertThatThrownBy(() -> authService.signIn(signInByUsernameRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found by username: " + USERNAME);

        verify(internalUserService).findUserEntityByCriterial(USERNAME);
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    @DisplayName("Should throw exception when password is invalid for username sign in")
    void signInByUsername_InvalidPassword_ThrowsException() {
        when(internalUserService.findUserEntityByCriterial(USERNAME)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        assertThatThrownBy(() -> authService.signIn(signInByUsernameRequest))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid credentials");

        verify(internalUserService).findUserEntityByCriterial(USERNAME);
        verify(passwordEncoder).matches(PASSWORD, ENCODED_PASSWORD);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Should successfully sign up new user")
    void signUp_ValidRequest_ReturnsTokens() {
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userService.create(any(CreateUserRequest.class))).thenReturn(null);
        when(jwtService.generateAuthToken(EMAIL)).thenReturn(jwtAuthenticationDto);

        JwtAuthenticationDto result = authService.signUp(createUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(JWT_TOKEN);
        assertThat(result.getRefreshToken()).isEqualTo(REFRESH_TOKEN);
        assertThat(createUserRequest.getPassword()).isEqualTo(ENCODED_PASSWORD);

        verify(passwordEncoder).encode(PASSWORD);
        verify(userService).create(createUserRequest);
        verify(jwtService).generateAuthToken(EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when signing out with null refresh token")
    void signOut_NullRefreshToken_ThrowsException() {
        assertThatThrownBy(() -> authService.signOut(null))
                .isInstanceOf(AuthException.class)
                .hasMessage("Refresh token required for sign out");

        verify(refreshTokenRepository, never()).revokeByToken(anyString());
    }

    @Test
    @DisplayName("Should throw exception when signing out with blank refresh token")
    void signOut_BlankRefreshToken_ThrowsException() {
        RefreshTokenRequest request = new RefreshTokenRequest("   ");
        assertThatThrownBy(() -> authService.signOut(request))
                .isInstanceOf(AuthException.class)
                .hasMessage("Refresh token required for sign out");

        verify(refreshTokenRepository, never()).revokeByToken(anyString());
    }

    @Test
    @DisplayName("Should successfully refresh token")
    void refreshToken_ValidRefreshToken_ReturnsNewTokens() {
        when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(true);
        when(jwtService.getEmailFromToken(REFRESH_TOKEN)).thenReturn(EMAIL);
        when(jwtService.refreshBaseToken(EMAIL, REFRESH_TOKEN)).thenReturn(jwtAuthenticationDto);

        JwtAuthenticationDto result = authService.refreshToken(refreshTokenRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(JWT_TOKEN);
        assertThat(result.getRefreshToken()).isEqualTo(REFRESH_TOKEN);

        verify(jwtService).validateRefreshToken(REFRESH_TOKEN);
        verify(jwtService).getEmailFromToken(REFRESH_TOKEN);
        verify(jwtService).refreshBaseToken(EMAIL, REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid")
    void refreshToken_InvalidRefreshToken_ThrowsException() {
        when(jwtService.validateRefreshToken(REFRESH_TOKEN)).thenReturn(false);

        assertThatThrownBy(() -> authService.refreshToken(refreshTokenRequest))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid or expired refresh token");

        verify(jwtService).validateRefreshToken(REFRESH_TOKEN);
        verify(jwtService, never()).getEmailFromToken(anyString());
        verify(jwtService, never()).refreshBaseToken(anyString(), anyString());
    }
}