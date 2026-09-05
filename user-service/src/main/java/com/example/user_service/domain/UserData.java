package com.example.user_service.domain;

public record UserData(
        String userName,
        String password,
        Integer age
) {
}
