package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final CardMapper cardMapper;

    public UserResponse toResponse(User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String middleName = user.getMiddleName();
        LocalDate birthDate = user.getBirthDate();
        RoleType role = user.getRole();

        UserResponse response = new UserResponse();
        UserInfoResponse userInfo = new UserInfoResponse();

        userInfo.setUsername(username);
        userInfo.setEmail(email);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        userInfo.setMiddleName(middleName);
        userInfo.setBirthDate(birthDate);
        userInfo.setRole(role);

        response.setId(user.getId());
        response.setUsername(username);
        response.setEmail(email);
        response.setPassword(user.getPassword());
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setMiddleName(middleName);
        response.setBirthDate(birthDate);
        response.setRole(role);
        response.setCards(cardMapper.toDtoList(user.getCards(), userInfo));

        return response;
    }

    public List<UserResponse> toListResponse(List<User> users) {
        List<UserResponse> list = new ArrayList<>(users.size());

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
        user.setBirthDate(request.getBirthDate());
        user.setRole(request.getRole());
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
