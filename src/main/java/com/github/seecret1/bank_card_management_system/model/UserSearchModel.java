package com.github.seecret1.bank_card_management_system.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchModel {

    @NotNull(message = "Page must be set!")
    @Builder.Default
    private PageModel page = new PageModel(0, 10);

    private String id;

    private String username;

    private String email;
}
