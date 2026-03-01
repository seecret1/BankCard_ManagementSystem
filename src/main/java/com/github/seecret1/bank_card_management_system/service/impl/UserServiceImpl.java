package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.RegisterUserException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.mapper.UserMapper;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.model.UserSearchModel;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.repository.specification.UserSpecification;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserResponse> findAllUsers() {
        log.info("Find all users");
        List<User> users = userRepository.findAll();

        log.debug("Users list: {}, size list: {}", users, users.size());
        return userMapper.toListResponse(users);
    }

    @Override
    public PageResponse<UserResponse> findByFilter(UserFilterModel filter) {
        log.info("Find users by filter: {}", filter);

        var page = userRepository.findAll(
                UserSpecification.withFilter(filter),
                filter.getPage().toPageRequest()
        );
        log.debug("Find page by filter, Page: {}", page);
        return new PageResponse<>(
                page.getTotalElements(),
                page.getTotalPages(),
                userMapper.toListResponse(page.getContent()));
    }

    @Override
    public UserResponse findByCriterial(String criterial) {
        log.info("Find user by criterial: {}", criterial);
        User user = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by criterial: " + criterial
                ));
        log.debug("Found by id user: {}", user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findBySearchModel(UserSearchModel searchModel) {
        log.info("Find user by search model: {}", searchModel);

        var user = userRepository.searchByModel(searchModel)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by search model: " + searchModel
                ));
        log.debug("Found user by searchModel. User: {}", user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        log.info("Call method create");

        var username = request.getUsername();
        var email = request.getEmail();

        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new RegisterUserException(
                    MessageFormat.format(
                            "User by username {0} or email {1} exists",
                            username, email
                    )
            );
        }

        User user = userRepository.save(userMapper.toEntity(request));
        log.debug("Success create user: {}", user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse updateFull(String id, CreateUserRequest request) {
        log.info("Update user by id: {}", id);

        var userToUpdate = userMapper.toEntity(request);
        userToUpdate.setId(id);
        userRepository.save(userToUpdate);

        log.debug("Success full update user: {}", userToUpdate);
        return userMapper.toResponse(userToUpdate);
    }

    @Override
    public UserResponse update(String criterial, UpdateUserRequest request) {
        log.info("Update user by criterial: {}", criterial);

        var userUpdate = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with criterial: " + criterial
                ));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            userUpdate.setUsername(request.getUsername());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            userUpdate.setEmail(request.getEmail());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            userUpdate.setPassword(request.getPassword());
        }
        userRepository.save(userUpdate);
        log.debug("Success update user: {}", userUpdate);
        return userMapper.toResponse(userUpdate);
    }

    @Override
    public void delete(String criterial) {
        log.info("Delete user by criterial: {}", criterial);
        userRepository.delete(UserSpecification.searchByCriterial(criterial));
    }
}
