package com.github.seecret1.bank_card_management_system.repository;

import com.github.seecret1.bank_card_management_system.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, String>, JpaSpecificationExecutor<Card> {

    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<Card> findAll(Specification<Card> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user"})
    List<Card> findAll();

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM Card c WHERE c.id = :criterial OR c.number = :criterial")
    Optional<Card> findByCriterial(String criterial);

    @Query("SELECT c FROM Card c JOIN c.user u WHERE (c.id = :cardCriterial OR c.number = :cardCriterial) " +
            "AND (u.id = :userCriterial OR u.email = :userCriterial OR u.username = :userCriterial)")
    Optional<Card> findCardAndUserByCriterial(String cardCriterial, String userCriterial);
}
