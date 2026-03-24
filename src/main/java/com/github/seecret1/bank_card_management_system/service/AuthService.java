package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.*;

public interface AuthService {

    JwtAuthenticationDto signIn(SignInByEmailRequest request);

    JwtAuthenticationDto signIn(SignInByUsernameRequest request);

    JwtAuthenticationDto signUp(SignUpRequest request);

    void signOut(RefreshTokenRequest request);

    JwtAuthenticationDto refreshToken(RefreshTokenRequest request);
}
