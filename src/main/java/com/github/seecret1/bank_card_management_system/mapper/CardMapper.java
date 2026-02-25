package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.CardDto;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CardMapper {

    public Set<CardDto> toDtoList(Set<Card> cards, UserInfoResponse userInfo) {
        Set<CardDto> dtoList = new HashSet<>(cards.size());

        for (var card : cards) {
            dtoList.add(toDto(card, userInfo));
        }

        return dtoList;
    }

    public CardDto toDto(Card card, UserInfoResponse userInfo) {
        CardDto dto = new CardDto();
        dto.setNumber(card.getNumber());
        dto.setDate_activation(card.getDateActivation());
        dto.setDate_expiry(card.getDateExpiry());
        dto.setStatus(card.getStatus());
        dto.setBalance(card.getBalance());
        dto.setUser(userInfo);

        return dto;
    }
}
