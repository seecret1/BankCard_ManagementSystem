package com.github.seecret1.bank_card_management_system.repository;

import com.github.seecret1.bank_card_management_system.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {

    Optional<Card> findByNumber(String number);
}
