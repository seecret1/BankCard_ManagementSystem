package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;

import java.util.Set;

public class UserRequest {

    private String username;

    private String email;

    private String password;

    private Set<RoleType> roles;
}
