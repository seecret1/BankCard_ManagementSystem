package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse entityToResponse(User user) {
        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getBirth_date(),
                user.getRoles(),
                user.getCards().stream()
                        .map(Card::getId)
                        .collect(Collectors.toSet())
        );
    }

    public List<UserResponse> toListResponse(List<User> users) {
        List<UserResponse> list = new ArrayList<>();

        for (var response : users) {
            list.add(entityToResponse(response));
        }
        return list;
    }
}
