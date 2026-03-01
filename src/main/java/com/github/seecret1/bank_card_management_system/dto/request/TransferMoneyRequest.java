package com.github.seecret1.bank_card_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferMoneyRequest {

    @NotBlank(message = "number card from must be set!")
    @Size(min = 16, max = 16, message = "The card number must contain 16 characters")
    private String numberFrom;

    @NotBlank(message = "number card to must be set!")
    @Size(min = 16, max = 16, message = "The card number must contain 16 characters")
    private String numberTo;

    @NotBlank(message = "Amount must be set!")
    private BigDecimal amount;
}
