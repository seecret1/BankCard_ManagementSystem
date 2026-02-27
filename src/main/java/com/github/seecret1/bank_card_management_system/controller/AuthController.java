package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.RefreshTokenRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByEmailRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignInByUsernameRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignUpRequest;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sing-in/email")
    public ResponseEntity<JwtAuthenticationDto> singInByEmail(
            @RequestBody SignInByEmailRequest request
    ) {
        return ResponseEntity.ok(authService.singIn(request));
    }

    @PostMapping("/sing-in/username")
    public ResponseEntity<JwtAuthenticationDto> singInByUsername(
            @RequestBody SignInByUsernameRequest request
    ) {
        return ResponseEntity.ok(authService.singIn(request));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationDto> signUp(
            @RequestBody SignUpRequest request
    ) {
        return ResponseEntity.ok(authService.singUp(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationDto> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
