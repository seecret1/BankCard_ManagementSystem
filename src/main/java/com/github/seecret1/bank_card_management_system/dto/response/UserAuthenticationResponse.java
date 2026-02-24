package com.github.seecret1.bank_card_management_system.dto.response;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserAuthenticationResponse {

    private String email;

    private Set<RoleType> roles;
}
