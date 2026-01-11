package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GroupResponseDTO {
    private Long id;
    private String name;
    private String description;
    private User adminUser;
    private List<User> users;
}
