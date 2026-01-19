package com.example.catalog.domain.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class PasswordResetConfirmDTO {
    @NotBlank
    private String token;

    @NotBlank
    private String newPassword;
}
