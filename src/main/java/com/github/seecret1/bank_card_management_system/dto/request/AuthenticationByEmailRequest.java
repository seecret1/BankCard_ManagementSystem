package com.github.seecret1.bank_card_management_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationByEmailRequest {

    @NotBlank(message = "Email must be set!")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password must be set!")
    @Size(min = 8, max = 50, message = "Size of password must start from {min} and to {max}")
    private String password;
}
