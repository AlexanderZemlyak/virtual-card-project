package com.example.user_service.domain.exceptions;

public class UserNameIsAlreadyTaken extends RuntimeException {
    public UserNameIsAlreadyTaken(String message) {
        super(message);
    }
}
