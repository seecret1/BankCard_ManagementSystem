package com.github.seecret1.bank_card_management_system.unit.service;

import com.github.seecret1.bank_card_management_system.dto.request.CardRequest;
import com.github.seecret1.bank_card_management_system.dto.request.TransferMoneyRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateStatusCardRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardResponse;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.entity.Card;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.CardStatus;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.exception.*;
import com.github.seecret1.bank_card_management_system.mapper.CardMapper;
import com.github.seecret1.bank_card_management_system.model.CardFilterModel;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.repository.CardRepository;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.security.CustomUserDetails;
import com.github.seecret1.bank_card_management_system.service.impl.CardServiceImpl;
import com.github.seecret1.bank_card_management_system.utils.CardHashUtils;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;
    private Card cardFrom;
    private Card cardTo;
    private CardResponse cardResponse;
    private CardSummaryResponse cardSummaryResponse;
    private CardRequest cardRequest;
    private UpdateStatusCardRequest updateStatusRequest;
    private TransferMoneyRequest transferRequest;
    private PageModel pageModel;
    private Authentication authentication;
    private SecurityContext securityContext;

    private final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private final String USERNAME = "testuser123";
    private final String EMAIL = "test@example.com";
    private final String CARD_ID = "card-123e4567-e89b-12d3-a456-426614174000";
    private final String CARD_NUMBER = "4111111111111111";
    private final String CARD_NUMBER_HASH = CardHashUtils.hash(CARD_NUMBER);
    private final String CARD_NUMBER_FROM = "4111111111111112";
    private final String CARD_NUMBER_FROM_HASH = CardHashUtils.hash(CARD_NUMBER_FROM);
    private final String CARD_NUMBER_TO = "4111111111111113";
    private final String CARD_NUMBER_TO_HASH = CardHashUtils.hash(CARD_NUMBER_TO);

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setFirstName("Pavel");
        user.setLastName("Muzhikov");
        user.setRole(RoleType.ROLE_USER);

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setUsername(USERNAME);
        userInfoResponse.setEmail(EMAIL);
        userInfoResponse.setFirstName("Pavel");
        userInfoResponse.setLastName("Muzhikov");
        userInfoResponse.setRole(RoleType.ROLE_USER);

        card = new Card();
        card.setId(CARD_ID);
        card.setNumber(CARD_NUMBER);
        card.setNumberHash(CARD_NUMBER_HASH);
        card.setDateActivation(LocalDate.now().minusMonths(1));
        card.setDateExpiry(LocalDate.now().plusYears(2));
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(BigDecimal.valueOf(1000.00));
        card.setUser(user);

        cardFrom = new Card();
        cardFrom.setId("card-from-id");
        cardFrom.setNumber(CARD_NUMBER_FROM);
        cardFrom.setNumberHash(CARD_NUMBER_FROM_HASH);
        cardFrom.setDateActivation(LocalDate.now().minusMonths(1));
        cardFrom.setDateExpiry(LocalDate.now().plusYears(2));
        cardFrom.setStatus(CardStatus.ACTIVE);
        cardFrom.setBalance(BigDecimal.valueOf(1000.00));
        cardFrom.setUser(user);

        cardTo = new Card();
        cardTo.setId("card-to-id");
        cardTo.setNumber(CARD_NUMBER_TO);
        cardTo.setNumberHash(CARD_NUMBER_TO_HASH);
        cardTo.setDateActivation(LocalDate.now().minusMonths(1));
        cardTo.setDateExpiry(LocalDate.now().plusYears(2));
        cardTo.setStatus(CardStatus.ACTIVE);
        cardTo.setBalance(BigDecimal.valueOf(500.00));
        cardTo.setUser(user);

        cardResponse = new CardResponse();
        cardResponse.setNumber(CARD_NUMBER);
        cardResponse.setDateActivation(LocalDate.now().minusMonths(1));
        cardResponse.setDateExpiry(LocalDate.now().plusYears(2));
        cardResponse.setStatus(CardStatus.ACTIVE);
        cardResponse.setBalance(BigDecimal.valueOf(1000.00));
        cardResponse.setUser(userInfoResponse);

        cardSummaryResponse = new CardSummaryResponse();
        cardSummaryResponse.setNumber(CARD_NUMBER);
        cardSummaryResponse.setStatus(CardStatus.ACTIVE);
        cardSummaryResponse.setBalance(BigDecimal.valueOf(1000.00));

        cardRequest = new CardRequest();
        cardRequest.setNumber(CARD_NUMBER);
        cardRequest.setDateActivation(LocalDate.now());
        cardRequest.setDateExpiry(LocalDate.now().plusYears(2));
        cardRequest.setBalance(BigDecimal.valueOf(1000.00));
        cardRequest.setUserCriterial(EMAIL);

        updateStatusRequest = new UpdateStatusCardRequest();
        updateStatusRequest.setNumber(CARD_NUMBER);
        updateStatusRequest.setStatus(CardStatus.BLOCKED);

        transferRequest = new TransferMoneyRequest();
        transferRequest.setNumberFrom(CARD_NUMBER_FROM);
        transferRequest.setNumberTo(CARD_NUMBER_TO);
        transferRequest.setAmount(BigDecimal.valueOf(200.00));

        pageModel = new PageModel(0, 10);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupSecurityContext(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should return paginated list of all cards")
    void findAll_ValidPageModel_ReturnsPageResponse() {
        Pageable pageable = pageModel.toPageRequest();
        List<Card> cards = List.of(card);
        Page<Card> cardPage = new PageImpl<>(cards, pageable, cards.size());

        when(cardRepository.findAll(pageable)).thenReturn(cardPage);
        when(cardMapper.toDtoResponseList(cards)).thenReturn(List.of(cardResponse));

        PageResponse<CardResponse> result = cardService.findAll(pageModel);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getNumber()).isEqualTo(CARD_NUMBER);

        verify(cardRepository).findAll(pageable);
        verify(cardMapper).toDtoResponseList(cards);
    }

    @Test
    @DisplayName("Should return filtered cards by criteria")
    void findByFilter_ValidFilter_ReturnsFilteredCards() {
        CardFilterModel filter = new CardFilterModel();
        filter.setStatus(CardStatus.ACTIVE);
        filter.setPage(pageModel);

        Pageable pageable = pageModel.toPageRequest();
        List<Card> cards = List.of(card);
        Page<Card> cardPage = new PageImpl<>(cards, pageable, cards.size());

        when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);
        when(cardMapper.toDtoResponseList(cards)).thenReturn(List.of(cardResponse));

        PageResponse<CardResponse> result = cardService.findByFilter(filter);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getData()).hasSize(1);

        verify(cardRepository).findAll(any(Specification.class), eq(pageable));
        verify(cardMapper).toDtoResponseList(cards);
    }

    @Test
    @DisplayName("Should find card by number")
    void findByCriterial_ValidNumber_ReturnsCard() {
        setupSecurityContext(user);

        when(cardRepository.findByNumberHash(CARD_NUMBER_HASH)).thenReturn(Optional.of(card));
        when(cardMapper.toYourDtoResponse(card)).thenReturn(cardResponse);

        CardResponse result = cardService.findByCriterial(CARD_NUMBER);

        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(CARD_NUMBER);

        verify(cardRepository).findByNumberHash(CARD_NUMBER_HASH);
        verify(cardMapper).toYourDtoResponse(card);
    }

    @Test
    @DisplayName("Should return user's cards by user criterial")
    void findYourCards_ValidUserCriterial_ReturnsUserCards() {
        Pageable pageable = pageModel.toPageRequest();
        List<Card> cards = List.of(card);
        Page<Card> cardPage = new PageImpl<>(cards, pageable, cards.size());

        when(cardRepository.findAllByUserCriterial(USERNAME, pageable)).thenReturn(cardPage);
        when(cardMapper.toYourDtoResponseList(cards)).thenReturn(List.of(cardResponse));

        PageResponse<CardResponse> result = cardService.findYourCards(USERNAME, pageModel);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getNumber()).isEqualTo(CARD_NUMBER);

        verify(cardRepository).findAllByUserCriterial(USERNAME, pageable);
        verify(cardMapper).toYourDtoResponseList(cards);
        verify(userRepository, never()).existsUserByCriterial(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user not found for cards")
    void findYourCards_UserNotFound_ThrowsException() {
        Pageable pageable = pageModel.toPageRequest();
        Page<Card> emptyPage = Page.empty(pageable);

        when(cardRepository.findAllByUserCriterial(USERNAME, pageable)).thenReturn(emptyPage);
        when(userRepository.existsUserByCriterial(USERNAME)).thenReturn(false);

        assertThatThrownBy(() -> cardService.findYourCards(USERNAME, pageModel))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found by criterial: " + USERNAME);

        verify(cardRepository).findAllByUserCriterial(USERNAME, pageable);
        verify(userRepository).existsUserByCriterial(USERNAME);
    }

    @Test
    @DisplayName("Should create new card successfully")
    void create_ValidRequest_CreatesCard() {
        when(userRepository.findByCriterial(EMAIL)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(cardRequest, user)).thenReturn(card);
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDtoResponse(card)).thenReturn(cardResponse);

        CardResponse result = cardService.create(cardRequest);

        assertThat(result).isNotNull();
        assertThat(result.getNumber()).isEqualTo(CARD_NUMBER);
        assertThat(result.getStatus()).isEqualTo(CardStatus.ACTIVE);

        verify(userRepository).findByCriterial(EMAIL);
        verify(cardMapper).toEntity(cardRequest, user);
        verify(cardRepository).save(card);
        verify(cardMapper).toDtoResponse(card);
    }

    @Test
    @DisplayName("Should throw exception when creating card with expired date")
    void create_ExpiredDate_ThrowsException() {
        cardRequest.setDateExpiry(LocalDate.now().minusDays(1));
        when(userRepository.findByCriterial(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> cardService.create(cardRequest))
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("Date expiry is before now!");

        verify(userRepository).findByCriterial(EMAIL);
        verify(cardRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when user not found for card creation")
    void create_UserNotFound_ThrowsException() {
        when(userRepository.findByCriterial(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.create(cardRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by email: " + EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when card number already exists")
    void create_DuplicateCardNumber_ThrowsException() {
        when(userRepository.findByCriterial(EMAIL)).thenReturn(Optional.of(user));
        when(cardMapper.toEntity(cardRequest, user)).thenReturn(card);
        when(cardRepository.save(card)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> cardService.create(cardRequest))
                .isInstanceOf(CardExistsException.class)
                .hasMessageContaining("Card with number " + CARD_NUMBER + " already exists!");
    }

    @Test
    @DisplayName("Should update card status successfully")
    void updateStatus_ValidRequest_UpdatesStatus() {
        when(cardRepository.findByNumberHash(CARD_NUMBER_HASH)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        when(cardMapper.toDtoResponse(card)).thenReturn(cardResponse);

        CardResponse result = cardService.updateStatus(updateStatusRequest);

        assertThat(result).isNotNull();
        assertThat(card.getStatus()).isEqualTo(CardStatus.BLOCKED);

        verify(cardRepository).findByNumberHash(CARD_NUMBER_HASH);
        verify(cardRepository).save(card);
        verify(cardMapper).toDtoResponse(card);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent card")
    void updateStatus_CardNotFound_ThrowsException() {
        when(cardRepository.findByNumberHash(CARD_NUMBER_HASH)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.updateStatus(updateStatusRequest))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessage("Card not found by number: " + CARD_NUMBER);
    }

    @Test
    @DisplayName("Should throw exception when activating expired card")
    void updateStatus_ActivateExpiredCard_ThrowsException() {
        card.setDateExpiry(LocalDate.now().minusDays(1));
        updateStatusRequest.setStatus(CardStatus.ACTIVE);

        when(cardRepository.findByNumberHash(CARD_NUMBER_HASH)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.updateStatus(updateStatusRequest))
                .isInstanceOf(CardStatusException.class)
                .hasMessage("The card status cannot be active");
    }

    @Test
    @DisplayName("Should throw exception when setting expired status on active card")
    void updateStatus_SetExpiredOnActiveCard_ThrowsException() {
        card.setDateExpiry(LocalDate.now().plusYears(1));
        updateStatusRequest.setStatus(CardStatus.EXPIRED);

        when(cardRepository.findByNumberHash(CARD_NUMBER_HASH)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardService.updateStatus(updateStatusRequest))
                .isInstanceOf(CardStatusException.class)
                .hasMessage("The card status cannot be expired");
    }

    @Test
    @DisplayName("Should transfer money between cards successfully")
    void transferMoney_ValidRequest_TransfersMoney() {
        when(cardRepository.findByNumberHash(CARD_NUMBER_FROM_HASH)).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByNumberHash(CARD_NUMBER_TO_HASH)).thenReturn(Optional.of(cardTo));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));
        when(cardMapper.toResponse(cardFrom)).thenReturn(cardSummaryResponse);
        when(cardMapper.toResponse(cardTo)).thenReturn(cardSummaryResponse);

        List<CardSummaryResponse> result = cardService.transferMoney(transferRequest);

        assertThat(result).hasSize(2);
        assertThat(cardFrom.getBalance()).isEqualByComparingTo("800.00");
        assertThat(cardTo.getBalance()).isEqualByComparingTo("700.00");

        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    @DisplayName("Should throw exception when transferring to same card")
    void transferMoney_SameCard_ThrowsException() {
        transferRequest.setNumberFrom(CARD_NUMBER_FROM);
        transferRequest.setNumberTo(CARD_NUMBER_FROM);

        when(cardRepository.findByNumberHash(CARD_NUMBER_FROM_HASH)).thenReturn(Optional.of(cardFrom));

        assertThatThrownBy(() -> cardService.transferMoney(transferRequest))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("it is impossible to carry out a transaction with the same cards");
    }

    @Test
    @DisplayName("Should throw exception when insufficient balance")
    void transferMoney_InsufficientBalance_ThrowsException() {
        transferRequest.setAmount(BigDecimal.valueOf(2000.00));

        when(cardRepository.findByNumberHash(CARD_NUMBER_FROM_HASH)).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByNumberHash(CARD_NUMBER_TO_HASH)).thenReturn(Optional.of(cardTo));

        assertThatThrownBy(() -> cardService.transferMoney(transferRequest))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("Card balance < transfer amount");
    }

    @Test
    @DisplayName("Should throw exception when transferring negative amount")
    void transferMoney_NegativeAmount_ThrowsException() {
        transferRequest.setAmount(BigDecimal.valueOf(-100.00));

        when(cardRepository.findByNumberHash(CARD_NUMBER_FROM_HASH)).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByNumberHash(CARD_NUMBER_TO_HASH)).thenReturn(Optional.of(cardTo));

        assertThatThrownBy(() -> cardService.transferMoney(transferRequest))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception when transferring with zero amount")
    void transferMoney_ZeroAmount_ThrowsException() {
        transferRequest.setAmount(BigDecimal.ZERO);

        when(cardRepository.findByNumberHash(CARD_NUMBER_FROM_HASH)).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByNumberHash(CARD_NUMBER_TO_HASH)).thenReturn(Optional.of(cardTo));

        assertThatThrownBy(() -> cardService.transferMoney(transferRequest))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessage("The amount cannot be negative");
    }

    @Test
    @DisplayName("Should throw exception when source card is blocked")
    void transferMoney_SourceCardBlocked_ThrowsException() {
        cardFrom.setStatus(CardStatus.BLOCKED);

        when(cardRepository.findByNumberHash(CARD_NUMBER_FROM_HASH)).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByNumberHash(CARD_NUMBER_TO_HASH)).thenReturn(Optional.of(cardTo));

        assertThatThrownBy(() -> cardService.transferMoney(transferRequest))
                .isInstanceOf(CardStatusException.class)
                .hasMessage("The card status cannot be transferred");
    }

    @Test
    @DisplayName("Should transfer money between user's own cards successfully")
    void transferMoneyYourCards_ValidRequest_TransfersMoney() {
        setupSecurityContext(user);

        when(cardRepository.findByNumberHash(CARD_NUMBER_FROM_HASH)).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findByNumberHash(CARD_NUMBER_TO_HASH)).thenReturn(Optional.of(cardTo));
        when(cardRepository.save(any(Card.class))).thenAnswer(i -> i.getArgument(0));
        when(cardMapper.toResponse(cardFrom)).thenReturn(cardSummaryResponse);
        when(cardMapper.toResponse(cardTo)).thenReturn(cardSummaryResponse);

        List<CardSummaryResponse> result = cardService.transferMoneyYourCards(transferRequest);

        assertThat(result).hasSize(2);
        assertThat(cardFrom.getBalance()).isEqualByComparingTo("800.00");
        assertThat(cardTo.getBalance()).isEqualByComparingTo("700.00");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent card")
    void delete_CardNotFound_ThrowsException() {
        lenient().when(cardRepository.findById("unknown")).thenReturn(Optional.empty());
        lenient().when(cardRepository.findByNumberHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.delete("unknown"))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining("Card not found by criterial: unknown");
    }
}