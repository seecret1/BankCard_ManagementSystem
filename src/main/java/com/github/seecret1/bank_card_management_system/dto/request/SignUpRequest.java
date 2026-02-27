package com.github.seecret1.bank_card_management_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Username must be set!")
    @Size(min = 8, message = "Size of username must start from {min}")
    private String username;

    @NotBlank(message = "Email must be set!")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password must be set!")
    @Size(min = 8, message = "Size of password must start from {min}")
    private String password;
}
