package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.util.CardHashUtil;
import com.github.seecret1.bank_card_management_system.util.CardMaskUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Component
public final class CardMapper {

    public List<CardResponse> toDtoResponseList(List<Card> cards) {
        List<CardResponse> dtoList = new ArrayList<>(cards.size());

        for (var card : cards) {
            dtoList.add(toDtoResponse(card));
        }

        return dtoList;
    }

    public List<CardResponse> toYourDtoResponseList(List<Card> cards) {
        List<CardResponse> dtoList = new ArrayList<>(cards.size());

        for (var card : cards) {
            dtoList.add(toYourDtoResponse(card));
        }

        return dtoList;
    }

    public Set<CardSummaryResponse> toResponseList(Set<Card> cards) {
        Set<CardSummaryResponse> dtoList = new HashSet<>(cards.size());

        for (var card : cards) {
            dtoList.add(toResponse(card));
        }

        return dtoList;
    }

    public CardResponse toDtoResponse(Card card) {
        CardResponse dto = new CardResponse();
        dto.setNumber(CardMaskUtil.maskCardNumber(card.getNumber()));
        dto.setDateActivation(card.getDateActivation());
        dto.setDateExpiry(card.getDateExpiry());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());

        if (card.getUser() != null) {
            User user = card.getUser();
            UserInfoResponse response = new UserInfoResponse();
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setMiddleName(user.getMiddleName());
            response.setBirthDate(user.getBirthDate());
            response.setRole(user.getRole());

            dto.setUser(response);
        }

        return dto;
    }

    public CardResponse toYourDtoResponse(Card card) {
        CardResponse dto = new CardResponse();
        dto.setNumber(card.getNumber());
        dto.setDateActivation(card.getDateActivation());
        dto.setDateExpiry(card.getDateExpiry());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());

        if (card.getUser() != null) {
            User user = card.getUser();
            UserInfoResponse response = new UserInfoResponse();
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setFirstName(user.getFirstName());
            response.setLastName(user.getLastName());
            response.setMiddleName(user.getMiddleName());
            response.setBirthDate(user.getBirthDate());
            response.setRole(user.getRole());

            dto.setUser(response);
        }

        return dto;
    }

    public CardSummaryResponse toResponse(Card card) {
        CardSummaryResponse dto = new CardSummaryResponse();
        dto.setNumber(card.getNumber());
        dto.setNumber(CardMaskUtil.maskCardNumber(card.getNumber()));
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());

        return dto;
    }

    public Card toEntity(CardRequest request, User user) {
        Card card = new Card();
        card.setNumber(request.getNumber());
        card.setNumberHash(CardHashUtil.hash(request.getNumber()));
        card.setDateActivation(request.getDateActivation());
        card.setDateExpiry(request.getDateExpiry());
        card.setBalance(request.getBalance());
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);
        return card;
    }
}
