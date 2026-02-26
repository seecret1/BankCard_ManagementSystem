package com.github.seecret1.bank_card_management_system.dto.request;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Username must be set!")
    @Size(min = 8, message = "Size of username must start from {min}")
    private String username;

    @NotBlank(message = "Email must be set!")
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Password must be set!")
    @Size(min = 8, message = "Size of password must start from {min}")
    private String password;

    @NotBlank(message = "First name mast be set!")
    @Size(max = 80, message = "Size of first name must start to {max}")
    private String firstName;

    @NotBlank(message = "Last name mast be set!")
    @Size(max = 100, message = "Size of first name must start to {max}")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Birth date mast be set!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private RoleType role;
}
