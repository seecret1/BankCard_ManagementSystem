package com.github.seecret1.bank_card_management_system.repository;

import com.github.seecret1.bank_card_management_system.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, String>, JpaSpecificationExecutor<Card> {

    @Override
    Page<Card> findAll(Specification<Card> spec, Pageable pageable);

    @Override
    List<Card> findAll();

    @Query("SELECT c FROM Card c WHERE c.id = :criterial OR c.number = :criterial")
    Optional<Card> findByCriterial(String criterial);
}
