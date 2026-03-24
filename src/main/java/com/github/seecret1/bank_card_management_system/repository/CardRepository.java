package com.github.seecret1.bank_card_management_system.repository;

import com.github.seecret1.bank_card_management_system.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, String>, JpaSpecificationExecutor<Card> {

    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<Card> findAll(Specification<Card> spec, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"user"})
    Page<Card> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM Card c JOIN c.user u" +
            " WHERE u.id = :criterial OR u.email = :criterial OR u.username = :criterial")
    Page<Card> findAllByUserCriterial(String criterial, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Card> findByNumberHash(String numberHash);
}
