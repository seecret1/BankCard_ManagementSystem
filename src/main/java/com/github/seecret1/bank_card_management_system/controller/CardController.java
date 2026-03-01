package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> findById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(cardService.findById(id));
    }

    @GetMapping("/card-number/{number}")
    public ResponseEntity<CardResponse> findCardByNumber(
            @PathVariable String number
    ) {
        return ResponseEntity.ok(cardService.findByNumber(number));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CardResponse> createCard(
            @RequestBody CardRequest cardRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.create(cardRequest));
    }
}
