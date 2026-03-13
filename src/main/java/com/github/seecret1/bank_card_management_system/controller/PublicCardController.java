package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.service.CardService;
import com.github.seecret1.bank_card_management_system.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/public/cards")
@RequiredArgsConstructor
public class PublicCardController {

    private final CardService cardService;

    @GetMapping("/{criterial}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<CardResponse> findByCriterial(
            @PathVariable String criterial
    ) {
        return ResponseEntity.ok(cardService.findByCriterial(criterial));
    }

    @GetMapping("/your")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<PageResponse<CardResponse>> findYourCards(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid PageModel pageModel
    ) {
        return ResponseEntity.ok(cardService.findYourCards(
                AuthUtils.getCurrentUserId(userDetails),
                pageModel
        ));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<CardSummaryResponse>> transferMoney(
            @Valid @RequestBody TransferMoneyRequest request
    ) {
        return ResponseEntity.ok(cardService.transferMoney(request));
    }

    @PostMapping("/transfer/your")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<CardSummaryResponse>> transferMoneyYourCards(
            @Valid @RequestBody TransferMoneyRequest request
    ) {
        return ResponseEntity.ok(cardService.transferMoneyYourCards(request));
    }
}
