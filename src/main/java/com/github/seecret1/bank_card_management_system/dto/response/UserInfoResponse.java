package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class UserInfoResponse {

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private RoleType role;

    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    private boolean deleted;

    private String deletedBy;
}
