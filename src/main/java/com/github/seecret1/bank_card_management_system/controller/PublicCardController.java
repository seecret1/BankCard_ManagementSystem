package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.service.CardService;
import com.github.seecret1.bank_card_management_system.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Card Management", description = "User endpoints for card operations")
@SecurityRequirement(name = "bearerAuth")
public class PublicCardController {

    private final CardService cardService;

    @GetMapping("/{criterial}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get card by criterial", description = "Get card by ID or number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success get card by ID or number"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<CardResponse> findByCriterial(
            @PathVariable String criterial
    ) {
        return ResponseEntity.ok(cardService.findByCriterial(criterial));
    }

    @GetMapping("/your")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Get all your cards", description = "Get all cards belonging to current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success get cards to current user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
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
    @Operation(summary = "transfer between any cards", description = "Transfer money between any cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success transfer money between any cards"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<CardSummaryResponse>> transferMoney(
            @Valid @RequestBody TransferMoneyRequest request
    ) {
        return ResponseEntity.ok(cardService.transferMoney(request));
    }

    @PostMapping("/transfer/your")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Transfer money your cards", description = "Transfer money between your cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success transfer money between your cards"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<List<CardSummaryResponse>> transferMoneyYourCards(
            @Valid @RequestBody TransferMoneyRequest request
    ) {
        return ResponseEntity.ok(cardService.transferMoneyYourCards(request));
    }
}
