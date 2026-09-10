package com.example.card_service.application.providers.exceptions;

public class CustomerServiceUnavailableException extends RuntimeException {
    public CustomerServiceUnavailableException(String message) {
        super(message);
    }
}
