package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.dto.CardDto;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String username;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private RoleType role;

    private Set<CardDto> cards;
}
