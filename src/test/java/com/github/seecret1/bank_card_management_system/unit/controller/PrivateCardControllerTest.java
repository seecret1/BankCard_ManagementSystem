package com.github.seecret1.bank_card_management_system.unit.controller;

import com.github.seecret1.bank_card_management_system.controller.PrivateCardController;
import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateStatusCardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.model.CardFilterModel;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrivateCardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private PrivateCardController privateCardController;

    private PageModel pageModel;
    private CardFilterModel cardFilterModel;
    private CardRequest cardRequest;
    private UpdateStatusCardRequest updateStatusRequest;
    private CardResponse cardResponse;
    private PageResponse<CardResponse> pageResponse;
    private UserInfoResponse userInfoResponse;

    private final String CARD_NUMBER = "1234567890123456";
    private final LocalDate DATE_ACTIVATION = LocalDate.of(2024, 1, 1);
    private final LocalDate DATE_EXPIRY = LocalDate.of(2028, 1, 1);
    private final BigDecimal BALANCE = BigDecimal.valueOf(1000.00);
    private final CardStatus STATUS = CardStatus.ACTIVE;
    private final String USER_CRITERIAL = "testuser";

    @BeforeEach
    void setUp() {
        // Setup PageModel
        pageModel = new PageModel(0, 10);

        // Setup CardFilterModel
        cardFilterModel = CardFilterModel.builder()
                .page(pageModel)
                .status(CardStatus.ACTIVE)
                .balance(BALANCE)
                .dateActivation(DATE_ACTIVATION)
                .dateExpiry(DATE_EXPIRY)
                .build();

        // Setup CardRequest
        cardRequest = new CardRequest();
        cardRequest.setNumber(CARD_NUMBER);
        cardRequest.setDateActivation(DATE_ACTIVATION);
        cardRequest.setDateExpiry(DATE_EXPIRY);
        cardRequest.setBalance(BALANCE);
        cardRequest.setUserCriterial(USER_CRITERIAL);

        // Setup UpdateStatusCardRequest
        updateStatusRequest = new UpdateStatusCardRequest(CARD_NUMBER, CardStatus.BLOCKED);

        // Setup UserInfoResponse
        userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUsername(USER_CRITERIAL);
        userInfoResponse.setEmail("test@example.com");
        userInfoResponse.setRole(RoleType.ROLE_USER);

        // Setup CardResponse
        cardResponse = new CardResponse();
        cardResponse.setNumber(CARD_NUMBER);
        cardResponse.setDateActivation(DATE_ACTIVATION);
        cardResponse.setDateExpiry(DATE_EXPIRY);
        cardResponse.setStatus(CardStatus.ACTIVE);
        cardResponse.setBalance(BALANCE);
        cardResponse.setUser(userInfoResponse);

        // Setup PageResponse
        pageResponse = new PageResponse<>(1L, 1, List.of(cardResponse));
    }

    @Test
    void findAllCards_ShouldReturnPageOfCards() {
        // given
        when(cardService.findAll(any(PageModel.class))).thenReturn(pageResponse);

        // when
        ResponseEntity<PageResponse<CardResponse>> response = privateCardController.findAllCards(pageModel);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).getNumber()).isEqualTo(CARD_NUMBER);
        verify(cardService).findAll(pageModel);
    }

    @Test
    void findCardsByFilter_ShouldReturnFilteredCards() {
        // given
        when(cardService.findByFilter(any(CardFilterModel.class))).thenReturn(pageResponse);

        // when
        ResponseEntity<PageResponse<CardResponse>> response = privateCardController.findCardsByFilter(cardFilterModel);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        verify(cardService).findByFilter(cardFilterModel);
    }

    @Test
    void findCardsByFilter_WithEmptyFilter_ShouldReturnAllCards() {
        // given
        CardFilterModel emptyFilter = CardFilterModel.builder()
                .page(pageModel)
                .build();
        when(cardService.findByFilter(any(CardFilterModel.class))).thenReturn(pageResponse);

        // when
        ResponseEntity<PageResponse<CardResponse>> response = privateCardController.findCardsByFilter(emptyFilter);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cardService).findByFilter(emptyFilter);
    }

    @Test
    void createCard_ShouldReturnCreatedCard() {
        // given
        when(cardService.create(any(CardRequest.class))).thenReturn(cardResponse);

        // when
        ResponseEntity<CardResponse> response = privateCardController.createCard(cardRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumber()).isEqualTo(CARD_NUMBER);
        assertThat(response.getBody().getBalance()).isEqualTo(BALANCE);
        assertThat(response.getBody().getStatus()).isEqualTo(CardStatus.ACTIVE);
        verify(cardService).create(cardRequest);
    }

    @Test
    void updateCard_ShouldReturnUpdatedCard() {
        // given
        when(cardService.updateStatus(any(UpdateStatusCardRequest.class))).thenReturn(cardResponse);

        // when
        ResponseEntity<CardResponse> response = privateCardController.updateCard(updateStatusRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumber()).isEqualTo(CARD_NUMBER);
        assertThat(response.getBody().getStatus()).isEqualTo(CardStatus.ACTIVE);
        verify(cardService).updateStatus(updateStatusRequest);
    }

    @Test
    void deleteCards_ShouldReturnNoContent() {
        // given
        doNothing().when(cardService).delete(CARD_NUMBER);

        // when
        ResponseEntity<Void> response = privateCardController.deleteCards(CARD_NUMBER);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(cardService).delete(CARD_NUMBER);
    }

    @Test
    void findAllCards_WithCustomPageParameters_ShouldPassCorrectParameters() {
        // given
        PageModel customPageModel = new PageModel(2, 20);
        when(cardService.findAll(any(PageModel.class))).thenReturn(pageResponse);

        // when
        ResponseEntity<PageResponse<CardResponse>> response = privateCardController.findAllCards(customPageModel);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cardService).findAll(customPageModel);
    }

    @Test
    void findCardsByFilter_WithAllFilters_ShouldReturnFilteredCards() {
        // given
        CardFilterModel fullFilter = CardFilterModel.builder()
                .page(pageModel)
                .status(CardStatus.BLOCKED)
                .balance(BigDecimal.valueOf(500.00))
                .dateActivation(LocalDate.of(2024, 2, 1))
                .dateExpiry(LocalDate.of(2029, 2, 1))
                .build();
        when(cardService.findByFilter(any(CardFilterModel.class))).thenReturn(pageResponse);

        // when
        ResponseEntity<PageResponse<CardResponse>> response = privateCardController.findCardsByFilter(fullFilter);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cardService).findByFilter(fullFilter);
    }

    @Test
    void updateCard_WithDifferentStatus_ShouldUpdate() {
        // given
        UpdateStatusCardRequest blockRequest = new UpdateStatusCardRequest(CARD_NUMBER, CardStatus.BLOCKED);
        CardResponse blockedResponse = new CardResponse();
        blockedResponse.setNumber(CARD_NUMBER);
        blockedResponse.setStatus(CardStatus.BLOCKED);
        when(cardService.updateStatus(any(UpdateStatusCardRequest.class))).thenReturn(blockedResponse);

        // when
        ResponseEntity<CardResponse> response = privateCardController.updateCard(blockRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(CardStatus.BLOCKED);
        verify(cardService).updateStatus(blockRequest);
    }
}