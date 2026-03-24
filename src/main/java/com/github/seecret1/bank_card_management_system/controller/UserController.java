package com.github.seecret1.bank_card_management_system.controller;

import com.github.seecret1.bank_card_management_system.dto.request.CreateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.request.UpdateUserRequest;
import com.github.seecret1.bank_card_management_system.dto.response.PageResponse;
import com.github.seecret1.bank_card_management_system.dto.response.UserResponse;
import com.github.seecret1.bank_card_management_system.model.PageModel;
import com.github.seecret1.bank_card_management_system.model.UserFilterModel;
import com.github.seecret1.bank_card_management_system.service.UserService;
import com.github.seecret1.bank_card_management_system.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @Valid PageModel pageModel
    ) {
        return ResponseEntity.ok(userService.findAllUsers(pageModel));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findByFilter(
            @Valid UserFilterModel filter
    ) {
        return ResponseEntity.ok(userService.findByFilter(filter));
    }

    @GetMapping("/{criterial}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> findByCriterial(
            @PathVariable String criterial
    ) {
        return ResponseEntity.ok(userService.findByCriterial(criterial));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> create(
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PutMapping("/{criterial}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> updateFull(
            @PathVariable String criterial,
            @Valid @RequestBody CreateUserRequest request
    ) {
        return ResponseEntity.ok(userService.updateFull(criterial, request));
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponse> updateYour(
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(userService.updateYour(
                AuthUtils.getCurrentUserId(userDetails),
                request
        ));
    }

    @DeleteMapping("/{criterial}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable String criterial
    ) {
        userService.delete(criterial);
        return ResponseEntity.noContent().build();
    }
}
