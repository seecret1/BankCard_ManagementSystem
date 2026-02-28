package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardRequest {

    private String number;

    private LocalDate dateActivation;

    private LocalDate dateExpiry;

    private BigDecimal balance;

    private UserInfoResponse user;
}
