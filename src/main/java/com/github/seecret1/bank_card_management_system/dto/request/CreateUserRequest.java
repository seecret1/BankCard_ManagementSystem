package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class CreateUserRequest {

    private String username;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birth_date;

    private Set<RoleType> roles;

    private String card_id;
}
