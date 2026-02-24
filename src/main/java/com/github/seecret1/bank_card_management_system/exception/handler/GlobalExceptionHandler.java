package com.github.seecret1.bank_card_management_system.exception.handler;

import com.github.seecret1.bank_card_management_system.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.sasl.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(
            AuthException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("AuthException: " + ex.getMessage());
    }

    @ExceptionHandler(CheckPasswordException.class)
    public ResponseEntity<String> handleCheckPasswordException(
            CheckPasswordException ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("CheckPasswordException: " + ex.getMessage());
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<String> handleEmailExistsException(
            EmailExistsException ex
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("EmailExistsException: " + ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ResponseEntity<String> handleRefreshTokenException(
            RefreshTokenException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("RefreshTokenException: " + ex.getMessage());
    }

    @ExceptionHandler(TokenParseException.class)
    public ResponseEntity<String> handleTokenParseException(
            TokenParseException ex
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("TokenParseException: " + ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(
            UserNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("UserNotFoundException: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(
            Exception ex
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred: " + ex.getMessage());
    }
}
