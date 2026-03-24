package com.github.seecret1.bank_card_management_system.unit.controller;

import com.github.seecret1.bank_card_management_system.controller.PublicCardController;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.service.CardService;
import com.github.seecret1.bank_card_management_system.utils.AuthUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicCardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private PublicCardController publicCardController;

    private PageModel pageModel;
    private TransferMoneyRequest transferRequest;
    private CardResponse cardResponse;
    private CardSummaryResponse cardSummaryResponse;
    private PageResponse<CardResponse> pageResponse;
    private UserInfoResponse userInfoResponse;

    private final String CARD_NUMBER = "1234567890123456";
    private final String CARD_NUMBER_FROM = "1111222233334444";
    private final String CARD_NUMBER_TO = "5555666677778888";
    private final BigDecimal AMOUNT = BigDecimal.valueOf(500.00);
    private final BigDecimal BALANCE = BigDecimal.valueOf(1000.00);
    private final String USER_ID = "user-123";
    private final String USERNAME = "testuser";

    @BeforeEach
    void setUp() {
        pageModel = new PageModel(0, 10);

        transferRequest = new TransferMoneyRequest();
        transferRequest.setNumberFrom(CARD_NUMBER_FROM);
        transferRequest.setNumberTo(CARD_NUMBER_TO);
        transferRequest.setAmount(AMOUNT);

        userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUsername(USERNAME);
        userInfoResponse.setEmail("test@example.com");
        userInfoResponse.setRole(RoleType.ROLE_USER);

        cardResponse = new CardResponse();
        cardResponse.setNumber(CARD_NUMBER);
        cardResponse.setDateActivation(LocalDate.of(2024, 1, 1));
        cardResponse.setDateExpiry(LocalDate.of(2028, 1, 1));
        cardResponse.setStatus(CardStatus.ACTIVE);
        cardResponse.setBalance(BALANCE);
        cardResponse.setUser(userInfoResponse);

        cardSummaryResponse = new CardSummaryResponse();
        cardSummaryResponse.setNumber(CARD_NUMBER);
        cardSummaryResponse.setStatus(CardStatus.ACTIVE);
        cardSummaryResponse.setBalance(BALANCE);

        pageResponse = new PageResponse<>(1L, 1, List.of(cardResponse));
    }

    @Test
    void findByCriterial_ShouldReturnCard() {
        when(cardService.findByCriterial(CARD_NUMBER)).thenReturn(cardResponse);

        ResponseEntity<CardResponse> response = publicCardController.findByCriterial(CARD_NUMBER);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumber()).isEqualTo(CARD_NUMBER);
        assertThat(response.getBody().getBalance()).isEqualTo(BALANCE);
        verify(cardService).findByCriterial(CARD_NUMBER);
    }

    @Test
    void findYourCards_ShouldReturnUserCards() {
        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(() -> AuthUtils.getCurrentUserId(userDetails))
                    .thenReturn(USER_ID);
            when(cardService.findYourCards(eq(USER_ID), any(PageModel.class)))
                    .thenReturn(pageResponse);

            ResponseEntity<PageResponse<CardResponse>> response = publicCardController.findYourCards(userDetails, pageModel);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).hasSize(1);
            assertThat(response.getBody().getData().get(0).getNumber()).isEqualTo(CARD_NUMBER);
            verify(cardService).findYourCards(USER_ID, pageModel);
        }
    }

    @Test
    void findYourCards_WithCustomPageParameters_ShouldPassCorrectParameters() {
        PageModel customPageModel = new PageModel(2, 20);
        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(() -> AuthUtils.getCurrentUserId(userDetails))
                    .thenReturn(USER_ID);
            when(cardService.findYourCards(eq(USER_ID), any(PageModel.class)))
                    .thenReturn(pageResponse);

            ResponseEntity<PageResponse<CardResponse>> response = publicCardController.findYourCards(userDetails, customPageModel);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(cardService).findYourCards(USER_ID, customPageModel);
        }
    }

    @Test
    void transferMoney_ShouldReturnUpdatedCards() {
        List<CardSummaryResponse> updatedCards = List.of(cardSummaryResponse);
        when(cardService.transferMoney(any(TransferMoneyRequest.class))).thenReturn(updatedCards);

        ResponseEntity<List<CardSummaryResponse>> response = publicCardController.transferMoney(transferRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getNumber()).isEqualTo(CARD_NUMBER);
        verify(cardService).transferMoney(transferRequest);
    }

    @Test
    void transferMoneyYourCards_ShouldReturnUpdatedCards() {
        List<CardSummaryResponse> updatedCards = List.of(cardSummaryResponse);
        when(cardService.transferMoneyYourCards(any(TransferMoneyRequest.class))).thenReturn(updatedCards);

        ResponseEntity<List<CardSummaryResponse>> response = publicCardController.transferMoneyYourCards(transferRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getNumber()).isEqualTo(CARD_NUMBER);
        verify(cardService).transferMoneyYourCards(transferRequest);
    }

    @Test
    void findByCriterial_WithInvalidCardNumber_ShouldReturnEmpty() {
        String invalidCardNumber = "0000000000000000";
        when(cardService.findByCriterial(invalidCardNumber))
                .thenThrow(new RuntimeException("Card not found"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            publicCardController.findByCriterial(invalidCardNumber);
        });
        verify(cardService).findByCriterial(invalidCardNumber);
    }

    @Test
    void transferMoney_WithInsufficientBalance_ShouldThrowException() {
        when(cardService.transferMoney(any(TransferMoneyRequest.class)))
                .thenThrow(new RuntimeException("Insufficient balance"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            publicCardController.transferMoney(transferRequest);
        });
        verify(cardService).transferMoney(transferRequest);
    }

    @Test
    void transferMoney_WithSameCard_ShouldThrowException() {
        TransferMoneyRequest sameCardRequest = new TransferMoneyRequest();
        sameCardRequest.setNumberFrom(CARD_NUMBER);
        sameCardRequest.setNumberTo(CARD_NUMBER);
        sameCardRequest.setAmount(AMOUNT);

        when(cardService.transferMoney(any(TransferMoneyRequest.class)))
                .thenThrow(new RuntimeException("Cannot transfer to the same card"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            publicCardController.transferMoney(sameCardRequest);
        });
        verify(cardService).transferMoney(sameCardRequest);
    }

    @Test
    void transferMoney_WithZeroAmount_ShouldThrowException() {
        TransferMoneyRequest zeroAmountRequest = new TransferMoneyRequest();
        zeroAmountRequest.setNumberFrom(CARD_NUMBER_FROM);
        zeroAmountRequest.setNumberTo(CARD_NUMBER_TO);
        zeroAmountRequest.setAmount(BigDecimal.ZERO);

        when(cardService.transferMoney(any(TransferMoneyRequest.class)))
                .thenThrow(new RuntimeException("Amount must be positive"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            publicCardController.transferMoney(zeroAmountRequest);
        });
        verify(cardService).transferMoney(zeroAmountRequest);
    }

    @Test
    void findYourCards_WhenUserHasNoCards_ShouldReturnEmptyPage() {
        PageResponse<CardResponse> emptyResponse = new PageResponse<>(0L, 0, List.of());
        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(() -> AuthUtils.getCurrentUserId(userDetails))
                    .thenReturn(USER_ID);
            when(cardService.findYourCards(eq(USER_ID), any(PageModel.class)))
                    .thenReturn(emptyResponse);

            ResponseEntity<PageResponse<CardResponse>> response = publicCardController.findYourCards(userDetails, pageModel);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getData()).isEmpty();
            assertThat(response.getBody().getTotalElements()).isEqualTo(0);
            verify(cardService).findYourCards(USER_ID, pageModel);
        }
    }

    @Test
    void transferMoneyYourCards_ShouldValidateOwnership() {
        List<CardSummaryResponse> updatedCards = List.of(cardSummaryResponse);
        when(cardService.transferMoneyYourCards(any(TransferMoneyRequest.class))).thenReturn(updatedCards);

        ResponseEntity<List<CardSummaryResponse>> response = publicCardController.transferMoneyYourCards(transferRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(cardService).transferMoneyYourCards(transferRequest);
    }
}