package com.github.seecret1.bank_card_management_system.unit.service;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.entity.RefreshToken;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.repository.RefreshTokenRepository;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtService jwtService;

    private final String RAW_SECRET_KEY = "MyVeryLongAndSecureSecretKeyForJWTTokenSigningThatIsAtLeast64CharactersLongToMeetHS512Requirements123456789";
    private final String BASE64_SECRET_KEY = Base64.getEncoder().encodeToString(RAW_SECRET_KEY.getBytes());
    private final Duration TOKEN_EXPIRATION = Duration.ofMinutes(15);
    private final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(7);

    private final String EMAIL = "test@example.com";
    private final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", BASE64_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "tokenExpiration", TOKEN_EXPIRATION);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", REFRESH_TOKEN_EXPIRATION);

        user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setUsername("testuser");

        refreshToken = new RefreshToken();
        refreshToken.setToken("test-refresh-token");
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        refreshToken.setRevoked(false);
    }

    @Test
    @DisplayName("Should generate auth token successfully")
    void generateAuthToken_GenerateTokens() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto result = jwtService.generateAuthToken(EMAIL);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isNotBlank();
        assertThat(result.getRefreshToken()).isNotBlank();

        verify(refreshTokenRepository).revokeAllByUserId(USER_ID);
        verify(refreshTokenRepository, atLeast(1)).save(any(RefreshToken.class));

        assertThat(jwtService.validateJwtToken(result.getToken())).isTrue();
        assertThat(jwtService.getEmailFromToken(result.getToken())).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when generating token for non-existent user")
    void generateAuthToken_UserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jwtService.generateAuthToken(EMAIL))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("User not found");

        verify(refreshTokenRepository, never()).revokeAllByUserId(anyString());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when refresh token not found")
    void refreshBaseToken_TokenNotFound() {
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jwtService.refreshBaseToken(EMAIL, "invalid-token"))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Invalid refresh token");
    }

    @Test
    @DisplayName("Should throw exception when refresh token is revoked")
    void refreshBaseToken_TokenRevoked() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto initialTokens = jwtService.generateAuthToken(EMAIL);
        String token = initialTokens.getRefreshToken();

        refreshToken.setToken(token);
        refreshToken.setRevoked(true);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> jwtService.refreshBaseToken(EMAIL, token))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Refresh token has been revoked");
    }

    @Test
    @DisplayName("Should throw exception when refresh token expired")
    void refreshBaseToken_TokenExpired() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto initialTokens = jwtService.generateAuthToken(EMAIL);
        String token = initialTokens.getRefreshToken();

        refreshToken.setToken(token);
        refreshToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> jwtService.refreshBaseToken(EMAIL, token))
                .isInstanceOf(AuthException.class)
                .hasMessageContaining("Refresh token expired");

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    @DisplayName("Should extract email from token")
    void getEmailFromToken_ExtractEmail() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(EMAIL);
        String token = tokens.getToken();
        String extractedEmail = jwtService.getEmailFromToken(token);

        assertThat(extractedEmail).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Should validate valid JWT token")
    void validateJwtToken_ValidToken() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(EMAIL);
        String token = tokens.getToken();

        assertThat(jwtService.validateJwtToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should return false for invalid JWT token")
    void validateJwtToken_InvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThat(jwtService.validateJwtToken(invalidToken)).isFalse();
    }

    @Test
    @DisplayName("Should validate refresh token successfully")
    void validateRefreshToken_ValidToken() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(EMAIL);
        String token = tokens.getRefreshToken();
        refreshToken.setToken(token);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        assertThat(jwtService.validateRefreshToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should return false for expired refresh token")
    void validateRefreshToken_ExpiredToken() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(EMAIL);
        String token = tokens.getRefreshToken();

        refreshToken.setToken(token);
        refreshToken.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        assertThat(jwtService.validateRefreshToken(token)).isFalse();
    }

    @Test
    @DisplayName("Should return false for revoked refresh token")
    void validateRefreshToken_RevokedToken() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(EMAIL);
        String token = tokens.getRefreshToken();

        refreshToken.setToken(token);
        refreshToken.setRevoked(true);
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        assertThat(jwtService.validateRefreshToken(token)).isFalse();
    }

    @Test
    @DisplayName("Should return false when refresh token not found in DB")
    void validateRefreshToken_TokenNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        JwtAuthenticationDto tokens = jwtService.generateAuthToken(EMAIL);
        String token = tokens.getRefreshToken();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThat(jwtService.validateRefreshToken(token)).isFalse();
    }
}