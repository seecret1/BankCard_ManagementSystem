package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
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

    public Set<CardSummaryResponse> toResponseList(Set<Card> cards) {
        Set<CardSummaryResponse> dtoList = new HashSet<>(cards.size());

        for (var card : cards) {
            dtoList.add(toResponse(card));
        }

        return dtoList;
    }

    public CardResponse toDtoResponse(Card card) {
        CardResponse dto = new CardResponse();
        dto.setNumber(card.getNumber());
        dto.setDateActivation(card.getDateActivation());
        dto.setDateExpiry(card.getDateExpiry());
        dto.setStatus(card.getStatus());

        return dto;
    }

    public CardSummaryResponse toResponse(Card card) {
        CardSummaryResponse dto = new CardSummaryResponse();
        dto.setNumber(card.getNumber());
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
