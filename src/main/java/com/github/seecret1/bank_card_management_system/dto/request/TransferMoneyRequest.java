package com.github.seecret1.bank_card_management_system.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferMoneyRequest {

    private String numberFrom;

    private String numberTo;

    private BigDecimal amount;
}
