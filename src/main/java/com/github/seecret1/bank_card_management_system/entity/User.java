package com.github.seecret1.bank_card_management_system.entity;

import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;

    private String email;

    private String password;

    private String firstName;

    private String lastName;

    private String middleName;

    private LocalDate birthDate;

    private RoleType role;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Card> cards = new HashSet<>();
}
