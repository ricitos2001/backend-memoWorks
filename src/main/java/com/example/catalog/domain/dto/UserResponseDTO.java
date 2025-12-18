package com.example.catalog.domain.dto;

import com.example.catalog.domain.entities.Task;
import com.example.catalog.domain.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String name;
    private String surnames;
    private String username;
    private String phoneNumber;
    private String email;
    private String password;
    private List<Task> tasks;
    private Rol rol;
    private String avatar;

}
