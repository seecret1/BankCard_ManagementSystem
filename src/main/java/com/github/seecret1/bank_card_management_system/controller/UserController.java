package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> findByEmail(
            @PathVariable String email
    ) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> findByUsername(
            @PathVariable String username
    ) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PutMapping("/update/put/{id}")
    public ResponseEntity<UserResponse> updateFull(
            @PathVariable String id,
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateFull(id, request));
    }

    @PatchMapping("/update/path/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<UserResponse> delete(
            @PathVariable String id
    ) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
