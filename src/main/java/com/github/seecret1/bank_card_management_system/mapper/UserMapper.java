package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
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
            list.add(toResponse(response));
        }
        return list;
    }

    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setBirth_date(request.getBirth_date());
        user.setRoles(request.getRoles());
        user.setCards(mappedToCards(request.getCards()));
        return user;
    }

    private Card mappedToCard(String cardId) {
        Card card = new Card();
        card.setId(cardId);
        return card;
    }

    private Set<Card> mappedToCards(Set<String> cardsId) {
        Set<Card> cards = new HashSet<>();
        for (String cardId : cardsId) {
            cards.add(mappedToCard(cardId));
        }
        return cards;
    }
}
