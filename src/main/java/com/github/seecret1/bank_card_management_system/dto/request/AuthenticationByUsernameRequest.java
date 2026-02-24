package com.github.seecret1.bank_card_management_system.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationByUsernameRequest {

    private String email;

    private String password;
}
