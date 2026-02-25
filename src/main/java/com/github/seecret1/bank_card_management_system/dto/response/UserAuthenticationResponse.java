package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthenticationResponse {

    private String email;

    private RoleType role;
}
