package com.github.seecret1.bank_card_management_system.security.jwt;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtService {

    @Value("${user-service.jwt.secret}")
    private String jwtSecret;

    @Value("${user-service.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    @Value("${user-service.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    public JwtAuthenticationDto generateAuthToken(String email) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(generateRefreshToken(email));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
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
