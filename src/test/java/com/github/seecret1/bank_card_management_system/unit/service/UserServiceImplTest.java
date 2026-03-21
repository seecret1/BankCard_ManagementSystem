package com.github.seecret1.bank_card_management_system.unit.service;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.exception.RegisterUserException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.mapper.UserMapper;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.service.impl.UserServiceImpl;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    private final String USER_ID = "123e4567-e89b-12d3-a456-426614174000";
    private final String USERNAME = "testuser123";
    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword(ENCODED_PASSWORD);
        user.setFirstName("Pavel");
        user.setLastName("Muzhikov");
        user.setMiddleName("Ivanovich");
        user.setBirthDate(LocalDate.of(1990, 1, 15));
        user.setRole(RoleType.ROLE_USER);

        userResponse = new UserResponse();
        userResponse.setId(USER_ID);
        userResponse.setUsername(USERNAME);
        userResponse.setEmail(EMAIL);
        userResponse.setFirstName("Pavel");
        userResponse.setLastName("Muzhikov");
        userResponse.setMiddleName("Ivanovich");
        userResponse.setBirthDate(LocalDate.of(1990, 1, 15));
        userResponse.setRole(RoleType.ROLE_USER);
        userResponse.setCards(Set.of());

        createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(USERNAME);
        createUserRequest.setEmail(EMAIL);
        createUserRequest.setPassword(PASSWORD);
        createUserRequest.setFirstName("Pavel");
        createUserRequest.setLastName("Muzhikov");
        createUserRequest.setMiddleName("Ivanovich");
        createUserRequest.setBirthDate(LocalDate.of(1990, 1, 15));
        createUserRequest.setRole(RoleType.ROLE_USER);

        updateUserRequest = new UpdateUserRequest(
                "newusername123",
                "newemail@example.com",
                "newpassword123",
                RoleType.ROLE_ADMIN
        );
    }

    @Test
    @DisplayName("Should return paginated list of all users")
    void findAllUsers_ValidPageModel_ReturnsPageResponse() {
        PageModel pageModel = new PageModel(0, 10);
        Pageable pageable = pageModel.toPageRequest();
        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toListResponse(users)).thenReturn(List.of(userResponse));

        PageResponse<UserResponse> result = userService.findAllUsers(pageModel);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getData()).hasSize(1);
        assertThat(result.getData().get(0).getId()).isEqualTo(USER_ID);
        assertThat(result.getData().get(0).getUsername()).isEqualTo(USERNAME);

        verify(userRepository).findAll(pageable);
        verify(userMapper).toListResponse(users);
    }

    @Test
    @DisplayName("Should return filtered users by criteria")
    void findByFilter_ValidFilter_ReturnsFilteredUsers() {
        UserFilterModel filter = new UserFilterModel();
        filter.setFirsName("Pavel");
        PageModel pageModel = new PageModel(0, 10);
        filter.setPage(pageModel);

        Pageable pageable = pageModel.toPageRequest();
        List<User> users = List.of(user);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toListResponse(users)).thenReturn(List.of(userResponse));

        PageResponse<UserResponse> result = userService.findByFilter(filter);

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getData()).hasSize(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toListResponse(users);
    }

    @Test
    @DisplayName("Should find user by username")
    void findByCriterial_ValidUsername_ReturnsUser() {
        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findByCriterial(USERNAME);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(userRepository).findByCriterial(USERNAME);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should find user by email")
    void findByCriterial_ValidEmail_ReturnsUser() {
        when(userRepository.findByCriterial(EMAIL)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findByCriterial(EMAIL);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(userRepository).findByCriterial(EMAIL);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should find user by ID")
    void findByCriterial_ValidId_ReturnsUser() {
        when(userRepository.findByCriterial(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.findByCriterial(USER_ID);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(USER_ID);

        verify(userRepository).findByCriterial(USER_ID);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should throw exception when user not found by criterial")
    void findByCriterial_UserNotFound_ThrowsException() {
        when(userRepository.findByCriterial("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByCriterial("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by criterial: unknown");

        verify(userRepository).findByCriterial("unknown");
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Should find user entity by criterial for internal use")
    void findUserEntityByCriterial_ValidCriterial_ReturnsUser() {
        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));

        User result = userService.findUserEntityByCriterial(USERNAME);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(userRepository).findByCriterial(USERNAME);
    }

    @Test
    @DisplayName("Should throw exception when user entity not found")
    void findUserEntityByCriterial_UserNotFound_ThrowsException() {
        when(userRepository.findByCriterial("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserEntityByCriterial("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by criterial: unknown");

        verify(userRepository).findByCriterial("unknown");
    }

    @Test
    @DisplayName("Should create new user successfully")
    void create_ValidRequest_CreatesUser() {
        when(userRepository.existsByUsernameOrEmail(USERNAME, EMAIL)).thenReturn(false);
        when(userMapper.toEntity(createUserRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.create(createUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(userRepository).existsByUsernameOrEmail(USERNAME, EMAIL);
        verify(userMapper).toEntity(createUserRequest);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing username or email")
    void create_UserExists_ThrowsException() {
        when(userRepository.existsByUsernameOrEmail(USERNAME, EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.create(createUserRequest))
                .isInstanceOf(RegisterUserException.class)
                .hasMessageContaining("User by username " + USERNAME + " or email " + EMAIL + " exists");

        verify(userRepository).existsByUsernameOrEmail(USERNAME, EMAIL);
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Should fully update existing user")
    void updateFull_ValidRequest_UpdatesUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateFull(USER_ID, createUserRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(USER_ID);

        verify(userRepository).findById(USER_ID);
        verify(passwordEncoder).encode(PASSWORD);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void updateFull_UserNotFound_ThrowsException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateFull(USER_ID, createUserRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: " + USER_ID);

        verify(userRepository).findById(USER_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should partially update user with all fields")
    void update_ValidRequest_PartiallyUpdatesUser() {
        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.update(USERNAME, updateUserRequest);

        assertThat(result).isNotNull();
        assertThat(user.getUsername()).isEqualTo("newusername123");
        assertThat(user.getEmail()).isEqualTo("newemail@example.com");
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(user.getRole()).isEqualTo(RoleType.ROLE_USER);

        verify(userRepository).findByCriterial(USERNAME);
        verify(passwordEncoder).encode("newpassword123");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should partially update user with only username")
    void update_OnlyUsername_UpdatesUsernameOnly() {
        UpdateUserRequest partialRequest = new UpdateUserRequest(
                "newusername123", null, null, null
        );

        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.update(USERNAME, partialRequest);

        assertThat(result).isNotNull();
        assertThat(user.getUsername()).isEqualTo("newusername123");
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getPassword()).isEqualTo(ENCODED_PASSWORD);

        verify(userRepository).findByCriterial(USERNAME);
        verify(userRepository).save(user);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should partially update user with only email")
    void update_OnlyEmail_UpdatesEmailOnly() {
        UpdateUserRequest partialRequest = new UpdateUserRequest(
                null, "newemail@example.com", null, null
        );

        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.update(USERNAME, partialRequest);

        assertThat(result).isNotNull();
        assertThat(user.getEmail()).isEqualTo("newemail@example.com");
        assertThat(user.getUsername()).isEqualTo(USERNAME);

        verify(userRepository).findByCriterial(USERNAME);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should partially update user with only password")
    void update_OnlyPassword_UpdatesPasswordOnly() {
        UpdateUserRequest partialRequest = new UpdateUserRequest(
                null, null, "newpassword123", null
        );

        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.update(USERNAME, partialRequest);

        assertThat(result).isNotNull();
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getEmail()).isEqualTo(EMAIL);

        verify(userRepository).findByCriterial(USERNAME);
        verify(passwordEncoder).encode("newpassword123");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate data")
    void update_DataIntegrityViolation_ThrowsException() {
        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> userService.update(USERNAME, updateUserRequest))
                .isInstanceOf(AuthException.class);

        verify(userRepository).findByCriterial(USERNAME);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void update_UserNotFound_ThrowsException() {
        when(userRepository.findByCriterial("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update("unknown", updateUserRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with criterial: unknown");

        verify(userRepository).findByCriterial("unknown");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete user by criterial")
    void delete_ValidCriterial_DeletesUser() {
        when(userRepository.findByCriterial(USERNAME)).thenReturn(Optional.of(user));

        userService.delete(USERNAME);

        verify(userRepository).findByCriterial(USERNAME);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void delete_UserNotFound_ThrowsException() {
        when(userRepository.findByCriterial("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found by criterial: unknown");

        verify(userRepository).findByCriterial("unknown");
        verify(userRepository, never()).delete((User) any());
    }
}