package com.github.seecret1.bank_card_management_system.entity;

import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String number;

    private LocalDate dateActivation;

    private LocalDate dateExpiry;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private BigDecimal balance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Card(
            String number,
            LocalDate dateActivation,
            LocalDate dateExpiry,
            CardStatus status,
            BigDecimal balance,
            User user
    ) {
        this.number = number;
        this.dateActivation = dateActivation;
        this.dateExpiry = dateExpiry;
        this.status = status;
        this.balance = balance;
        this.user = user;
    }
}
