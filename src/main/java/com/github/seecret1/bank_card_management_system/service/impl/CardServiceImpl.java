package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateStatusCardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.exception.CardNotFoundException;
import com.github.seecret1.bank_card_management_system.exception.CardStatusException;
import com.github.seecret1.bank_card_management_system.exception.InvalidTransferException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.mapper.CardMapper;
import com.github.seecret1.bank_card_management_system.model.CardFilterModel;
import com.github.seecret1.bank_card_management_system.repository.CardRepository;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.repository.specification.CardSpecification;
import com.github.seecret1.bank_card_management_system.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final UserRepository userRepository;

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    @Override
    public List<CardResponse> findAll() {
        log.info("Find all cards");
        List<Card> cards = cardRepository.findAll();
        log.debug("Cards list: {}", cards);
        return cardMapper.toDtoResponseList(cards);
    }

    @Override
    public PageResponse<CardResponse> findByFilter(CardFilterModel filter) {
        log.info("Find card by filter: {}", filter);
        var page = cardRepository.findAll(
                CardSpecification.withFilter(filter),
                filter.getPage().toPageRequest()
        );
        log.debug("Find page cards by filter. Page: {}", page);
        return new PageResponse<>(
                page.getTotalElements(),
                page.getTotalPages(),
                cardMapper.toDtoResponseList(page.getContent())
        );
    }

    @Override
    public CardResponse findByCriterial(String criterial) {
        log.info("Find card by criterial: {}", criterial);
        var card = cardRepository.findByCriterial(criterial)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found by criterial: " + criterial
                ));
        log.debug("Find by criterial card: {}", card.toString());
        return cardMapper.toDtoResponse(card);
    }

    @Override
    public List<CardResponse> findCardsUser(String userCriterial) {
        log.info("Find cards by user: {}", userCriterial);
        var user = userRepository.findByCriterial(userCriterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by criterial: " + userCriterial
                ));
        var listCards = user.getCards();
        log.debug("List cards: {}, user: {}", listCards, user);
        return cardMapper.toDtoResponseList(listCards.stream().toList());
    }

    @Override
    public CardResponse create(CardRequest request) {
        String criterial = request.getUserCriterial();
        log.info("Creating a user card, criterial: {}", criterial);
        var user = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by email: " + criterial
                ));

        var card = cardRepository.save(cardMapper.toEntity(request, user));
        log.debug("Created card: {}", card);
        log.info("Create card successful");
        return cardMapper.toDtoResponse(card);
    }

    @Override
    public CardResponse updateStatus(UpdateStatusCardRequest request) {
        log.info("Update status for card: {}", request.getNumber());
        var card = cardRepository.findByCriterial(request.getNumber())
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found by id: " + request.getNumber()
                ));

        CardStatus status = request.getStatus();
        if (status == CardStatus.ACTIVE &&
                card.getDateExpiry().isAfter(LocalDate.now())) {
            throw new CardStatusException("The card status cannot be active");
        }
        if (status == CardStatus.EXPIRED &&
                card.getDateExpiry().isBefore(LocalDate.now())) {
            throw new CardStatusException("The card status cannot be expired");
        }
        card.setStatus(status);
        log.debug("Update card status: {}", card);
        cardRepository.save(card);
        log.info("Update card successful");
        return cardMapper.toDtoResponse(card);
    }

    @Override
    @Transactional
    public List<CardSummaryResponse> transferMoney(TransferMoneyRequest request) {
        log.info("Transfer money cards");
        String numberFrom = request.getNumberFrom();
        String numberTo = request.getNumberTo();
        var cardFrom = cardRepository.findByCriterial(numberFrom)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found by number: " + numberFrom
                ));
        var cardTo = cardRepository.findByCriterial(numberTo)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found by number: " + numberTo
                ));

        if (cardFrom.getStatus() != CardStatus.ACTIVE &&
                cardTo.getStatus() != CardStatus.ACTIVE) {
            throw new CardStatusException("The card status cannot be transferred");
        }
        if (!checkCardValid(cardFrom) || !checkCardValid(cardTo)) {
            throw new CardStatusException("The card status cannot be expired");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException(
                    "The amount cannot be negative"
            );
        }
        if (cardFrom.getBalance().compareTo(request.getAmount()) >= 0) {
            throw new InvalidTransferException(
                    "Card balance < transfer amount"
            );
        }
        log.debug("Transfer money cards request: {}", request);

        cardFrom.setBalance(cardFrom.getBalance().subtract(request.getAmount()));
        cardTo.setBalance(cardTo.getBalance().add(request.getAmount()));
        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);

        log.info("Transfer card successful");
        return List.of(cardMapper.toResponse(cardFrom), cardMapper.toResponse(cardTo));
    }

    @Override
    public void delete(String cardCriterial, String userCriterial) {
        log.info("Delete card by card criterial: {}", cardCriterial);
        var user = userRepository.findByCriterial(userCriterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by criterial: " + userCriterial
                ));

        log.debug("Delete card by criterial: {}, and user by criterial: {}",
                cardCriterial, userCriterial);

        var card = findByCriterial(cardCriterial);
        cardRepository.delete(cardMapper.toEntity(card, user));
        log.info("Delete card successful");
    }

    private boolean checkCardValid(Card card) {
        if (card.getDateExpiry().isBefore(LocalDate.now())) {
            updateStatus(new UpdateStatusCardRequest(
                    card.getNumber(),
                    CardStatus.EXPIRED
            ));
            return false;
        }

        return true;
    }
}
