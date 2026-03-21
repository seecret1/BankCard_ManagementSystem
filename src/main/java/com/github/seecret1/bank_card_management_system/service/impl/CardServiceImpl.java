package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateStatusCardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.exception.*;
import com.github.seecret1.bank_card_management_system.mapper.CardMapper;
import com.github.seecret1.bank_card_management_system.model.CardFilterModel;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.repository.CardRepository;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.repository.specification.CardSpecification;
import com.github.seecret1.bank_card_management_system.service.CardService;
import com.github.seecret1.bank_card_management_system.utils.AuthUtils;
import com.github.seecret1.bank_card_management_system.utils.CardHashUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final UserRepository userRepository;

    private final CardRepository cardRepository;

    private final CardMapper cardMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CardResponse> findAll(PageModel pageModel) {
        log.info("Find all page cards");

        Pageable pageable = pageModel.toPageRequest();
        var pageResult = cardRepository.findAll(pageable);

        log.debug("Find cards list. page: {}, page size: {}, page elements: {}",
                pageResult.getTotalPages(), pageResult.getTotalElements(), pageResult.getContent());

        return new PageResponse<>(
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                cardMapper.toDtoResponseList(pageResult.getContent())
        );
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public CardResponse findByCriterial(String criterial) {
        log.info("Find card by criterial: {}", criterial);

        var card = findCardByCriterial(criterial);
        AuthUtils.checkCardAccess(card);

        log.debug("Find by criterial card: {}", card);
        return cardMapper.toYourDtoResponse(card);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CardResponse> findYourCards(String userCriterial, PageModel pageModel) {
        log.info("Find cards by user criterial: {}, page: {}, size: {}",
                userCriterial, pageModel.getNumber(), pageModel.getSize());

        Pageable pageable = pageModel.toPageRequest();
        var page = cardRepository.findAllByUserCriterial(userCriterial, pageable);

        if (page.isEmpty() && !userRepository.existsUserByCriterial(userCriterial)) {
            throw new UserNotFoundException("User not found by criterial: " + userCriterial);
        }
        log.debug("Found {} cards for user {}, total pages: {}",
                page.getContent().size(), userCriterial, page.getTotalPages());

        return new PageResponse<>(
                page.getTotalElements(),
                page.getTotalPages(),
                cardMapper.toYourDtoResponseList(page.getContent())
        );
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CardResponse create(CardRequest request) {
        String criterial = request.getUserCriterial();
        log.info("Creating a user card, criterial: {}", criterial);

        var user = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by email: " + criterial
                ));

        if (request.getDateExpiry().isBefore(LocalDate.now())) {
            throw new InvalidParameterException(
                    "Date expiry is before now!"
            );
        }
        try {
            var card = cardRepository.save(cardMapper.toEntity(request, user));

            log.debug("Created card: {}", card);
            log.info("Create card successful");

            return cardMapper.toDtoResponse(card);

        } catch (DataIntegrityViolationException ex) {
            throw new CardExistsException(
                    "Card with number " + request.getNumber() + " already exists!"
            );
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CardResponse updateStatus(UpdateStatusCardRequest request) {
        log.info("Update status for card: {}", request.getNumber());

        String hash = CardHashUtils.hash(request.getNumber());
        var card = cardRepository.findByNumberHash(hash)
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found by number: " + request.getNumber()
                ));

        CardStatus status = request.getStatus();
        if (status == CardStatus.ACTIVE &&
                card.getDateExpiry().isBefore(LocalDate.now())) {
            throw new CardStatusException("The card status cannot be active");
        }
        if (status == CardStatus.EXPIRED &&
                card.getDateExpiry().isAfter(LocalDate.now())) {
            throw new CardStatusException("The card status cannot be expired");
        }
        card.setStatus(status);
        log.debug("Update card status: {}", card);
        cardRepository.save(card);
        log.info("Update card successful");
        return cardMapper.toDtoResponse(card);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CardSummaryResponse> transferMoney(TransferMoneyRequest request) {

        List<Card> cards = startTransfer(request);
        var cardFrom = cards.get(0);
        var cardTo = cards.get(1);

        completeTransfer(request, cardFrom, cardTo, request.getAmount());
        return List.of(cardMapper.toResponse(cardFrom), cardMapper.toResponse(cardTo));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CardSummaryResponse> transferMoneyYourCards(TransferMoneyRequest request) {

        List<Card> cards = startTransfer(request);
        var cardFrom = cards.get(0);
        var cardTo = cards.get(1);

        AuthUtils.checkCardAccess(cardFrom);
        AuthUtils.checkCardAccess(cardTo);

        if (!cardFrom.getUser().getId().equals(cardTo.getUser().getId())) {
            throw new InvalidTransferException("The numbers listed do not belong to the same user");
        }

        completeTransfer(request, cardFrom, cardTo, request.getAmount());
        return List.of(cardMapper.toResponse(cardFrom), cardMapper.toResponse(cardTo));
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(String criterial) {
        log.info("Delete card by criterial: {}", criterial);
        var card = findCardByCriterial(criterial);
        cardRepository.delete(card);
        log.info("Delete card successful");
    }

    private List<Card> startTransfer(TransferMoneyRequest request) {
        log.info("Transfer money cards");
        String numberFrom = request.getNumberFrom();
        String numberTo = request.getNumberTo();

        var cardFrom = cardRepository.findByNumberHash(CardHashUtils.hash(numberFrom))
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found by number: " + numberFrom
                ));
        var cardTo = cardRepository.findByNumberHash(CardHashUtils.hash(numberTo))
                .orElseThrow(() -> new CardNotFoundException(
                        "Card not found by number: " + numberTo
                ));

        return List.of(cardFrom, cardTo);
    }

    private void completeTransfer(
            TransferMoneyRequest request,
            Card cardFrom,
            Card cardTo,
            BigDecimal amount
    ) {
        validateTransfer(amount, cardFrom, cardTo);

        log.debug("Transfer money cards request: {}", request);

        cardFrom.setBalance(cardFrom.getBalance().subtract(amount));
        cardTo.setBalance(cardTo.getBalance().add(amount));
        cardRepository.save(cardFrom);
        cardRepository.save(cardTo);

        log.info("Transfer card successful");
    }

    private void validateTransfer(BigDecimal amount, Card cardFrom, Card cardTo) {
        if (cardFrom.getNumber().equals(cardTo.getNumber())) {
            throw new InvalidTransferException(
                    "it is impossible to carry out a transaction with the same cards"
            );
        }

        if (cardFrom.getStatus() != CardStatus.ACTIVE ||
                cardTo.getStatus() != CardStatus.ACTIVE) {
            throw new CardStatusException("The card status cannot be transferred");
        }
        if (!checkCardValid(cardFrom) || !checkCardValid(cardTo)) {
            throw new CardStatusException("The card status cannot be expired");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException(
                    "The amount cannot be negative"
            );
        }
        if (cardFrom.getBalance().compareTo(amount) < 0) {
            throw new InvalidTransferException(
                    "Card balance < transfer amount"
            );
        }
    }

    private boolean checkCardValid(Card card) {
        if (card.getDateExpiry().isBefore(LocalDate.now())) {
            card.setStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
            return false;
        }

        return true;
    }

    private Card findCardByCriterial(String criterial) {
        log.debug("Searching card by criterial: {}", criterial);

        if (criterial != null && criterial.length() == 36) {
            Optional<Card> byId = cardRepository.findById(criterial);
            if (byId.isPresent()) {
                log.debug("Card found by ID: {}", criterial);
                return byId.get();
            }
        }

        String hash = CardHashUtils.hash(criterial);
        Optional<Card> byHash = cardRepository.findByNumberHash(hash);
        if (byHash.isPresent()) {
            log.debug("Card found by number hash: {}", criterial);
            return byHash.get();
        }

        throw new CardNotFoundException("Card not found by criterial: " + criterial);
    }
}
