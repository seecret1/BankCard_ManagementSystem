package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusCardRequest {

    private String number;

    private CardStatus status;
}
