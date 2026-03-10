package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateStatusCardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.model.CardFilterModel;

import java.util.List;

public interface CardService {

    List<CardResponse> findAll();

    PageResponse<CardResponse> findByFilter(CardFilterModel filter);

    CardResponse findByCriterial(String criterial);

    List<CardResponse> findYourCards(String userCriterial);

    CardResponse create(CardRequest request);

    CardResponse updateStatus(UpdateStatusCardRequest request);

    List<CardSummaryResponse> transferMoney(TransferMoneyRequest request);

    List<CardSummaryResponse> transferMoneyYourCards(TransferMoneyRequest request);

    void delete(String criterial);
}
