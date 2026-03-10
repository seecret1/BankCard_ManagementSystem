package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardSummaryResponse {

    private String number;

    private CardStatus status;

    private BigDecimal balance;
}
