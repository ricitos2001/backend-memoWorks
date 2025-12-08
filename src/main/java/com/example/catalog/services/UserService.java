package com.example.catalog.services;

import com.example.catalog.domain.dto.*;
import com.example.catalog.domain.entities.User;
import com.example.catalog.mappers.UserMapper;
import com.example.catalog.repositories.UserRepository;
import com.example.catalog.web.exceptions.DuplicatedUserException;
import com.example.catalog.web.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponseDTO> list(Pageable pageable) {
        Page<UserResponseDTO> users = userRepository.findAll(pageable).map(UserMapper::toDTO);
        return users;
    }

    public UserResponseDTO showById(Long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        } else {
            return UserMapper.toDTO(user);
        }
    }

    public UserResponseDTO showByName(String username) {
        User user = userRepository.getUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException(username);
        } else {
            return UserMapper.toDTO(user);
        }
    }

    public UserResponseDTO create(UserRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicatedUserException(dto.getUsername());
        } else {
            User user = UserMapper.toEntity(dto);
            User savedUser = userRepository.save(user);
            return UserMapper.toDTO(savedUser);
        }
    }

    public User createUser(UserRequestDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicatedUserException(dto.getUsername());
        } else {
            User user = UserMapper.toEntity(dto);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            User savedUser = userRepository.save(user);
            return savedUser;
        }
    }

    public UserResponseDTO update(Long id, @RequestBody UserRequestDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        updateBasicFields(dto, user);
        User updatedUser = userRepository.save(user);
        return UserMapper.toDTO(updatedUser);
    }

    private void updateBasicFields(UserRequestDTO user, User updatedUser) {
        Optional.ofNullable(user.getName()).ifPresent(updatedUser::setName);
        Optional.ofNullable(user.getSurnames()).ifPresent(updatedUser::setSurnames);
        Optional.ofNullable(user.getUsername()).ifPresent(updatedUser::setUsername);
        Optional.ofNullable(user.getPhoneNumber()).ifPresent(updatedUser::setPhoneNumber);
        Optional.ofNullable(user.getEmail()).ifPresent(updatedUser::setEmail);
        Optional.ofNullable(user.getPassword()).ifPresent(updatedUser::setPassword);
        Optional.ofNullable(user.getTasks()).ifPresent(updatedUser::setTasks);
        Optional.ofNullable(user.getRol()).ifPresent(updatedUser::setRol);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) throw new IllegalArgumentException("User not found");
        userRepository.deleteById(id);
    }
}
