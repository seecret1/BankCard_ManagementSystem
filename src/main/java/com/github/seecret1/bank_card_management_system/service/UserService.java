package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;

public interface UserService {

    PageResponse<UserResponse> findAllUsers(PageModel pageModel);

    PageResponse<UserResponse> findByFilter(UserFilterModel filter);

    UserResponse findByCriterial(String criterial);

    UserResponse create(CreateUserRequest request);

    UserResponse updateFull(String criterial, CreateUserRequest request);

    UserResponse updateYour(String userId, UpdateUserRequest request);

    void delete(String criterial);
}
