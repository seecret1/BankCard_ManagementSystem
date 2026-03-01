package com.github.seecret1.bank_card_management_system.repository.specification;

import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.model.UserSearchModel;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface UserSpecification {

    static Specification<User> withFilter(UserFilterModel filterModel) {
        return Specification.where(isEquals("firstName", filterModel.getFirsName())
                        .and(isEquals("lastName", filterModel.getLastName()))
                        .and(isEquals("middleName", filterModel.getMiddleName()))
                        .and(isEquals("birthDate", filterModel.getBirthDate()))
                        .and(isEquals("role", filterModel.getRole()))
                );
    }

    private static <T> Specification<User> isEquals(String fieldName, T object) {
        return (root, query, cb) -> {
            if (object == null) {
                return null;
            }
            return cb.equal(root.get(fieldName), object);
        };
    }

    private static <T> Specification<User> isEquals(String fieldName, LocalDate date) {
        return (root, query, cb) -> {
            if (date == null) {
                return null;
            }

            return cb.equal(
                    cb.function(
                        "date", LocalDate.class, root.get(fieldName)),
                        date
            );
        };
    }

    private static <T> Specification<User> isEquals(String fieldName, RoleType roleType) {
        return (root, query, cb) -> {
            return cb.equal(root.get(fieldName), roleType);
        };
    }

    static Specification<User> withSearchModel(UserSearchModel searchModel) {
        return (root, query, cb) -> {

            if (searchModel == null) return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchModel.getId())) {
                predicates.add(cb.equal(root.get("id"), searchModel.getId()));
            }
            if (StringUtils.hasText(searchModel.getUsername())) {
                predicates.add(cb.equal(root.get("username"), searchModel.getUsername()));
            }
            if (StringUtils.hasText(searchModel.getEmail())) {
                predicates.add(cb.equal(root.get("email"), searchModel.getEmail()));
            }

            if (predicates.isEmpty()) return cb.conjunction();
            else if (predicates.size() == 1) return predicates.get(0);

            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    static Specification<User> searchByCriterial(String searchCriterial) {
        return (root, query, cb) -> {

            if (!StringUtils.hasText(searchCriterial))  return cb.conjunction();

            return cb.or(
                    cb.equal(root.get("id"), searchCriterial),
                    cb.equal(root.get("username"), searchCriterial),
                    cb.equal(root.get("email"), searchCriterial)
            );
        };
    }
}
