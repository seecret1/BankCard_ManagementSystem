package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.RefreshTokenRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UserCredentialsRequest;

public interface AuthService {

    JwtAuthenticationDto singIn(UserCredentialsRequest request);

    JwtAuthenticationDto refreshToken(RefreshTokenRequest request);
}
