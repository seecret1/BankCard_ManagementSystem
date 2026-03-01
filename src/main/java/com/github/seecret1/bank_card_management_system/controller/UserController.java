package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.model.UserSearchModel;
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

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<UserResponse>> findByFilter(
            @Valid @RequestBody UserFilterModel filter
    ) {
        return ResponseEntity.ok(userService.findByFilter(filter));
    }

    @GetMapping("/search")
    public ResponseEntity<UserResponse> findBySearchModel(
            @Valid @RequestBody UserSearchModel searchModel
    ) {
        return ResponseEntity.ok(userService.findBySearchModel(searchModel));
    }

    @GetMapping("/search/{criterial}")
    public ResponseEntity<UserResponse> findByCriterial(
            @PathVariable String criterial
    ) {
        return ResponseEntity.ok(userService.findByCriterial(criterial));
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PutMapping("/update/put/{criterial}")
    public ResponseEntity<UserResponse> updateFull(
            @PathVariable String criterial,
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateFull(criterial, request));
    }

    @PatchMapping("/update/path/{criterial}")
    public ResponseEntity<UserResponse> update(
            @PathVariable String criterial,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.update(criterial, request));
    }

    @DeleteMapping("/delete/{criterial}")
    public ResponseEntity<UserResponse> delete(
            @PathVariable String criterial
    ) {
        userService.delete(criterial);
        return ResponseEntity.noContent().build();
    }
}
