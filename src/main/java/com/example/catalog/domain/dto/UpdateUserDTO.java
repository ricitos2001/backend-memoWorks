package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.Task;

import java.util.List;

public record UpdateUserDTO(String name, String username, String phoneNumber, String email, String password, List<Task> tasks) {
}
