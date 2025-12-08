package com.example.catalog.segurity.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

}
