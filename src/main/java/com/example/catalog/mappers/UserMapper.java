package com.example.catalog.mappers;

import com.example.catalog.domain.dto.UserRequestDTO;
import com.example.catalog.domain.dto.UserResponseDTO;
import com.example.catalog.domain.entities.Group;
import com.example.catalog.domain.entities.Task;
import com.example.catalog.domain.entities.User;

public class UserMapper {
    public static User toEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setSurnames(dto.getSurnames());
        user.setUsername(dto.getUsername());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setTasks(dto.getTasks());
        user.setRol(dto.getRol());
        user.setAvatar(dto.getAvatar());
        user.setGroups(dto.getGroups());
        return user;
    }

    public static UserResponseDTO toDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurnames(),
                user.getUsername(),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getPassword(),
                user.getTasks().stream().map(Task::getId).toList(),
                user.getRol(),
                user.getAvatar(),
                user.getGroups().stream().map(Group::getId).toList()
        );
    }
}
