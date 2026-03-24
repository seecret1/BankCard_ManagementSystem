package com.github.seecret1.bank_card_management_system.mapper;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.SignUpRequest;
import com.github.seecret1.bank_card_management_system.dto.response.UserInfoResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.entity.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
@RequiredArgsConstructor
public final class UserMapper {

    private final CardMapper cardMapper;

    public UserResponse toResponse(User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String middleName = user.getMiddleName();
        LocalDate birthDate = user.getBirthDate();
        RoleType role = user.getRole();

        UserResponse response = new UserResponse();
        UserInfoResponse userInfo = new UserInfoResponse();

        userInfo.setUsername(username);
        userInfo.setEmail(email);
        userInfo.setFirstName(firstName);
        userInfo.setLastName(lastName);
        userInfo.setMiddleName(middleName);
        userInfo.setBirthDate(birthDate);
        userInfo.setRole(role);

        response.setId(user.getId());
        response.setUsername(username);
        response.setEmail(email);
        response.setFirstName(firstName);
        response.setLastName(lastName);
        response.setMiddleName(middleName);
        response.setBirthDate(birthDate);
        response.setRole(role);
        response.setCards(cardMapper.toResponseList(user.getCards()));

        return response;
    }

    public CreateUserRequest toCreateUserRequest(SignUpRequest request) {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(request.getUsername());
        createUserRequest.setEmail(request.getEmail());
        createUserRequest.setPassword(request.getPassword());
        createUserRequest.setFirstName(request.getFirstName());
        createUserRequest.setLastName(request.getLastName());
        createUserRequest.setMiddleName(request.getMiddleName());
        createUserRequest.setBirthDate(request.getBirthDate());
        createUserRequest.setRole(RoleType.ROLE_USER);
        return createUserRequest;
    }

    public List<UserResponse> toListResponse(List<User> users) {
        List<UserResponse> list = new ArrayList<>(users.size());

        for (var response : users) {
            list.add(toResponse(response));
        }
        return list;
    }

    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setMiddleName(request.getMiddleName());
        user.setBirthDate(request.getBirthDate());
        user.setRole(request.getRole());
        return user;
    }
}
