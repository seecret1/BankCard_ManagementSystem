package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private RoleType role;

    private Instant createdAt;

    private Instant updatedAt;

    private boolean deleted;

    private Instant deletedAt;

    private String deletedBy;

    private Set<CardSummaryResponse> cards;
}
