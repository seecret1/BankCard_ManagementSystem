package com.github.seecret1.bank_card_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token must be set!")
    private String refreshToken;
}
