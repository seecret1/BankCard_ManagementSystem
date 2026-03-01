package com.github.seecret1.bank_card_management_system.service;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.model.UserSearchModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    List<UserResponse> findAllUsers();

    PageResponse<UserResponse> findByFilter(UserFilterModel filter);

    UserResponse findBySearchModel(UserSearchModel searchModel);

    UserResponse findByCriterial(String criterial);

    UserResponse create(CreateUserRequest request);

    UserResponse updateFull(String criterial, CreateUserRequest request);

    UserResponse update(String criterial, UpdateUserRequest request);

    void delete(String criterial);
}
