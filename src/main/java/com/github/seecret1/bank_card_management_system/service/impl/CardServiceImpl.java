package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateStatusCardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.exception.CardStatusException;
import com.github.seecret1.bank_card_management_system.exception.InvalidTransferException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.mapper.CardMapper;
import com.github.seecret1.bank_card_management_system.repository.CardRepository;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
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
        return cardMapper.toDtoList(cards);
    }

    @Override
    public CardResponse findByNumber(String number) {
        log.info("Find card by number: {}", number);
        var card = cardRepository.findByNumber(number);
        log.debug("Find by number card: {}", card.toString());
        return cardMapper.toDto(card);
    }

    @Override
    public CardResponse create(CardRequest request, String userEmail) {
        log.info("Creating a user card, email {}", userEmail);
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by email: " + userEmail
                ));
        var card = cardRepository.save(cardMapper.toEntity(request, user));
        log.debug("Created card: {}", card);
        log.info("Create card successful");
        return cardMapper.toDto(card);
    }

    @Override
    @Transactional
    public CardResponse updateStatus(UpdateStatusCardRequest request) {
        log.info("Update status for card: {}", request.getNumber());
        var card = cardRepository.findByNumber(request.getNumber());

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
        return cardMapper.toDto(card);
    }

    @Override
    @Transactional
    public List<CardResponse> transferMoney(TransferMoneyRequest request) {
        log.info("Transfer money cards");
        var cardFrom = cardRepository.findByNumber(request.getNumberFrom());
        var cardTo = cardRepository.findByNumber(request.getNumberTo());

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
        return List.of(cardMapper.toDto(cardFrom), cardMapper.toDto(cardTo));
    }

    @Override
    public void delete(String number, String userEmail) {
        log.info("Delete card by number: {}", number);
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by email: " + userEmail
                ));

        log.debug("Delete card by number: {}, and user email: {}",
                number, userEmail);

        var card = findByNumber(number);
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
