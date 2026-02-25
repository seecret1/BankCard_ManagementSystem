package com.github.seecret1.bank_card_management_system.dto;

import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class CardDto {

    private String number;

    private LocalDate date_activation;

    private LocalDate date_expiry;

    private CardStatus status;

    private BigDecimal balance;

    private UserInfoResponse user;
}
