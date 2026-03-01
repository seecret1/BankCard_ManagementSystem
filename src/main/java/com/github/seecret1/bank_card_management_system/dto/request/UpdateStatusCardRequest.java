package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusCardRequest {

    @NotBlank(message = "Card number must be set!")
    @Size(min = 16, max = 16, message = "The card number must contain 16 characters")
    private String number;

    @NotBlank(message = "Status must be set!")
    private CardStatus status;
}
