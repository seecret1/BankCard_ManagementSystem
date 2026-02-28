package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Component
public class CardMapper {

    public Set<CardResponse> toDtoList(Set<Card> cards, UserInfoResponse userInfo) {
        Set<CardResponse> dtoList = new HashSet<>(cards.size());

        for (var card : cards) {
            dtoList.add(toDto(card, userInfo));
        }

        return dtoList;
    }

    public List<CardResponse> toDtoList(List<Card> cards) {
        List<CardResponse> dtoList = new ArrayList<>(cards.size());

        for (var card : cards) {
            dtoList.add(toDto(card));
        }

        return dtoList;
    }

    public CardResponse toDto(Card card, UserInfoResponse userInfo) {
        CardResponse dto = new CardResponse();
        dto.setNumber(card.getNumber());
        dto.setDateActivation(card.getDateActivation());
        dto.setDateExpiry(card.getDateExpiry());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        dto.setUser(userInfo);

        return dto;
    }

    public CardResponse toDto(Card card) {
        CardResponse dto = new CardResponse();
        dto.setNumber(card.getNumber());
        dto.setDateActivation(card.getDateActivation());
        dto.setDateExpiry(card.getDateExpiry());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        return dto;
    }

    public Card toEntity(CardResponse response, User user) {
        Card card = new Card();
        card.setNumber(response.getNumber());
        card.setDateActivation(response.getDateActivation());
        card.setDateExpiry(response.getDateExpiry());
        card.setStatus(response.getStatus());
        card.setBalance(response.getBalance());
        card.setUser(user);
        return card;
    }

    public Card toEntity(CardRequest request, User user) {
        Card card = new Card();
        card.setNumber(request.getNumber());
        card.setDateActivation(request.getDateActivation());
        card.setDateExpiry(request.getDateExpiry());
        card.setBalance(request.getBalance());
        card.setStatus(CardStatus.ACTIVE);
        card.setUser(user);
        return card;
    }
}
