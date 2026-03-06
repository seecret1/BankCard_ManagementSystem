package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.model.CardFilterModel;
import com.github.seecret1.bank_card_management_system.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CardResponse>> findAllCards() {
        return ResponseEntity.ok(cardService.findAll());
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<CardResponse>> findCardsByFilter(CardFilterModel filter) {
        return ResponseEntity.ok(cardService.findByFilter(filter));
    }

    @GetMapping("/{criterial}")
    public ResponseEntity<CardResponse> findByCriterial(
            @PathVariable String criterial
    ) {
        return ResponseEntity.ok(cardService.findByCriterial(criterial));
    }

    @GetMapping("/user/{criterial}")
    public ResponseEntity<List<CardResponse>> findCardsUser(
            @PathVariable String criterial
    ) {
        return ResponseEntity.ok(cardService.findCardsUser(criterial));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CardRequest cardRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.create(cardRequest, cardRequest.getUser().getEmail()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<List<CardResponse>> transferMoney(
            @Valid @RequestBody TransferMoneyRequest request
    ) {
        return ResponseEntity.ok(cardService.transferMoney(request));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{cardCriterial}/user/{userCriterial}")
    public ResponseEntity<?> deleteCards(
            @PathVariable String cardCriterial,
            @PathVariable String userCriterial
    ) {
        cardService.delete(cardCriterial, userCriterial);
        return ResponseEntity.noContent().build();
    }
}
