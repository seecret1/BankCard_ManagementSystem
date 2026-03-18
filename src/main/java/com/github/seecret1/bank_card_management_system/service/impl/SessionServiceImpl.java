package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.session-timeout}")
    private Duration ttl;

    @Value("${app.max-active-sessions}")
    private long maxActiveSessions;

    private static final String SESSION_PREFIX = "session:";

    private static final String USER_SESSIONS_PREFIX = "user:sessions:";

    public void createSession(String userId, String refreshToken) {
        log.info("Creating session for userId={} and refreshToken", userId);
        long activeSessions = getActiveSessionsCount(userId);

        if (activeSessions >= maxActiveSessions) {
            log.error("Max active sessions reached");
            throw new AuthException(MessageFormat.format(
                    "Maximum number of active sessions {0} reached." +
                            " Please sign out from other devices first.",
                    maxActiveSessions
            ));
        }

        String sessionKey = SESSION_PREFIX + refreshToken;
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;

        redisTemplate.opsForValue().set(sessionKey, userId, ttl);
        redisTemplate.opsForSet().add(userSessionsKey, refreshToken);
        redisTemplate.expire(userSessionsKey, ttl);
    }

    public boolean isSessionActive(String refreshToken) {
        log.info("Checking if active sessions for refresh token");
        return redisTemplate.hasKey(SESSION_PREFIX + refreshToken);
    }

    public void removeSession(String refreshToken) {
        log.info("Removing session for refresh token");
        String sessionKey = SESSION_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(sessionKey);

        if (userId != null) {
            log.debug("Removing session for refresh token. UserId = {}", userId);
            redisTemplate.delete(sessionKey);
            redisTemplate.opsForSet().remove(USER_SESSIONS_PREFIX + userId, refreshToken);
        }
    }

    private long getActiveSessionsCount(String userId) {
        String key = USER_SESSIONS_PREFIX + userId;
        Long size = redisTemplate.opsForSet().size(key);
        return size != null ? size : 0;
    }
}