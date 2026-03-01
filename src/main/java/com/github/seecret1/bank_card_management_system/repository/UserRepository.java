package com.github.seecret1.bank_card_management_system.repository;

import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.model.UserSearchModel;
import com.github.seecret1.bank_card_management_system.repository.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {

    @Override
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @Override
    List<User> findAll();

    void delete(Specification<User> spec);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsernameOrEmail(String username, String email);

    default Optional<User> searchByModel(UserSearchModel searchModel) {
        return findOne(UserSpecification.withSearchModel(searchModel));
    }

    default Optional<User> findByCriterial(String searchCriterial) {
        return findOne(UserSpecification.searchByCriterial(searchCriterial));
    }
}
