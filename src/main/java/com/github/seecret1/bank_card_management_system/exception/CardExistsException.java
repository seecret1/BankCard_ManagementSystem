package com.github.seecret1.bank_card_management_system.exception;

public class CardExistsException extends RuntimeException{

    public CardExistsException(String message) {
        super(message);
    }

    public CardExistsException() { }
}
