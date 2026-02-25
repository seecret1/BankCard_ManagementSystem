package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserInfoResponse {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private RoleType role;
}
