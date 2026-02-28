package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import org.springframework.stereotype.Component;

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
}
