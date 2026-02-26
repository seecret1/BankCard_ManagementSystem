package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.RegisterUserException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.mapper.UserMapper;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public UserResponse findById(String id) {
        log.info("Find user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ));
        log.debug("Found by id user: {}", user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findByUsername(String username) {
        log.info("Find user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username: " + username
                ));
        log.debug("Found by username user: {}", user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
        log.info("Call method findUserByEmail");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + email
                ));
        log.debug("Found by email user: {}", user);
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
    public UserResponse update(String id, UpdateUserRequest request) {
        log.info("Update user by ID: {}", id);

        var userUpdate = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
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
    public void delete(String id) {
        log.info("Delete user by ID: {}", id);
        userRepository.deleteById(id);
        log.debug("Success delete user by ID: {}", id);
    }
}
