package com.github.seecret1.bank_card_management_system.service.impl;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.exception.UserNotFoundException;
import com.github.seecret1.bank_card_management_system.mapper.UserMapper;
import com.github.seecret1.bank_card_management_system.repository.UserRepository;
import com.github.seecret1.bank_card_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;


    @Override
    public List<UserResponse> findAllUsers() {
        return userMapper.toListResponse(userRepository.findAll());
    }

    @Override
    public UserResponse findById(String id) {
        return userMapper.entityToResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + id
                ))
        );
    }

    @Override
    public UserResponse findByUsername(String username) {
        return userMapper.entityToResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username: " + username
                ))
        );
    }

    @Override
    public UserResponse findUserByEmail(String email) {
        return userMapper.entityToResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with email: " + email
                ))
        );
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        return null;
    }

    @Override
    public UserResponse update(String id, UpdateUserRequest request) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
