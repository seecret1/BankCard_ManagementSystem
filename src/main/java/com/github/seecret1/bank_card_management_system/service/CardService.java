package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateStatusCardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.model.CardFilterModel;
import com.github.seecret1.bank_card_management_system.model.PageModel;

import java.util.List;

public interface CardService {

    PageResponse<CardResponse> findAll(PageModel pageModel);

    PageResponse<CardResponse> findByFilter(CardFilterModel filter);

    CardResponse findByCriterial(String criterial);

    PageResponse<CardResponse> findYourCards(String userCriterial, PageModel pageModel);

    CardResponse create(CardRequest request);

    CardResponse updateStatus(UpdateStatusCardRequest request);

    List<CardSummaryResponse> transferMoney(TransferMoneyRequest request);

    List<CardSummaryResponse> transferMoneyYourCards(TransferMoneyRequest request);

    void delete(String criterial);
}
