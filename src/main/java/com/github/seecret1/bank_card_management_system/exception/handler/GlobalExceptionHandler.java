package com.github.seecret1.bank_card_management_system.exception.handler;

import com.github.seecret1.bank_card_management_system.dto.response.ErrorResponse;
import com.github.seecret1.bank_card_management_system.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(
            AuthException ex
    ) {
        log.error("GlobalRestControllerAdvice -> AuthException: " + ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(CheckPasswordException.class)
    public ResponseEntity<ErrorResponse> handleCheckPasswordException(
            CheckPasswordException ex
    ) {
        log.error("GlobalRestControllerAdvice -> CheckPasswordException: " + ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RegisterUserException.class)
    public ResponseEntity<ErrorResponse> handleEmailExistsException(
            RegisterUserException ex
    ) {
        log.error("GlobalRestControllerAdvice -> RegisterUserException: " + ex);
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(
            RefreshTokenException ex
    ) {
        log.error("GlobalRestControllerAdvice -> RefreshTokenException: " + ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(TokenParseException.class)
    public ResponseEntity<ErrorResponse> handleTokenParseException(
            TokenParseException ex
    ) {
        log.error("GlobalRestControllerAdvice -> TokenParseException: " + ex);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex
    ) {
        log.error("GlobalRestControllerAdvice -> UserNotFoundException: " + ex);
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex
    ) {
        log.error("GlobalRestControllerAdvice -> Exception: " + ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.error("GlobalRestControllerAdvice -> MethodArgumentNotValidException: " + errors);
        return buildResponse(HttpStatus.BAD_REQUEST, errors.toString());
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message
    ) {
        return ResponseEntity
                .status(status)
                .body(ErrorResponse
                        .builder()
                        .status(status.value())
                        .message(message)
                        .timestamp(LocalDateTime.now())
                        .build()
                );
    }
}
