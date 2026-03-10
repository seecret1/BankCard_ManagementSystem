package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.util.CardMaskUtil;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CardSummaryResponse {

    private String number;

    private CardStatus status;

    private BigDecimal balance;

    public String getMaskedNumber() {
        return CardMaskUtil.maskCardNumber(this.number);
    }
}
