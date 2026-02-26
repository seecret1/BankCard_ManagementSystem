package com.github.seecret1.bank_card_management_system.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenRequest {

    @JsonProperty("refreshToken")
    private String refreshToken;
}
