package com.github.seecret1.bank_card_management_system.service;

public interface SessionService {

    void createSession(String userId, String refreshToken);

    boolean isSessionActive(String refreshToken);

    void removeSession(String refreshToken);
}
