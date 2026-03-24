package com.github.seecret1.bank_card_management_system.unit.controller;

import com.github.seecret1.bank_card_management_system.controller.UserController;
import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.CardSummaryResponse;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.service.UserService;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private UserController userController;

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UserResponse userResponse;
    private PageResponse<UserResponse> pageResponse;
    private PageModel pageModel;
    private UserFilterModel userFilterModel;

    private final String USER_ID = "user-123";
    private final String USERNAME = "testuser";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final String FIRST_NAME = "Pavel";
    private final String LAST_NAME = "Muzhikov";
    private final String MIDDLE_NAME = "Ivanovich";
    private final LocalDate BIRTH_DATE = LocalDate.of(2000, 1, 1);
    private final RoleType ROLE = RoleType.ROLE_USER;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setEmail(EMAIL);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setFirstName(FIRST_NAME);
        createUserRequest.setLastName(LAST_NAME);
        createUserRequest.setMiddleName(MIDDLE_NAME);
        createUserRequest.setBirthDate(BIRTH_DATE);
        createUserRequest.setRole(ROLE);

        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setUsername("updatedusername");
        updateUserRequest.setEmail("updated@example.com");
        updateUserRequest.setPassword("newpassword123");
        updateUserRequest.setRole(RoleType.ROLE_ADMIN);

        Set<CardSummaryResponse> cards = new HashSet<>();
        userResponse = new UserResponse(
                USER_ID, USERNAME, EMAIL,
                FIRST_NAME, LAST_NAME, MIDDLE_NAME,
                BIRTH_DATE, ROLE, cards
        );

        pageResponse = new PageResponse<UserResponse>();
        pageResponse.setTotalPages(1);

        pageModel = new PageModel(0, 10);

        userFilterModel = UserFilterModel.builder()
                .page(pageModel)
                .role(ROLE)
                .birthDate(BIRTH_DATE)
                .build();
    }

    @Test
    void findByFilter_WithEmptyFilter_ShouldReturnAllUsers() {
        UserFilterModel emptyFilter = UserFilterModel.builder()
                .page(pageModel)
                .build();
        when(userService.findByFilter(any(UserFilterModel.class))).thenReturn(pageResponse);

        ResponseEntity<PageResponse<UserResponse>> response = userController.findByFilter(emptyFilter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).findByFilter(emptyFilter);
    }

    @Test
    void findByCriterial_WithUsername_ShouldReturnUser() {
        when(userService.findByCriterial(USERNAME)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.findByCriterial(USERNAME);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(USER_ID);
        assertThat(response.getBody().getUsername()).isEqualTo(USERNAME);
        verify(userService).findByCriterial(USERNAME);
    }

    @Test
    void findByCriterial_WithEmail_ShouldReturnUser() {
        when(userService.findByCriterial(EMAIL)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.findByCriterial(EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(EMAIL);
        verify(userService).findByCriterial(EMAIL);
    }

    @Test
    void create_ShouldReturnCreatedUser() {
        when(userService.create(any(CreateUserRequest.class))).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.create(createUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(USER_ID);
        assertThat(response.getBody().getUsername()).isEqualTo(USERNAME);
        assertThat(response.getBody().getEmail()).isEqualTo(EMAIL);
        assertThat(response.getBody().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(response.getBody().getLastName()).isEqualTo(LAST_NAME);
        verify(userService).create(createUserRequest);
    }

    @Test
    void updateFull_ShouldReturnUpdatedUser() {
        when(userService.updateFull(eq(USERNAME), any(CreateUserRequest.class)))
                .thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.updateFull(USERNAME, createUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(USER_ID);
        verify(userService).updateFull(USERNAME, createUserRequest);
    }

    @Test
    void updateYour_ShouldReturnUpdatedUser() {
        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(() -> AuthUtils.getCurrentUserId(userDetails))
                    .thenReturn(USER_ID);
            when(userService.updateYour(eq(USER_ID), any(UpdateUserRequest.class)))
                    .thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.updateYour(updateUserRequest, userDetails);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getId()).isEqualTo(USER_ID);
            verify(userService).updateYour(USER_ID, updateUserRequest);
        }
    }

    @Test
    void updateYour_WithDifferentUserId_ShouldStillWork() {
        String differentUserId = "different-user-id";
        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(() -> AuthUtils.getCurrentUserId(userDetails))
                    .thenReturn(differentUserId);
            when(userService.updateYour(eq(differentUserId), any(UpdateUserRequest.class)))
                    .thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.updateYour(updateUserRequest, userDetails);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).updateYour(differentUserId, updateUserRequest);
        }
    }

    @Test
    void updateYour_WithPartialUpdateRequest_ShouldWork() {
        UpdateUserRequest partialUpdate = new UpdateUserRequest();
        partialUpdate.setUsername("newusername");

        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(() -> AuthUtils.getCurrentUserId(userDetails))
                    .thenReturn(USER_ID);
            when(userService.updateYour(eq(USER_ID), any(UpdateUserRequest.class)))
                    .thenReturn(userResponse);

            ResponseEntity<UserResponse> response = userController.updateYour(partialUpdate, userDetails);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            verify(userService).updateYour(eq(USER_ID), any(UpdateUserRequest.class));
        }
    }

    @Test
    void delete_ShouldReturnNoContent() {
        doNothing().when(userService).delete(USERNAME);

        ResponseEntity<Void> response = userController.delete(USERNAME);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).delete(USERNAME);
    }

    @Test
    void findAll_WithCustomPageParameters_ShouldPassCorrectParameters() {
        PageModel customPageModel = new PageModel(2, 20);
        when(userService.findAllUsers(any(PageModel.class))).thenReturn(pageResponse);

        ResponseEntity<PageResponse<UserResponse>> response = userController.findAll(customPageModel);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).findAllUsers(customPageModel);
    }

    @Test
    void create_WithMinimalValidRequest_ShouldReturnCreatedUser() {
        CreateUserRequest minimalRequest = new CreateUserRequest();
        minimalRequest.setUsername("minimal");
        minimalRequest.setEmail("minimal@example.com");
        minimalRequest.setPassword("password123");
        minimalRequest.setFirstName("Minimal");
        minimalRequest.setLastName("User");
        minimalRequest.setBirthDate(LocalDate.of(1990, 1, 1));
        minimalRequest.setRole(RoleType.ROLE_USER);

        Set<CardSummaryResponse> cards = new HashSet<>();
        UserResponse minimalResponse = new UserResponse(
                "minimal-id",
                "minimal",
                "minimal@example.com",
                "Minimal",
                "User",
                null,
                LocalDate.of(1990, 1, 1),
                RoleType.ROLE_USER,
                cards
        );

        when(userService.create(any(CreateUserRequest.class))).thenReturn(minimalResponse);

        ResponseEntity<UserResponse> response = userController.create(minimalRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("minimal");
        verify(userService).create(minimalRequest);
    }

    @Test
    void findByFilter_WithAllFilters_ShouldReturnFilteredUsers() {
        UserFilterModel fullFilter = UserFilterModel.builder()
                .page(pageModel)
                .firsName(FIRST_NAME)
                .lastName(LAST_NAME)
                .middleName(MIDDLE_NAME)
                .birthDate(BIRTH_DATE)
                .role(ROLE)
                .build();
        when(userService.findByFilter(any(UserFilterModel.class))).thenReturn(pageResponse);

        ResponseEntity<PageResponse<UserResponse>> response = userController.findByFilter(fullFilter);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).findByFilter(fullFilter);
    }

    @Test
    void delete_WithEmail_ShouldDeleteUser() {
        doNothing().when(userService).delete(EMAIL);

        ResponseEntity<Void> response = userController.delete(EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).delete(EMAIL);
    }

    @Test
    void updateFull_WithEmailCriterial_ShouldUpdateUser() {
        when(userService.updateFull(eq(EMAIL), any(CreateUserRequest.class)))
                .thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.updateFull(EMAIL, createUserRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userService).updateFull(EMAIL, createUserRequest);
    }
}