package com.github.seecret1.bank_card_management_system.entity;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    private String username;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    private boolean deleted = false;

    private Instant deletedAt;

    private String deletedBy;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Card> cards = new HashSet<>();

    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.deletedAt = Instant.now();
        this.deletedBy = deletedBy;
    }
}
