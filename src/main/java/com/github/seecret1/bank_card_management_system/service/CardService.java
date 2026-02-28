package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;

import java.util.List;

public interface CardService {

    List<CardResponse> findAll();

    CardResponse findByNumber(String number);

    CardResponse create(CardRequest request);

    CardResponse updateStatus(CardRequest request);

    List<CardResponse> transferMoney(TransferMoneyRequest request);

    void delete(String number);
}
