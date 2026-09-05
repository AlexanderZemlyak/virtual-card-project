package com.example.user_service.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record RegisterRequest (
        @NotBlank
        @Length(min = 2, max = 50)
        String userName,

        @NotBlank
        @Length(min = 5, max = 100)
        String password,

        @NotNull
        @Positive
        @Max(200)
        Integer age
) { }
