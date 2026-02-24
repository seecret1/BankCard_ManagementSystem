package com.github.seecret1.bank_card_management_system.exception;

public class EmailExistsException extends RuntimeException {

    public EmailExistsException(String message) {
        super(message);
    }
}
