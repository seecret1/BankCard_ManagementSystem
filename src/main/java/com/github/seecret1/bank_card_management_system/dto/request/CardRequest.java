package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardRequest {

    @NotBlank(message = "Card number must be set!")
    @Size(min = 16, max = 16, message = "The card number must contain 16 characters")
    private String number;

    @NotBlank(message = "Date activation must be set!")
    private LocalDate dateActivation;

    @NotBlank(message = "Date expiry must be set!")
    private LocalDate dateExpiry;

    @NotBlank(message = "Balance must be set!")
    private BigDecimal balance;

    @NotBlank(message = "User must be set!")
    private UserInfoResponse user;
}
