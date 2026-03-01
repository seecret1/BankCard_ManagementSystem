package com.github.seecret1.bank_card_management_system.model;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterModel {

    @NotNull(message = "Page must be set!")
    @Builder.Default
    private PageModel page = new PageModel(0, 10);

    private String firsName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private RoleType role;
}
