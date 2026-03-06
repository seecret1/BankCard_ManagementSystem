package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 8, message = "Size of username must start from {min}")
    private String username;

    @Email(message = "Invalid email address")
    private String email;

    @Size(min = 8, message = "Size of password must start from {min}")
    private String password;

    private RoleType role;
}
