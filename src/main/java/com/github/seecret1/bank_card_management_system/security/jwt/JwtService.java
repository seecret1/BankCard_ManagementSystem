package com.github.seecret1.bank_card_management_system.security.jwt;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.entity.RefreshToken;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.repository.RefreshTokenRepository;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    @Value("${user-service.jwt.secret}")
    private String jwtSecret;

    @Value("${user-service.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    @Value("${user-service.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public JwtAuthenticationDto generateAuthToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        // Отзываем все предыдущие refresh токены пользователя
        refreshTokenRepository.revokeAllByUserId(user.getId());

        String jwtToken = generateJwtToken(email);
        String refreshToken = generateRefreshToken(email);

        // Сохраняем новый refresh токен в БД
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plus(refreshTokenExpiration));
        refreshTokenEntity.setRevoked(false);
        refreshTokenRepository.save(refreshTokenEntity);

        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(jwtToken);
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    @Transactional
    public JwtAuthenticationDto refreshBaseToken(String email, String oldRefreshToken) {
        // Проверяем, существует ли и не отозван ли старый refresh токен
        RefreshToken storedToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new AuthException("Invalid refresh token"));

        if (storedToken.isRevoked()) {
            throw new AuthException("Refresh token has been revoked");
        }

        if (storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new AuthException("Refresh token expired");
        }

        // Отзываем старый токен
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // Генерируем новые токены
        return generateAuthToken(email);
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JwtException", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JwtException", ex);
        } catch (MalformedJwtException ex) {
            log.error("Malformed JwtException", ex);
        } catch (SecurityException ex) {
            log.error("Security Exception", ex);
        } catch (Exception ex) {
            log.error("Invalid token", ex);
        }
        return false;
    }

    public boolean validateRefreshToken(String token) {
        try {
            if (!validateJwtToken(token)) {
                return false;
            }

            RefreshToken storedToken = refreshTokenRepository.findByToken(token)
                    .orElse(null);

            return storedToken != null &&
                    !storedToken.isRevoked() &&
                    storedToken.getExpiryDate().isAfter(LocalDateTime.now());

        } catch (Exception ex) {
            log.error("Error validating refresh token", ex);
            return false;
        }
    }

    private String generateJwtToken(String email) {
        Date date = Date.from(
                LocalDateTime.now().plusMinutes(tokenExpiration.toMinutes())
                        .atZone(ZoneId.systemDefault()).toInstant()
        );
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private String generateRefreshToken(String email) {
        Date date = Date.from(
                LocalDateTime.now().plusMinutes(refreshTokenExpiration.toMinutes())
                        .atZone(ZoneId.systemDefault()).toInstant()
        );
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}