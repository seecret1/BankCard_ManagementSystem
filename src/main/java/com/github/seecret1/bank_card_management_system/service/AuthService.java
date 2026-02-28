package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByUsernameRequest;
import com.github.seecret1.bank_card_management_system.dto.request.RefreshTokenRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByEmailRequest;

public interface AuthService {

    JwtAuthenticationDto singIn(SignInByEmailRequest request);

    JwtAuthenticationDto singIn(SignInByUsernameRequest request);

    JwtAuthenticationDto singUp(CreateUserRequest request);

    JwtAuthenticationDto refreshToken(RefreshTokenRequest request);
}
