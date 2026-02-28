package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.mapper.CardMapper;
import com.github.seecret1.bank_card_management_system.repository.CardRepository;
import com.github.seecret1.bank_card_management_system.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    @Override
    public List<CardResponse> findAll() {
        return List.of();
    }

    @Override
    public CardResponse findByNumber(String number) {
        return cardMapper.toDto(cardRepository.findByNumber(number));
    }

    @Override
    public CardResponse create(CardRequest request) {
        return null;
    }

    @Override
    public CardResponse updateStatus(CardRequest request) {
        return null;
    }

    @Override
    public List<CardResponse> transferMoney(TransferMoneyRequest request) {
        return List.of();
    }

    @Override
    public void delete(String number) {
        var card = findByNumber(number);
//        cardRepository.delete(cardMapper.toEntity(card));
    }
}
