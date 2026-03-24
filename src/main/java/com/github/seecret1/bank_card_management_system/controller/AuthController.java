package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.JwtAuthenticationDto;
import com.github.seecret1.bank_card_management_system.dto.request.*;
import com.github.seecret1.bank_card_management_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in/email")
    public ResponseEntity<JwtAuthenticationDto> signInByEmail(
            @Valid @RequestBody SignInByEmailRequest request
    ) {
        return ResponseEntity.ok(authService.signIn(request));
    }

    @PostMapping("/sign-in/username")
    public ResponseEntity<JwtAuthenticationDto> signInByUsername(
            @Valid @RequestBody SignInByUsernameRequest request
    ) {
        return ResponseEntity.ok(authService.signIn(request));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationDto> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        authService.signOut(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationDto> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
