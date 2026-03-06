package com.github.seecret1.bank_card_management_system.model;

import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardFilterModel {

    @NotNull(message = "Page must be set!")
    @Builder.Default
    private PageModel page = new PageModel(0, 10);

    private LocalDate dateActivation;

    private LocalDate dateExpiry;

    private CardStatus status;

    private BigDecimal balance;
}
