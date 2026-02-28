package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponse {

    private String number;

    private LocalDate dateActivation;

    private LocalDate dateExpiry;

    private CardStatus status;

    private BigDecimal balance;

    private UserInfoResponse user;
}
