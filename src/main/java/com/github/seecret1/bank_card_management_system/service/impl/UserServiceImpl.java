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
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public List<UserResponse> findAllUsers() {
        log.info("Call method findAllUsers");
        return userMapper.toListResponse(userRepository.findAll());
    }

    @Override
    public UserResponse findById(String id) {
        log.info("Call method findById");
        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ))
        );
    }

    @Override
    public UserResponse findByUsername(String username) {
        log.info("Call method findByUsername");
        return userMapper.toResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username: " + username
                ))
        );
    }

    @Override
    public UserResponse findUserByEmail(String email) {
        log.info("Call method findUserByEmail");
        return userMapper.toResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + email
                ))
        );
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        log.info("Call method create");

        var username = request.getUsername();
        var email = request.getEmail();

        if (username == null || username.isBlank() ||
                email == null || email.isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank() ||
                request.getFirstName() == null || request.getFirstName().isBlank() ||
                request.getLastName() == null || request.getLastName().isBlank() ||
                request.getMiddleName() == null || request.getMiddleName().isBlank() ||
                request.getBirth_date().isBefore(LocalDate.now()) ||
                request.getRoles() == null || request.getRoles().isEmpty() ||
                request.getCards() == null || request.getCards().isEmpty()) {

            throw new IllegalArgumentException("Invalid Request for create user");
        }
        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new RegisterUserException(
                    MessageFormat.format(
                            "User by username {0} or email {1} exists", username, email
                    )
            );
        }

        User user = userRepository.save(userMapper.toEntity(request));
        log.info("success create user");
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse update(String id, UpdateUserRequest request) {
        log.info("Call method update");

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
        log.info("success update user");
        return userMapper.toResponse(userUpdate);
    }

    @Override
    public void delete(String id) {
        log.info("Call method delete");
        userRepository.deleteById(id);
    }
}
