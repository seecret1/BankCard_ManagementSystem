package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.entity.User;
import com.github.seecret1.bank_card_management_system.exception.AuthException;
import com.github.seecret1.bank_card_management_system.exception.RegisterUserException;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.mapper.UserMapper;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.repository.specification.UserSpecification;
import com.github.seecret1.bank_card_management_system.service.InternalUserService;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, InternalUserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> findAllUsers(PageModel pageModel) {
        log.info("Find all users");

        Pageable pageable = pageModel.toPageRequest();
        var page = userRepository.findAll(pageable);
        log.debug("Find users list. page: {}, size list: {}, page list: {}",
                page.getTotalPages(), page.getTotalElements(), page.getContent());

        return new PageResponse<>(
                page.getTotalElements(),
                page.getTotalPages(),
                userMapper.toListResponse(page.getContent())
        );
    }

    @Override
    @Transactional(readOnly = true)
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
                userMapper.toListResponse(page.getContent())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findByCriterial(String criterial) {
        log.info("Find user by criterial: {}", criterial);
        User user = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by criterial: " + criterial
                ));
        log.debug("Found user by criterial. User: {}", user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
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
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserResponse updateFull(String id, CreateUserRequest request) {
        log.info("Update user by id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ));

        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setMiddleName(request.getMiddleName());
        existingUser.setBirthDate(request.getBirthDate());
        existingUser.setRole(request.getRole());

        User savedUser = userRepository.save(existingUser);

        log.debug("Success full update user: {}", savedUser);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserResponse update(String criterial, UpdateUserRequest request) {
        log.info("Update user by criterial: {}", criterial);

        var userUpdate = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with criterial: " + criterial
                ));
        try {
            if (request.getUsername() != null) {
                userUpdate.setUsername(request.getUsername());
            }
            if (request.getEmail() != null) {
                userUpdate.setEmail(request.getEmail());
            }
            if (request.getPassword() != null) {
                userUpdate.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            userRepository.save(userUpdate);
            log.debug("Success update user: {}", userUpdate);
            return userMapper.toResponse(userUpdate);

        } catch (DataIntegrityViolationException ex) {
            throw new AuthException(ex.getMessage());
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(String criterial) {
        log.info("Delete user by criterial: {}", criterial);
        var user = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by criterial: " + criterial
                ));
        log.debug("Success delete user: {}", user);
        userRepository.delete(user);
    }

    @Override
    public User findUserEntityByCriterial(String criterial) {
        log.info("Find user entity by criterial: {}", criterial);
        User user = userRepository.findByCriterial(criterial)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found by criterial: " + criterial
                ));
        log.debug("Found user entity by criterial. User: {}", user);
        return user;
    }
}
