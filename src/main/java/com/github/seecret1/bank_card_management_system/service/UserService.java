package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> findAllUsers();

    UserResponse findById(String id);

    UserResponse findByUsername(String username);

    UserResponse findByEmail(String email);

    UserResponse create(CreateUserRequest request);

    UserResponse updateFull(String id, CreateUserRequest request);

    UserResponse update(String id, UpdateUserRequest request);

    void delete(String id);
}
