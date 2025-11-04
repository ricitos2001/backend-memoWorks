package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.User;

import java.util.List;

public record UpdateGroupDTO(User adminUser, List<User> users) {
}
