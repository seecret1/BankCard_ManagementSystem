package com.github.seecret1.bank_card_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInByUsernameRequest {

    @NotBlank(message = "Username must be set!")
    @Size(min = 8, message = "Size of username must start from {min}")
    private String username;

    @NotBlank(message = "Password must be set!")
    @Size(min = 8, message = "Size of password must start from {min}")
    private String password;
}
